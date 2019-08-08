package cn.sherlockzp.adapter

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

/**
 * 文 件 名: IMultiItem
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/25 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 *  移除了getViewType接口，添加
 */
interface IMultiItem {

    /**
     * 不同类型的item请使用不同的布局文件（即使它们的布局是一样的），
     * 确保生成不同的ID值
     * @return 返回item对应的布局文件id
     */
    @LayoutRes fun getLayoutRes(): Int

    /**
     * 进行数据处理，显示文本，图片等内容
     * @param holder Holder Helper
     */
    fun convert(holder: BaseViewHolder)

    /**
     * 在布局为GridLayoutManager时才有用处，
     * 返回当前布局所占用的SpanSize
     * @return 如果返回的SpanSize 小于或等于 0 或者 大于 GridLayoutManager#getSpanCount()
     *  则{@link BaseAdapter} 会在BaseAdapter#onAttachedToRecyclerView(RecyclerView)
     *  自适应为1或者GridLayoutManager#getSpanCount(),详情参考BaseAdapter#onAttachedToRecyclerView(RecyclerView)
     *  默认为1
     */
    fun getSpanSize() = 1

    /**
     * 在布局为StaggeredGridLayoutManager时才有用处，
     * 详情参考BaseAdapter#onViewAttachedToWindow(BaseViewHolder)
     * @return
     *      true : 此item独占一行（或一列）
     *      false : 正常占用一格
     *      默认返回false
     */
    fun isFullSpan() = false
}

interface IMultiSelectItem : IMultiItem {
    var checked: Boolean

    /**
     * 用于点击更新选中状态的view id,此view 必须实现Checkable接口，
     * 否则应该是无效的
     * @return 返回一个实现了Checkable接口的View id.
     */
    @IdRes
    fun getCheckableViewId(): Int
}

interface IExpandable : IMultiItem {
    /**
     * true 表示展开子列表，false 表示关闭子列表
     */
    var expandable: Boolean

    /**
     * 返回可展开的子列表
     */
    fun getSubItems(): MutableList<IMultiItem>?
}
