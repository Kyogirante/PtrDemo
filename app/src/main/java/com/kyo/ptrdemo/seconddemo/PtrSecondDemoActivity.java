package com.kyo.ptrdemo.seconddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.kyo.ptrdemo.PtrListAdapter;
import com.kyo.ptrdemo.PtrMockDataUtil;
import com.kyo.ptrdemo.R;

/**
 * @author KyoWang
 * @since 2017/03/10
 */
public class PtrSecondDemoActivity extends AppCompatActivity {

    private PtrSecondRefreshableView mRefreshableContainer;
    private ListView mListView;

    private PtrListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ptr_activity_second_demo);

        initView();
        initAction();
    }

    private void initView() {
        mRefreshableContainer = (PtrSecondRefreshableView) findViewById(R.id.ptr_second_demo_container);
        mListView = (ListView) findViewById(R.id.ptr_second_demo_lv);

        mAdapter = new PtrListAdapter(this, PtrMockDataUtil.getMockData(this));
        mListView.setAdapter(mAdapter);
    }

    private void initAction() {
        mRefreshableContainer.setOnRefreshListener(new PtrSecondRefreshableView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshableContainer.finishRefresh();
                    }
                }, 3000);
            }
        });
    }
}
