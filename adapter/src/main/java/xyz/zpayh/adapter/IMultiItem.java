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

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 文 件 名: IMultiItem
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/25 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 *  移除了getViewType接口，添加
 */

public interface IMultiItem {

    /**
     * 不同类型的item请使用不同的布局文件，
     * 即使它们的布局是一样的，也要copy多一份出来。
     * @return 返回item对应的布局id
     */
    @LayoutRes int getLayoutRes();

    /**
     * 进行数据处理，显示文本，图片等内容
     * @param holder Holder Helper
     */
    void convert(BaseViewHolder holder);

    /**
     * 在布局为{@link GridLayoutManager}时才有用处，
     * 返回当前布局所占用的SpanSize
     * @return 如果返回的SpanSize 小于或等于 0 或者 大于 {@link GridLayoutManager#getSpanCount()}
     *  则{@link BaseAdapter} 会在{@link BaseAdapter#onAttachedToRecyclerView(RecyclerView)}
     *  自适应为1或者{@link GridLayoutManager#getSpanCount()},详情参考{@link BaseAdapter#onAttachedToRecyclerView(RecyclerView)}
     */
    int getSpanSize();
}
