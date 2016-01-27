package com.angcyo.ondemand;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.angcyo.ondemand.base.RBaseAdapter;
import com.angcyo.ondemand.components.RWorkService;
import com.angcyo.ondemand.components.RWorkThread;
import com.angcyo.ondemand.control.RTableControl;
import com.angcyo.ondemand.event.EventDeliveryservice;
import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.model.DeliveryserviceBean;
import com.angcyo.ondemand.util.Util;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class Main2Activity extends BaseActivity {

    boolean isFirst = true;
    @Bind(R.id.oddnum_list)
    RecyclerView oddnumList;
    OddAdapter oddAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void initAfter() {
        setTitle("接单中:" + OdApplication.userInfo.member.getName_real());
        oddnumList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        oddnumList.setItemAnimator(new DefaultItemAnimator());

        oddAdapter = new OddAdapter(this);
        oddnumList.setAdapter(oddAdapter);
    }

    @OnClick(R.id.button)
    public void onOk() {
//        if (oddnums.size() < 1) {
//            PopupTipWindow.showTip(this, "未添加配送单号");
//            return;
//        }
//        showDialogTip("准备派送,请等待...");
//        RWorkService.addTask(new RWorkThread.TaskRunnable() {
//            @Override
//            public void run() {
//                if (Util.isNetOk(MainActivity.this)) {
//                    for (OddnumBean bean : oddnums) {
//                        try {
////                            RTableControl.executeOddnum(bean.oddnum, bean.platformId, bean.sid_seller, bean.sid_customer, bean.memberId, bean.status);//不创建订单
////                            RTableControl.updateOddnumState(bean.oddnum, 1);//更新订单状态//订单状态（0待命 1锁单 2派送中 3派送丢失 4客户拒收 9客户已收）
//                            RTableControl.updateOddnum(bean.memberId, 1, bean.sid);
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                            EventBus.getDefault().post(new EventException());
//                            return;
//                        } catch (ClassNotFoundException e) {
//                            e.printStackTrace();
//                            EventBus.getDefault().post(new EventException());
//                            return;
//                        }
//                    }
//                    EventBus.getDefault().post(new EventOddnumOk());
//                } else {
//                    EventBus.getDefault().post(new EventNoNet());
//                }
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Hawk.put(LoginActivity.KEY_USER_PW, "");
            OdApplication.userInfo = null;
            launchActivity(LoginActivity.class);
            super.onBackPressed();
            return true;
        }
        if (id == R.id.refresh) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isFirst) {
            isFirst = false;
            loadDeliveryservice();
        }
    }

    /**
     * 加载所有订单
     */
    private void loadDeliveryservice() {
        showDialogTip("拉取订单中...");
        RWorkService.addTask(new RWorkThread.TaskRunnable() {
            @Override
            public void run() {
                if (Util.isNetOk(Main2Activity.this)) {
                    EventDeliveryservice event = new EventDeliveryservice();
                    ArrayList<DeliveryserviceBean> been = RTableControl.getAllDeliveryservice2();
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

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventDeliveryservice event) {
        hideDialogTip();
        if (event.isSucceed) {
            oddAdapter.resetData(event.beans);
        }
    }

    public static class OddAdapter extends RBaseAdapter<DeliveryserviceBean> {

        public OddAdapter(Context context) {
            super(context);
        }

        public OddAdapter(Context context, List<DeliveryserviceBean> datas) {
            super(context, datas);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.adapter_oddnum_item2;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, DeliveryserviceBean bean) {
            holder.tV(R.id.platform).setText(bean.getCaption() + "");
            holder.tV(R.id.oddnum).setText(bean.getSeller_order_identifier() + "");
        }
    }
}
