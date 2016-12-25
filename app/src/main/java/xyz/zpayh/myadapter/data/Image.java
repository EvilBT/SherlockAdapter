package xyz.zpayh.myadapter.data;

import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.myadapter.R;

/**
 * 文 件 名: Image
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/23 19:42
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class Image implements IMultiItem{
    public int mImageId;

    public Image(int imageId) {
        mImageId = imageId;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_image;
    }

    @Override
    public int getViewType() {
        return Constant.IMAGE;
    }

    @Override
    public int getSpanSize() {
        return 0;
    }
}
