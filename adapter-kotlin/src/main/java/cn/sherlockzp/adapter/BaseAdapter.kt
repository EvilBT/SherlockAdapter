package cn.sherlockzp.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseViewHolder>(){

    val data = ArrayList<T>()

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
    private var emptyLayout = sDefaultViewCreator.getEmptyViewLayout()
    @LayoutRes
    private var errorLayout = sDefaultViewCreator.getErrorViewLayout()
    @LayoutRes
    private var loadMoreLayout = sDefaultViewCreator.getLoadMoreViewLayout()

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

    fun setData(data: List<T>?) {

        if (openAutoLoadMore) {
            loadState = LoadState.LOADING
            isLoading = false
        }
        showErrorView = false

        this.data.clear()
        data?.let {
            this.data.addAll(it)
        }

        doNotifyDataSetChanged()
    }

    fun addData(data: T) {

        val startPos = this.data.size + headSize
        val itemCount = 1 + footSize + if (canAutoLoadMore()) 1 else 0

        this.data.add(data)

        if (openAutoLoadMore) {
            loadState = LoadState.LOADING
            isLoading = false
        }
        showErrorView = false

        doNotifyItemRangeChanged(startPos, itemCount)
    }

    fun addData(data: List<T>) {
        val startPos = this.data.size + headSize
        val itemCount = data.size + footSize + if (canAutoLoadMore()) 1 else 0

        this.data.addAll(data)

        if (openAutoLoadMore) {
            loadState = LoadState.LOADING
            isLoading = false
        }
        showErrorView = false

        doNotifyItemRangeChanged(startPos, itemCount)
    }

    fun removeData(data: T) {
        if (this.data.contains(data)) {
            val index = this.data.indexOf(data)
            this.data.remove(data)
            doNotifyItemRemoved(headSize+index)
        }
    }

    fun removeDataAt(adapterPosition: Int) {
        val index = adapterPosition - headSize
        if (index in 0 until data.size) {
            data.removeAt(index)
            doNotifyItemRemoved(adapterPosition)
        }
    }

    fun removeHead(adapterPosition: Int) {
        if (adapterPosition in 0 until headSize) {
            headLayoutConfigs.removeAt(adapterPosition)
            doNotifyItemRemoved(adapterPosition)
        }
    }

    fun removeAllHead() {
        if (hasHead) {
            val size = headSize
            headLayoutConfigs.clear()
            doNotifyItemRangeRemoved(0, size)
        }
    }

    fun removeAllFoot() {
        if (hasFoot) {
            val size = footSize
            footLayoutConfigs.clear()
            doNotifyItemRangeRemoved(headSize+data.size, size)
        }
    }

    fun removeFoot(adapterPosition: Int) {
        val index = adapterPosition - headSize - data.size
        if (index in 0 until footSize) {
            footLayoutConfigs.removeAt(index)
            doNotifyItemRemoved(adapterPosition)
        }
    }

    fun getData(adapterPosition: Int): T? {
        val index = adapterPosition - headSize
        if (index in 0 until data.size) {
            return data[index]
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

        /*val holder = BaseViewHolder(LayoutInflater.from(parent.context)
                .inflate(layoutRes, parent, false))*/
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
        if (index < data.size) {
            return getLayoutRes(index)
        }

        index = position - headSize - data.size
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

        return headSize + data.size + footSize +
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

        if (index < data.size) {
            convert(holder, data[index], index)
            return
        }

        index = position - headSize - data.size

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

    @LayoutRes
    abstract fun getLayoutRes(index: Int): Int

    abstract fun convert(holder: BaseViewHolder, data: T, index: Int)

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

        val index = position - (headSize + data.size)
        if (index in 0 until footSize) {
            lp.isFullSpan = footLayoutConfigs[index].fullSpan
            return
        }
        if (index > 0) {
            lp.isFullSpan = true
            return
        }

        val data = getData(holder.adapterPosition)
        if (data is IMultiItem) {
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

                val index = position - (headSize + data.size)
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
                if (data != null && data is IMultiItem) {
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

    fun doNotifyItemChanged(position: Int, payload: Any? = null) {
        recyclerView?.let {
            if (it.isComputingLayout) {
                it.post { notifyItemChanged(position, payload) }
                return
            }
        }
        notifyItemChanged(position, payload)
    }

    fun doNotifyItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any? = null) {
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

    companion object {
        var sDefaultViewCreator = object : DefaultViewCreator {
        }
    }
}

interface DefaultViewCreator {

    @LayoutRes
    fun getEmptyViewLayout() = R.layout.default_empty_view

    @LayoutRes
    fun getErrorViewLayout() = R.layout.default_error_view

    @LayoutRes
    fun getLoadMoreViewLayout() = R.layout.default_loadmore_view
}

interface OnLoadMoreListener {

    /**
     * 加载更多回调接口
     */
    fun onLoadMore()
}

enum class LoadState {
    LOADING,
    LOAD_COMPLETED,
    LOAD_FAILED
}

data class LayoutConfig(
        @LayoutRes var layout: Int,
        var fullSpan: Boolean,
        var spanSize: Int
        )