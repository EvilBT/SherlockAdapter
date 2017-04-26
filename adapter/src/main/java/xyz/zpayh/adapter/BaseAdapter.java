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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文 件 名: BaseAdapter
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/25 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder>
    implements LoadMore{

    protected final List<T> mData = new ArrayList<>();

    private int mHeadLayoutAndSpanStates[] = new int[0];

    private int mFootLayoutAndSpanStates[] = new int[0];

    /**
     * 是否在数据异常或者没有数据的时候依旧显示头部，默认为false
     */
    private boolean mAlwaysShowHead = false;

    /**
     * 是否在数据异常或者没有数据的时候依旧显示尾部，默认为false
     */
    private boolean mAlwaysShowFoot = false;

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

    private DiffUtilCallback<T> mCallback;

    private boolean mDetectMoves = true;

    /**
     * 设置新数据，会清除掉原有数据，并有可能重置加载更多状态
     * @param data 数据集合
     */
    public void setData(@Nullable List<? extends T> data){
        //mData.clear();
        //if (data != null){
        //    mData.addAll(data);
        //}

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

        final List<T> oldData = new ArrayList<>(mData);
        mData.clear();
        if (data != null){
            mData.addAll(data);
        }
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldData.size();
            }

            @Override
            public int getNewListSize() {
                return mData.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                final T oldItem = oldData.get(oldItemPosition);
                final T newItem = mData.get(newItemPosition);
                return mCallback.areItemsTheSame(oldItem,newItem);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                final T oldItem = oldData.get(oldItemPosition);
                final T newItem = mData.get(newItemPosition);
                return mCallback.areContentsTheSame(oldItem,newItem);
            }

            @Nullable
            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                final T oldItem = oldData.get(oldItemPosition);
                final T newItem = mData.get(newItemPosition);
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
    public void addData(T data){
        if (data == null){
            return;
        }
        final int startPos = mData.size() + getHeadSize();
        final int itemCount = 1 + getFootSize() + (canAutoLoadMore()?1:0);
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
    public void addData(List<? extends T> data){
        if (data == null){
            return;
        }
        final int startPos = mData.size() + getHeadSize();
        final int itemCount = data.size() + getFootSize() + (canAutoLoadMore()?1:0);
        mData.addAll(data);

        if (mOpenAutoLoadMore){
            mLoadState = LOADING;
            mIsLoading = false;
        }
        mShowErrorView = false;
        notifyItemRangeChanged(startPos,itemCount);
    }

    public void removeData(T data){
        if (data == null || !mData.contains(data)){
            return;
        }

        int index = mData.indexOf(data);
        mData.remove(data);
        notifyItemRemoved(getHeadSize()+index);
    }

    public void removeData(int adapterPosition){
        int index = adapterPosition - getHeadSize();
        if (index < 0 || index >= mData.size()){
            return;
        }
        mData.remove(index);
        notifyItemRemoved(adapterPosition);
    }

    public void removeHead(int adapterPosition){
        if (adapterPosition < 0 || adapterPosition >= getHeadSize()){
            return;
        }

        for (int i = adapterPosition; i < mHeadLayoutAndSpanStates.length/3-1; i++) {
            mHeadLayoutAndSpanStates[i*3] = mHeadLayoutAndSpanStates[(i+1)*3];
            mHeadLayoutAndSpanStates[i*3+1] = mHeadLayoutAndSpanStates[(i+1)*3+1];
            mHeadLayoutAndSpanStates[i*3+2] = mHeadLayoutAndSpanStates[(i+1)*3+2];
            if (mHeadLayoutAndSpanStates[(i+1)*3] == 0){
                return;
            }
        }

        mHeadLayoutAndSpanStates[mHeadLayoutAndSpanStates.length-1] = 0;
        mHeadLayoutAndSpanStates[mHeadLayoutAndSpanStates.length-2] = 0;
        mHeadLayoutAndSpanStates[mHeadLayoutAndSpanStates.length-3] = 0;
        notifyItemRemoved(adapterPosition);
    }

    public void removeFoot(int adapterPosition){
        int index = adapterPosition - getHeadSize() - mData.size();
        if (index < 0 || index >= getFootSize()){
            return;
        }

        for (int i = index; i < mFootLayoutAndSpanStates.length/3-1; i++) {
            mFootLayoutAndSpanStates[i*3] = mFootLayoutAndSpanStates[(i+1)*3];
            mFootLayoutAndSpanStates[i*3+1] = mFootLayoutAndSpanStates[(i+1)*3+1];
            mFootLayoutAndSpanStates[i*3+2] = mFootLayoutAndSpanStates[(i+1)*3+2];
            if (mFootLayoutAndSpanStates[(i+1)*3] == 0){
                return;
            }
        }

        mFootLayoutAndSpanStates[mFootLayoutAndSpanStates.length-1] = 0;
        mFootLayoutAndSpanStates[mFootLayoutAndSpanStates.length-2] = 0;
        mFootLayoutAndSpanStates[mFootLayoutAndSpanStates.length-3] = 0;
        notifyItemRemoved(adapterPosition);
    }

    public void removeAllHead(){
        int size = getHeadSize();
        for (int i = 0; i < mHeadLayoutAndSpanStates.length/3; i++) {
            if(mHeadLayoutAndSpanStates[i*3] == 0){
                break;
            }
            mHeadLayoutAndSpanStates[i*3] = 0;
            mHeadLayoutAndSpanStates[i*3+1] = 0;
            mHeadLayoutAndSpanStates[i*3+2] = 0;
        }
        notifyItemRangeRemoved(0,size);
    }

    public void removeAllFoot(){
        int size = getFootSize();
        for (int i = 0; i < mFootLayoutAndSpanStates.length/3; i++) {
            if (mFootLayoutAndSpanStates[i*3] == 0){
                break;
            }
            mFootLayoutAndSpanStates[i*3] = 0;
            mFootLayoutAndSpanStates[i*3+1] = 0;
            mFootLayoutAndSpanStates[i*3+2] = 0;
        }
        notifyItemRangeRemoved(getHeadSize()+mData.size(),size);
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

    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener onItemLongClickListener){
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 设置当显示Empty布局或者Error布局时是否也显示头部布局
     * @param alwaysShowHead
     * true 显示头部布局
     * false 不显示头部布局
     */
    public void setAlwaysShowHead(boolean alwaysShowHead) {
        if (mAlwaysShowHead == alwaysShowHead){
            return;
        }
        mAlwaysShowHead = alwaysShowHead;
        if (hasHead() && (mShowErrorView || mData.isEmpty())){
            if (mAlwaysShowHead) {
                notifyItemRangeInserted(0, getHeadSize());
            }else{
                notifyItemRangeRemoved(0, getHeadSize());
            }
        }
    }

    /**
     * 是否总是显示头部
     * @return 当显示Empty布局或者Error布局时是否也显示头部布局返回true,否则返回false
     */
    public boolean isAlwaysShowHead() {
        return mAlwaysShowHead;
    }

    /**
     * 设置当显示Empty布局或者Error布局时是否也显示尾部布局
     * @param alwaysShowFoot
     * true 显示尾部布局
     * false 不显示尾部布局
     */
    public void setAlwaysShowFoot(boolean alwaysShowFoot) {
        if (mAlwaysShowFoot == alwaysShowFoot){
            return;
        }
        mAlwaysShowFoot = alwaysShowFoot;
        if (hasFoot()  && (mShowErrorView || mData.isEmpty()) ){
            // 拿到新的显示长度
            int itemCount = getItemCount();
            if (mAlwaysShowFoot){
                notifyItemRangeInserted(itemCount - getFootSize(),getFootSize());
            }else{
                notifyItemRangeRemoved(itemCount,getFootSize());
            }
        }
    }

    /**
     * 是否总是显示尾部
     * @return 当显示Empty布局或者Error布局时是否也显示尾部布局返回true,否则返回false
     */
    public boolean isAlwaysShowFoot() {
        return mAlwaysShowFoot;
    }

    /**
     * 按调用顺序添加头部布局
     * @param headLayout 布局id
     */
    public void addHeadLayout(@LayoutRes int headLayout) {
        addHeadLayout(headLayout,true);
    }

    /**
     * 按调用顺序添加头部布局
     * @param headLayout 布局id
     * @param fullSpan 是否横跨布局,在{@link GridLayoutManager}布局的时候只有
     *                 Layout的spanSize 小于或等于 0的时候生效
     *                 true 为横跨布局，false为1
     */
    public void addHeadLayout(@LayoutRes int headLayout, boolean fullSpan) {
        addHeadLayout(headLayout, fullSpan,0);
    }

    /**
     * 按调用顺序添加头部布局
     * @param headLayout 布局id
     * @param fullSpan 是否横跨布局,在{@link GridLayoutManager}布局的时候只有
     *                 Layout的spanSize 小于或等于 0的时候生效
     *                 true 为横跨布局，false为1
     * @param spanSize 在{@link GridLayoutManager}布局的时候生效，如果小于或等于0，则根据fullSpan的值设置
     *                 spanSize的大小.
     *                 true 为横跨布局，false为1
     */
    public void addHeadLayout(@LayoutRes int headLayout, boolean fullSpan, int spanSize) {

        int indexToAdd = -1;

        for (int i = 0; i < mHeadLayoutAndSpanStates.length; i+=3) {
            if (mHeadLayoutAndSpanStates[i] == 0){
                indexToAdd = i;
                break;
            }
        }

        if (indexToAdd == -1){
            indexToAdd = mHeadLayoutAndSpanStates.length;
            mHeadLayoutAndSpanStates = Arrays.copyOf(mHeadLayoutAndSpanStates,
                    indexToAdd < 3 ? 3 : indexToAdd * 3);
            for (int i = indexToAdd; i < mHeadLayoutAndSpanStates.length; i++) {
                mHeadLayoutAndSpanStates[i] = 0;
            }
        }

        mHeadLayoutAndSpanStates[indexToAdd] = headLayout;
        mHeadLayoutAndSpanStates[indexToAdd+1] = fullSpan ? 1 : 0;
        mHeadLayoutAndSpanStates[indexToAdd+2] = spanSize;
    }

    public boolean hasHead(){
        return getHeadSize() > 0;
    }

    public int getHeadSize(){
        for (int i = 0; i < mHeadLayoutAndSpanStates.length/3; i++) {
            if (mHeadLayoutAndSpanStates[i*3] == 0){
                return i;
            }
        }
        return mHeadLayoutAndSpanStates.length/3;
    }

    /**
     * 按调用顺序添加尾部布局
     * @param footLayout 布局id
     */
    public void addFootLayout(@LayoutRes int footLayout){
        addFootLayout(footLayout,true);
    }

    /**
     * 按调用顺序添加尾部布局
     * @param footLayout 布局id
     * @param fullSpan 是否横跨布局,在{@link GridLayoutManager}布局的时候只有
     *                 Layout的spanSize 小于或等于 0的时候生效
     *                 true 为横跨布局，false为1
     */
    public void addFootLayout(@LayoutRes int footLayout,boolean fullSpan){
        addFootLayout(footLayout, fullSpan,0);
    }

    /**
     * 按调用顺序添加尾部布局
     * @param footLayout 布局id
     * @param fullSpan 是否横跨布局,在{@link GridLayoutManager}布局的时候只有
     *                 Layout的spanSize 小于或等于 0的时候生效
     *                 true 为横跨布局，false为1
     * @param spanSize 在{@link GridLayoutManager}布局的时候生效，如果小于或等于0，则根据fullSpan的值设置
     *                 spanSize的大小.
     *                 true 为横跨布局，false为1
     */
    public void addFootLayout(@LayoutRes int footLayout,boolean fullSpan, int spanSize){
        int indexToAdd = -1;

        for (int i = 0; i < mFootLayoutAndSpanStates.length; i+=3) {
            if (mFootLayoutAndSpanStates[i] == 0){
                indexToAdd = i;
                break;
            }
        }

        if (indexToAdd == -1){
            indexToAdd = mFootLayoutAndSpanStates.length;
            mFootLayoutAndSpanStates = Arrays.copyOf(mFootLayoutAndSpanStates,
                    indexToAdd < 3 ? 3 : indexToAdd*3);
            for (int i = indexToAdd; i < mFootLayoutAndSpanStates.length; i++) {
                mFootLayoutAndSpanStates[i] = 0;
            }
        }

        mFootLayoutAndSpanStates[indexToAdd] = footLayout;
        mFootLayoutAndSpanStates[indexToAdd+1] = fullSpan ? 1 : 0;
        mFootLayoutAndSpanStates[indexToAdd+2] = spanSize;
    }

    public boolean hasFoot(){
        return getHeadSize() > 0;
    }

    public int getFootSize(){
        for (int i = 0; i < mFootLayoutAndSpanStates.length/3; i++) {
            if (mFootLayoutAndSpanStates[i*3] == 0){
                return i;
            }
        }

        return mFootLayoutAndSpanStates.length/3;
    }

    public void setCallback(DiffUtilCallback<T> callback) {
        mCallback = callback;
        mDetectMoves = true;
    }

    public void setCallback(DiffUtilCallback<T> callback, boolean detectMoves){
        mCallback = callback;
        mDetectMoves = detectMoves;
    }

    public DiffUtilCallback<T> getCallback() {
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

        if (mShowErrorView){
            int index = position;
            if (mAlwaysShowHead){
                if (index < getHeadSize()){
                    //头部布局
                    convertHead(holder, mHeadLayoutAndSpanStates[index*3],index);
                    return;
                }
                index = position - getHeadSize();
            }
            if (index == 0) {
                convertError(holder);
                return;
            }
            index = index - 1;
            if (index < getFootSize()){
                //尾部布局
                convertFoot(holder, mFootLayoutAndSpanStates[index*3],index);
                return;
            }

            convertError(holder);
            return;
        }
        //没有数据时显示空数据布局
        if (mData.isEmpty()){
            int index = position;
            if (mAlwaysShowHead){
                if (index < getHeadSize()){
                    //头部布局
                    convertHead(holder, mHeadLayoutAndSpanStates[index*3],index);
                    return;
                }
                index = position - getHeadSize();
            }
            if (index == 0) {
                convertEmpty(holder);
                return;
            }
            index = index - 1;
            if (index < getFootSize()){
                //尾部布局
                convertFoot(holder, mFootLayoutAndSpanStates[index*3],index);
                return;
            }

            convertEmpty(holder);
            return;
        }

        int index = position;
        if (index < getHeadSize()){
            //头部布局
            convertHead(holder, mHeadLayoutAndSpanStates[index*3],index);
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
            convertFoot(holder, mFootLayoutAndSpanStates[index*3],index);
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
        int itemCount = 0;
        if (mShowErrorView){
            //显示加载错误时根据设置显示头部或者尾部
            itemCount = 1;
            if (mAlwaysShowHead){
                itemCount += getHeadSize();
            }
            if (mAlwaysShowFoot){
                itemCount += getFootSize();
            }
            return itemCount;
        }
        if (mData.isEmpty()){
            //没有数据时只显示空布局或者根据设置显示头部和尾部
            itemCount = 1;
            if (mAlwaysShowHead){
                itemCount += getHeadSize();
            }
            if (mAlwaysShowFoot){
                itemCount += getFootSize();
            }
            return itemCount;
        }
        final int loadMoreCount = canAutoLoadMore()?1:0;
        return getHeadSize() + mData.size() + getFootSize()+loadMoreCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowErrorView){

            int index = position;
            if (mAlwaysShowHead){
                if (index < getHeadSize()){
                    return mHeadLayoutAndSpanStates[index*3];
                }
                index = position - getHeadSize();
            }

            if (index == 0){
                return mErrorLayout;
            }

            index = index -1;
            if (mAlwaysShowFoot){
                if (index < getFootSize()){
                    return mFootLayoutAndSpanStates[index*3];
                }
            }

            return mErrorLayout;
        }

        if (mData.isEmpty()){

            int index = position;
            if (mAlwaysShowHead){
                if (index < getHeadSize()){
                    return mHeadLayoutAndSpanStates[index*3];
                }
                index = position - getHeadSize();
            }

            if (index == 0){
                return mEmptyLayout;
            }

            index = index -1;
            if (mAlwaysShowFoot){
                if (index < getFootSize()){
                    return mFootLayoutAndSpanStates[index*3];
                }
            }

            return mEmptyLayout;
        }

        int index = position;
        if (index < getHeadSize()){
            return mHeadLayoutAndSpanStates[index*3];
        }
        index = position - getHeadSize();
        if (index < mData.size()){
            return getLayoutRes(index);
        }
        index = position - getHeadSize() - mData.size();
        if (index < getFootSize()) {
            return mFootLayoutAndSpanStates[index*3];
        }

        return mLoadMoreLayout;
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp == null || !(lp instanceof StaggeredGridLayoutManager.LayoutParams)) return;
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) lp;

        final int position = holder.getAdapterPosition();
        if (mShowErrorView){
            if (mAlwaysShowHead && position < getHeadSize()){
                //显示头部
                layoutParams.setFullSpan(mHeadLayoutAndSpanStates[position*3+1] == 1);
                return;
            }
            if (mAlwaysShowFoot) {
                int index = position - (mAlwaysShowHead ? (getHeadSize() + 1) : 1);
                if (index >= 0 && index < getFootSize()) {
                    //显示尾部
                    layoutParams.setFullSpan(mFootLayoutAndSpanStates[index * 3 + 1] == 1);
                    return;
                }
            }
            //当数据异常时，Error界面为FullSpan
            layoutParams.setFullSpan(true);
            return;
        }
        if (mData.isEmpty()){
            if (mAlwaysShowHead && position < getHeadSize()){
                //显示头部
                layoutParams.setFullSpan(mHeadLayoutAndSpanStates[position*3+1] == 1);
                return;
            }
            if (mAlwaysShowFoot) {
                int index = position - (mAlwaysShowHead ? (getHeadSize() + 1) : 1);
                if (index >= 0 && index < getFootSize()) {
                    //显示尾部
                    layoutParams.setFullSpan(mFootLayoutAndSpanStates[index * 3 + 1] == 1);
                    return;
                }
            }
            //当数据为空时，Empty界面为FullSpan
            layoutParams.setFullSpan(true);
            return;
        }
        if (position < getHeadSize()){
            //显示头部
            layoutParams.setFullSpan(mHeadLayoutAndSpanStates[position*3+1] == 1);
            return;
        }
        int index = position - (getHeadSize() + mData.size());
        if (index >= 0 && index < getFootSize()){
            //显示尾部
            layoutParams.setFullSpan(mFootLayoutAndSpanStates[index*3+1] == 1);
            return;
        }
        if (index >= 0){
            //显示加载更多
            layoutParams.setFullSpan(true);
            return;
        }
        T data = getData(holder.getAdapterPosition());
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
                if (mShowErrorView){
                    if (mAlwaysShowHead && position < getHeadSize()){
                        //显示头部
                        if (mHeadLayoutAndSpanStates[position*3+2]>0){
                            int spanCount = gridLayoutManager.getSpanCount();
                            return mHeadLayoutAndSpanStates[position*3+2] > spanCount ? spanCount : mHeadLayoutAndSpanStates[position*3+2];
                        }
                        if (mHeadLayoutAndSpanStates[position*3+1] == 0){
                            return 1;
                        }
                        return gridLayoutManager.getSpanCount();
                    }
                    if (mAlwaysShowFoot) {
                        int index = position - (mAlwaysShowHead ? (getHeadSize() + 1) : 1);
                        if (index >= 0 && index < getFootSize()) {
                            //显示尾部
                            if (mFootLayoutAndSpanStates[index*3+2] > 0) {
                                int spanCount = gridLayoutManager.getSpanCount();
                                return mFootLayoutAndSpanStates[index*3+2] > spanCount ? spanCount : mFootLayoutAndSpanStates[index*3+2];
                            }
                            if (mFootLayoutAndSpanStates[index*3+1] == 0){
                                return 1;
                            }
                            return gridLayoutManager.getSpanCount();
                        }
                    }
                    //当数据异常时，Error界面都FullSpan
                    return gridLayoutManager.getSpanCount();
                }
                if (mData.isEmpty()){
                    if (mAlwaysShowHead && position < getHeadSize()){
                        //显示头部
                        if (mHeadLayoutAndSpanStates[position*3+2]>0){
                            int spanCount = gridLayoutManager.getSpanCount();
                            return mHeadLayoutAndSpanStates[position*3+2] > spanCount ? spanCount : mHeadLayoutAndSpanStates[position*3+2];
                        }
                        if (mHeadLayoutAndSpanStates[position*3+1] == 0){
                            return 1;
                        }
                        return gridLayoutManager.getSpanCount();
                    }
                    if (mAlwaysShowFoot) {
                        int index = position - (mAlwaysShowHead ? (getHeadSize() + 1) : 1);
                        if (index >= 0 && index < getFootSize()) {
                            //显示尾部
                            if (mFootLayoutAndSpanStates[index*3+2] > 0) {
                                int spanCount = gridLayoutManager.getSpanCount();
                                return mFootLayoutAndSpanStates[index*3+2] > spanCount ? spanCount : mFootLayoutAndSpanStates[index*3+2];
                            }
                            if (mFootLayoutAndSpanStates[index*3+1] == 0){
                                return 1;
                            }
                            return gridLayoutManager.getSpanCount();
                        }
                    }
                    //当数据为空时，Empty界面都FullSpan
                    return gridLayoutManager.getSpanCount();
                }
                if (position < getHeadSize()){
                    //显示头部
                    if (mHeadLayoutAndSpanStates[position*3+2] > 0) {
                        int spanCount = gridLayoutManager.getSpanCount();
                        return mHeadLayoutAndSpanStates[position*3+2] > spanCount ? spanCount : mHeadLayoutAndSpanStates[position*3+2];
                    }
                    if (mHeadLayoutAndSpanStates[position*3+1] == 0){
                        return 1;
                    }
                    return gridLayoutManager.getSpanCount();
                }

                int index = position - (getHeadSize() + mData.size());
                if (index >= 0 && index < getFootSize()){
                    //显示尾部
                    if (mFootLayoutAndSpanStates[index*3+2] > 0) {
                        int spanCount = gridLayoutManager.getSpanCount();
                        return mFootLayoutAndSpanStates[index*3+2] > spanCount ? spanCount : mFootLayoutAndSpanStates[index*3+2];
                    }
                    if (mFootLayoutAndSpanStates[index*3+1] == 0){
                        return 1;
                    }
                    return gridLayoutManager.getSpanCount();
                }
                if (index >= 0 ){
                    //显示加载更多
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
    public void convertHead(BaseViewHolder holder,int headLayout, int index){

    }

    /**
     * 如果要对尾布局进行处理可重写此方法
     * @param holder 布局holder
     * @param footLayout 布局id，跟index可以确定布局是第几个布局
     * @param index 添加时的顺序
     */
    public void convertFoot(BaseViewHolder holder,int footLayout, int index){

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


    protected void bindData(BaseViewHolder baseViewHolder, int layoutRes) {
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
     * 返回布局layout
     * @param index 列表位置
     * @return
     *  布局Layout ID
     */
    @LayoutRes
    public abstract int getLayoutRes(int index);

    /**
     * 在这里设置显示
     * @param holder 默认的ViewHolder
     * @param data 对应的数据
     * @param index 对应的列表位置（不一定是数据在数据集合List中的位置）
     */
    public abstract void convert(BaseViewHolder holder, T data, int index);

    /**
     * 开启子view的点击事件，或者其他监听
     * @param holder 默认的ViewHolder
     * @param layoutRes 对应的布局Layout ID，也代表为ViewType
     */
    public abstract void bind(BaseViewHolder holder,int layoutRes);
}
