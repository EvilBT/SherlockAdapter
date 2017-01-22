package xyz.zpayh.myadapter.data;

import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.DefaultMultiSelectItem;
import xyz.zpayh.myadapter.R;

/**
 * Created by Administrator on 2017/1/22.
 */

public class SelectorCard extends DefaultMultiSelectItem<Integer> {

    public SelectorCard(int data) {
        //只需要设置好布局文件，以及对应的实现Checkable接口的View的id即可。
        super(R.layout.item_select,R.id.checkbox,data);
    }

    @Override
    public void convert(BaseViewHolder holder) {
        //根据是否选中更新文本
        holder.setText(R.id.text,isChecked()?getData()+"被选中":getData()+"未选中");
    }
}
