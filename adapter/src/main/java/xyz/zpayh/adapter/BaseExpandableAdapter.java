package xyz.zpayh.adapter;


import java.util.List;

/**
 * 文 件 名: BaseExpandableAdapter
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/23 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public abstract class BaseExpandableAdapter extends BaseMultiAdapter<IMultiItem> {

    /**
     * 展开下级菜单
     */
    public void expand(int adapterPosition){
        final IMultiItem data = getData(adapterPosition);
        if (!(data instanceof IExpandable)){
            return;
        }
        final IExpandable expandable = (IExpandable) data;
        final List<IMultiItem> subItems = expandable.getSubItems();
        if (subItems == null || subItems.isEmpty()){
            return;
        }


        if (expandable.isExpandable() && mData.containsAll(subItems)){
            return;
        }

        expandable.setExpandable(true);

        mData.addAll(adapterPosition-getHeadSize()+1,subItems);
        notifyItemChanged(adapterPosition);
        notifyItemRangeInserted(adapterPosition+1,subItems.size());
    }

    /**
     * 展开全部下级菜单
     */
    public void expandAll(int adapterPosition){
        final IMultiItem data = getData(adapterPosition);
        if (!(data instanceof IExpandable)){
            return;
        }

        final IExpandable expandable = (IExpandable) data;

        final List<IMultiItem> subItems = expandable.getSubItems();
        if (subItems == null || subItems.isEmpty()){
            return;
        }

        if (expandable.isExpandable() && mData.containsAll(subItems)){
            return;
        }


        expandable.setExpandable(true);

        int size = 0;
        int index = adapterPosition-getHeadSize()+1;
        for (IMultiItem subItem : subItems) {
            size +=addAll(index+size,subItem);
        }
        notifyItemChanged(adapterPosition);
        notifyItemRangeInserted(adapterPosition+1,size);
    }

    private int addAll(int index, IMultiItem item){
        if (item == null){
            return 0;
        }
        int size = 1;
        mData.add(index,item);
        if (!(item instanceof IExpandable)){
            return size;
        }
        final IExpandable expandable = (IExpandable) item;
        final List<IMultiItem> subItems = expandable.getSubItems();
        if (subItems == null || subItems.isEmpty()){
            return size;
        }

        if (expandable.isExpandable() && mData.containsAll(subItems)){
            return size;
        }

        expandable.setExpandable(true);
        for (IMultiItem subItem : subItems) {
            size += addAll(index+size,subItem);
        }
        return size;
    }

    public void collapse(int adapterPosition){
        final IMultiItem data = getData(adapterPosition);
        if (!(data instanceof IExpandable)){
            return;
        }
        final IExpandable expandable = (IExpandable) data;
        final List<IMultiItem> subItems = expandable.getSubItems();
        if (subItems == null || subItems.isEmpty()){
            return;
        }

        if (!expandable.isExpandable() && !mData.containsAll(subItems)){
            return;
        }

        expandable.setExpandable(false);

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
            final List<IMultiItem> items = expandable.getSubItems();
            if (items == null || items.isEmpty()){
                continue;
            }
            if (!expandable.isExpandable() && !mData.containsAll(items))
                continue;
            expandable.setExpandable(false);
            size += removeAll(items);
        }
        mData.removeAll(subItems);
        return size;
    }
}
