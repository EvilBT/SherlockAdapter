package xyz.zpayh.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

/**
 * 文 件 名: LoadMore
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/20 23:17
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public interface LoadMore {

    /**
     * 打开自动加载更多
     */
    void openAutoLoadMore();

    /**
     * 加载完成
     */
    void loadCompleted();

    /**
     * 加载失败
     */
    void loadFailed();

    /**
     * 设置加载更多监听事件
     * @param onLoadMoreListener 当为null时不会开启自动加载
     */
    void setOnLoadMoreListener(@Nullable OnLoadMoreListener onLoadMoreListener);

    /**
     * 设置加载界面
     * @param moreLayout
     */
    void setLoadMoreLayout(@LayoutRes int moreLayout);

    /**
     *
     * @return 返回是否自动加载
     */
    boolean canAutoLoadMore();
}
