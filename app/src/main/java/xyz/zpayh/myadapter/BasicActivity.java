/*
 * Copyright 2017 陈志鹏
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.adapter.OnItemLongClickListener;
import xyz.zpayh.myadapter.adapter.MyMultiAdapter;
import xyz.zpayh.myadapter.data.Image;
import xyz.zpayh.myadapter.data.Text;

public class BasicActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyMultiAdapter mAdapter;
    private List<IMultiItem> mData;

    private GridLayoutManager mGridLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    private boolean mIsGrid;

    private CheckBox mFullSpan1;
    private CheckBox mFullSpan2;
    private EditText mSpanSize1;
    private EditText mSpanSize2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFullSpan1 = (CheckBox) findViewById(R.id.cb_head1);
        mFullSpan2 = (CheckBox) findViewById(R.id.cb_head2);
        mSpanSize1 = (EditText) findViewById(R.id.et_span_count1);
        mSpanSize2 = (EditText) findViewById(R.id.et_span_count2);

        mGridLayoutManager = new GridLayoutManager(this,3);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mIsGrid = true;
        mAdapter = new MyMultiAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.addHeadLayout(R.layout.item_head1,false,0);
        mAdapter.addHeadLayout(R.layout.item_head2,false,0);
        //mAdapter.addFootLayout(R.layout.item_head1,false,0);
        //mAdapter.addFootLayout(R.layout.item_head2,false,0);

        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull View view, final int adapterPosition) {
                new AlertDialog.Builder(BasicActivity.this)
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

        mFullSpan1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeHead();
            }
        });
        mFullSpan2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeHead();
            }
        });

        mSpanSize1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeHead();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSpanSize2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeHead();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void changeHead() {
        mAdapter.removeAllHead();
        //mAdapter.removeAllFoot();
        int spanSize1 = 0;
        String text = mSpanSize1.getText().toString();
        if (!TextUtils.isEmpty(text)&&TextUtils.isDigitsOnly(text)){
            spanSize1 = Integer.parseInt(text);
        }
        int spanSize2 = 0;
        text = mSpanSize2.getText().toString();
        if (!TextUtils.isEmpty(text)&&TextUtils.isDigitsOnly(text)){
            spanSize2 = Integer.parseInt(text);
        }
        mAdapter.addHeadLayout(R.layout.item_head1,mFullSpan1.isChecked(),spanSize1);
        //mAdapter.addFootLayout(R.layout.item_head1,mFullSpan1.isChecked(),spanSize1);
        mAdapter.addHeadLayout(R.layout.item_head2,mFullSpan2.isChecked(),spanSize2);
        //mAdapter.addFootLayout(R.layout.item_head2,mFullSpan2.isChecked(),spanSize2);

        mAdapter.notifyDataSetChanged();
    }

    private void initGridData() {
        mData.clear();
        String[] list = getResources().getStringArray(R.array.list);
        for (int i = 0; i < list.length; i++) {
            mData.add(new Text(list[i],i%3+1));
        }

        int width = getResources().getDisplayMetrics().widthPixels / mGridLayoutManager.getSpanCount();

        mData.add(2,new Image(R.drawable.girl,width,2));
        mData.add(3,new Image(R.drawable.easter_eggs,width));
        mData.add(4,new Image(R.drawable.adult,width,3));

        mData.add(7,new Image(R.drawable.girl,width,3));
        mData.add(7,new Image(R.drawable.sunset,width,1));
        mData.add(7,new Image(R.drawable.horses,width,1));

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
        mData.add(2,new Image(R.drawable.girl1,width));
        mData.add(2,new Image(R.drawable.easter_eggs,width));
        mData.add(2,new Image(R.drawable.adult,width));
        mData.add(2,new Image(R.drawable.sunset,width));
        mData.add(2,new Image(R.drawable.fashion,width));
        mData.add(2,new Image(R.drawable.notes,width));
        mData.add(7,new Image(R.drawable.girl2,width));

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
