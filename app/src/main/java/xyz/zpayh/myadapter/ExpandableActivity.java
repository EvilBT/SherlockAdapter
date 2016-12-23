package xyz.zpayh.myadapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xyz.zpayh.adapter.IExpandable;
import xyz.zpayh.adapter.IMultiItem;
import xyz.zpayh.adapter.OnItemClickListener;
import xyz.zpayh.myadapter.adapter.MyExpandableAdapter;
import xyz.zpayh.myadapter.data.Details;
import xyz.zpayh.myadapter.data.Title;

public class ExpandableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyExpandableAdapter adapter = new MyExpandableAdapter();
        recyclerView.setAdapter(adapter);

        List<IMultiItem> data = new ArrayList<>();

        String[] list = getResources().getStringArray(R.array.list);
        Random random = new Random();
        for (String s : list) {
            Title title = new Title(s);
            int size = random.nextInt(2)+2;
            for (int i = 0; i < size; i++){
                Details details = new Details("我是内容"+i);
                title.add(details);
            }
            data.add(title);
        }
        Title title = new Title("Sherlock");
        Title subTitle = new Title("你开心就好");
        int size = random.nextInt(2)+2;
        for (int i = 0; i < size; i++){
            Details details = new Details("我是内容"+i);
            subTitle.add(details);
        }
        title.add(subTitle);
        data.add(3,title);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                if (view.getId() == R.id.details){
                    IMultiItem item = adapter.getData(adapterPosition);
                    if (item instanceof IExpandable){
                        IExpandable expandable = (IExpandable) item;
                        if (expandable.isExpandable()){
                            adapter.collapse(adapterPosition);
                        }else{
                            adapter.expand(adapterPosition);
                        }
                    }
                }
            }
        });
        adapter.setData(data);
    }

}
