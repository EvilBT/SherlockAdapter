package xyz.zpayh.adapter;

import android.support.annotation.IdRes;

/**
 * 文 件 名: IMultiSelectItem
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/01/22 09:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 *
 */

public interface IMultiSelectItem extends IMultiItem{

    void setChecked(boolean checked);

    boolean isChecked();

    /**
     * 用于点击更新选中状态的view id,此view 必须实现Checkable接口，
     * 否则应该是无效的
     * @return 返回一个实现了Checkable接口的View id.
     */
    @IdRes
    int getCheckableViewId();
}
