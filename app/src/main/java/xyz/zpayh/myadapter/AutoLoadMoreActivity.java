package xyz.zpayh.myadapter;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.adapter.OnLoadMoreListener;
import xyz.zpayh.myadapter.adapter.AutoLoadAdapter;
import xyz.zpayh.myadapter.data.ImageCard;

public class AutoLoadMoreActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int LOAD_ADD = 0;
    public static final int LOAD_FAILED = 1;
    public static final int LOAD_COMPLETED = 2;

    private int mState = LOAD_ADD;

    private AutoLoadAdapter mAdapter;

    private List<ImageCard> data;

    private String mTitles[] = {"Adult","Easter Eggs","Girl", "Sunset"};
    private int mImageResId[] = {R.drawable.adult, R.drawable.easter_eggs, R.drawable.girl, R.drawable.sunset};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_auto_load_more);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AutoLoadAdapter(this);
        recyclerView.setAdapter(mAdapter);
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

        mAdapter.addHeadLayout(R.layout.item_head);
        mAdapter.addFootLayout(R.layout.item_foot2);

        // 模拟数据
        data = new ArrayList<>();

        for (int i = 0; i < mTitles.length; i++) {
            ImageCard card = new ImageCard(mImageResId[i],mTitles[i]);
            data.add(card);
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
                },500);
            }
        });

        //必须设置事件监听与开启auto
        mAdapter.openAutoLoadMore(true);
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d("Sherlock","加载更多");
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
                },800);
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
                mAdapter.setData(data);
                mState = LOAD_ADD;
                break;
            case R.id.action_failed:
                setTitle("加载更多失败");
                mAdapter.openAutoLoadMore(true);
                mAdapter.setData(data);
                mState = LOAD_FAILED;
                break;
            case R.id.action_completed:
                setTitle("没有更多数据");
                mAdapter.openAutoLoadMore(true);
                mAdapter.setData(data);
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
