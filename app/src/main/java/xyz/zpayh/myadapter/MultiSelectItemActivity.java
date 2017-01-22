package xyz.zpayh.myadapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.adapter.IMultiSelectItem;
import xyz.zpayh.adapter.OnItemCheckedChangeListener;
import xyz.zpayh.myadapter.adapter.MyMultiSelectAdapter;
import xyz.zpayh.myadapter.data.NoSelectCard;
import xyz.zpayh.myadapter.data.SelectorCard;

public class MultiSelectItemActivity extends AppCompatActivity {

    MyMultiSelectAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_multi_select_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedList();
            }
        });

        findViewById(R.id.selectAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 全选
                adapter.selectAll();
            }
        });
        findViewById(R.id.unselectAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 全不选
                adapter.clearSelectAll();
            }
        });

        adapter = new MyMultiSelectAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        //屏障默认的Change动画，默认的Change动画会闪烁一下
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        adapter.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
            @Override
            public void onItemCheck(@NonNull View view, boolean isChecked, int adapterPosition) {
                final int id = view.getId();
                if (id == R.id.checkbox){

                    IMultiSelectItem selectorItem = adapter.getData(adapterPosition);
                    if (selectorItem != null) {
                        final int selectedSize = adapter.getSelectedItems().size();
                        if (isChecked && selectedSize > 9){
                            Toast.makeText(MultiSelectItemActivity.this, "您最多只能选择9个", Toast.LENGTH_SHORT).show();
                            selectorItem.setChecked(false);
                            adapter.notifyItemChanged(adapterPosition);
                        }
                    }


                }
            }
        });

        List<IMultiSelectItem> data = new ArrayList<>(20);
        for (int i = 0; i < 40; i++) {
            SelectorCard card = new SelectorCard(i);
            data.add(card);
        }

        //添加一个不可选中的数据在列表上
        NoSelectCard card = new NoSelectCard("我跟他们不一样");
        data.add(0,card);

        adapter.setData(data);
    }

    private void getSelectedList() {
        //拿到全部选中的data
        List<IMultiSelectItem> list = adapter.getSelectedItems();
        for (IMultiSelectItem selectorItem : list) {
            if (selectorItem instanceof SelectorCard){
                Log.d("MultiSelectorItem",((SelectorCard) selectorItem).getData()+"被选中了");
            }
        }
    }

}
