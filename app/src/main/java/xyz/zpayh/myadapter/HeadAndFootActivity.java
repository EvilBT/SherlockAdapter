package xyz.zpayh.myadapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.myadapter.adapter.HeadAndFootAdapter;

public class HeadAndFootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_and_foot);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        HeadAndFootAdapter adapter = new HeadAndFootAdapter();

        recyclerView.setAdapter(adapter);

        adapter.addHeadLayout(R.layout.item_head);
        adapter.addHeadLayout(R.layout.item_head);
        adapter.addHeadLayout(R.layout.item_head);
        adapter.addFootLayout(R.layout.item_foot);
        adapter.addFootLayout(R.layout.item_foot);

        List<String> data = new ArrayList<>();

        String[] list = getResources().getStringArray(R.array.list);
        for (String s : list) {
            data.add(s);
        }

        adapter.setData(data);
    }
}
