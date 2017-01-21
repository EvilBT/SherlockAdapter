package xyz.zpayh.myadapter.data;

import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.DefaultMultiItem;
import xyz.zpayh.myadapter.R;

/**
 * 文 件 名: Details
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/23 19:42
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class Details extends DefaultMultiItem<String>{

    public Details(String content) {
        super(R.layout.item_details,content);
    }

    @Override
    public void convert(BaseViewHolder holder) {
        holder.setText(R.id.content,mData);
    }
}
