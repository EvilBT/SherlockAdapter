package xyz.zpayh.adapter;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/12/19.
 */
@SuppressWarnings("unchecked")
public class BaseViewHolder extends RecyclerView.ViewHolder {

    private Object[] mIdsAndViews = new Object[0];

    private OnItemClickListener mOnItemClickListener;

    private boolean mInitClickListener;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    };

    public BaseViewHolder(View itemView) {
        super(itemView);
        mInitClickListener = false;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;

        if (onItemClickListener != null && !mInitClickListener){
            mInitClickListener = true;
            this.itemView.setOnClickListener(mOnClickListener);
        }
    }

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

    public BaseViewHolder setVisibility(@IdRes int id, int visibility){
        View view = find(id);
        if (view != null){
            view.setVisibility(visibility);
        }
        return this;
    }

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
