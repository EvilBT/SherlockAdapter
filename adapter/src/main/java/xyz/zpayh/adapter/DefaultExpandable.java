package xyz.zpayh.adapter;

import android.support.annotation.LayoutRes;

import java.util.ArrayList;
import java.util.List;

/**
 * 文 件 名: DefaultExpandable
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/01/21 14:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 *  一个默认的实现{@link IExpandable}大部分方法的抽象类，一般继承实现此抽象类即可。
 */

public abstract class DefaultExpandable<T> implements IExpandable {

    protected final List<IMultiItem> mSubData;

    private int mSpanSize;

    private boolean mExpandable;

    @LayoutRes
    private final int mLayoutRes;

    protected T mData;

    public DefaultExpandable(@LayoutRes int layoutRes) {
        this(layoutRes, null);
    }

    public DefaultExpandable(@LayoutRes int layoutRes, T data){
        this(layoutRes, data, 1);
    }

    public DefaultExpandable(@LayoutRes int layoutRes,T data, int spanSize){
        mLayoutRes = layoutRes;
        mData = data;
        mSpanSize = spanSize;
        mSubData = new ArrayList<>();
        mExpandable = false;
    }

    public T getData() {
        return mData;
    }

    public void setData(T mData) {
        this.mData = mData;
    }

    public void setSubData(List<IMultiItem> subData){
        mSubData.clear();
        if (subData != null){
            mSubData.addAll(subData);
        }
    }

    public void addSubData(List<IMultiItem> subData){
        if (subData != null){
            mSubData.addAll(subData);
        }
    }

    public void addSubData(IMultiItem subData){
        if (subData != null){
            mSubData.add(subData);
        }
    }

    @Override
    public boolean isExpandable() {
        return mExpandable;
    }

    @Override
    public void setExpandable(boolean expandable) {
        mExpandable = expandable;
    }

    @Override
    public List<IMultiItem> getSubItems() {
        return mSubData;
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
