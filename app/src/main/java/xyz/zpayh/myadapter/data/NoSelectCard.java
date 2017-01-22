package xyz.zpayh.myadapter.data;

import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.DefaultMultiSelectItem;
import xyz.zpayh.myadapter.R;

import static android.view.View.NO_ID;

/**
 * 文 件 名: NoSelectCard
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/1/23 01:07
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class NoSelectCard extends DefaultMultiSelectItem<String> {

    public NoSelectCard(String data) {
        super(R.layout.item_no_select,NO_ID,data);
    }

    @Override
    public void convert(BaseViewHolder holder) {
        holder.setText(R.id.text,mData);
    }
}
