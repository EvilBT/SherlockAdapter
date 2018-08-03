package cn.sherlockzp.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.sherlockzp.adapter.BaseAdapter.Companion.sDefaultViewCreator
import java.util.*

open class BaseExpandableAdapter : RecyclerView.Adapter<BaseViewHolder>(){

    val data = ArrayList<IMultiItem>()

    private val headLayoutConfigs = ArrayList<LayoutConfig>()
    val headSize get() = headLayoutConfigs.size
    val hasHead get() = headLayoutConfigs.isNotEmpty()

    private val footLayoutConfigs = ArrayList<LayoutConfig>()
    val footSize get() = footLayoutConfigs.size
    val hasFoot get() = footLayoutConfigs.isNotEmpty()

    var alwaysShowHead = false
        set(value) {
            if(field == value) return
            field = value
            if (hasHead && (isShowError || data.isEmpty())) {
                if (value) {
                    doNotifyItemRangeInserted(0, headSize)
                } else {
                    doNotifyItemRangeRemoved(0, headSize)
                }
            }
        }
    var alwaysShowFoot = false
        set(value) {
            if (field == value) return
            field = value
            if (hasFoot && (isShowError || data.isEmpty())) {
                if (value) {
                    doNotifyItemRangeInserted(itemCount - footSize, footSize)
                } else {
                    doNotifyItemRangeRemoved(itemCount, footSize)
                }
            }
        }

    private var showErrorView = false

    val isShowError get() = showErrorView

    @LayoutRes
    var emptyLayout = sDefaultViewCreator.getEmptyViewLayout()
    @LayoutRes
    var errorLayout = sDefaultViewCreator.getErrorViewLayout()
    @LayoutRes
    var loadMoreLayout = sDefaultViewCreator.getLoadMoreViewLayout()

    var onLoadMoreListener: OnLoadMoreListener? = null
    private var openAutoLoadMore = false
    var loadState = LoadState.LOADING
    private var isLoading = false

    var onItemClickListener: OnItemClickListener? = null
    var onItemLongClickListener: OnItemLongClickListener? = null

    fun setOnLoadMoreListener(listener: () -> Unit) {
        onLoadMoreListener = object : OnLoadMoreListener {
            override fun onLoadMore() {
                listener()
            }
        }
    }

    fun setOnItemClickListener(listener: (view: View, adapterPosition: Int) -> Unit) {
        onItemClickListener = object : OnItemClickListener{
            override fun onItemClick(view: View, adapterPosition: Int) {
                listener(view, adapterPosition)
            }
        }
    }

    fun setOnItemLongClickListener(listener: (view: View, adapterPosition: Int) -> Boolean) {
        onItemLongClickListener = object : OnItemLongClickListener {
            override fun onItemLongClick(view: View, adapterPosition: Int): Boolean {
                return listener(view, adapterPosition)
            }
        }
    }

    private var recyclerView: RecyclerView? = null

    private var helper: AsyncListDiffer? = null

    fun setItemDiffCallback(diffCallback: DiffUtil.ItemCallback<IMultiItem>?) {
        diffCallback?.let {
            helper = AsyncListDiffer(it)
        }?:let {
            helper = null
        }
    }

    fun setData(data: List<IMultiItem>?) {

        if (openAutoLoadMore) {
            loadState = LoadState.LOADING
            isLoading = false
        }
        showErrorView = false

        helper?.submitList(data)
                ?:let {
                    this.data.clear()
                    data?.let {
                        this.data.addAll(it)
                    }
                    doNotifyDataSetChanged()
                }
    }

    fun addData(data: IMultiItem) {

        val startPos = getShowDataSize() + headSize
        var itemCount = 1 + footSize + if (canAutoLoadMore()) 1 else 0
        if ( data is IExpandable && data.expandable) {
            itemCount += getShowDataSize(data.getSubItems())
        }

        this.data.add(data)

        if (openAutoLoadMore) {
            loadState = LoadState.LOADING
            isLoading = false
        }
        showErrorView = false

        doNotifyItemRangeChanged(startPos, itemCount)
    }

