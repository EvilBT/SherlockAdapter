package xyz.zpayh.myadapter.data;

import xyz.zpayh.adapter.BaseViewHolder;
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

    private int mSpanSize;

    public Text(String text,int spanSize) {
        mText = text;
        mSpanSize = spanSize;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_text;
    }

    @Override
    public void convert(BaseViewHolder holder) {

    }

    @Override
    public int getSpanSize() {
        return mSpanSize;
    }
}
