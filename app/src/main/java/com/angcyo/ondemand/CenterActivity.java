package com.angcyo.ondemand;

import android.content.Intent;
import android.os.Bundle;

import com.angcyo.ondemand.base.BaseActivity;

public class CenterActivity extends BaseActivity {

    public static void launch(BaseActivity activity) {
        Intent intent = new Intent(activity, CenterActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_center;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void initAfter() {

    }
}
