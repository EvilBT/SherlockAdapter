/*
 * Copyright 2017 陈志鹏
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.zpayh.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 文 件 名: BaseMultiAdapter
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/01/22 12:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间: 2016/01/23 0:51
 * 修改备注:
 */

public abstract class BaseMultiSelectAdapter extends BaseAdapter<IMultiSelectItem>
    implements MultiSelect {

    private OnItemCheckedChangeListener mOnItemCheckedChangeListener;

    @Override
    public int getLayoutRes(int index) {
        final IMultiItem data = mData.get(index);
        return data.getLayoutRes();
    }

    @Override
    public void convert(BaseViewHolder holder, IMultiSelectItem data, int index) {
        holder.setChecked(data.getCheckableViewId(),data.isChecked());
        data.convert(holder);
    }

    @Override
    public void clearSelectAll() {
        for (IMultiSelectItem selectorItem : mData) {
            selectorItem.setChecked(false);
        }
        doNotifyDataSetChanged();
    }

    @Override
    public void selectAll() {
        for (IMultiSelectItem selectorItem : mData) {
            selectorItem.setChecked(true);
        }
        doNotifyDataSetChanged();
    }

    @Override
    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener onCheckedChangeListener) {
        mOnItemCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public List<IMultiSelectItem> getSelectedItems() {
        List<IMultiSelectItem> selectedItems = new ArrayList<>();
        for (IMultiSelectItem selectorItem : mData) {
            if (selectorItem.isChecked() && selectorItem.getCheckableViewId() != View.NO_ID){
                selectedItems.add(selectorItem);
            }
        }
        return selectedItems;
    }

    @Override
    protected void bindData(BaseViewHolder baseViewHolder, int layoutRes) {
        baseViewHolder.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
            @Override
            public void onItemCheck(@NonNull View view, boolean isChecked, int adapterPosition) {
                final int id = view.getId();
                final IMultiSelectItem item = getData(adapterPosition);
                if (item != null && id == item.getCheckableViewId()){
                    item.setChecked(isChecked);
                    doNotifyItemChanged(adapterPosition);
                }
                if (mOnItemCheckedChangeListener != null){
                    mOnItemCheckedChangeListener.onItemCheck(view, isChecked, adapterPosition);
                }
            }
        });
        super.bindData(baseViewHolder, layoutRes);
    }
}
