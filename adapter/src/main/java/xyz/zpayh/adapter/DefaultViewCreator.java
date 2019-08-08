package xyz.zpayh.adapter;

import androidx.annotation.LayoutRes;

/**
 * 文 件 名: DefaultViewCreator
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/10/10 11:25
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public interface DefaultViewCreator {

    @LayoutRes
    int getEmptyViewLayout();

    @LayoutRes
    int getErrorViewLayout();

    @LayoutRes
    int getLoadMoreViewLayout();
}
