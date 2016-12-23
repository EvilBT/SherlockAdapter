package xyz.zpayh.adapter;

import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 */

public interface IExpandable extends IMultiItem{

    boolean isExpandable();

    void setExpandable(boolean expandable);

    List<IMultiItem> getSubItems();
}
