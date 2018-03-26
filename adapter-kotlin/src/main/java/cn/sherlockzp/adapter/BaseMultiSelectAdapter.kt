package cn.sherlockzp.adapter

import android.view.View


class BaseMultiSelectAdapter : BaseAdapter<IMultiSelectItem>(){

    var onItemCheckedChangeListener: OnItemCheckedChangeListener? = null

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

    fun getSelectedItems() = data.filter { it.checked }
}