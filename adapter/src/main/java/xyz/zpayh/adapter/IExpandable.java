package xyz.zpayh.adapter;

import java.util.List;

/**
 * 文 件 名: IExpandable
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/25 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public interface IExpandable extends IMultiItem{

    /**
     * 用来判断区分是否展开
     * @return true 表示是展开状态， false 表示是关闭状态
     */
    boolean isExpandable();

    /**
     * 设置展开状态
     */
    void setExpandable(boolean expandable);

    /**
     * 返回可以展开的子列表
     */
    List<IMultiItem> getSubItems();
}
