/*
 * Copyright 2017 陈志鹏
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.zpayh.adapter;


import android.support.annotation.CheckResult;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文 件 名: ExpandableAdapter
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/23 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 * 重写支持展开Adapter
 */

public abstract class ExpandableAdapter extends RecyclerView.Adapter<BaseViewHolder>
        implements LoadMore{

    protected final List<IMultiItem> mData = new ArrayList<>();

    private int mHeadLayouts[] = new int[0];

    private int mFootLayouts[] = new int[0];

    /**
     * Empty布局，在没有数据的时间显示，默认是R.layout.default_empty
     */
    @LayoutRes
    private int mEmptyLayout = R.layout.default_empty;

    /**
     * Error布局，在没有数据的时间显示，默认是R.layout.default_error
     */
    @LayoutRes
    private int mErrorLayout = R.layout.default_error;

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
    private int mLoadMoreLayout = R.layout.default_loadmore;

    /**
     * 加载更多状态，有{@link LoadMore#LOADING},{@link LoadMore#LOAD_COMPLETED}以及
     * {@link LoadMore#LOAD_FAILED}三种状态，用来区分加载更多情况，可以参考
     * {@link #convertLoadMore(BaseViewHolder, int)}
     */
    @LoadState
    private int mLoadState;

    private boolean mIsLoading = false;

    private OnItemClickListener mOnItemClickListener;

    private OnItemLongClickListener mOnItemLongClickListener;

    private DiffUtilCallback<IMultiItem> mCallback;

    private boolean mDetectMoves = true;

    /**
     * 设置新数据，会清除掉原有数据，并有可能重置加载更多状态
     * @param data 数据集合
     */
    public void setData(@Nullable List<? extends IMultiItem> data){

        if (mOpenAutoLoadMore){
            mLoadState = LOADING;
            mIsLoading = false;
        }
        mShowErrorView = false;

        if (mCallback == null){
            mData.clear();
            if (data != null){
                mData.addAll(data);
            }
            notifyDataSetChanged();
            return;
        }

        final List<IMultiItem> oldData = new ArrayList<>(mData);
        mData.clear();
        if (data != null){
            mData.addAll(data);
        }
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return getShowSize(oldData);
            }

            @Override
            public int getNewListSize() {
                return getShowSize(mData);
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                final IMultiItem oldItem = getData(oldData,oldItemPosition);
                final IMultiItem newItem = getData(mData,newItemPosition);
                return mCallback.areItemsTheSame(oldItem,newItem);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                final IMultiItem oldItem = getData(oldData,oldItemPosition);
                final IMultiItem newItem = getData(mData,newItemPosition);
                return mCallback.areContentsTheSame(oldItem,newItem);
            }

            @Nullable
            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                final IMultiItem oldItem = getData(oldData,oldItemPosition);
                final IMultiItem newItem = getData(mData,newItemPosition);
                return mCallback.getChangePayload(oldItem,newItem);
            }
        },mDetectMoves);

        result.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {
                int skew = getHeadSize();
                notifyItemRangeInserted(position + skew, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                int skew = getHeadSize();
                notifyItemRangeRemoved(position + skew, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                int skew = getHeadSize();
                notifyItemMoved(fromPosition + skew, toPosition + skew);
            }

            @Override
            public void onChanged(int position, int count, Object payload) {
                int skew = getHeadSize();
                notifyItemRangeChanged(position + skew, count, payload);
            }
        });
    }

    /**
     * 添加新数据，并有可能重置加载更多状态
     * @param data 数据集合
     */
    public void addData(IMultiItem data){
        if (data == null){
            return;
        }
        final int startPos = getDataSize() + getHeadSize();
        int itemCount = 1 + getFootSize() + (canAutoLoadMore()?1:0);
        if (data instanceof IExpandable && ((IExpandable) data).isExpandable()){
            itemCount += getShowSize(((IExpandable) data).getSubItems());
        }

        mData.add(data);

        if (mOpenAutoLoadMore){
            mLoadState = LOADING;
        }
        mShowErrorView = false;
        notifyItemRangeChanged(startPos,itemCount);
    }

    /**
     * 添加新数据，并有可能重置加载更多状态
     * @param data 数据集合
     */
    public void addData(List<? extends IMultiItem> data){
        if (data == null){
            return;
        }
        final int startPos = getDataSize() + getHeadSize();
        final int itemCount = getShowSize(data) + getFootSize() + (canAutoLoadMore()?1:0);
        mData.addAll(data);

        if (mOpenAutoLoadMore){
            mLoadState = LOADING;
            mIsLoading = false;
        }
        mShowErrorView = false;
        notifyItemRangeChanged(startPos,itemCount);
    }

    public void removeData(IMultiItem data){
        if (data == null){
            return;
        }

        int index = findData(mData,data);
        if (index != -1){
            removeData(index+getHeadSize());
        }
    }

    private int findData(List<? extends IMultiItem> list, IMultiItem data) {

        int showSubSize = 0;
        for (int i = 0; i < list.size(); i++) {
            final IMultiItem item = list.get(i);
            if (item == data){
                return i + showSubSize;
            }
            if (item instanceof IExpandable && ((IExpandable) item).isExpandable()){
                int result = findData(((IExpandable) item).getSubItems(),data);
                if (result != -1){
                    return i + 1 + result + showSubSize;
                }
                showSubSize = getShowSize(((IExpandable) item).getSubItems());
            }
        }
        return -1;
    }

    // -1 表示没有这个data，-2表示移除了没有展开的data
    /*private int removeData(@NonNull List<IMultiItem> list,@NonNull IMultiItem data, boolean add, int offset){

        int size = 0;

        for (int i = 0; i < list.size(); i++) {
            final IMultiItem item = list.get(i);
            if (item == data){
                list.remove(data);
            }
        }
    }*/

    public void removeData(int adapterPosition){
        int index = adapterPosition - getHeadSize();
        if (index < 0 || index >= getDataSize()){
            return;
        }
        int removeSize = removeData(mData,index);
        if (removeSize == 0){
            Log.d("ExpandableAdapter", "有异常");
        }else {
            notifyItemRangeRemoved(adapterPosition,removeSize);
        }

    }

    private int removeData(List<? extends IMultiItem> list, int index){
        for (IMultiItem item : list) {
            if (index == 0){
                int removeSize = 1;
                if (item instanceof IExpandable && ((IExpandable) item).isExpandable()) {
                    final List<IMultiItem> itemList = ((IExpandable) item).getSubItems();
                    final int itemShowSize = getShowSize(itemList);
                    removeSize += itemShowSize;
                }
                list.remove(item);
                return removeSize;
            }
            index--;
            if (item instanceof IExpandable && ((IExpandable) item).isExpandable()){
                final List<IMultiItem> itemList = ((IExpandable) item).getSubItems();
                final int itemShowSize = getShowSize(itemList);
                if (index < itemShowSize){
                    //在这个子项里
                    return removeData(itemList, index);
                }
                index -= itemShowSize;
            }
        }
        return 0;
    }

    public void removeHead(int adapterPosition){
        if (adapterPosition < 0 || adapterPosition >= getHeadSize()){
            return;
        }

        for (int i = adapterPosition; i < mHeadLayouts.length-1; i++) {
            mHeadLayouts[i] = mHeadLayouts[i+1];
            if (mHeadLayouts[i+1] == 0){
                return;
            }
        }

        mHeadLayouts[mHeadLayouts.length-1] = 0;
        notifyItemRemoved(adapterPosition);
    }

    public void removeFoot(int adapterPosition){
        int index = adapterPosition - getHeadSize() - getDataSize();
        if (index < 0 || index >= getFootSize()){
            return;
        }

        for (int i = index; i < mFootLayouts.length-1; i++) {
            mFootLayouts[i] = mFootLayouts[i+1];
            if (mFootLayouts[i+1] == 0){
                return;
            }
        }

        mFootLayouts[mFootLayouts.length-1] = 0;
        notifyItemRemoved(adapterPosition);
    }

    public void removeAllHead(){
        int size = getHeadSize();
        for (int i = 0; i < mHeadLayouts.length; i++) {
            if(mHeadLayouts[i] == 0){
                break;
            }
            mHeadLayouts[i] = 0;
        }
        notifyItemRangeRemoved(0,size);
    }

    public void removeAllFoot(){
        int size = getFootSize();
        for (int i = 0; i < mFootLayouts.length; i++) {
            if (mFootLayouts[i] == 0){
                break;
            }
            mFootLayouts[i] = 0;
        }
        notifyItemRangeRemoved(getHeadSize()+ getDataSize(),size);
    }

    /**
     * 此position是AdapterPosition
     * @param adapterPosition 当前项的AdapterPosition
     * @return 如果当前项是数据项，则返回对应的数据，
     *         如果不是，则返回null,所以使用前必须检查返回值
     */
    @CheckResult
    @Nullable
    public IMultiItem getData(int adapterPosition){
        final int index = adapterPosition - getHeadSize();
        if (index >= 0 && index < getDataSize()){
            return getData(mData,index);
        }
        return null;
    }

    public List<IMultiItem> getData(){
        return mData;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener onItemLongClickListener){
        this.mOnItemLongClickListener = onItemLongClickListener;
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

    public void setCallback(DiffUtilCallback<IMultiItem> callback) {
        mCallback = callback;
    }

    public void setCallback(DiffUtilCallback<IMultiItem> callback, boolean detectMoves) {
        mCallback = callback;
        mDetectMoves = detectMoves;
    }

    public DiffUtilCallback<? extends IMultiItem> getCallback() {
        return mCallback;
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
        if (index < getDataSize()) {
            //数据布局
            //final IMultiItem data = mData.get(index);
            final IMultiItem data = getData(mData,index);
            data.convert(holder);
            return;
        }
        index = position - getHeadSize() - getDataSize();
        if (index < getFootSize()){
            //尾部布局
            convertFoot(holder,mFootLayouts[index],index);
            return;
        }
        if (canAutoLoadMore()){
            if (mLoadState == LOADING && !mIsLoading){
                mIsLoading = true;
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
        return getHeadSize() + getDataSize() + getFootSize()+loadMoreCount;
    }

    private int getDataSize() {
        return getShowSize(mData);
    }

    private int getShowSize(List<? extends IMultiItem> data) {
        if (data == null || data.isEmpty()){
            return 0;
        }
        int size = data.size();
        for (IMultiItem item : data) {
            //只添加展开的
            if (item instanceof IExpandable && ((IExpandable) item).isExpandable()){
                size += getShowSize(((IExpandable) item).getSubItems());
            }
        }
        return size;
    }

    private IMultiItem getData(List<? extends IMultiItem> list, int index){

        for (IMultiItem item : list) {
            if (index == 0){
                return item;
            }
            index--;
            if (item instanceof IExpandable && ((IExpandable) item).isExpandable()){
                final List<IMultiItem> itemList = ((IExpandable) item).getSubItems();
                final int itemShowSize = getShowSize(itemList);
                if (index < itemShowSize){
                    //在这个子项里
                    return getData(itemList, index);
                }
                index -= itemShowSize;
            }
        }

        throw new IndexOutOfBoundsException("index < 0 or index >= getDataSize()");
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
        if (index < getDataSize()){
            return getData(mData,index).getLayoutRes();
        }
        index = position - getHeadSize() - getDataSize();
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
        if (position >= getHeadSize()+ getDataSize()){
            //显示尾部及加载更多
            layoutParams.setFullSpan(true);
            return;
        }
        IMultiItem data = getData(holder.getAdapterPosition());
        if (data instanceof IFullSpan){
            layoutParams.setFullSpan(((IFullSpan) data).isFullSpan());
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
                if (position >= getHeadSize()+ getDataSize()){
                    //显示尾部及加载更多
                    return gridLayoutManager.getSpanCount();
                }

                final IMultiItem data = getData(position);
                if (data != null ){
                    int spanSize = data.getSpanSize();
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
     * @param holder 默认的ViewHolder
     */
    public void convertEmpty(BaseViewHolder holder){

    }

    /**
     * 如果设置了自定义Error布局，且想对其设置一些数据显示处理，可以重写此方法
     * @param holder 默认的ViewHolder
     */
    public void convertError(BaseViewHolder holder){

    }

    /**
     * 默认加载更多的点击事件实现
     * @param holder 默认的ViewHolder
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
     * @param holder 默认的ViewHolder
     */
    protected void bindEmpty(BaseViewHolder holder){}

    /**
     * 默认异常布局的点击事件实现
     * @param holder 默认的ViewHolder
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
        baseViewHolder.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull View view, int adapterPosition) {
                if (mOnItemLongClickListener != null){
                    return mOnItemLongClickListener.onItemLongClick(view, adapterPosition);
                }
                return false;
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
        mIsLoading = false;
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
        mIsLoading = false;
        if (canAutoLoadMore()) {
            notifyItemChanged(getItemCount()-1);
        }
    }

    @Override
    public void loadFailed() {
        mLoadState = LOAD_FAILED;
        mIsLoading = false;
        if (canAutoLoadMore()) {
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

    /**
     * 开启子view的点击事件，或者其他监听
     * @param holder 默认的ViewHolder
     * @param layoutRes 对应的布局Layout ID，也代表为ViewType
     */
    public abstract void bind(BaseViewHolder holder,int layoutRes);

    /**
     * 展开下级菜单
     * @param adapterPosition 想展开下级菜单的父菜单位置
     */
    public void expand(int adapterPosition){
        final IMultiItem data = getData(adapterPosition);
        if (!(data instanceof IExpandable)){
            return;
        }
        final IExpandable expandable = (IExpandable) data;
        expandable.setExpandable(true);

        int addShowSize = getShowSize(expandable.getSubItems());

        notifyItemChanged(adapterPosition);
        notifyItemRangeInserted(adapterPosition+1, addShowSize);
    }

    /**
     * 展开全部下级菜单
     * @param adapterPosition 想展开下级菜单的父菜单位置
     */
    public void expandAll(int adapterPosition){
        final IMultiItem data = getData(adapterPosition);
        if (!(data instanceof IExpandable)){
            return;
        }

        final IExpandable expandable = (IExpandable) data;

        int subShowSize = 0;
        if (expandable.isExpandable()){
            subShowSize = getShowSize(expandable.getSubItems());
        }
        expandable.setExpandable(true);

        expandAll(expandable.getSubItems());

        int newShowSize = getShowSize(expandable.getSubItems());

        notifyItemRangeChanged(adapterPosition,subShowSize+1);
        notifyItemRangeInserted(adapterPosition+1+subShowSize,newShowSize-subShowSize);
    }

    private void expandAll(List<IMultiItem> list){
        if (list == null || list.isEmpty()) return;
        for (IMultiItem item : list) {
            if (item instanceof IExpandable){
                ((IExpandable) item).setExpandable(true);
                expandAll(((IExpandable) item).getSubItems());
            }
        }
    }

    private void collapseAll(List<IMultiItem> list){
        if (list == null || list.isEmpty()) return;
        for (IMultiItem item : list) {
            if (item instanceof IExpandable){
                final IExpandable expandable = (IExpandable) item;
                if (expandable.isExpandable()) {
                    ((IExpandable) item).setExpandable(false);
                    collapseAll(((IExpandable) item).getSubItems());
                }
            }
        }
    }

    /**
     * 关闭下级菜单
     * @param adapterPosition 想关闭下级菜单的父菜单位置
     */
    public void collapse(int adapterPosition){
        final IMultiItem data = getData(adapterPosition);
        if (!(data instanceof IExpandable)){
            return;
        }
        final IExpandable expandable = (IExpandable) data;


        if (expandable.isExpandable()){
            expandable.setExpandable(false);
            int removeSize = getShowSize(expandable.getSubItems());
            notifyItemChanged(adapterPosition);
            if (removeSize != 0){
                notifyItemRangeRemoved(adapterPosition+1,removeSize);
            }
        }
    }

    /**
     * 关闭下级菜单树下的所有菜单
     * @param adapterPosition 想关闭下级菜单的父菜单位置
     */
    public void collapseAll(int adapterPosition){
        final IMultiItem data = getData(adapterPosition);
        if (!(data instanceof IExpandable)){
            return;
        }

        final IExpandable expandable = (IExpandable) data;

        if (expandable.isExpandable()){
            expandable.setExpandable(false);
            int removeSize = getShowSize(expandable.getSubItems());
            collapseAll(expandable.getSubItems());
            notifyItemChanged(adapterPosition);
            if (removeSize != 0){
                notifyItemRangeRemoved(adapterPosition+1,removeSize);
            }
        }
    }
}
