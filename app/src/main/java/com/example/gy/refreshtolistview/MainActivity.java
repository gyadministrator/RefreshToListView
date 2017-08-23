package com.example.gy.refreshtolistview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import adapter.ListViewAdapter;
import view.RefreshListView;

public class MainActivity extends AppCompatActivity implements RefreshListView.IRefreshListener {
    private List<String> allValues = new ArrayList<>();
    private ListViewAdapter adapter;
    private RefreshListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getData();
        showList(allValues);

    }

    private void getData() {
        for (int i = 0; i < 10; i++) {
            allValues.add("原始数据" + (i + 1));
        }
    }

    private void getRefreshData() {
        for (int i = 0; i < 3; i++) {
            allValues.add("刷新数据" + (i + 1));
        }
    }

    private void showList(List<String> values) {
        if (adapter == null) {
            listView = (RefreshListView) findViewById(R.id.listView);
            listView.setListener(this);
            adapter = new ListViewAdapter(this, allValues);
            listView.setAdapter(adapter);
        } else {
            adapter.onDateChange(values);
        }
    }

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取最新数据
                getRefreshData();
                //通知界面显示
                showList(allValues);
                //通知listview刷新数据完毕
                listView.refreshComplete();
            }
        }, 2000);
    }
}
