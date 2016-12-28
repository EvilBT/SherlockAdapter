package xyz.zpayh.myadapter;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.adapter.OnLoadMoreListener;
import xyz.zpayh.myadapter.adapter.MyAdapter;

public class AutoLoadMoreActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int LOAD_ADD = 0;
    public static final int LOAD_FAILED = 1;
    public static final int LOAD_COMPLETED = 2;

    private boolean mShowLoadMore;

    private int mState = LOAD_ADD;

    private MyAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_auto_load_more);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyAdapter();
        recyclerView.setAdapter(mAdapter);
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

        // 模拟数据
        final List<String> data = new ArrayList<>();

        String[] list = getResources().getStringArray(R.array.list);
        for (String s : list) {
            data.add(s);
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //模拟刷新
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        mAdapter.setData(data);
                    }
                },1500);
            }
        });

        //必须设置事件监听与开启auto
        mAdapter.openAutoLoadMore(true);
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //模拟加载更多
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mState == LOAD_ADD){
                            mAdapter.addData(data);
                        }else if (mState == LOAD_COMPLETED){
                            mAdapter.loadCompleted();
                        }else if (mState == LOAD_FAILED){
                            mAdapter.loadFailed();
                        }
                    }
                },1500);
            }
        });

        findViewById(R.id.action_add).setOnClickListener(this);
        findViewById(R.id.action_failed).setOnClickListener(this);
        findViewById(R.id.action_completed).setOnClickListener(this);
        findViewById(R.id.action_empty).setOnClickListener(this);
        findViewById(R.id.action_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_add:
                setTitle("加载更多");
                mAdapter.openAutoLoadMore(true);
                mState = LOAD_ADD;
                break;
            case R.id.action_failed:
                setTitle("加载更多失败");
                mAdapter.openAutoLoadMore(true);
                mState = LOAD_FAILED;
                break;
            case R.id.action_completed:
                setTitle("没有更多数据");
                mAdapter.openAutoLoadMore(true);
                mState = LOAD_COMPLETED;
                break;
            case R.id.action_empty:
                setTitle("没有数据");
                mAdapter.setData(null);
                break;
            case R.id.action_close:
                mAdapter.openAutoLoadMore(false);
                break;
        }
    }
}
