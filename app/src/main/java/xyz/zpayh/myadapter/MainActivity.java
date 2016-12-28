package xyz.zpayh.myadapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.adapter.BaseAdapter;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.OnItemClickListener;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final BaseAdapter<String> adapter = new BaseAdapter<String>() {
            @Override
            public int getLayoutRes(int index) {
                return R.layout.item_list;
            }

            @Override
            public void convert(BaseViewHolder holder, String data, int index) {
                holder.setText(R.id.tv_act_title,data);
            }

            @Override
            public void bind(BaseViewHolder holder, int layoutRes) {
                holder.setClickable(R.id.app_root,true);
            }
        };
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                String action = adapter.getData(adapterPosition);
                Intent intent = new Intent();
                intent.setAction(action);
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(adapter);

        String[] list = getResources().getStringArray(R.array.activity_title);
        final List<String> data = new ArrayList<>(list.length);
        for (String s : list) {
            data.add(s);
        }
        adapter.setData(data);
    }
}
