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
