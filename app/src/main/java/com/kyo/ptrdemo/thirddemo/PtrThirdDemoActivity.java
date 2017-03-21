package com.kyo.ptrdemo.thirddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kyo.ptrdemo.PtrListAdapter;
import com.kyo.ptrdemo.PtrMockDataUtil;
import com.kyo.ptrdemo.R;

/**
 * @author KyoWang
 * @since 2017/03/10
 */

public class PtrThirdDemoActivity extends AppCompatActivity {

    private PtrThirdRefreshableView mRefreshableListView;
    private PtrListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ptr_activity_third_demo);

        initView();
        initAction();
    }

    private void initView() {
        mRefreshableListView = (PtrThirdRefreshableView) findViewById(R.id.ptr_list_view);

        mAdapter = new PtrListAdapter(this, PtrMockDataUtil.getMockData(this));
        mRefreshableListView.setAdapter(mAdapter);
    }

    private void initAction() {
        mRefreshableListView.setRefreshListener(new PtrThirdRefreshableView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshableListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshableListView.finishRefresh();
                    }
                }, 3000);
            }
        });
    }
}
