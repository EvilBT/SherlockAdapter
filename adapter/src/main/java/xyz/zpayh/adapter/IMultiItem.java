package xyz.zpayh.adapter;

import android.support.annotation.LayoutRes;

/**
 * Created by Administrator on 2016/12/23.
 */

public interface IMultiItem {

    @LayoutRes int getLayoutRes();

    int getViewType();

    int getSpanSize();
}
