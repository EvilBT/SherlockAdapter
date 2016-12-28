# SherlockAdapter
[![](https://jitpack.io/v/EvilBT/SherlockAdapter.svg)](https://jitpack.io/#EvilBT/SherlockAdapter)

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

## 注意事项
先说注意事项，一般来讲，由于SherlockAdapter采用LayoutRes的值来作为ItemViewType返回，而ItemViewType是用来区分不同的Item的，所以如果不是同种Item，就不要使用同一个Layout文件，例如头部HeadLayout跟ItemLayout的布局是一样的情况下，就复制多一个Layout出来就行，不要共用一个Layout。

## 开始配置
**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```
**Step 2.** Add the dependency
```
dependencies {
    compile 'com.github.EvilBT:SherlockAdapter:v1.0.1'
}
```

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
        holder.setText(R.id.tv_head,"这是头部"+(index+1));
    }
}
```
![添加头部](http://o9qzkbu2x.bkt.clouddn.com/8.jpg?imageMogr2/auto-orient/thumbnail/300x)
### 添加尾部FootLayout
``` java 
adapter.addFootLayout(R.layout.item_foot);
```
然后在 `convertFoot` 里控制显示。
``` java
public class HeadAndFootAdapter extends BaseAdapter<String> {
    @Override
    public void convertFoot(BaseViewHolder holder, @LayoutRes int footLayout, int index) {
        holder.setText(R.id.tv_foot,"这是尾部"+(index+1));
    }
}
```
![添加尾部](http://o9qzkbu2x.bkt.clouddn.com/7.jpg?imageMogr2/auto-orient/thumbnail/300x)
具体参考[`HeadAndFootActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/HeadAndFootActivity.java)
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
![正在自动加载](http://o9qzkbu2x.bkt.clouddn.com/4.jpg?imageMogr2/auto-orient/thumbnail/300x)
![完成加载](http://o9qzkbu2x.bkt.clouddn.com/1.jpg?imageMogr2/auto-orient/thumbnail/300x)
![加载失败](http://o9qzkbu2x.bkt.clouddn.com/3.jpg?imageMogr2/auto-orient/thumbnail/300x)
![没有数据](http://o9qzkbu2x.bkt.clouddn.com/2.jpg?imageMogr2/auto-orient/thumbnail/300x)
### 支持多布局
继承*BaseMultiAdapter*抽象类，数据类型实现*IMultiItem*接口即可。
具体参考Demo中的[`MultiItemActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/MultiItemActivity.java)
![多布局](http://o9qzkbu2x.bkt.clouddn.com/5.jpg?imageMogr2/auto-orient/thumbnail/300x)
### 支持伸缩子项
继承BaseExpandableAdapter，如果有可子项需要伸缩，数据类型实现*IExpandable*，子项数据类型实现*IMultiItem*，如果
没有子项可伸缩，则数据类型实现*IMultiItem*即可，如果子项也有它的子项，则子项也需要实现*IExpandable*，子项的子项数据类型
实现*IMultiItem*接口。详情参考Demo中的[`ExpandableActivity`](https://github.com/EvilBT/SherlockAdapter/blob/master/app/src/main/java/xyz/zpayh/myadapter/ExpandableActivity.java)
更多细节请下载Demo查看源代码。
![伸缩子项](http://o9qzkbu2x.bkt.clouddn.com/6.jpg?imageMogr2/auto-orient/thumbnail/300x)
## License

> Copyright (C) 2016 zpayh.
     http://zpayh.xyz
>
>  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
>
>     http://www.apache.org/licenses/LICENSE-2.0
>
>  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
