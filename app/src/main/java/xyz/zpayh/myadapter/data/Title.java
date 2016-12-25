package xyz.zpayh.myadapter.data;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.adapter.IExpandable;
import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.myadapter.R;

/**
 * 文 件 名: Title
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/23 19:42
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class Title implements IExpandable{
    private boolean mExpandable;

    private final List<IMultiItem> mItems;

    public String mTitle;

    public Title(String title) {
        mTitle = title;
        mItems = new ArrayList<>();
    }

    public void add(IMultiItem item){
        mItems.add(item);
    }

    @Override
    public boolean isExpandable() {
        return mExpandable;
    }

    @Override
    public void setExpandable(boolean expandable) {
        mExpandable = expandable;
    }

    @Override
    public List<IMultiItem> getSubItems() {
        return mItems;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_title;
    }

    @Override
    public int getViewType() {
        return Constant.TITLE;
    }

    @Override
    public int getSpanSize() {
        return 0;
    }
}
