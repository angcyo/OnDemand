package com.angcyo.ondemand;

import android.os.Bundle;
import android.view.View;

import com.angcyo.ondemand.base.BaseActivity;

public class TestActivity extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.content_test;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void initAfter() {

    }

    public void showEmpty(View view) {
        showEmptyLayout();
    }

    public void showNonet(View view) {
        showNonetLayout();
    }

    public void showLoad(View view) {
        showLoadLayout();
    }

    public void hideOverlay(View view) {
        hideOverlayLayout();
    }
}
