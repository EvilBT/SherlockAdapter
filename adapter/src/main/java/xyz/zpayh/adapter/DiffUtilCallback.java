package xyz.zpayh.adapter;

/**
 * 文 件 名: DiffUtilCallback
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/3/31 16:46
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public abstract class DiffUtilCallback<T> {

    /**
     * 判断是否为同一个对象
     */
    public abstract boolean areItemsTheSame(T oldItem, T newItem);

    /**
     * 如果{@link DiffUtilCallback#areItemsTheSame(Object, Object)}返回true，则会调用此方法判断内容是否发生改变
     */
    public abstract boolean areContentsTheSame(T oldItem, T newItem);

    public Object getChangePayload(T oldItem, T newItem){
        return null;
    }
}
