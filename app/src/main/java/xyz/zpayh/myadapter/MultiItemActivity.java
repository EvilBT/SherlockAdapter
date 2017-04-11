package xyz.zpayh.myadapter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.adapter.OnItemLongClickListener;
import xyz.zpayh.myadapter.adapter.MyMultiAdapter;
import xyz.zpayh.myadapter.data.Image;
import xyz.zpayh.myadapter.data.Text;

public class MultiItemActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyMultiAdapter mAdapter;
    private List<IMultiItem> mData;

    private GridLayoutManager mGridLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    private boolean mIsGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_item);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGridLayoutManager = new GridLayoutManager(this,3);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mIsGrid = true;
        mAdapter = new MyMultiAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull View view, final int adapterPosition) {
                new AlertDialog.Builder(MultiItemActivity.this)
                        .setTitle("是否删除第"+adapterPosition+"项")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.removeData(adapterPosition);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
                return true;
            }
        });
        mData = new ArrayList<>();
        initGridData();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setData(mData);
                        refreshLayout.setRefreshing(false);
                    }
                },500);
            }
        });
    }

    private void initGridData() {
        mData.clear();
        String[] list = getResources().getStringArray(R.array.list);
        for (int i = 0; i < list.length; i++) {
            mData.add(new Text(list[i],i%3+1));
        }

        int width = getResources().getDisplayMetrics().widthPixels / mGridLayoutManager.getSpanCount();

        mData.add(2,new Image(R.drawable.girl,width));
        mData.add(2,new Image(R.drawable.girl,width,2));
        mData.add(2,new Image(R.drawable.easter_eggs,width,2));
        mData.add(2,new Image(R.drawable.sunset,width,2));
        mData.add(4,new Image(R.drawable.adult,width,3));
        mData.add(7,new Image(R.drawable.sunset,width));

        mAdapter.setData(mData);
    }

    private void initStaggeredGridData() {
        mData.clear();
        String[] list = getResources().getStringArray(R.array.list);
        for (int i = 0; i < list.length; i++) {
            mData.add(new Text(list[i],i%3+1));
        }

        int width = getResources().getDisplayMetrics().widthPixels / mStaggeredGridLayoutManager.getSpanCount();

        mData.add(2,new Image(R.drawable.girl,width));
        mData.add(2,new Image(R.drawable.girl,width));
        mData.add(2,new Image(R.drawable.easter_eggs,width));
        mData.add(2,new Image(R.drawable.adult,width));
        mData.add(2,new Image(R.drawable.sunset,width));
        mData.add(2,new Image(R.drawable.easter_eggs,width));
        mData.add(2,new Image(R.drawable.adult,width));
        mData.add(7,new Image(R.drawable.sunset,width));

        mAdapter.setData(mData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_grid:
                if (mIsGrid){
                    return true;
                }
                mIsGrid = true;
                mRecyclerView.setLayoutManager(mGridLayoutManager);
                mAdapter.onAttachedToRecyclerView(mRecyclerView);
                initGridData();
                return true;
            case R.id.action_staggered:
                if (!mIsGrid){
                    return true;
                }
                mIsGrid = false;
                mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
                initStaggeredGridData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multiitem_menu, menu);
        return true;
    }
}
