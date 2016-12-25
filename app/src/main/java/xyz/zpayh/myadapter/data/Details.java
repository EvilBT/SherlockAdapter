package xyz.zpayh.myadapter.data;

import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.myadapter.R;

/**
 * 文 件 名: Details
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/23 19:42
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class Details implements IMultiItem{
    public String mContent;

    public Details(String content) {
        mContent = content;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_details;
    }

    @Override
    public int getViewType() {
        return Constant.DETAILS;
    }

    @Override
    public int getSpanSize() {
        return 0;
    }
}
