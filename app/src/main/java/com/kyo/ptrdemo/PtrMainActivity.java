package com.kyo.ptrdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kyo.ptrdemo.firstdemo.PtrFirstDemoActivity;
import com.kyo.ptrdemo.seconddemo.PtrSecondDemoActivity;
import com.kyo.ptrdemo.thirddemo.PtrThirdDemoActivity;

/**
 * @author KyoWang
 * @since 2017/03/10
 */

public class PtrMainActivity extends AppCompatActivity implements View.OnClickListener {

    private View mFirstDemoBtn;
    private View mSecondDemoBtn;
    private View mThirdDemoBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ptr_activity_main);

        mFirstDemoBtn = findViewById(R.id.ptr_first_demo);
        mSecondDemoBtn = findViewById(R.id.ptr_second_demo);
        mThirdDemoBtn = findViewById(R.id.ptr_third_demo);

        mFirstDemoBtn.setOnClickListener(this);
        mSecondDemoBtn.setOnClickListener(this);
        mThirdDemoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.ptr_first_demo) {
            route(PtrFirstDemoActivity.class);
        } else if(id == R.id.ptr_second_demo) {
            route(PtrSecondDemoActivity.class);
        } else if(id == R.id.ptr_third_demo) {
            route(PtrThirdDemoActivity.class);
        }
    }

    private void route(Class clz) {
        Intent intent = new Intent(this, clz);
        startActivity(intent);
    }
}
