package cn.sherlockzp.adapter

import androidx.databinding.ViewDataBinding
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.SparseArray
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Checkable
import android.widget.ImageView
import android.widget.TextView
import java.util.concurrent.atomic.AtomicBoolean


class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val viewMaps = SparseArray<View>()

    var binding: ViewDataBinding? = null

    constructor(binding: ViewDataBinding) : this(binding.root) {
        this.binding = binding
    }

    private lateinit var onItemClickListener: OnItemClickListener

    private var initClickListener = AtomicBoolean(false)

    private val onClickListener = View.OnClickListener{
        onItemClickListener.onItemClick(it, adapterPosition)
    }

    private lateinit var onItemLongClickListener: OnItemLongClickListener

    private var initLongClickListener = AtomicBoolean(false)

    private val onLongClickListener = View.OnLongClickListener {
        return@OnLongClickListener onItemLongClickListener.onItemLongClick(it, adapterPosition)
    }

    var onItemCheckedChangeListener: OnItemCheckedChangeListener? = null

    private val onItemCheckedListener = View.OnClickListener {
        if (it is Checkable) {
            onItemCheckedChangeListener?.onItemCheck(it, it.isChecked, adapterPosition)
        }
    }

    internal fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener

        if (initClickListener.compareAndSet(false, true)) {
            itemView.setOnClickListener(onClickListener)
        }

    }

    internal fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener

        if (initLongClickListener.compareAndSet(false, true)) {
            itemView.setOnLongClickListener(onLongClickListener)
        }
    }

    fun setVariable(variableId: Int, value: Any?): BaseViewHolder {
        binding?.setVariable(variableId, value)
        return this
    }

    fun setClickable(@IdRes id: Int, clickable: Boolean = true): BaseViewHolder {
        val view = find<View>(id)
        view?.setOnClickListener(if (clickable) onClickListener else null)
        return this
    }

    fun setLongClickable(@IdRes id: Int, clickable: Boolean = true): BaseViewHolder {
        val view = find<View>(id)
        view?.setOnLongClickListener(if (clickable) onLongClickListener else null)
        return this
    }

    fun setCheckable(@IdRes id: Int, clickable: Boolean = true): BaseViewHolder {
        val view = find<View>(id)
        if (view is Checkable) {
            view.setOnClickListener(if(clickable)onItemCheckedListener else null)
        }
        return this
    }

    fun setChecked(@IdRes id: Int, checked : Boolean = true): BaseViewHolder {
        val view = find<View>(id)
        if (view is Checkable && view.isChecked != checked) {
            view.isChecked = checked
        }
        return this
    }

    fun setText(@IdRes id: Int, text: String): BaseViewHolder {
        findText(id)?.text = text
        return this
    }

    fun setText(@IdRes id: Int, @StringRes strRes: Int) = setText(id, itemView.resources.getString(strRes))

    fun setText(@IdRes id: Int, callback: TextCallback) = setView(id, callback)

    fun setText(@IdRes id: Int, callback: (TextView) -> Unit) = setView(id, callback)

    fun setTypeface(@IdRes id: Int, typeface: Typeface): BaseViewHolder {
        findText(id)?.typeface = typeface
        return this
    }

    fun setImage(@IdRes id: Int, @DrawableRes resId: Int): BaseViewHolder {
        findImage(id)?.setImageResource(resId)
        return this
    }

    fun setImage(@IdRes id: Int, drawable: Drawable): BaseViewHolder {
        findImage(id)?.setImageDrawable(drawable)
        return this
    }

    fun setImage(@IdRes id: Int, bm: Bitmap): BaseViewHolder {
        findImage(id)?.setImageBitmap(bm)
        return this
    }

    fun setImage(@IdRes id: Int, callback: (ImageView) -> Unit) = setView(id, callback)

    fun setImage(@IdRes id: Int, callback: ImageCallback) = setView(id, callback)

    fun <T : View> setView(@IdRes id: Int, callback: (T) -> Unit): BaseViewHolder {
        find<T>(id)?.let {
            callback(it)
        }
        return this
    }

    fun <T : View> setView(@IdRes id: Int, callback: ViewCallBack<T>): BaseViewHolder {
        find<T>(id)?.let {
            callback.callback(it)
        }
        return this
    }

    fun setVisibility(@IdRes id: Int, visibility: Int = View.VISIBLE): BaseViewHolder {
        find<View>(id)?.visibility = visibility
        return this
    }

    fun <T : View> find(@IdRes id: Int): T? {
        if(viewMaps.indexOfKey(id) > 0){
            @Suppress("UNCHECKED_CAST")
            return viewMaps[id] as T
        }
        val view = itemView.findViewById<T>(id)
        view?.let {
            viewMaps.put(id,it)
        }
        return view
    }

    fun findText(@IdRes id: Int) = find<TextView>(id)

    fun findImage(@IdRes id: Int) = find<ImageView>(id)
}


interface OnItemClickListener {
    fun onItemClick(view: View, adapterPosition: Int)
}

interface OnItemLongClickListener {
    fun onItemLongClick(view: View, adapterPosition: Int): Boolean
}

interface OnItemCheckedChangeListener {
    fun onItemCheck(view: View, isChecked: Boolean, adapterPosition: Int)
}