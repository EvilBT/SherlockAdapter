package cn.sherlockzp.adapter

class BaseMultiAdapter : BaseAdapter<IMultItem>(){

    override fun getLayoutRes(index: Int) = data[index].getLayoutRes()

    override fun convert(holder: BaseViewHolder, data: IMultItem, index: Int) = data.convert(holder)
}