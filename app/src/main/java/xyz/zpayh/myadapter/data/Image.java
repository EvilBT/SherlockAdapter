package xyz.zpayh.myadapter.data;

import android.graphics.Point;
import android.support.annotation.DrawableRes;
import android.util.Log;

import com.facebook.drawee.view.SimpleDraweeView;

import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.myadapter.R;
import xyz.zpayh.myadapter.util.FrescoUtil;

/**
 * 文 件 名: Image
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/23 19:42
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class Image implements IMultiItem{
    @DrawableRes
    private int mImageId;

    private int mSpanSize;

    private int mImageWidth;

    private Point mImageViewSize;

    public Image(int imageId,int width ) {
        mImageId = imageId;
        mSpanSize = 1;
        mImageWidth = width * mSpanSize;
    }

    public Image(int imageId,int width, int spanSize){
        mImageId = imageId;
        mSpanSize = spanSize;
        mImageWidth = width * mSpanSize;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_image;
    }

    @Override
    public void convert(BaseViewHolder holder) {

        SimpleDraweeView sdv = holder.find(R.id.image);

        if (mImageViewSize == null  || mImageViewSize.x <= 0 || mImageViewSize.y <= 0) {
            mImageViewSize = new Point();
            FrescoUtil.setWrapAndResizeImage(sdv, mImageId, mImageWidth,mImageViewSize);
            Log.d("Image", "第一次，需要重绘");
        }else{
            FrescoUtil.resizeImage(sdv,mImageId,mImageViewSize.x,mImageViewSize.y);
            Log.d("Image", "不是第一次，不需要重绘："+mImageViewSize.toString());
            Log.d("Image", "不是第一次："+sdv.getAspectRatio()+","+mImageId);
        }
    }

    @Override
    public int getSpanSize() {
        return mSpanSize;
    }
}
