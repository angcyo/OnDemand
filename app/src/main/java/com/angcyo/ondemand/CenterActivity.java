package com.angcyo.ondemand;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angcyo.ondemand.base.BaseActivity;
import com.angcyo.ondemand.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CenterActivity extends BaseActivity {

    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.viewpager)
    ViewPager viewpager;

    public static void launch(BaseActivity activity) {
        Intent intent = new Intent(activity, CenterActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_center;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ButterKnife.bind(this);
//        EventBus.getDefault().register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("个人中心");
        viewpager.setAdapter(new CenterAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(viewpager);
    }

    @Override
    protected void initAfter() {

    }

    /**
     * 视图展示
     */
    public static class CommonFragment extends BaseFragment {
        public static final String KEY_TYPE = "type";
        public static final String TYPE_TODAY = "today'";//今天完成的订单
        public static final String TYPE_HISTORY = "history'";//历史完成的订单
        @Bind(R.id.recycle)
        RecyclerView recycle;
        @Bind(R.id.refresh)
        SwipeRefreshLayout refresh;
        private String type;

        public static CommonFragment newInstance(String type) {
            CommonFragment fragment = new CommonFragment();
            Bundle args = new Bundle();
            args.putString(KEY_TYPE, type);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected void loadData(Bundle savedInstanceState) {
            type = getArguments().getString(KEY_TYPE, TYPE_TODAY);
        }

        @Override
        protected View loadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.layout_refresh_recycler, container, false);
            ButterKnife.bind(this, rootView);
            return view;
        }

        @Override
        protected void initView(View rootView) {

        }
    }

    /**
     * 适配器
     */
    public static class CenterAdapter extends FragmentStatePagerAdapter {
        public String[] tabTitles = new String[]{"今天的订单", "历史订单"};

        public CenterAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return CommonFragment.newInstance(CommonFragment.TYPE_TODAY);
            } else if (position == 1) {
                return CommonFragment.newInstance(CommonFragment.TYPE_HISTORY);
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
