package xyz.zpayh.myadapter.adapter;

import androidx.annotation.LayoutRes;

import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.ExpandableAdapter;
import xyz.zpayh.myadapter.R;

/**
 * Created by Administrator on 2016/12/23.
 */

public class MyExpandableAdapter extends ExpandableAdapter {

    @Override
    public void bind(BaseViewHolder holder, int layoutRes) {
        holder.setClickable(R.id.details,true);
    }

    @Override
    public void convertHead(BaseViewHolder holder, @LayoutRes int headLayout, int index) {
        holder.setText(R.id.tv_head,"这是图片展");
    }
}
