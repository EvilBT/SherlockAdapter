package xyz.zpayh.myadapter.data;

import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.myadapter.R;

/**
 * 文 件 名: Text
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/23 19:41
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class Text implements IMultiItem{
    public String mText;

    public Text(String text) {
        mText = text;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_text;
    }

    @Override
    public int getViewType() {
        return Constant.TEXT;
    }

    @Override
    public int getSpanSize() {
        return 0;
    }
}
