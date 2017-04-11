package xyz.zpayh.myadapter.data;

import android.graphics.Point;
import android.util.Log;

import com.facebook.drawee.view.SimpleDraweeView;

import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.DefaultMultiItem;
import xyz.zpayh.myadapter.R;
import xyz.zpayh.myadapter.util.FrescoUtil;

/**
 * 文 件 名: Card
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/4/12 00:21
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class Card extends DefaultMultiItem<ImageCard>{

    private int mWidth;

    private Point mImageViewSize;

    public Card(int width, ImageCard data) {
        super(R.layout.item_card, data);
        mWidth = width;
    }

    @Override
    public void convert(BaseViewHolder holder) {
        holder.setText(R.id.tv_image_title,mData.mImageTitle);
        SimpleDraweeView sdv = holder.find(R.id.iv_bg);

        if (mImageViewSize == null || mImageViewSize.x <= 0 || mImageViewSize.y <= 0) {
            mImageViewSize = new Point();
            FrescoUtil.setWrapAndResizeImage(sdv, mData.mImageResId, mWidth,mImageViewSize);
            Log.d("Image", "第一次，需要重绘");
        }else{
            FrescoUtil.resizeImage(sdv,mData.mImageResId,mImageViewSize.x,mImageViewSize.y);
            Log.d("Image", "不是第一次，不需要重绘："+mImageViewSize.toString());
            Log.d("Image", "不是第一次："+sdv.getAspectRatio()+","+mData.mImageResId);
        }
    }
}
