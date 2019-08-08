package xyz.zpayh.myadapter.data;

import androidx.annotation.DrawableRes;

/**
 * 文 件 名: ImageCard
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/4/11 17:03
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class ImageCard {

    @DrawableRes
    public int mImageResId;

    public String mImageTitle;

    public ImageCard(int imageResId, String imageTitle) {
        mImageResId = imageResId;
        mImageTitle = imageTitle;
    }
}
