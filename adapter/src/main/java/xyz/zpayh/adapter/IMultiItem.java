package xyz.zpayh.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 文 件 名: IMultiItem
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/25 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public interface IMultiItem {

    /**
     * 不同类型的item请使用不同的布局文件，
     * 即使它们的布局是一样的，也要copy多一份出来。
     * @return 返回item对应的布局id
     */
    @LayoutRes int getLayoutRes();

    /**
     *
     * @return 返回item类型，请返回一个唯一识别符
     */
    int getViewType();

    /**
     * 在布局为{@link android.support.v7.widget.GridLayoutManager}时才有用处，
     * 返回当前布局所占用的SpanSize
     * @return 如果返回的SpanSize <= 0 或者 > {@link GridLayoutManager#getSpanCount()}
     *  则{@link BaseAdapter} 会在{@link BaseAdapter#onAttachedToRecyclerView(RecyclerView)}
     *  自适应为1或者{@link GridLayoutManager#getSpanCount()},详情参考{@link BaseAdapter#onAttachedToRecyclerView(RecyclerView)}
     */
    int getSpanSize();
}
