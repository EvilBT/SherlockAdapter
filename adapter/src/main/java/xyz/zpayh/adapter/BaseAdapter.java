package xyz.zpayh.adapter;

import android.support.annotation.CheckResult;
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
 * 一个简单通用的Adapter控件
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder>
    implements LoadMore{

    protected final List<T> mData;

    private int mHeadLayouts[] = new int[0];

    private int mFootLayouts[] = new int[0];

    /**
     * Empty布局，在没有数据的时间显示，默认是R.layout.default_empty
     */
    @LayoutRes
    private int mEmptyLayout;

    /**
     * Error布局，在没有数据的时间显示，默认是R.layout.default_error
     */
    @LayoutRes
    private int mErrorLayout;

    /**
     * 是否显示Error布局
     */
    private boolean mShowErrorView;
    /**
     * 加载更多监听，必须设置监听事件才会出现加载更多功能
     */
    private OnLoadMoreListener mOnLoadMoreListener;

    /**
     * 打开关闭自动加载更多功能，true为打开，false为关闭
     * 只有同时设置了监听以及打开了自动加载更多功能，才会出现加载更多功能
     */
    private boolean mOpenAutoLoadMore;

    /**
     * LoadMore布局，加载成功，失败以及加载中都是显示此布局，
     * 因此你可能要重写{@link #convertLoadMore(BaseViewHolder, int)}
     * 来控制显示隐藏一些View，已经提供了默认实现
     */
    @LayoutRes
    private int mLoadMoreLayout;

    /**
     * 加载更多状态，有{@link LoadMore#LOADING},{@link LoadMore#LOAD_COMPLETED}以及
     * {@link LoadMore#LOAD_FAILED}三种状态，用来区分加载更多情况，可以参考
     * {@link #convertLoadMore(BaseViewHolder, int)}
     */
    @LoadState
    private int mLoadState;

    private OnItemClickListener mOnItemClickListener;

    public BaseAdapter(){
        mData = new ArrayList<>();
        mEmptyLayout = R.layout.default_empty;
        mErrorLayout = R.layout.default_error;
        mLoadMoreLayout = R.layout.default_loadmore;
    }

    /**
     * 设置新数据，会清除掉原有数据，并有可能重置加载更多状态
     * @param data 数据集合
     */
    public void setData(@Nullable List<T> data){
        mData.clear();
        if (data != null){
            mData.addAll(data);
        }

        if (mOpenAutoLoadMore){
            mLoadState = LOADING;
        }
        mShowErrorView = false;
        notifyDataSetChanged();
    }

    /**
     * 添加新数据，并有可能重置加载更多状态
     * @param data 数据集合
     */
    public void addData(List<T> data){
        if (data == null){
            return;
        }
        final int startPos = mData.size() + getHeadSize();
        final int itemCount = data.size() + getFootSize() + (canAutoLoadMore()?1:0);
        mData.addAll(data);

        if (mOpenAutoLoadMore){
            mLoadState = LOADING;
        }
        mShowErrorView = false;
        notifyItemRangeChanged(startPos,itemCount);
    }

    /**
     * 此position是AdapterPosition
     * @param adapterPosition 当前项的AdapterPosition
     * @return 如果当前项是数据项，则返回对应的数据，
     *         如果不是，则返回null,所以使用前必须检查返回值
     */
    @CheckResult
    @Nullable
    public T getData(int adapterPosition){
        final int index = adapterPosition - getHeadSize();
        if (index >= 0 && index < mData.size()){
            return mData.get(index);
        }
        return null;
    }

    public List<T> getData(){
        return mData;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * 按调用顺序添加头部布局
     * @param headLayout 布局id
     */
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

    /**
     * 按调用顺序添加尾部布局
     * @param footLayout 布局id
     */
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
        if (layoutRes == mLoadMoreLayout){
            //默认实现的加载更多添加点击事件
            bindLoadMore(baseViewHolder);
        }else if (layoutRes == mEmptyLayout){
            bindEmpty(baseViewHolder);
        }else if (layoutRes == mErrorLayout){
            bindError(baseViewHolder);
        }else{
            bindData(baseViewHolder,layoutRes);
        }
        return baseViewHolder;
    }

    @Override
    public final void onBindViewHolder(BaseViewHolder holder, int position) {

        if (mShowErrorView && position == 0){
            convertError(holder);
            return;
        }
        //没有数据时只显示空数据布局
        if (mData.isEmpty() && position == 0){
            convertEmpty(holder);
            return;
        }

        int index = position;
        if (index < getHeadSize()){
            //头部布局
            convertHead(holder,mHeadLayouts[index],index);
            return;
        }
        index = position - getHeadSize();
        if (index < mData.size()) {
            //数据布局
            final T data = mData.get(index);
            convert(holder, data, index);
            return;
        }
        index = position - getHeadSize() - mData.size();
        if (index < getFootSize()){
            //尾部布局
            convertFoot(holder,mFootLayouts[index],index);
            return;
        }
        if (canAutoLoadMore()){
            if (mLoadState == LOADING){
                mOnLoadMoreListener.onLoadMore();
            }
            //加载更多布局
            convertLoadMore(holder,mLoadState);
        }
    }

    @Override
    public final int getItemCount() {
        if (mShowErrorView){
            //显示加载错误时不显示其他
            return 1;
        }
        if (mData.isEmpty()){
            //没有数据时只显示空布局
            return 1;
        }
        final int loadMoreCount = canAutoLoadMore()?1:0;
        return getHeadSize() + mData.size() + getFootSize()+loadMoreCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowErrorView){
            return mErrorLayout;
        }

        if (mData.isEmpty()){
            return mEmptyLayout;
        }

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

        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp == null || !(lp instanceof StaggeredGridLayoutManager.LayoutParams)) return;
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) lp;

        final int position = holder.getLayoutPosition();
        if (mShowErrorView && position == 0){
            //显示数据异常
            layoutParams.setFullSpan(true);
            return;
        }
        if (mData.isEmpty() && position == 0){
            //显示空布局
            layoutParams.setFullSpan(true);
            return;
        }
        if (position < getHeadSize()){
            //显示头部
            layoutParams.setFullSpan(true);
            return;
        }
        if (position >= getHeadSize()+mData.size()){
            //显示尾部及加载更多
            layoutParams.setFullSpan(true);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager == null || !(manager instanceof GridLayoutManager)) return;
        final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mShowErrorView && position == 0){
                    //显示数据异常
                    return gridLayoutManager.getSpanCount();
                }
                if (mData.isEmpty() && position == 0){
                    //显示空布局
                    return gridLayoutManager.getSpanCount();
                }
                if (position < getHeadSize()){
                    //显示头部
                    return gridLayoutManager.getSpanCount();
                }
                if (position >= getHeadSize()+mData.size()){
                    //显示尾部及加载更多
                    return gridLayoutManager.getSpanCount();
                }

                final T data = getData(position);
                if (data != null && data instanceof IMultiItem){
                    int spanSize = ((IMultiItem)data).getSpanSize();
                    return spanSize <= 0 ? 1 :
                            spanSize > gridLayoutManager.getSpanCount()?
                            gridLayoutManager.getSpanCount():spanSize;
                }
                return 1;
            }
        });
    }

    /**
     * 如果要对头布局进行处理可重写此方法
     * @param holder 布局holder
     * @param headLayout 布局id，跟index可以确定布局是第几个布局
     * @param index 添加时的顺序
     */
    public void convertHead(BaseViewHolder holder, @LayoutRes int headLayout, int index){

    }

    /**
     * 如果要对尾布局进行处理可重写此方法
     * @param holder 布局holder
     * @param footLayout 布局id，跟index可以确定布局是第几个布局
     * @param index 添加时的顺序
     */
    public void convertFoot(BaseViewHolder holder, @LayoutRes int footLayout, int index){

    }

    /**
     * 如果设置了自定义加载更多布局，则需要重写此方法，然后根据 loadState 的值来控制显示
     * 、隐藏布局。具体可以参考默认实现。
     * @param holder 布局holder
     * @param loadState 是 {@link LoadMore#LOADING}、{@link LoadMore#LOAD_COMPLETED}
     *                  或者{@link LoadMore#LOAD_FAILED} 之一，分别对应加载中，完成加载，
     *                  还有加载失败三种情况
     */
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

    /**
     * 如果设置了自定义Empty布局，且想对其设置一些数据显示处理，可以重写此方法
     */
    public void convertEmpty(BaseViewHolder holder){

    }

    /**
     * 如果设置了自定义Error布局，且想对其设置一些数据显示处理，可以重写此方法
     */
    public void convertError(BaseViewHolder holder){

    }

    /**
     * 默认加载更多的点击事件实现
     */
    private void bindLoadMore(BaseViewHolder holder){
        holder.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                if (mLoadState == LOAD_FAILED){
                    // 点击加载更多
                    mLoadState = LOADING;
                    notifyItemChanged(getItemCount()-1);
                }
            }
        });
    }

    /**
     * 默认空布局的点击事件实现
     */
    protected void bindEmpty(BaseViewHolder holder){}

    /**
     * 默认异常布局的点击事件实现
     */
    protected void bindError(BaseViewHolder holder){}


    private void bindData(BaseViewHolder baseViewHolder, int layoutRes) {
        baseViewHolder.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                if (mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(view, adapterPosition);
                }
            }
        });
        bind(baseViewHolder, layoutRes);
    }

    public void setEmptyLayout(@LayoutRes int emptyLayout){
        mEmptyLayout = emptyLayout;
    }

    public void setErrorLayout(@LayoutRes int errorLayout){
        mErrorLayout = errorLayout;
    }

    public void showErrorView(){
        mShowErrorView = true;
        notifyDataSetChanged();
    }
    //======================= LoadMore ==========================

    @Override
    public void openAutoLoadMore(boolean open) {
        //mAutoLoadMore = true;
        if (canAutoLoadMore()&&!open) {
            notifyDataSetChanged();
        }
        if (!mOpenAutoLoadMore&&open) {
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
            //mLoadFailed = true;
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
    public abstract int getLayoutRes(int index);

    public abstract void convert(BaseViewHolder holder, T data, int index);

    public abstract void bind(BaseViewHolder holder,int layoutRes);
}
