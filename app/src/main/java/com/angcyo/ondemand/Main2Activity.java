package com.angcyo.ondemand;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class Main2Activity extends BaseActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void initAfter() {

    }

}
