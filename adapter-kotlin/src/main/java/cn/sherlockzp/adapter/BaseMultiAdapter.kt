package cn.sherlockzp.adapter

open class BaseMultiAdapter : BaseAdapter<IMultiItem>(){

    override fun getLayoutRes(index: Int) = data[index].getLayoutRes()

    override fun convert(holder: BaseViewHolder, data: IMultiItem, index: Int) = data.convert(holder)
}