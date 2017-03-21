package com.kyo.ptrdemo.firstdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.kyo.ptrdemo.PtrListAdapter;
import com.kyo.ptrdemo.PtrMockDataUtil;
import com.kyo.ptrdemo.R;

/**
 * @author KyoWang
 * @since 2017/03/10
 */
public class PtrFirstDemoActivity extends AppCompatActivity {

    private PtrFirstRefreshableView mRefreshContainer;
    private ListView mListView;

    private PtrListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ptr_activity_first_demo);

        initView();
        initAction();
    }

    private void initView() {
        mRefreshContainer = (PtrFirstRefreshableView) findViewById(R.id.ptr_first_demo_container);
        mListView = (ListView) findViewById(R.id.ptr_first_demo_lv);

        mAdapter = new PtrListAdapter(this, PtrMockDataUtil.getMockData(this));
        mListView.setAdapter(mAdapter);
    }

    private void initAction() {
        mRefreshContainer.setOnRefreshListener(new PtrFirstRefreshableView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshContainer.finishRefreshing();
                    }
                }, 3000);
            }
        });
    }
}
