package xyz.zpayh.myadapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.adapter.OnItemClickListener;
import xyz.zpayh.adapter.OnLoadMoreListener;
import xyz.zpayh.myadapter.adapter.MyAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mRefreshLayout;

    private MyAdapter mMyAdapter;

    private int mLoadState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,1));
        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);

        String[] list = getResources().getStringArray(R.array.list);
        final List<String> data = new ArrayList<>(list.length);
        for (String s : list) {
            data.add(s);
        }

        mMyAdapter.addHeadLayout(android.R.layout.simple_list_item_1);
        mMyAdapter.addFootLayout(android.R.layout.simple_list_item_1);

        mMyAdapter.openAutoLoadMore(true);
        mMyAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d("MainActivity", "加载更多");

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mLoadState == 0){
                            mMyAdapter.addData(data);
                        }else if(mLoadState == 1){
                            mMyAdapter.loadCompleted();
                        }else if (mLoadState == 2){
                            mMyAdapter.loadFailed();
                        }
                    }
                },2000);
            }
        });
        mMyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {

                /*if (view instanceof TextView){
                    Toast.makeText(view.getContext(), ((TextView)view).getText(), Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMyAdapter.setData(data);
                        mRefreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });

        mMyAdapter.setData(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_empty) {
            mMyAdapter.setData(null);
            return true;
        }

        if (id == R.id.action_add){
            mMyAdapter.openAutoLoadMore(true);
            mLoadState = 0;
            return true;
        }

        if (id == R.id.action_completed){
            mMyAdapter.openAutoLoadMore(true);
            mLoadState = 1;
            return true;
        }

        if (id == R.id.action_failed){
            mMyAdapter.openAutoLoadMore(true);
            mLoadState = 2;
            return true;
        }

        if (id == R.id.action_close){
            mMyAdapter.openAutoLoadMore(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
