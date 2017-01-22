package xyz.zpayh.adapter;

import android.support.annotation.LayoutRes;

/**
 * Created by Administrator on 2017/1/22.
 */

public abstract class DefaultMultiSelectItem<T> implements IMultiSelectItem {

    @LayoutRes
    private final int mLayoutRes;

    private boolean mChecked;

    private int mSpanSize;

    protected T mData;

    public DefaultMultiSelectItem(int layoutRes) {
        this(layoutRes,null);
    }

    public DefaultMultiSelectItem(int layoutRes, T data) {
        this(layoutRes, data, 1);
    }

    public DefaultMultiSelectItem(int layoutRes, T data, int spanSize) {
        this.mLayoutRes = layoutRes;
        this.mData = data;
        this.mChecked = false;
        this.mSpanSize = spanSize;
    }

    public T getData() {
        return mData;
    }

    public void setData(T mData) {
        this.mData = mData;
    }

    @Override
    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    @Override
    public boolean isChecked() {
        return this.mChecked;
    }

    @Override
    public int getLayoutRes() {
        return this.mLayoutRes;
    }

    @Override
    public int getSpanSize() {
        return this.mSpanSize;
    }
}
