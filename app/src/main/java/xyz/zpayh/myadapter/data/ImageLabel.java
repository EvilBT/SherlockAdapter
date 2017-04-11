package xyz.zpayh.myadapter.data;

import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.DefaultExpandable;
import xyz.zpayh.adapter.IFullSpan;
import xyz.zpayh.myadapter.R;

/**
 * 文 件 名: ImageLabel
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/4/12 00:11
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class ImageLabel extends DefaultExpandable<String> implements IFullSpan{

    public ImageLabel(String label) {
        super(R.layout.item_image_label,label,Integer.MAX_VALUE);
    }

    @Override
    public void convert(BaseViewHolder holder) {
        holder.setText(R.id.tv_image_label,mData);
    }

    @Override
    public boolean isFullSpan() {
        return true;
    }
}
