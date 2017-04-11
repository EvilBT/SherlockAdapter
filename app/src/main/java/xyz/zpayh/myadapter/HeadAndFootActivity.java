package xyz.zpayh.myadapter;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.adapter.DiffUtilCallback;
import xyz.zpayh.myadapter.adapter.HeadAndFootAdapter;
import xyz.zpayh.myadapter.data.ImageCard;

public class HeadAndFootActivity extends AppCompatActivity {

    private boolean mChangeOrder = true;

    private String mTitles[] = {"Adult","Easter Eggs","Girl", "Sunset"};
    private int mImageResId[] = {R.drawable.adult, R.drawable.easter_eggs, R.drawable.girl, R.drawable.sunset};

    private HeadAndFootAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_and_foot);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HeadAndFootAdapter(this);
        recyclerView.setAdapter(mAdapter);

        mAdapter.addHeadLayout(R.layout.item_head);
        mAdapter.addFootLayout(R.layout.item_foot);
        mAdapter.addFootLayout(R.layout.item_foot2);

        List<ImageCard> data = new ArrayList<>();

        for (int i = 0; i < mTitles.length; i++) {
            ImageCard card = new ImageCard(mImageResId[i],mTitles[i]);
            data.add(card);
        }

        mAdapter.setData(data);

        final SwipeRefreshLayout refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                refresh.setRefreshing(false);
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    private void refresh() {
        if (mChangeOrder){
            List<ImageCard> data = new ArrayList<>();
            for (int i = mTitles.length - 1; i >= 0; i--) {
                ImageCard card = new ImageCard(mImageResId[i],mTitles[i]);
                data.add(card);
            }
            mAdapter.setData(data);
        }else{
            List<ImageCard> data = new ArrayList<>();
            for (int i = 0; i < mTitles.length; i++) {
                ImageCard card = new ImageCard(mImageResId[i],mTitles[i]);
                data.add(card);
            }

            mAdapter.setData(data);
        }
        mChangeOrder = !mChangeOrder;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_anim:
                mAdapter.setCallback(new DiffUtilCallback<ImageCard>() {
                    @Override
                    public boolean areItemsTheSame(ImageCard oldItem, ImageCard newItem) {
                        return oldItem.mImageResId == newItem.mImageResId;
                    }

                    @Override
                    public boolean areContentsTheSame(ImageCard oldItem, ImageCard newItem) {
                        return TextUtils.equals(oldItem.mImageTitle,newItem.mImageTitle);
                    }
                });
                return true;
            case R.id.action_not_anim:
                mAdapter.setCallback(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.head_menu, menu);
        return true;
    }
}
