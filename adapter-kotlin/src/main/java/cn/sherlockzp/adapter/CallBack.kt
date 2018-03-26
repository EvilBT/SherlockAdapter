package cn.sherlockzp.adapter

import android.widget.ImageView
import android.widget.TextView

interface ImageCallback : ViewCallBack<ImageView>

interface TextCallback : ViewCallBack<TextView>

interface ViewCallBack<in View> {
    fun callback(view: View)
}