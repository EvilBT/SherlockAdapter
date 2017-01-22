package xyz.zpayh.myadapter.adapter;

import xyz.zpayh.adapter.BaseMultiSelectAdapter;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.myadapter.R;

/**
 * Created by Administrator on 2017/1/22.
 */

public class MyMultiSelectAdapter extends BaseMultiSelectAdapter {

    @Override
    public void bind(BaseViewHolder holder, int layoutRes) {
        holder.setCheckable(R.id.checkbox,true);
    }
}
