package xyz.zpayh.adapter;

import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

/**
 * 文 件 名: BaseViewHolder
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/20 23:17
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 * 提供操作View的一些方法，如{@link #setVisibility(int, int)}，{@link #setClickable(int, boolean)}等
 */
@SuppressWarnings("unchecked")
public class BaseViewHolder extends RecyclerView.ViewHolder {

    private Object[] mIdsAndViews = new Object[0];

    private OnItemClickListener mOnItemClickListener;

    private boolean mInitClickListener;

    int mIndex;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(v,mIndex);
            }
        }
    };

    public BaseViewHolder(View itemView) {
        super(itemView);
        mInitClickListener = false;
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;

        if (onItemClickListener != null && !mInitClickListener){
            mInitClickListener = true;
            this.itemView.setOnClickListener(mOnClickListener);
        }
    }

    public int getIndex(){
        return mIndex;
    }

    /**
     * 设置响应点击事件，如果设置了clickable为true的话，在{@link BaseAdapter#setOnItemClickListener(OnItemClickListener)}
     * 中会得到响应事件的回调,详情参考{@link BaseAdapter#setOnItemClickListener(OnItemClickListener)}
     * @param id 响应点击事件的View Id
     * @param clickable true响应点击事件，false不响应点击事件
     */
    public BaseViewHolder setClickable(@IdRes int id, boolean clickable){
        View view = find(id);
        if (view != null){
            if (clickable){
                view.setOnClickListener(mOnClickListener);
            }else{
                view.setOnClickListener(null);
            }
        }
        return this;
    }

    public BaseViewHolder setText(@IdRes int textId, String text){
        TextView textView = find(textId);
        if (textView != null){
            textView.setText(text);
        }
        return this;
    }

    public BaseViewHolder setText(@IdRes int textId, @StringRes int stringId){
        String text = itemView.getResources().getString(stringId);
        return setText(textId,text);
    }

    /**
     * 设置View的visibility状态
     * @param id View id
     * @param visibility 可以设置为{@link View#GONE},{@link View#VISIBLE}或者{@link View#INVISIBLE}
     */
    public BaseViewHolder setVisibility(@IdRes int id, int visibility){
        View view = find(id);
        if (view != null){
            view.setVisibility(visibility);
        }
        return this;
    }

    /**
     * 根据当前id查找对应的View控件
     * @param viewId View id
     * @param <T> 子View的具体类型
     * @return 返回当前id对应的子View控件，如果没有，则返回null
     */
    @CheckResult
    public <T extends View> T find(@IdRes int viewId){
        int indexToAdd = -1;
        for (int i = 0; i < mIdsAndViews.length; i+=2) {
            Integer id = (Integer) mIdsAndViews[i];
            if (id != null && id == viewId){
                return (T) mIdsAndViews[i+1];
            }

            if (id == null){
                indexToAdd = i;
            }
        }

        if (indexToAdd == -1){
            indexToAdd = mIdsAndViews.length;
            mIdsAndViews = Arrays.copyOf(mIdsAndViews,
                    indexToAdd < 2 ? 2 : indexToAdd * 2);
        }

        mIdsAndViews[indexToAdd] = viewId;
        mIdsAndViews[indexToAdd+1] = itemView.findViewById(viewId);
        return (T) mIdsAndViews[indexToAdd+1];
    }
}
