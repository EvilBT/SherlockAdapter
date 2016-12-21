package xyz.zpayh.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 陈志鹏
 * on 2016/12/19.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder>
    implements LoadMore{

    private final List<T> mData;

    private int mHeadLayouts[] = new int[0];

    private int mFootLayouts[] = new int[0];

    public BaseAdapter(){
        mData = new ArrayList<>();
    }

    public void setData(List<T> data){
        mData.clear();
        if (data != null){
            mData.addAll(data);
        }

        if (mOpenAutoLoadMore){
            mLoadFailed = false;
            mLoadState = LOADING;
        }

        notifyDataSetChanged();
    }

    public void addData(List<T> data){
        if (data != null){
            final int startPos = mData.size() + getHeadSize();
            final int itemCount = data.size() + getFootSize() + (canAutoLoadMore()?1:0);
            mData.addAll(data);

            if (mOpenAutoLoadMore){
                mLoadFailed = false;
                mLoadState = LOADING;
            }

            notifyItemRangeChanged(startPos,itemCount);
        }
    }

    public void addHeadLayout(@LayoutRes int headLayout) {

        int indexToAdd = -1;

        for (int i = 0; i < mHeadLayouts.length; i++) {
            if (mHeadLayouts[i] == 0){
                indexToAdd = i;
                break;
            }
        }

        if (indexToAdd == -1){
            indexToAdd = mHeadLayouts.length;
            mHeadLayouts = Arrays.copyOf(mHeadLayouts,
                    indexToAdd < 2 ? 2 : indexToAdd * 2);
            for (int i = indexToAdd; i < mHeadLayouts.length; i++) {
                mHeadLayouts[i] = 0;
            }
        }

        mHeadLayouts[indexToAdd] = headLayout;
    }

    public boolean hasHead(){
        return getHeadSize() > 0;
    }

    public int getHeadSize(){
        for (int i = 0; i < mHeadLayouts.length; i++) {
            if (mHeadLayouts[i] == 0){
                return i;
            }
        }
        return mHeadLayouts.length;
    }

    public void addFootLayout(@LayoutRes int footLayout){
        int indexToAdd = -1;

        for (int i = 0; i < mFootLayouts.length; i++) {
            if (mFootLayouts[i] == 0){
                indexToAdd = i;
                break;
            }
        }

        if (indexToAdd == -1){
            indexToAdd = mFootLayouts.length;
            mFootLayouts = Arrays.copyOf(mFootLayouts,
                    indexToAdd < 2 ? 2 : indexToAdd*2);
            for (int i = indexToAdd; i < mFootLayouts.length; i++) {
                mFootLayouts[i] = 0;
            }
        }

        mFootLayouts[indexToAdd] = footLayout;
    }

    public boolean hasFoot(){
        return getHeadSize() > 0;
    }

    public int getFootSize(){
        for (int i = 0; i < mFootLayouts.length; i++) {
            if (mFootLayouts[i] == 0){
                return i;
            }
        }

        return mFootLayouts.length;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int layoutRes) {
        BaseViewHolder baseViewHolder = new BaseViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(layoutRes, parent, false));
        bind(baseViewHolder,layoutRes);

        if (layoutRes == R.layout.default_loadmore){
            //默认的加载更多
            bindLoadMore(baseViewHolder);
        }
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        int index = position;
        if (index < getHeadSize()){
            convertHead(holder,mHeadLayouts[index],index);
            return;
        }
        index = position - getHeadSize();
        if (index < mData.size()) {
            final T data = mData.get(index);
            convert(holder, data, index);
            return;
        }
        index = position - getHeadSize() - mData.size();
        if (index < getFootSize()){
            convertFoot(holder,mFootLayouts[index],index);
            return;
        }
        if (canAutoLoadMore()){
            if (mLoadState != LOAD_COMPLETED){
                if (!mLoadFailed){
                    mLoadState = LOADING;
                    mOnLoadMoreListener.onLoadMore();
                }else{
                    mLoadState = LOAD_FAILED;
                }
            }
            convertLoadMore(holder,mLoadState);
        }
    }

    @Override
    public int getItemCount() {
        final int loadMoreCount = canAutoLoadMore()?1:0;
        return getHeadSize() + mData.size() + getFootSize()+loadMoreCount;
    }

    @Override
    public int getItemViewType(int position) {
        int index = position;
        if (index < getHeadSize()){
            return mHeadLayouts[index];
        }
        index = position - getHeadSize();
        if (index < mData.size()){
            return getLayoutRes(index);
        }
        index = position - getHeadSize() - mData.size();
        if (index < getFootSize()) {
            return mFootLayouts[index];
        }

        return mLoadMoreLayout;
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        final int position = holder.getLayoutPosition();
        if (position < getHeadSize() || position >= getHeadSize()+mData.size()) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null) {
                if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
                }
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager != null && manager instanceof GridLayoutManager){
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position < getHeadSize()){
                        return gridLayoutManager.getSpanCount();
                    }
                    if (position >= getHeadSize() + mData.size()){
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    public void convertHead(BaseViewHolder holder, @LayoutRes int headLayout, int index){

    }

    public void convertFoot(BaseViewHolder holder, @LayoutRes int footLayout, int index){

    }

    public void convertLoadMore(BaseViewHolder holder, @LoadState int loadState){
        if (loadState == LOADING){
            holder.setVisibility(R.id.progressBar, View.VISIBLE)
                    .setVisibility(R.id.load_tips, View.VISIBLE)
                    .setVisibility(R.id.load_completed, View.GONE)
                    .setVisibility(R.id.iv_load_tips, View.GONE)
                    .setText(R.id.load_tips,R.string.loading);
        }else if (loadState == LOAD_FAILED) {
            holder.setVisibility(R.id.progressBar, View.GONE)
                    .setVisibility(R.id.load_tips, View.VISIBLE)
                    .setVisibility(R.id.load_completed, View.GONE)
                    .setVisibility(R.id.iv_load_tips, View.VISIBLE)
                    .setText(R.id.load_tips, R.string.load_failed);
        }else if (loadState == LOAD_COMPLETED){
            holder.setVisibility(R.id.progressBar, View.GONE)
                    .setVisibility(R.id.load_tips, View.GONE)
                    .setVisibility(R.id.iv_load_tips, View.GONE)
                    .setVisibility(R.id.load_completed, View.VISIBLE);
        }
    }

    private void bindLoadMore(BaseViewHolder holder){
        holder.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                if (mLoadState == LOAD_FAILED){
                    // 点击加载更多
                    mLoadFailed = false;
                    notifyItemChanged(getItemCount()-1);
                }
            }
        });
    }

    //======================= LoadMore ==========================

    private OnLoadMoreListener mOnLoadMoreListener;

    //private boolean mAutoLoadMore;

    private boolean mOpenAutoLoadMore;

    private boolean mLoadFailed;

    @LayoutRes
    private int mLoadMoreLayout = R.layout.default_loadmore;

    @LoadState
    private int mLoadState;

    @Override
    public void openAutoLoadMore(boolean open) {
        //mAutoLoadMore = true;
        if (canAutoLoadMore()&&!open) {
            notifyDataSetChanged();
        }
        if (!mOpenAutoLoadMore&&open) {
            mLoadFailed = false;
            mLoadState = LOADING;
            notifyDataSetChanged();
        }
        mOpenAutoLoadMore = open;
    }

    @Override
    public void loadCompleted() {
        mLoadState = LOAD_COMPLETED;
        if (canAutoLoadMore()) {
            notifyItemChanged(getItemCount()-1);
        }
    }

    @Override
    public void loadFailed() {
        mLoadState = LOAD_FAILED;
        if (canAutoLoadMore()) {
            mLoadFailed = true;
            notifyItemChanged(getItemCount()-1);
        }
    }

    @Override
    public void setOnLoadMoreListener(@Nullable OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void setLoadMoreLayout(@LayoutRes int moreLayout) {
        mLoadMoreLayout = moreLayout;
    }

    @Override
    public boolean canAutoLoadMore() {
        return mOnLoadMoreListener != null && mOpenAutoLoadMore;
    }

    //======================= LoadMore ==========================

    @LayoutRes
    public abstract int getLayoutRes(int position);

    public abstract void convert(BaseViewHolder holder, T data, int index);

    public abstract void bind(BaseViewHolder holder,int viewType);
}
