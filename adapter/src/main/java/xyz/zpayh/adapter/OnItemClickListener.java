package xyz.zpayh.adapter;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * 文 件 名: OnItemClickListener
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/25 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public interface OnItemClickListener {

    /**
     * 响应点击事件监听回调
     * @param view 响应事件的View
     * @param adapterPosition item所在的位置
     */
    void onItemClick(@NonNull View view, int adapterPosition);
}
