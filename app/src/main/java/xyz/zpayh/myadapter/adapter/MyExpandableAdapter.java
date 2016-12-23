package xyz.zpayh.myadapter.adapter;

import xyz.zpayh.adapter.BaseExpandableAdapter;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.myadapter.R;
import xyz.zpayh.myadapter.data.Contanst;
import xyz.zpayh.myadapter.data.Details;
import xyz.zpayh.myadapter.data.Title;

/**
 * Created by Administrator on 2016/12/23.
 */

public class MyExpandableAdapter extends BaseExpandableAdapter {


    @Override
    public void convert(BaseViewHolder holder, IMultiItem data, int index) {
        if (data.getViewType() == Contanst.DETAILS){
            Details details = (Details) data;
            holder.setText(R.id.content,details.mContent);
        }else if (data.getViewType() == Contanst.TITLE){
            Title title = (Title) data;
            holder.setText(R.id.title,title.mTitle);
        }
    }

    @Override
    public void bind(BaseViewHolder holder, int layoutRes) {
        holder.setClickable(R.id.details,true);
    }
}
