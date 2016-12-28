package xyz.zpayh.myadapter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.adapter.OnItemLongClickListener;
import xyz.zpayh.myadapter.adapter.MyMultiAdapter;
import xyz.zpayh.myadapter.data.Image;
import xyz.zpayh.myadapter.data.Text;

public class MultiItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_item);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        final MyMultiAdapter adapter = new MyMultiAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull View view, final int adapterPosition) {
                new AlertDialog.Builder(MultiItemActivity.this)
                        .setTitle("是否删除"+adapterPosition+"项")
                        .setNegativeButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.removeData(adapterPosition);
                            }
                        })
                        .setPositiveButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
                return true;
            }
        });

        final List<IMultiItem> data = new ArrayList<>();

        String[] list = getResources().getStringArray(R.array.list);
        for (int i = 0; i < list.length; i++) {
            data.add(new Text(list[i],i%3+1));
        }

        data.add(2,new Image(R.drawable.base_load_failed));
        data.add(2,new Image(R.drawable.base_empty));
        data.add(4,new Image(R.drawable.base_error));
        data.add(7,new Image(R.drawable.base_load_failed));

        adapter.setData(data);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setData(data);
                        refreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });
    }

}
