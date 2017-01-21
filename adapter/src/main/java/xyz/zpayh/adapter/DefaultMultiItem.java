package xyz.zpayh.adapter;

import android.support.annotation.LayoutRes;

/**
 * 文 件 名: DefaultMultiItem
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/01/21 14:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 *  一个默认的实现{@link IMultiItem}大部分方法的抽象类，一般继承实现此抽象类即可。
 */


public abstract class DefaultMultiItem<T> implements IMultiItem {

    @LayoutRes
    private final int mLayoutRes;

    private int mSpanSize;

    protected T mData;

    public DefaultMultiItem(@LayoutRes int layoutRes) {
        this(layoutRes,null);
    }

    public DefaultMultiItem(@LayoutRes int layoutRes,T data) {
        this(layoutRes, data, 1);
    }

    public DefaultMultiItem(@LayoutRes int layoutRes,T data, int spanSize) {
        this.mLayoutRes = layoutRes;
        this.mData = data;
        this.mSpanSize = spanSize;
    }

    public T getData() {
        return mData;
    }

    public void setData(T mData) {
        this.mData = mData;
    }

    @Override
    public int getLayoutRes() {
        return mLayoutRes;
    }

    @Override
    public int getSpanSize() {
        return mSpanSize;
    }
}
