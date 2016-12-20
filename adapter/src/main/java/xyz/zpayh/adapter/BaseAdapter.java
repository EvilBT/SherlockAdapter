package xyz.zpayh.adapter;

import android.support.annotation.LayoutRes;
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
 * Created by Administrator on 2016/12/19.
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
            mAutoLoadMore = true;
            mLoadFailed = false;
        }

        notifyDataSetChanged();
    }

    public void addData(List<T> data){
        if (data != null){
            final int startPos = mData.size();
            mData.addAll(data);

            if (mOpenAutoLoadMore){
                mAutoLoadMore = true;
                mLoadFailed = false;
            }

            notifyItemRangeInserted(startPos,data.size());
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
            if (mLoadFailed){
                holder.setVisibility(R.id.progressBar, View.GONE)
                    .setText(R.id.load_tips,R.string.load_failed);
            } else {
                holder.setVisibility(R.id.progressBar, View.VISIBLE)
                        .setText(R.id.load_tips,R.string.loading);
                mOnLoadMoreListener.onLoadMore();
            }
            convertLoadMore(holder,mLoadFailed);
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
        if (position < getHeadSize() || (position > getHeadSize()+mData.size()
                && position < mData.size() + getHeadSize() + getFootSize())) {
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
                    if (position > getHeadSize() + mData.size()){
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

    public void convertLoadMore(BaseViewHolder holder, boolean loadResult){

    }

    //======================= LoadMore ==========================

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean mAutoLoadMore;

    private boolean mOpenAutoLoadMore;

    private boolean mLoadFailed;

    @LayoutRes
    private int mLoadMoreLayout = R.layout.default_loadmore;

    @Override
    public void openAutoLoadMore() {
        mAutoLoadMore = true;
        mOpenAutoLoadMore = true;
    }

    @Override
    public void loadCompleted() {
        if (canAutoLoadMore()) {
            mAutoLoadMore = false;
            notifyItemRemoved(getItemCount());
        }
    }

    @Override
    public void loadFailed() {
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
        return mOnLoadMoreListener != null && mAutoLoadMore;
    }

    //======================= LoadMore ==========================

    @LayoutRes
    public abstract int getLayoutRes(int position);

    public abstract void convert(BaseViewHolder holder, T data, int index);

    public abstract void bind(BaseViewHolder holder,int viewType);
}
