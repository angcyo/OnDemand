package com.angcyo.ondemand;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.OnClick;

public class Main2Activity extends BaseActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void initAfter() {
        setTitle("接单中:");
    }

    @OnClick(R.id.ok)
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
}
