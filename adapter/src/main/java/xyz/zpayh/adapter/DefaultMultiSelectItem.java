/*
 * Copyright 2017 陈志鹏
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.zpayh.adapter;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

import static android.view.View.NO_ID;

/**
 * Created by Administrator on 2017/1/22.
 */

public abstract class DefaultMultiSelectItem<T> implements IMultiSelectItem {

    @LayoutRes
    private final int mLayoutRes;

    @IdRes
    protected int mCheckableViewId;

    private boolean mChecked;

    private int mSpanSize;

    protected T mData;

    public DefaultMultiSelectItem(@LayoutRes int layoutRes){
        this(layoutRes,NO_ID);
    }

    public DefaultMultiSelectItem(@LayoutRes int layoutRes, @IdRes int checkableViewId) {
        this(layoutRes,checkableViewId,null);
    }

    public DefaultMultiSelectItem(@LayoutRes int layoutRes, @IdRes int checkableViewId, T data) {
        this(layoutRes,checkableViewId, data, 1);
    }

    public DefaultMultiSelectItem(@LayoutRes int layoutRes, @IdRes int checkableViewId, T data, int spanSize) {
        this.mLayoutRes = layoutRes;
        this.mCheckableViewId = checkableViewId;
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
    public int getCheckableViewId() {
        return mCheckableViewId;
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
