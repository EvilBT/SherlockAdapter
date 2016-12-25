package xyz.zpayh.myadapter.adapter;

import android.support.annotation.LayoutRes;

import xyz.zpayh.adapter.BaseAdapter;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.myadapter.R;

/**
 * 文 件 名: HeadAndFootAdapter
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/12/26 02:06
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class HeadAndFootAdapter extends BaseAdapter<String> {

    @Override
    public int getLayoutRes(int index) {
        return R.layout.item_text;
    }

    @Override
    public void convert(BaseViewHolder holder, String data, int index) {
        holder.setText(R.id.text,data);
    }

    @Override
    public void bind(BaseViewHolder holder, int layoutRes) {

    }

    @Override
    public void convertHead(BaseViewHolder holder, @LayoutRes int headLayout, int index) {
        holder.setText(R.id.tv_head,"这是头部"+(index+1));
    }

    @Override
    public void convertFoot(BaseViewHolder holder, @LayoutRes int footLayout, int index) {
        holder.setText(R.id.tv_foot,"这是尾部"+(index+1));
    }
}
