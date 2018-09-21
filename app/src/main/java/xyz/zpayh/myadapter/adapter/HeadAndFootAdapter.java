package xyz.zpayh.myadapter.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;

import com.facebook.drawee.view.SimpleDraweeView;

import xyz.zpayh.adapter.BaseAdapter;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.myadapter.R;
import xyz.zpayh.myadapter.data.ImageCard;
import xyz.zpayh.myadapter.util.FrescoUtil;

/**
 * 文 件 名: HeadAndFootAdapter
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/26 02:06
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class HeadAndFootAdapter extends BaseAdapter<ImageCard> {

    private int mWidth;

    private OnBindListener mOnBindListener;

    public HeadAndFootAdapter(Context context) {
        mWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public void setOnBindListener(OnBindListener onBindListener) {
        this.mOnBindListener = onBindListener;
    }

    @Override
    public int getLayoutRes(int index) {
        return R.layout.item_card;
    }

    @Override
    public void convert(BaseViewHolder holder, ImageCard data, int index) {
        holder.setText(R.id.tv_image_title,data.mImageTitle);
        FrescoUtil.setWrapAndResizeImage((SimpleDraweeView) holder.find(R.id.iv_bg),data.mImageResId,mWidth);
    }

    @Override
    public void bind(BaseViewHolder holder, int layoutRes) {
        if (mOnBindListener != null) {
            mOnBindListener.onBind(holder, layoutRes);
        }
    }

    @Override
    public void convertHead(BaseViewHolder holder, @LayoutRes int headLayout, int index) {
        holder.setText(R.id.tv_head,"This is Head Layout");
    }

    @Override
    public void convertFoot(BaseViewHolder holder, @LayoutRes int footLayout, int index) {
        holder.setText(R.id.tv_foot,"This is Foot Layout");
    }

    public interface OnBindListener {
        void onBind(BaseViewHolder holder, int layoutRes);
    }
}
