package xyz.zpayh.adapter;

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
}
