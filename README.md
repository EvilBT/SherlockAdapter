# SherlockAdapter
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[ ![Download](https://api.bintray.com/packages/sherlock/maven/sherlockadapter/images/download.svg) ](https://bintray.com/sherlock/maven/sherlockadapter/_latestVersion)
[![](https://jitpack.io/v/EvilBT/SherlockAdapter.svg)](https://jitpack.io/#EvilBT/SherlockAdapter)

## 新增功能
- 2017-04-12 添加DiffUtil支持，添加StaggeredGridLayout的混合布局实现
- 2017-03-12 添加关闭子展开项上的所有已经可见的子项，即关闭当前展开项所有子项
- 2017-01-22 新添加支持多选item,可实现例如选择多张图片的功能

一个封装了RecyclerView.Adapter一些常用功能的库。
## 封装的功能
- item 的点击事件
- item 的长按事件
- item 的子View对应的点击事件
- item 的子View对应的长按事件
- 自动加载更多功能，内置一个基础的加载更多界面，如果有需要，可以自定义界面
- Empty界面,已经内置了一个基础的Empty界面，如果有需要，可以自定义界面
- Failed界面，已经内置了一个基础的Failed界面，如果有需要，可以自定义界面
- 支持添加任意数量的HeadLayout
- 支持添加任意数量的FootLayout
- 支持多布局数据界面
- 支持伸缩子项，理论上无层次限制
- 新添加支持多选item,可实现例如选择多张图片的功能

## 注意事项
先说注意事项，一般来讲，由于SherlockAdapter采用LayoutRes的值来作为ItemViewType返回，而ItemViewType是用来区分不同的Item的，所以如果不是同种Item，就不要使用同一个Layout文件，例如头部HeadLayout跟ItemLayout的布局是一样的情况下，就复制多一个Layout出来就行，不要共用一个Layout。

## 开始配置
**Step 1.**  Add the dependency
``` gradle
dependencies {
    compile 'xyz.zpayh:sherlockadapter:1.0.6'
}
```

## 新增功能使用方法
添加DiffUtil支持,详情参考Demo中的[`ExpandableActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/ExpandableActivity.java):
```java

public class ExpandableActivity extends AppCompatActivity {
    // ......
    private void initView() {
        // ......
        mAdapter.setCallback(new DiffUtilCallback<IMultiItem>() {
            @Override
            public boolean areItemsTheSame(IMultiItem oldItem, IMultiItem newItem) {
                //判断是否为同一条数据 是就返回true,否则返回false。
                if (oldItem instanceof ImageLabel && newItem instanceof ImageLabel){
                    return TextUtils.equals(((ImageLabel) oldItem).getData(),
                            ((ImageLabel) newItem).getData());
                }
                if (oldItem instanceof Card && newItem instanceof Card){
                    return ((Card) oldItem).getData().mImageResId == ((Card) newItem).getData().mImageResId;
                }
                return false;
            }

            @Override
            public boolean areContentsTheSame(IMultiItem oldItem, IMultiItem newItem) {
                //当上面的 areItemsTheSame 返回ture是会进一步调用此方法，进一步确定
                //同一条数据的内容是否发生变化
                if (oldItem instanceof ImageLabel && newItem instanceof ImageLabel){
                    return true;
                }
                if (oldItem instanceof Card && newItem instanceof Card){
                    return TextUtils.equals(((Card) oldItem).getData().mImageTitle,
                            ((Card) newItem).getData().mImageTitle);
                }
                return false;
            }
        });
    }
}
```
效果:

![伸缩子项](https://raw.githubusercontent.com/EvilBT/SherlockAdapter/master/gif/expandable.gif)

## 使用方法

一般用法，继承BaseAdapter<T>实现三个抽象方法即可：
``` java
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder>{    
    
    \\....
    
     /**
     * 返回布局layout
     */
    @LayoutRes
    public abstract int getLayoutRes(int index);

    /**
     * 在这里设置显示
     */
    public abstract void convert(BaseViewHolder holder, T data, int index);

    /**
     * 开启子view的点击事件，或者其他监听
     */
    public abstract void bind(BaseViewHolder holder,int layoutRes);
}
```
### 添加头部HeadLayout
``` java 
adapter.addHeadLayout(R.layout.item_head);
```
然后在 `convertHead` 里控制显示。
``` java
public class HeadAndFootAdapter extends BaseAdapter<String> {

    @Override
    public void convertHead(BaseViewHolder holder, @LayoutRes int headLayout, int index) {
        holder.setText(R.id.tv_head,"This is Head Layout");
    }
}
```
![添加头部](http://o9qzkbu2x.bkt.clouddn.com/head.png?imageMogr2/auto-orient/thumbnail/300x)
### 添加尾部FootLayout
``` java 
adapter.addFootLayout(R.layout.item_foot);
```
然后在 `convertFoot` 里控制显示。
``` java
public class HeadAndFootAdapter extends BaseAdapter<String> {
    @Override
    public void convertFoot(BaseViewHolder holder, @LayoutRes int footLayout, int index) {
        holder.setText(R.id.tv_foot,"This is Foot Layout");
    }
}
```
具体参考[`HeadAndFootActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/HeadAndFootActivity.java)

![添加尾部](http://o9qzkbu2x.bkt.clouddn.com/foot.png?imageMogr2/auto-orient/thumbnail/300x)
### 设置点击事件
在`bind(BaseViewHolder holder,int layoutRes)`里调用`holder.setClickable(ID,true);`启用item的子view的点击事件，并设置一下`BaseAdapter.setOnItemClickListener()`就可以了，详情参考[`MainActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/MainActivity.java)里的Adapter。如果只设置了点击事件，没有启用子view的点击，则是itemView响应消息。
### 设置长按事件
在`bind(BaseViewHolder holder,int layoutRes)`里调用`holder.setLongClickable(ID,true);`启用item的子view的长按事件，并设置一下`BaseAdapter.setOnItemLongClickListener()`就可以了，如果只设置了点击事件，没有启用子view的点击，则是itemView响应消息。基本使用方法与点击事件类似,具体参考Demo中的[`MultiItemActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/MultiItemActivity.java).
### 开启自动加载更多功能
参考 [`AutoLoadMoreActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/AutoLoadMoreActivity.java) 的代码:
``` java
        //必须设置事件监听与开启auto
        mAdapter.openAutoLoadMore(true);
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //TODO Do something
            }
        });
``` 
![正在自动加载](http://o9qzkbu2x.bkt.clouddn.com/loading.png?imageMogr2/auto-orient/thumbnail/300x)
![完成加载](http://o9qzkbu2x.bkt.clouddn.com/nomore.png?imageMogr2/auto-orient/thumbnail/300x)
![加载失败](http://o9qzkbu2x.bkt.clouddn.com/loadfaild.png?imageMogr2/auto-orient/thumbnail/300x)
![没有数据](http://o9qzkbu2x.bkt.clouddn.com/empty.png?imageMogr2/auto-orient/thumbnail/300x)
### 支持多布局
如果布局管理器是*GridLayoutManager*，则简单继承*BaseMultiAdapter*抽象类，数据类型实现[`IMultiItem`](https://github.com/EvilBT/SherlockAdapter/blob/master/adapter/src/main/java/xyz/zpayh/adapter/IMultiItem.java)接口(可以简单继承[`DefaultMultiItem`](https://github.com/EvilBT/SherlockAdapter/blob/master/adapter/src/main/java/xyz/zpayh/adapter/DefaultMultiItem.java))即可。如果是*StaggeredGridLayoutManager*，则需要再实现[`IFullSpan`](https://github.com/EvilBT/SherlockAdapter/blob/master/adapter/src/main/java/xyz/zpayh/adapter/IFullSpan.java)接口，
具体参考Demo中的[`MultiItemActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/MultiItemActivity.java)

![GridLayoutManager多布局](http://o9qzkbu2x.bkt.clouddn.com/mgrid.png?imageMogr2/auto-orient/thumbnail/300x)
![StaggeredGridLayoutManager多布局](http://o9qzkbu2x.bkt.clouddn.com/ms.png?imageMogr2/auto-orient/thumbnail/300x)
![切换布局管理器](http://o9qzkbu2x.bkt.clouddn.com/chose.png?imageMogr2/auto-orient/thumbnail/300x)
### 支持伸缩子项
继承BaseExpandableAdapter，如果有可子项需要伸缩，数据类型实现*IExpandable*(可以简单继承[`DefaultExpandable`](https://github.com/EvilBT/SherlockAdapter/blob/master/adapter/src/main/java/xyz/zpayh/adapter/DefaultExpandable.java))，子项数据类型实现*IMultiItem*，如果
没有子项可伸缩，则数据类型实现*IMultiItem*即可，如果子项也有它的子项，则子项也需要实现*IExpandable*，子项的子项数据类型
实现*IMultiItem*接口。详情参考Demo中的[`ExpandableActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/ExpandableActivity.java)

![伸缩子项](https://raw.githubusercontent.com/EvilBT/SherlockAdapter/master/gif/expandable.gif)
### 多选列表
继承BaseMultiSelectAdapter，数据类型实现*IMultiSelectItem*接口(可以简单继承[`DefaultMultiSelectItem`](https://github.com/EvilBT/SherlockAdapter/blob/master/adapter/src/main/java/xyz/zpayh/adapter/DefaultMultiSelectItem.java))即可，具体可以参考Demo中[`MultiSelectItemActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/MultiSelectItemActivity.java)的实现方式。

![多选列表](http://o9qzkbu2x.bkt.clouddn.com/9.png?imageMogr2/auto-orient/thumbnail/300x)

更多细节请下载Demo查看源代码。

## License

> Copyright 2016 陈志鹏
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
      http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
