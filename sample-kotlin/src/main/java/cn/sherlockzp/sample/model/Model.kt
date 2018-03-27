package cn.sherlockzp.sample.model

import android.support.annotation.DrawableRes
import cn.sherlockzp.adapter.BaseViewHolder
import cn.sherlockzp.adapter.IMultiItem
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