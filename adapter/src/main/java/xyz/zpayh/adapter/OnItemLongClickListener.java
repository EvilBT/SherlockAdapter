package xyz.zpayh.adapter;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * 文 件 名: OnItemLongClickListener
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/25 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public interface OnItemLongClickListener {

    /**
     * 长按事件监听接口
     * @param view 响应事件的View
     * @param adapterPosition item在列表中的位置
     * @return true 响应事件
     *          false 不响应事件
     */
    boolean onItemLongClick(@NonNull View view, int adapterPosition);
}
