package xyz.zpayh.myadapter.data;

import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.DefaultMultiSelectItem;
import xyz.zpayh.myadapter.R;

/**
 * Created by Administrator on 2017/1/22.
 */

public class SelectorCard extends DefaultMultiSelectItem<Integer> {

    public SelectorCard(int index) {
        super(R.layout.item_select,index);
    }

    @Override
    public void convert(BaseViewHolder holder) {
        holder.setText(R.id.text,isChecked()?getData()+"被选中":getData()+"未选中")
                .setChecked(R.id.checkbox,isChecked());
    }
}
