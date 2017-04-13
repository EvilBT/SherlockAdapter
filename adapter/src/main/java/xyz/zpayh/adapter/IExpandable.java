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

import java.util.List;

/**
 * 文 件 名: IExpandable
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/25 02:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public interface IExpandable extends IMultiItem{

    /**
     * 用来判断区分是否展开
     * @return true 表示是展开状态， false 表示是关闭状态
     */
    boolean isExpandable();

    /**
     * 设置展开状态
     * @param expandable true 表示是展开状态， false 表示是关闭状态
     */
    void setExpandable(boolean expandable);

    /**
     * 返回可以展开的子列表
     * @return 返回子列表
     */
    List<IMultiItem> getSubItems();
}