    fun addData(data: List<IMultiItem>) {
        val startPos = getShowDataSize() + headSize
        val itemCount = getShowDataSize(data) + footSize + if (canAutoLoadMore()) 1 else 0

        this.data.addAll(data)

        if (openAutoLoadMore) {
            loadState = LoadState.LOADING
            isLoading = false
        }
        showErrorView = false

        doNotifyItemRangeChanged(startPos, itemCount)
    }

    fun removeData(data: IMultiItem) {
        val index = findData(this.data, data)
        if (index != -1) {
            removeDataAt(index+headSize)
        }
    }

    fun findData(list: List<IMultiItem>?, data: IMultiItem): Int {
        if (list == null) {
            return -1
        }
        var showSubSize = 0
        for (i in list.indices) {
            val item = list[i]
            if (item === data) {
                return i + showSubSize
            }
            if (item is IExpandable && item.expandable) {
                val result = findData(item.getSubItems(), data)
                if (result != -1) {
                    return i + 1 + result + showSubSize
                }
                showSubSize += getShowDataSize(item.getSubItems())
            }
        }

        return -1
    }

    fun removeDataAt(adapterPosition: Int) {
        val index = adapterPosition - headSize
        if (index in 0 until getShowDataSize()) {
            val removeSize = removeData(data, index)
            if (removeSize == 0) {
                Log.d("BaseExpandableAdapter","有异常")
            } else {
                doNotifyItemRangeRemoved(adapterPosition, removeSize)
            }
        }
    }

    private fun removeData(list: MutableList<IMultiItem>?, index: Int): Int {
        if (list == null) {
            return 0
        }
        var pos = index
        for (item in list) {
            if (pos == 0) {
                var removeSize = 1
                if (item is IExpandable && item.expandable) {
                    val itemList = item.getSubItems()
                    val itemShowSize = getShowDataSize(itemList)
                    removeSize += itemShowSize
                }
                list.remove(item)
                return removeSize
            }
            pos--
            if (item is IExpandable && item.expandable) {
                val itemList = item.getSubItems()
                val itemShowSize = getShowDataSize(itemList)
                if (pos < itemShowSize) {
                    // 在子列表里
                    return removeData(itemList, pos)
                }
                pos -= itemShowSize
            }
        }
        return 0
    }

    fun removeHead(adapterPosition: Int) {
        if (adapterPosition in 0 until headSize) {
            headLayoutConfigs.removeAt(adapterPosition)
            if (isShowError || data.isEmpty()) {
                if (alwaysShowHead) {
                    doNotifyItemRemoved(adapterPosition)
                }
                return
            }
            doNotifyItemRemoved(adapterPosition)
        }
    }

    fun removeAllHead() {
        if (hasHead) {
            val size = headSize
            headLayoutConfigs.clear()
            if (isShowError || data.isEmpty()) {
                if (alwaysShowHead) {
                    doNotifyItemRangeRemoved(0, size)
                }
                return
            }
            doNotifyItemRangeRemoved(0, size)
        }
    }

    fun removeAllFoot() {
        if (hasFoot) {
            val size = footSize
            footLayoutConfigs.clear()
            if (isShowError || data.isEmpty()) {
                if (alwaysShowFoot) {
                    doNotifyItemRangeRemoved(if(alwaysShowHead) (headSize + 1) else 1, size)
                }
                return
            }
            doNotifyItemRangeRemoved(headSize+getShowDataSize(), size)
        }
    }

    fun removeFoot(adapterPosition: Int) {
        if (isShowError || data.isEmpty()) {
            val index = adapterPosition - if(alwaysShowHead) (headSize+1) else 1
            footLayoutConfigs.removeAt(index)
            if (alwaysShowFoot) {
                doNotifyItemRemoved(adapterPosition)
            }
            return
        }
        val index = adapterPosition - headSize - getShowDataSize()
        if (index in 0 until footSize) {
            footLayoutConfigs.removeAt(index)
            doNotifyItemRemoved(adapterPosition)
        }
    }

