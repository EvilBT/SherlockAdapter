package xyz.zpayh.adapter;


import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 */

public abstract class BaseExpandableAdapter extends BaseMultiAdapter<IMultiItem> {

    public void expand(int adapterPosition){
        final IMultiItem data = getData(adapterPosition);
        if (!(data instanceof IExpandable)){
            return;
        }
        final IExpandable expandable = (IExpandable) data;
        if (expandable.isExpandable()){
            return;
        }

        final List<IMultiItem> subItems = expandable.getSubItems();
        if (subItems == null || subItems.isEmpty()){
            return;
        }

        expandable.setExpandable(true);

        mData.addAll(adapterPosition-getHeadSize()+1,subItems);
        notifyItemChanged(adapterPosition);
        notifyItemRangeInserted(adapterPosition+1,subItems.size());
    }

    public void collapse(int adapterPosition){
        final IMultiItem data = getData(adapterPosition);
        if (!(data instanceof IExpandable)){
            return;
        }
        final IExpandable expandable = (IExpandable) data;
        if (!expandable.isExpandable()){
            return;
        }

        final List<IMultiItem> subItems = expandable.getSubItems();
        if (subItems == null || subItems.isEmpty()){
            return;
        }

        expandable.setExpandable(false);

       // mData.removeAll(subItems);
        int removeSize = removeAll(subItems);

        notifyItemChanged(adapterPosition);
        notifyItemRangeRemoved(adapterPosition+1,removeSize);
    }

    private int removeAll(List<IMultiItem> subItems){
        int size = subItems.size();
        for (IMultiItem subItem : subItems) {
            if (subItem == null || !(subItem instanceof IExpandable))
                continue;
            IExpandable expandable = (IExpandable) subItem;
            if (!expandable.isExpandable())
                continue;
            expandable.setExpandable(false);
            final List<IMultiItem> items = expandable.getSubItems();
            if (items == null || items.isEmpty()){
                continue;
            }
            size += removeAll(items);
        }
        mData.removeAll(subItems);
        return size;
    }
}
