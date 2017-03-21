package com.kyo.ptrdemo;

import android.content.Context;

import java.util.ArrayList;

/**
 * @author KyoWang
 * @since 2017/03/10
 */

public class PtrMockDataUtil {

    public static ArrayList<String> getMockData(Context context) {
        ArrayList<String> data = new ArrayList<>();
        String[] temp = context.getResources().getStringArray(R.array.lv_data);
        for (String s : temp) {
            data.add(s);
        }

        return data;
    }
}
