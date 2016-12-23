package xyz.zpayh.adapter;

/**
 * Created by Administrator on 2016/12/23.
 */

public abstract class BaseMultiAdapter<T extends IMultiItem> extends BaseAdapter<T> {

    @Override
    public int getLayoutRes(int index) {
        final T data = mData.get(index);
        return data.getLayoutRes();
    }
}
