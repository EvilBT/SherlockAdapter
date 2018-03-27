package cn.sherlockzp.sample.model

import android.support.annotation.DrawableRes
import android.view.View
import cn.sherlockzp.adapter.BaseViewHolder
import cn.sherlockzp.adapter.IMultiItem
import cn.sherlockzp.adapter.IMultiSelectItem
import cn.sherlockzp.sample.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


data class ImageCard(@DrawableRes val imageResId : Int, val title: String)

data class Text(val text: String,private val _spanSize: Int = 1) : IMultiItem {

    override fun getSpanSize() = _spanSize

    override fun getLayoutRes() = R.layout.item_text

    override fun convert(holder: BaseViewHolder) {
        holder.setText(R.id.text,text)
    }
}

data class Image(@DrawableRes private val imageId: Int,
                 private val width: Int,
                 private val _spanSize: Int = 1): IMultiItem {
    override fun getLayoutRes() = R.layout.item_image

    override fun convert(holder: BaseViewHolder) {
        holder.setImage(R.id.iv_image){
            Glide.with(holder.itemView.context)
                    .load(imageId)
                    .apply(RequestOptions.fitCenterTransform())
                    .apply(RequestOptions.overrideOf(width))
                    .into(it)
        }
    }

    override fun getSpanSize() = _spanSize
}

data class Item(val index: Int, override var checked: Boolean = false) : IMultiSelectItem {
    /**
     * 不同类型的item请使用不同的布局文件（即使它们的布局是一样的），
     * 确保生成不同的ID值
     * @return 返回item对应的布局文件id
     */
    override fun getLayoutRes() = R.layout.item_select

    /**
     * 进行数据处理，显示文本，图片等内容
     * @param holder Holder Helper
     */
    override fun convert(holder: BaseViewHolder) {
        // 根据是否选中更新文本
        holder.setText(R.id.tv_tips, if (checked) "${index}被选中" else "${index}未被选中")
    }

    /**
     * 用于点击更新选中状态的view id,此view 必须实现Checkable接口，
     * 否则应该是无效的
     * @return 返回一个实现了Checkable接口的View id.
     */
    override fun getCheckableViewId() = R.id.cb_select
}

data class NoSelected(val text: String, override var checked: Boolean = false) : IMultiSelectItem {
    /**
     * 不同类型的item请使用不同的布局文件（即使它们的布局是一样的），
     * 确保生成不同的ID值
     * @return 返回item对应的布局文件id
     */
    override fun getLayoutRes() = R.layout.item_no_select

    /**
     * 进行数据处理，显示文本，图片等内容
     * @param holder Holder Helper
     */
    override fun convert(holder: BaseViewHolder) {
        holder.setText(R.id.tv_tips, text)
    }

    /**
     * 用于点击更新选中状态的view id,此view 必须实现Checkable接口，
     * 否则应该是无效的
     * @return 返回一个实现了Checkable接口的View id.
     */
    override fun getCheckableViewId() = View.NO_ID
}