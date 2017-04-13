package xyz.zpayh.myadapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.zpayh.adapter.DefaultExpandable;
import xyz.zpayh.adapter.DiffUtilCallback;
import xyz.zpayh.adapter.IExpandable;
import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.adapter.OnItemClickListener;
import xyz.zpayh.adapter.OnItemLongClickListener;
import xyz.zpayh.myadapter.adapter.MyExpandableAdapter;
import xyz.zpayh.myadapter.data.Card;
import xyz.zpayh.myadapter.data.ImageCard;
import xyz.zpayh.myadapter.data.ImageLabel;

public class ExpandableActivity extends AppCompatActivity {

    private String mLabels[] = {"Animal","Beauty","Scenery","Tranquil"};

    // Animal Card
    private int mAnimalIds[] = {R.drawable.fuchs,R.drawable.horses};
    private String mAnimalTitles[] = {"Fuchs","Horses"};

    // Beauty Card
    private int mBeautyIds[] = {R.drawable.adult, R.drawable.girl,
            R.drawable.girl1,R.drawable.girl2,R.drawable.smile,R.drawable.fashion};
    private String mBeautyTitles[] = {"Adult","Girl", "Girl MM",
            "MM Girl","Smile","fashion"};

    // Scenery Card
    private int mSceneryIds[] = {R.drawable.architecture,R.drawable.denmark,R.drawable.sunset};
    private String mSceneryTitles[] = {"Architecture","Denmark","Sunset"};

    // Tranquil Card
    private int mTranquilIds[] = {R.drawable.easter_eggs,R.drawable.notes};
    private String mTranquilTitles[] = {"Easter Eggs","Notes"};

    private RecyclerView mRecyclerView;
    private MyExpandableAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    private boolean mIsGrid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mGridLayoutManager = new GridLayoutManager(this,2);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mAdapter = new MyExpandableAdapter();

        //mAdapter.setAlwaysShowFoot(true);
        //mAdapter.setAlwaysShowHead(true);

        mAdapter.addHeadLayout(R.layout.item_head);
        mAdapter.addFootLayout(R.layout.item_foot2);

        // 设置DiffUtil效果
        mAdapter.setCallback(new DiffUtilCallback<IMultiItem>() {
            @Override
            public boolean areItemsTheSame(IMultiItem oldItem, IMultiItem newItem) {
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

        mRecyclerView.setAdapter(mAdapter);
        //屏障默认的Change动画
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        // 点击展开/折叠事件
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                if (view.getId() == R.id.label_root){
                    IMultiItem item = mAdapter.getData(adapterPosition);
                    if (item instanceof IExpandable){
                        IExpandable expandable = (IExpandable) item;
                        if (expandable.isExpandable()){
                            mAdapter.collapseAll(adapterPosition);
                        }else{
                            mAdapter.expandAll(adapterPosition);
                        }
                    }
                }
            }
        });

        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull View view, int adapterPosition) {
                mAdapter.removeData(adapterPosition);
                return true;
            }
        });

        final SwipeRefreshLayout refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                refresh.setRefreshing(false);
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        initData();
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
                initData();
                return true;
            case R.id.action_staggered:
                if (!mIsGrid){
                    return true;
                }
                mIsGrid = false;
                mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
                initData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multiitem_menu, menu);
        return true;
    }

    private void refreshData(){

        List<IMultiItem> oldData = mAdapter.getData();
        List<IMultiItem> data = new ArrayList<>();
        data.addAll(oldData);

        Collections.reverse(data);

        DefaultExpandable expandable = (DefaultExpandable) data.get(1);
        Collections.reverse(expandable.getSubItems());
        expandable = (DefaultExpandable) data.get(2);
        Collections.reverse(expandable.getSubItems());

        mAdapter.setData(data);
    }

    private void initData(){
        int width = getResources().getDisplayMetrics().widthPixels / mStaggeredGridLayoutManager.getSpanCount();

        List<IMultiItem> data = new ArrayList<>();

        for (int i = 0; i < mLabels.length; i++) {
            ImageLabel label = new ImageLabel(mLabels[i]);
            data.add(label);
        }

        // Add animals
        List<IMultiItem> animalData = new ArrayList<>(2);
        for (int i = 0; i < mAnimalIds.length; i++) {
            ImageCard cardData = new ImageCard(mAnimalIds[i],mAnimalTitles[i]);
            Card card = new Card(width,cardData);
            animalData.add(card);
        }
        ((DefaultExpandable)data.get(0)).addSubData(animalData);

        // Add beauties
        List<IMultiItem> beautyData = new ArrayList<>(mBeautyIds.length);
        for (int i = 0; i < mBeautyIds.length; i++) {
            ImageCard cardData = new ImageCard(mBeautyIds[i],mBeautyTitles[i]);
            Card card = new Card(width,cardData);
            beautyData.add(card);
        }
        ((DefaultExpandable)data.get(1)).addSubData(beautyData);

        // Add scenery
        List<IMultiItem> sceneryData = new ArrayList<>(mSceneryIds.length);
        for (int i = 0; i < mSceneryIds.length; i++) {
            ImageCard cardData = new ImageCard(mSceneryIds[i],mSceneryTitles[i]);
            Card card = new Card(width,cardData);
            sceneryData.add(card);
        }
        ((DefaultExpandable)data.get(2)).addSubData(sceneryData);

        // Add tranquil
        List<IMultiItem> tranquilData = new ArrayList<>(mTranquilIds.length);
        for (int i = 0; i < mTranquilIds.length; i++) {
            ImageCard cardData = new ImageCard(mTranquilIds[i],mTranquilTitles[i]);
            Card card = new Card(width,cardData);
            tranquilData.add(card);
        }
        ((DefaultExpandable)data.get(3)).addSubData(tranquilData);

        mAdapter.setData(data);
    }
}
