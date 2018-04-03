package cn.sherlockzp.adapter

import android.view.View


open class BaseMultiSelectAdapter : BaseAdapter<IMultiSelectItem>(){

    var onItemCheckedChangeListener: OnItemCheckedChangeListener? = null

    fun setOnItemCheckChangeListener(listener: (view: View, isChecked: Boolean, adapterPosition: Int) -> Unit) {
        onItemCheckedChangeListener = object : OnItemCheckedChangeListener{
            override fun onItemCheck(view: View, isChecked: Boolean, adapterPosition: Int) {
                listener(view, isChecked, adapterPosition)
            }
        }
    }

    override fun getLayoutRes(index: Int) = data[index].getLayoutRes()

    override fun convert(holder: BaseViewHolder, data: IMultiSelectItem, index: Int) {
        holder.setChecked(data.getCheckableViewId(), data.checked)
        data.convert(holder)
    }

    override fun bind(holder: BaseViewHolder, layoutRes: Int) {
        holder.onItemCheckedChangeListener = object : OnItemCheckedChangeListener {
            override fun onItemCheck(view: View, isChecked: Boolean, adapterPosition: Int) {
                val data = getData(adapterPosition)
                if (data != null && view.id == data.getCheckableViewId()) {
                    data.checked = isChecked
                    doNotifyItemChanged(adapterPosition)
                }
                onItemCheckedChangeListener?.onItemCheck(view, isChecked, adapterPosition)
            }
        }
        super.bind(holder, layoutRes)
    }

    fun clearSelectAll() {
        for (item in data) {
            item.checked = false
        }
        doNotifyDataSetChanged()
    }

    fun selectAll() {
        for (item in data) {
            item.checked = true
        }
        doNotifyDataSetChanged()
    }

    /**
     * 返回所有选中的item项，另外，如果CheckableViewId == View.NO_ID 时，本身是没有选择状态的，这里剔除掉
     * 这些不可选择项
     */
    fun getSelectedItems() = data.filter { it.checked && it.getCheckableViewId() != View.NO_ID }
}