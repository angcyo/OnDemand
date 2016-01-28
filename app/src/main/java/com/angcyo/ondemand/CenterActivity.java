package com.angcyo.ondemand;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angcyo.ondemand.base.BaseActivity;
import com.angcyo.ondemand.base.BaseFragment;
import com.angcyo.ondemand.base.RBaseAdapter;
import com.angcyo.ondemand.components.RWorkService;
import com.angcyo.ondemand.components.RWorkThread;
import com.angcyo.ondemand.control.RTableControl;
import com.angcyo.ondemand.event.EventDeliveryserviceHistory;
import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.model.DeliveryserviceBean;
import com.angcyo.ondemand.util.PopupTipWindow;
import com.angcyo.ondemand.util.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

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
    public static class CommonFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
        public static final String KEY_TYPE = "type";
        public static final String TYPE_TODAY = "today";//今天完成的订单
        public static final String TYPE_HISTORY = "history";//历史完成的订单
        @Bind(R.id.recycle)
        RecyclerView recycle;
        @Bind(R.id.refresh)
        SwipeRefreshLayout refresh;
        private String type;
        private ItemAdapter itemAdapter;

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
            EventBus.getDefault().register(this);
        }

        private boolean isLoaded = false;

        @Override
        protected View loadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.layout_refresh_recycler, container, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        protected void initView(View rootView) {
            refresh.setOnRefreshListener(this);
            refresh.setColorSchemeResources(R.color.colorAccent);
            recycle.setLayoutManager(new LinearLayoutManager(mBaseActivity, LinearLayoutManager.VERTICAL, false));
            recycle.setItemAnimator(new DefaultItemAnimator());
            itemAdapter = new ItemAdapter(mBaseActivity);
            recycle.setAdapter(itemAdapter);
        }

        private String getTime() {
            if (type.equalsIgnoreCase(TYPE_TODAY)) {
                return Util.getDate();
            } else {
                return "1000-01-01";
            }
        }

        @Override
        protected void onShow() {
            super.onShow();
            if (!isLoaded) {
                refresh.setRefreshing(true);
                onRefresh();
            }
        }

        @Subscribe(threadMode = ThreadMode.MainThread)
        public void onEvent(EventDeliveryserviceHistory event) {
            hideDialogTip();
            refresh.setRefreshing(false);
            if (event.isSucceed) {
                itemAdapter.resetData(event.beans);
                PopupTipWindow.showTip(mBaseActivity, "拉取到 " + event.beans.size() + "条数据");
            } else {
                PopupTipWindow.showTip(mBaseActivity, "拉取失败,刷新重试吧!");
            }
            isLoaded = false;
        }

        @Override
        public void onRefresh() {
            if (!refresh.isRefreshing()) {
                return;
            }

            showDialogTip("拉取订单中...");
            RWorkService.addTask(new RWorkThread.TaskRunnable() {
                @Override
                public void run() {
                    if (Util.isNetOk(mBaseActivity)) {
                        EventDeliveryserviceHistory event = new EventDeliveryserviceHistory();
                        ArrayList<DeliveryserviceBean> been = RTableControl.getAllDeliveryserviceInfo(
                                OdApplication.userInfo.member.getSid(), 9, getTime());
                        if (been.size() == 0) {
                            event.isSucceed = false;
                        } else {
                            event.isSucceed = true;
                        }
                        event.beans = been;
                        EventBus.getDefault().post(event);
                    } else {
                        EventBus.getDefault().post(new EventNoNet());
                    }
                }
            });
        }
    }

    /**
     * Item 适配器
     */
    public static class ItemAdapter extends RBaseAdapter<DeliveryserviceBean> {

        public ItemAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.adapter_center_item;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, DeliveryserviceBean bean) {
            holder.tV(R.id.platform).setText(bean.getCaption());
            holder.tV(R.id.ec_platform).setText(bean.getEc_caption());
            holder.tV(R.id.oddnum).setText(bean.getSeller_order_identifier());
            holder.tV(R.id.address).setText(bean.getAddress());
            holder.tV(R.id.name).setText(bean.getName());
            holder.tV(R.id.phone).setText(bean.getPhone());

            //拨打客户电话
            if (Util.isEmpty(bean.getPhone())) {
                holder.tV(R.id.phone).setText("未识别号码");
                holder.tV(R.id.phone).setClickable(false);
            } else {
                holder.tV(R.id.phone).setText(bean.getPhone());
                holder.tV(R.id.phone).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
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
                return CenterActivity.CommonFragment.newInstance(CenterActivity.CommonFragment.TYPE_TODAY);
            } else {
                return CenterActivity.CommonFragment.newInstance(CenterActivity.CommonFragment.TYPE_HISTORY);
            }
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