    fun getData(adapterPosition: Int): IMultiItem? {
        val index = adapterPosition - headSize
        if (index in 0 until getShowDataSize()) {
            return getData(data, index)
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, layoutRes: Int): BaseViewHolder {
        val db = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context),
                layoutRes, parent, false)
        val holder = if ( db == null )
            BaseViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))
        else
            BaseViewHolder(db)
        holder.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(view: View, adapterPosition: Int) {
                onItemClickListener?.onItemClick(view, adapterPosition)
            }
        })
        holder.setOnItemLongClickListener(object : OnItemLongClickListener{
            override fun onItemLongClick(view: View, adapterPosition: Int): Boolean {
                return onItemLongClickListener?.onItemLongClick(view, adapterPosition)
                        ?: false
            }
        })

        // 实现默认的加载更多点击重新加载事件
        if (layoutRes == loadMoreLayout) {
            holder.find<View>(R.id.ll_default_load_more)?.setOnClickListener {
                if (loadState == LoadState.LOAD_FAILED) {
                    loadState = LoadState.LOADING
                    doNotifyItemChanged(itemCount-1)
                }
            }
        }

        bind(holder, layoutRes)
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        if (showErrorView || data.isEmpty()) {
            var index = position
            if (alwaysShowHead) {
                if (index < headSize) {
                    return headLayoutConfigs[index].layout
                }
                index = position - headSize
            }

            if (index == 0) {
                return if (showErrorView) errorLayout else emptyLayout
            }

            index--
            if (alwaysShowFoot) {
                if (index < footSize) {
                    return footLayoutConfigs[index].layout
                }
            }

            return if (showErrorView) errorLayout else emptyLayout
        }
        var index = position

        if (index < headSize) {
            return headLayoutConfigs[index].layout
        }

        index = position - headSize
        if (index < getShowDataSize()) {
            return getData(data, index).getLayoutRes()
        }

        index = position - headSize - getShowDataSize()
        if (index < footSize) {
            return footLayoutConfigs[index].layout
        }
        return loadMoreLayout
    }

    override fun getItemCount(): Int {
        if (showErrorView || data.isEmpty()) {
            return 1 + if(alwaysShowHead) headSize else 0 +
                    if (alwaysShowFoot) footSize else 0
        }

        return headSize + getShowDataSize() + footSize +
                if (canAutoLoadMore()) 1 else 0
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (showErrorView || data.isEmpty()) {
            var index = position
            if (alwaysShowHead) {
                if (index < headSize) {
                    convertHead(holder, headLayoutConfigs[index])
                    return
                }
                index = position - headSize
            }
            if (index == 0) {
                if (showErrorView) {
                    convertError(holder)
                } else {
                    convertEmpty(holder)
                }
                return
            }

            index--

            if (alwaysShowFoot) {
                if (index < footSize) {
                    convertFoot(holder, footLayoutConfigs[index])
                    return
                }
            }

            if (showErrorView) {
                convertError(holder)
            } else {
                convertEmpty(holder)
            }
            return
        }

        var index = position

        if (index < headSize) {
            convertHead(holder, headLayoutConfigs[index])
            return
        }

        index = position - headSize

        if (index < getShowDataSize()) {
            val item = getData(data, index)
            item.convert(holder)
            return
        }

        index = position - headSize - getShowDataSize()

        if (index < footSize) {
            convertFoot(holder, footLayoutConfigs[index])
            return
        }

        if (canAutoLoadMore()) {
            if (loadState == LoadState.LOADING && !isLoading) {
                isLoading = true
                onLoadMoreListener?.onLoadMore()
            }
            convertLoadMore(holder, loadState)
        }
    }

    open fun convertEmpty(holder: BaseViewHolder) {

    }

    open fun convertError(holder: BaseViewHolder) {

    }

    open fun convertHead(holder: BaseViewHolder, headLayout: LayoutConfig) {

    }

    open fun convertFoot(holder: BaseViewHolder, footLayout: LayoutConfig) {

    }

    open fun convertLoadMore(holder: BaseViewHolder, loadState: LoadState) {
        when(loadState) {
            LoadState.LOADING -> convertLoading(holder)
            LoadState.LOAD_FAILED -> convertLoadFailed(holder)
            LoadState.LOAD_COMPLETED -> convertLoadCompleted(holder)
        }
    }

    open fun convertLoading(holder: BaseViewHolder) {
        holder.setVisibility(R.id.progressBar)
                .setText(R.id.load_tips, R.string.loading)
    }

    open fun convertLoadFailed(holder: BaseViewHolder) {
        holder.setVisibility(R.id.progressBar, View.GONE)
                .setText(R.id.load_tips, R.string.load_failed)
    }

    open fun convertLoadCompleted(holder: BaseViewHolder) {
        holder.setVisibility(R.id.progressBar, View.GONE)
                .setText(R.id.load_tips, R.string.no_more)
    }

    open fun bind(holder: BaseViewHolder, layoutRes: Int) {

    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)

        val lp = holder.itemView.layoutParams
        if (lp == null || lp !is StaggeredGridLayoutManager.LayoutParams) return

        val position = holder.adapterPosition
        if (isShowError || data.isEmpty()) {
            if (alwaysShowHead && position < headSize) {
                lp.isFullSpan = headLayoutConfigs[position].fullSpan
                return
            }
            if (alwaysShowFoot) {
                val index = position -
                        (if (alwaysShowHead) (headSize + 1) else 1)
                if (index in 0 until footSize) {
                    lp.isFullSpan = footLayoutConfigs[index].fullSpan
                    return
                }
            }
            lp.isFullSpan = true
            return
        }

        if (position < headSize) {
            lp.isFullSpan = headLayoutConfigs[position].fullSpan
            return
        }

        val index = position - (headSize + getShowDataSize())
        if (index in 0 until footSize) {
            lp.isFullSpan = footLayoutConfigs[index].fullSpan
            return
        }
        if (index > 0) {
            lp.isFullSpan = true
            return
        }

        val data = getData(holder.adapterPosition)
        if (data != null) {
            lp.isFullSpan = data.isFullSpan()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        val manager = recyclerView.layoutManager
        if (manager == null || manager !is GridLayoutManager) return

        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (isShowError || data.isEmpty()) {
                    if (alwaysShowHead && position < headSize) {
                        // 显示头部
                        val config = headLayoutConfigs[position]
                        return getSpanCountFromConfig(config, manager)
                    }
                    if (alwaysShowFoot) {
                        val index = position - (if (alwaysShowHead) (headSize+1) else 1)
                        if (index in 0 until footSize) {
                            // 显示尾部
                            val config = footLayoutConfigs[index]
                            return getSpanCountFromConfig(config, manager)
                        }
                    }
                    //当数据异常时，Error界面都FullSpan
                    return manager.spanCount
                }

                if (position < headSize) {
                    // 显示头部
                    val config = headLayoutConfigs[position]
                    return getSpanCountFromConfig(config, manager)
                }

                val index = position - (headSize + getShowDataSize())
                if (index in 0 until footSize) {
                    // 显示尾部
                    val config = footLayoutConfigs[index]
                    return getSpanCountFromConfig(config, manager)
                }

                if (index >= 0) {
                    // 显示加载更多
                    return manager.spanCount
                }

                val data = getData(position)
                if (data != null ) {
                    return when(data.getSpanSize()){
                        in 1 until manager.spanCount -> data.getSpanSize()
                        else -> {
                            if (data.getSpanSize() <= 0)
                                1
                            else
                                manager.spanCount
                        }
                    }
                }

                return 1
            }
        }
    }

    /**
     * 在布局为GridLayoutManager时才有用处，
     * 返回当前布局所占用的SpanSize
     * @return 如果返回的SpanSize 小于或等于 0 或者 大于 GridLayoutManager#getSpanCount()
     *  则{@link BaseAdapter} 会在BaseAdapter#onAttachedToRecyclerView(RecyclerView)
     *  自适应为1或者GridLayoutManager#getSpanCount(),详情参考BaseAdapter#onAttachedToRecyclerView(RecyclerView)
     *  默认为1
     */
    private fun getSpanCountFromConfig(config: LayoutConfig, manager: GridLayoutManager): Int {
        return when(config.spanSize) {
            in 1 until manager.spanCount -> config.spanSize
            else -> {
                if (config.spanSize <= 0)
                    1
                else
                    manager.spanCount
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    fun doNotifyDataSetChanged() {
        recyclerView?.let {
            if (it.isComputingLayout) {
                it.post { notifyDataSetChanged() }
                return
            }
        }
        notifyDataSetChanged()
    }

    fun doNotifyItemChanged(position: Int, payload: Objects? = null) {
        recyclerView?.let {
            if (it.isComputingLayout) {
                it.post { notifyItemChanged(position, payload) }
                return
            }
        }
        notifyItemChanged(position, payload)
    }

    fun doNotifyItemRangeChanged(positionStart: Int, itemCount: Int, payload: Objects? = null) {
        recyclerView?.let {
            if (it.isComputingLayout) {
                it.post { notifyItemRangeChanged(positionStart, itemCount, payload) }
                return
            }
        }
        notifyItemRangeChanged(positionStart, itemCount, payload)
    }

    fun doNotifyItemInserted(position: Int) {
        recyclerView?.let {
            if (it.isComputingLayout) {
                it.post { notifyItemInserted(position) }
                return
            }
        }
        notifyItemInserted(position)
    }

    fun doNotifyItemMoved(fromPosition: Int, toPostion: Int) {
        recyclerView?.let {
            if (it.isComputingLayout) {
                it.post { notifyItemMoved(fromPosition, toPostion) }
                return
            }
        }
        notifyItemMoved(fromPosition, toPostion)
    }

    fun doNotifyItemRangeInserted(positionStart: Int, itemCount: Int) {
        recyclerView?.let {
            if (it.isComputingLayout) {
                it.post { notifyItemRangeInserted(positionStart, itemCount) }
                return
            }
        }
        notifyItemRangeInserted(positionStart, itemCount)
    }

    fun doNotifyItemRemoved(position: Int) {
        recyclerView?.let {
            if (it.isComputingLayout) {
                it.post { notifyItemRemoved(position) }
                return
            }
        }
        notifyItemRemoved(position)
    }

    fun doNotifyItemRangeRemoved(positionStart: Int, itemCount: Int) {
        recyclerView?.let {
            if (it.isComputingLayout) {
                it.post { notifyItemRangeRemoved(positionStart, itemCount) }
                return
            }
        }
        notifyItemRangeRemoved(positionStart, itemCount)
    }

    fun showErrorView() {
        showErrorView = true
        doNotifyDataSetChanged()
    }

    fun addHeadLayout(@LayoutRes headLayout: Int, fullSpan: Boolean = true, spanSize: Int = 0) {
        headLayoutConfigs.add(LayoutConfig(headLayout,fullSpan,spanSize))
    }

    fun addFootLayout(@LayoutRes footLayout: Int, fullSpan: Boolean = true, spanSize: Int = 0) {
        footLayoutConfigs.add(LayoutConfig(footLayout,fullSpan,spanSize))
    }

    //============= LoadMore =========
    fun canAutoLoadMore() = onLoadMoreListener != null && openAutoLoadMore

    fun openAutoLoadMore(open: Boolean = true) {
        isLoading = false
        if (canAutoLoadMore() && !open) {
            doNotifyDataSetChanged()
        }
        if (!openAutoLoadMore && open) {
            loadState = LoadState.LOADING
            doNotifyDataSetChanged()
        }
        openAutoLoadMore = open
    }

    fun loadCompleted() {
        loadState = LoadState.LOAD_COMPLETED
        isLoading = false
        if (canAutoLoadMore()) {
            doNotifyItemChanged(itemCount-1)
        }
    }

    fun loadFailed() {
        loadState = LoadState.LOAD_FAILED
        isLoading = false
        if (canAutoLoadMore()) {
            doNotifyItemChanged(itemCount-1)
        }
    }

    fun getShowDataSize() = getShowDataSize(data)

    private fun getShowDataSize(data: List<IMultiItem>?): Int {
        if (data == null || data.isEmpty()) {
            return 0
        }

        var size = data.size
        for (item in data) {
            // 只算已经展开的
            if (item is IExpandable && item.expandable) {
                size += getShowDataSize(item.getSubItems())
            }
        }
        return size
    }

    private fun getData(list: List<IMultiItem>, index: Int): IMultiItem {
        var pos = index
        for (item in list) {
            if (pos == 0) {
                return item
            }
            pos--
            if (item is IExpandable && item.expandable) {
                val itemList = item.getSubItems()
                val itemShowSize = getShowDataSize(itemList)
                if (pos < itemShowSize && itemList != null) {
                    // 在这个子列表里
                    return getData(itemList, pos)
                }
                pos -= itemShowSize
            }
        }

        throw IndexOutOfBoundsException("index < 0 or index >= getDataSize()")
    }

    /**
     * 展开下级菜单
     * @param adapterPosition 想展开下级菜单的父菜单位置
     */
    fun expand(adapterPosition: Int) {
        val item = getData(adapterPosition)
        if (item == null || item !is IExpandable) {
            return
        }
        item.expandable = true
        val addShowSize = getShowDataSize(item.getSubItems())

        doNotifyItemChanged(adapterPosition)
        if (addShowSize > 0 ) {
            doNotifyItemRangeInserted(adapterPosition + 1, addShowSize)
        }
    }

    /**
     * 展开全部下级菜单
     * @param adapterPosition 想展开下级菜单的父菜单位置
     */
    fun expandAll(adapterPosition: Int) {
        val item = getData(adapterPosition)
        if (item == null || item !is IExpandable) {
            return
        }
        var subShowSize = 0
        if (item.expandable) {
            subShowSize = getShowDataSize(item.getSubItems())
        }
        item.expandable = true

        expandAll(item.getSubItems())

        val newShowSize = getShowDataSize(item.getSubItems())

        doNotifyItemRangeChanged(adapterPosition, subShowSize + 1)
        if (newShowSize - subShowSize > 0) {
            doNotifyItemRangeInserted(adapterPosition + 1 + subShowSize, newShowSize - subShowSize)
        }
    }

    private fun expandAll(list: List<IMultiItem>?) {
        if (list == null || list.isEmpty()) {
            return
        }
        for (item in list) {
            if (item is IExpandable) {
                item.expandable = true
                expandAll(item.getSubItems())
            }
        }
    }

    private fun collapseAll(list: List<IMultiItem>?) {
        if (list == null || list.isEmpty()) {
            return
        }
        for (item in list) {
            if (item is IExpandable && item.expandable) {
                item.expandable = false
                collapseAll(item.getSubItems())
            }
        }
    }

    /**
     * 关闭下级菜单
     * @param adapterPosition 想关闭下级菜单的父菜单位置
     */
    fun collapse(adapterPosition: Int) {
        val item = getData(adapterPosition)
        if (item == null || item !is IExpandable) {
            return
        }
        if (item.expandable) {
            item.expandable = false
            val removeSize = getShowDataSize(item.getSubItems())
            doNotifyItemChanged(adapterPosition)
            if (removeSize > 0) {
                doNotifyItemRangeRemoved(adapterPosition+1, removeSize)
            }
        }
    }

    /**
     * 关闭下级菜单树下的所有菜单
     * @param adapterPosition 想关闭下级菜单的父菜单位置
     */
    fun collapseAll(adapterPosition: Int) {
        val item = getData(adapterPosition)
        if (item == null || item !is IExpandable) {
            return
        }
        if (item.expandable) {
            item.expandable = false
            val removeSize = getShowDataSize(item.getSubItems())
            collapseAll(item.getSubItems())
            doNotifyItemChanged(adapterPosition)
            if (removeSize > 0) {
                doNotifyItemRangeRemoved(adapterPosition + 1, removeSize)
            }
        }
    }

    /**
     * 通过子item的position找到它的父级item
     * @return 返回键值对(position, item)，如果找到确实是有父级item,则是对应的position及引用
     * 否则返回(-1，null)
     */
    fun findParentData(adapterPosition: Int): Pair<Int,IExpandable?> {
        val child = getData(adapterPosition) ?: return Pair(-1, null)

        for (position in adapterPosition-1 downTo 0) {
            val posItem = getData(position) ?: return Pair(-1, null)
            if (posItem is IExpandable && posItem.expandable) {
                if (checkIsChild(posItem, child)) {
                    return Pair(position, posItem)
                }
            }
        }

        return Pair(-1, null)
    }

    private fun checkIsChild(item: IExpandable, child: IMultiItem): Boolean {
        return item.getSubItems()?.contains(child)?:false
        /*item.getSubItems()?.forEach {
            if (it == child) {
                return true
            }
        }
        return false*/
    }


    inner class AsyncListDiffer(diffCallback: DiffUtil.ItemCallback<IMultiItem>) {
        private val config: AsyncDifferConfig<IMultiItem> = AsyncDifferConfig.Builder(diffCallback).build()
        private var maxScheduledGeneration = 0
        private val listUpdateCallback = AdapterListUpdateCallback()

        fun submitList(newList: List<IMultiItem>?) {
            if (data === newList) {
                return
            }

            val runGeneration = ++maxScheduledGeneration

            // fast simple remove all
            if (newList == null) {
                if (data.isEmpty()) return

                val position = if (alwaysShowHead) headSize else 0
                val count = if (!alwaysShowHead && !alwaysShowFoot) data.size + headSize + footSize
                else if (alwaysShowHead && alwaysShowFoot) data.size
                else if (!alwaysShowHead) data.size + headSize
                else data.size + footSize
                data.clear()
                doNotifyItemChanged(position)
                if (count - 1 > 0) {
                    doNotifyItemRangeRemoved(position + 1, count - 1)
                }
                return
            }

            // fast simple first insert
            if (data.isEmpty()) {
                if (newList.isEmpty()) return

                val position = if (alwaysShowHead) headSize else 0
                val count = if (!alwaysShowHead && !alwaysShowFoot) newList.size + headSize + footSize
                else if (alwaysShowHead && alwaysShowFoot) newList.size
                else if (!alwaysShowHead) newList.size + headSize
                else newList.size + footSize

                data.addAll(newList)
                doNotifyItemChanged(position)
                if (count - 1 > 0) {
                    doNotifyItemRangeInserted(position + 1, count - 1)
                }
                return
            }

            config.backgroundThreadExecutor.execute {
                val result = DiffUtil.calculateDiff(object : DiffUtil.Callback(){
                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem = getData(data, oldItemPosition)
                        val newItem = getData(newList, newItemPosition)
                        return config.diffCallback.areItemsTheSame(oldItem,newItem)
                    }

                    override fun getOldListSize() = getShowDataSize(data)

                    override fun getNewListSize() = getShowDataSize(newList)

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem = getData(data, oldItemPosition)
                        val newItem = getData(newList, newItemPosition)
                        return config.diffCallback.areContentsTheSame(oldItem,newItem)
                    }

                    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                        val oldItem = getData(data, oldItemPosition)
                        val newItem = getData(newList, newItemPosition)
                        return config.diffCallback.getChangePayload(oldItem,newItem)
                    }
                })

                config.mainThreadExecutor.execute {
                    if (runGeneration == maxScheduledGeneration) {
                        // 此时才刷新数据
                        data.clear()
                        data.addAll(newList)
                        result.dispatchUpdatesTo(listUpdateCallback)
                        if (canAutoLoadMore()) {
                            doNotifyItemChanged(itemCount - 1)
                        }
                    }
                }
            }
        }
    }

    inner class AdapterListUpdateCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            val skew = headSize
            doNotifyItemRangeChanged(skew, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            val skew = headSize
            doNotifyItemMoved(fromPosition + skew, toPosition + skew)
        }

        override fun onInserted(position: Int, count: Int) {
            val skew = headSize
            doNotifyItemRangeInserted(position + skew, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            val skew = headSize
            doNotifyItemRangeRemoved(position + skew, count)
        }
    }
}