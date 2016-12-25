package xyz.zpayh.myadapter.adapter;

import android.widget.ImageView;

import xyz.zpayh.adapter.BaseMultiAdapter;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.myadapter.R;
import xyz.zpayh.myadapter.data.Constant;
import xyz.zpayh.myadapter.data.Image;
import xyz.zpayh.myadapter.data.Text;

/**
 * Created by Administrator on 2016/12/23.
 */

public class MyMultiAdapter extends BaseMultiAdapter<IMultiItem> {
    @Override
    public void convert(BaseViewHolder holder, IMultiItem data, int index) {
        if (data.getViewType() == Constant.TEXT){
            holder.setText(R.id.text,((Text)data).mText);
        }else if (data.getViewType() == Constant.IMAGE){
            ((ImageView)holder.find(R.id.image)).setImageResource(((Image)data).mImageId);
        }
    }

    @Override
    public void bind(BaseViewHolder holder, int layoutRes) {

    }
}
