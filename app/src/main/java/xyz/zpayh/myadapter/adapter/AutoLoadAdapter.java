package xyz.zpayh.myadapter.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;

import com.facebook.drawee.view.SimpleDraweeView;

import xyz.zpayh.adapter.BaseAdapter;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.myadapter.R;
import xyz.zpayh.myadapter.data.ImageCard;
import xyz.zpayh.myadapter.util.FrescoUtil;

/**
 * 文 件 名: AutoLoadAdapter
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/20 22:21
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class AutoLoadAdapter extends BaseAdapter<ImageCard> {

    private int mWidth;

    public AutoLoadAdapter(Context context) {
        mWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public int getLayoutRes(int position) {
        return R.layout.item_card;
    }

    @Override
    public void convert(BaseViewHolder holder, ImageCard data, int index) {
        holder.setText(R.id.tv_image_title,data.mImageTitle);
        FrescoUtil.setWrapAndResizeImage((SimpleDraweeView) holder.find(R.id.iv_bg),data.mImageResId,mWidth);
    }

    @Override
    public void convertHead(BaseViewHolder holder, @LayoutRes int headLayout, int index) {
        holder.setText(R.id.tv_head,"This is auto load more");
    }

    @Override
    public void bind(BaseViewHolder holder, int viewType) {
        holder.setClickable(R.id.tv_act_title,true)
                .setLongClickable(R.id.tv_act_title,true);
    }
}
