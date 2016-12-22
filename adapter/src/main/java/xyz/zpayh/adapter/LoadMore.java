package xyz.zpayh.adapter;

import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 文 件 名: LoadMore
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/20 23:17
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public interface LoadMore {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOADING,LOAD_COMPLETED,LOAD_FAILED})
    @interface LoadState{
    }

    /**
     * 正在加载更多
     */
    int LOADING = 0;
    /**
     * 完成所有加载
     */
    int LOAD_COMPLETED = 1;
    /**
     * 加载更多失败
     */
    int LOAD_FAILED = 2;

    /**
     * 设置自动加载更多开关
     * @param open true打开加载更多，false关闭
     */
    void openAutoLoadMore(boolean open);

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
     * @param moreLayout LoadMore布局
     */
    void setLoadMoreLayout(@LayoutRes int moreLayout);

    /**
     *
     * @return 返回是否自动加载
     */
    boolean canAutoLoadMore();
}
