package xyz.zpayh.adapter;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * 文 件 名: OnItemCheckedChangeListener
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/01/22 09:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 * item 选中状态监听
 */

public interface OnItemCheckedChangeListener {

    void onItemCheck(@NonNull View view,boolean isChecked, int adapterPosition);
}
