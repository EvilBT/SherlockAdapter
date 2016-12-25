package xyz.zpayh.adapter;

/**
 * 文 件 名: BaseMultiAdapter
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/25 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public abstract class BaseMultiAdapter<T extends IMultiItem> extends BaseAdapter<T> {

    @Override
    public int getLayoutRes(int index) {
        final T data = mData.get(index);
        return data.getLayoutRes();
    }
}
