package com.angcyo.ondemand;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.angcyo.ondemand.base.BaseActivity;
import com.angcyo.ondemand.base.RBaseAdapter;
import com.angcyo.ondemand.components.RWorkService;
import com.angcyo.ondemand.components.RWorkThread;
import com.angcyo.ondemand.control.RTableControl;
import com.angcyo.ondemand.event.EventAcceptOddnum;
import com.angcyo.ondemand.event.EventDeliveryservice;
import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.event.EventRefresh;
import com.angcyo.ondemand.event.EventTakeOddnum;
import com.angcyo.ondemand.model.DeliveryserviceBean;
import com.angcyo.ondemand.util.PhoneUtil;
import com.angcyo.ondemand.util.PopupTipWindow;
import com.angcyo.ondemand.util.Util;
import com.angcyo.ondemand.view.CirclePathButton;
import com.angcyo.ondemand.view.SparseExitFragment;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class Main2Activity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    boolean isFirst = true;
    @Bind(R.id.oddnum_list)
    RecyclerView oddnumList;
    @Bind(R.id.button)
    Button button;
    @Bind(R.id.refresh)
    SwipeRefreshLayout refresh;
    OddAdapter oddAdapter;
    private boolean isSendSms = false;
    private boolean needLaunch = false;
    private ArrayList<DeliveryserviceBean> oldBeans;

    @Override
    protected int getContentView() {
        return R.layout.content_main2;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initAfter() {
        setTitle("接单中:" + OdApplication.userInfo.member.getName_real());
        oddnumList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        oddnumList.setItemAnimator(new DefaultItemAnimator());

        oddAdapter = new OddAdapter(this);
        oddnumList.setAdapter(oddAdapter);

        refresh.setOnRefreshListener(this);
        refresh.setColorSchemeResources(R.color.colorAccent);
    }

    @OnClick(R.id.button)
    public void onOk() {
        needLaunch = true;

        if (oddAdapter.netCommandCount.get() == 0) {
            needLaunch = false;
            hideDialogTip();
            ArrayList<DeliveryserviceBean> allAcceptOddnum = oddAdapter.getAllAcceptOddnum();
            final ArrayList<DeliveryserviceBean> allTakeOddnum = oddAdapter.getAllTakeOddnum();
            int allSize = oddAdapter.getAllDatas().size();
            int acceptSize = allAcceptOddnum.size();
            int takeSize = allTakeOddnum.size();
            if (allSize == 0) {
                PopupTipWindow.showTip(this, "没有订单需要配送");
            } else if (allSize == acceptSize && allSize == takeSize) {
                launchDetailActivity(allTakeOddnum);
            } else {
                PopupTipWindow.showTip(this, "请确保所有订单都已取货.");
            }

//            if (allAcceptOddnum.size() > allTakeOddnum.size() && allTakeOddnum.size() > 0) {
//                showMaterialDialog("提示", "你还有未取货的订单, 是否继续?", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mMaterialDialog.dismiss();
//                        launchDetailActivity(allTakeOddnum);
//                    }
//                }, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mMaterialDialog.dismiss();
//                        return;
//                    }
//                }, null);
//            } else if (allAcceptOddnum.size() == 0 || allTakeOddnum.size() == 0) {
//                PopupTipWindow.showTip(this, "没有订单需要配送");
//                return;
//            } else {
//                launchDetailActivity(allTakeOddnum);
//            }
        } else {
            showDialogTip("请等待...", true);
        }

//        notifyWidthSms(allTakeOddnum);
    }

    /**
     * 启动订单跟踪界面
     */
    private void launchDetailActivity(ArrayList<DeliveryserviceBean> allTakeOddnum) {
        isFirst = true;
        DetailActivity.launch(this, allTakeOddnum);
    }

    /**
     * 短信通知
     */
    private void notifyWidthSms(ArrayList<DeliveryserviceBean> allTakeOddnum) {
        //短信提醒
        final StringBuffer phones = new StringBuffer();
        String phone, sellerCaption = "--";////商户名称
        for (DeliveryserviceBean odd : allTakeOddnum) {
            phone = odd.getPhone();
            if (!TextUtils.isEmpty(phone)) {
                phones.append(phone + ";");
            }
            sellerCaption = odd.getCaption();//商户名称
        }
        final String content = String.format("昂递科技提醒:\n您好，你刚才在 %s 预订的外卖由我(%s)正在火速配送中，\n联系方式：%s，请稍等，谢谢!",
                sellerCaption, OdApplication.userInfo.member.getName_real(), OdApplication.userInfo.member.getPhone());

        phone = phones.toString();
        if (TextUtils.isEmpty(phone)) {
            launchActivity(DetailActivity.class);
        } else {
            showMaterialDialog("发送至:" + phone, content + "\n\n是否,发短信通知?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhoneUtil.sendSMSTo(Main2Activity.this, phones.toString(), content);
                    isSendSms = true;
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSendSms = false;
                    mMaterialDialog.dismiss();
                }
            }, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    launchActivity(DetailActivity.class);
                }
            });
        }
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
            onRefreshData();
            return true;
        }
        if (id == R.id.action_center) {//个人中心
            CenterActivity.launch(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 刷新订单列表,首先判断之前修改状态的订单是否已完成,再追加新增的订单
     */
    private void onRefreshData() {
        hideOverlayLayout();
        showLoadLayout();
        refresh.setRefreshing(true);
        showDialogTip("刷新订单中....");
        final ArrayList<DeliveryserviceBean> allAcceptOddnum = oddAdapter.getAllAcceptOddnum();
        RWorkService.addTask(new RWorkThread.TaskRunnable() {
            @Override
            public void run() {
                if (Util.isNetOk(Main2Activity.this)) {
                    EventRefresh event = new EventRefresh();
                    ArrayList<DeliveryserviceBean> beans = new ArrayList<>();
                    if (beans.size() == 0) {
                        event.isSucceed = true;
                    } else {
                        for (DeliveryserviceBean bean : allAcceptOddnum) {
                            try {
                                if (!RTableControl.isOrderFinish(bean.getSeller_order_identifier())) {//保存未完成的订单
                                    beans.add(bean);
                                    event.isSucceed = true;
                                }
                            } catch (Exception e) {
                                event.isSucceed = false;
                            }
                        }
                    }
                    event.beans = beans;
                    EventBus.getDefault().post(event);
                } else {
                    EventBus.getDefault().post(new EventNoNet());
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventRefresh event) {
        if (event.isSucceed) {
            oddAdapter.resetData(event.beans);
            oldBeans = new ArrayList<>();
            oldBeans.addAll(event.beans);
            setTitle("接单中:" + OdApplication.userInfo.member.getName_real() + "(" + event.beans.size() + ")");
            loadDeliveryservice();
        } else {
            hideDialogTip();
            refresh.setRefreshing(false);
            PopupTipWindow.showTip(this, "出现问题,请重试.");
        }
    }

    @Override
    protected void onOverlayRefresh(View v) {
        super.onOverlayRefresh(v);
        onRefreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSendSms) {
            if (mMaterialDialog != null) {
                isSendSms = false;
                mMaterialDialog.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (progressFragment == null) {
            SparseExitFragment.show(this);
        } else {
            hideDialogTip();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isFirst) {
            isFirst = false;
            onRefreshData();
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
                    ArrayList<DeliveryserviceBean> been = null;
                    try {
                        been = RTableControl.getAllDeliveryservice2(OdApplication.userInfo.member.getSid());
                        event.isSucceed = true;
                    } catch (Exception e) {
                        event.isSucceed = false;
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
        refresh.setRefreshing(false);
        if (event.isSucceed) {
            oddAdapter.appendData(event.beans);
            int oldSize = 0;
            if (oldBeans != null) {
                oldSize = oldBeans.size();
            }
            int allSize = event.beans.size() + oldSize;
            setTitle("接单中:" + OdApplication.userInfo.member.getName_real() + "(" + allSize + ")");
            if (allSize == 0) {
                showEmptyLayout();
                return;
            }
        } else {
            showNonetLayout();
        }
    }

    public void updateButtonUi() {
        int acceptSize = oddAdapter.nickAcceptBeans.size();
        int takeSize = oddAdapter.nickTakeBeans.size();
        if (acceptSize == 0 && takeSize == 0) {
            button.setText("开始派送");
        } else {
            button.setText("开始派送-->" + "取单(" + acceptSize + ")" + " 取货(" + takeSize + ")");
        }

        if (needLaunch) {
            onOk();
        }
    }

    @Override
    public void onRefresh() {
        if (refresh.isRefreshing()) {
            onRefreshData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(SparseExitFragment.ExitEvent event) {
        super.onBackPressed();
    }

    /**
     * 订单item布局 adapter
     */
    public static class OddAdapter extends RBaseAdapter<DeliveryserviceBean> {

        public ArrayList<DeliveryserviceBean> nickAcceptBeans;//保存网络请求生效的取单订单
        public ArrayList<DeliveryserviceBean> nickTakeBeans;//保存网络请求生效的取货订单
        public AtomicInteger netCommandCount;//网络请求操作的次数,当大于零时,所有请求处理完成

        public OddAdapter(Context context) {
            super(context);
            nickAcceptBeans = new ArrayList<>();
            nickTakeBeans = new ArrayList<>();
            netCommandCount = new AtomicInteger();
            EventBus.getDefault().register(this);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.adapter_oddnum_item2;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, final int position, final DeliveryserviceBean bean) {
            holder.tV(R.id.platform).setText(bean.getCaption() + "");
            holder.tV(R.id.oddnum).setText(bean.getSeller_order_identifier() + "");
            holder.tV(R.id.ec_platform).setText(bean.getEc_caption() + "");
            holder.tV(R.id.address).setText(bean.getAddress() + "");

            final CirclePathButton acceptButton = (CirclePathButton) holder.v(R.id.accept);//已取单
            final CirclePathButton takeButton = (CirclePathButton) holder.v(R.id.take);//已取货
            acceptButton.setSelected(bean.isAccept);
            takeButton.setSelected(bean.isTake);

            acceptButton.setOnSelectChanged(new CirclePathButton.OnSelectChanged() {
                @Override
                public void onSelectChanged(View view, boolean isSelect) {
                    if (bean.isTake && !isSelect) {//已取货,不允许取消订单
                        PopupTipWindow.showTip(mContext, "已取货订单,不允许此操作!");
                        acceptButton.setSelected(true);
                    } else {
                        netCommandCount.addAndGet(1);
                        acceptOddnum(String.valueOf(bean.getSeller_order_identifier()), isSelect, position);
                    }
                    bean.isAccept = acceptButton.isSelected();
                }
            });
            takeButton.setOnSelectChanged(new CirclePathButton.OnSelectChanged() {
                @Override
                public void onSelectChanged(View view, boolean isSelect) {
                    if (!bean.isAccept && isSelect) {//未取单,就取货
                        PopupTipWindow.showTip(mContext, "请先取单!");
                        takeButton.setSelected(false);
                    } else {
                        netCommandCount.addAndGet(1);
                        takeOddnum(String.valueOf(bean.getSeller_order_identifier()), isSelect, position);
                    }
                    bean.isTake = takeButton.isSelected();
                }
            });
        }

        /**
         * 取单
         */
        private void acceptOddnum(final String seller_order_identifier, final boolean isAccept, final int position) {
            RWorkService.addTask(new RWorkThread.TaskRunnable() {
                @Override
                public void run() {
                    try {
                        EventAcceptOddnum eventAcceptOddnum = new EventAcceptOddnum(position);
                        if (isAccept) {
                            RTableControl.updateOddnumState(seller_order_identifier, 1);
                            eventAcceptOddnum.state = 1;
                        } else {
                            RTableControl.updateOddnumState(seller_order_identifier, 0);
                            eventAcceptOddnum.state = 0;
                        }
                        eventAcceptOddnum.isSucceed = true;
                        EventBus.getDefault().post(eventAcceptOddnum);
                    } catch (Exception e) {
                        EventBus.getDefault().post(new EventAcceptOddnum(position, false));
                    }
                }
            });
        }

        /**
         * 取货
         */
        private void takeOddnum(final String seller_order_identifier, final boolean isTake, final int position) {
            RWorkService.addTask(new RWorkThread.TaskRunnable() {
                @Override
                public void run() {
                    try {
                        EventTakeOddnum eventTakeOddnum = new EventTakeOddnum(position);
                        if (isTake) {
                            RTableControl.updateOddnumState(seller_order_identifier, 2);
                            eventTakeOddnum.state = 2;
                        } else {
                            RTableControl.updateOddnumState(seller_order_identifier, 1);
                            eventTakeOddnum.state = 1;
                        }
                        eventTakeOddnum.isSucceed = true;
                        EventBus.getDefault().post(eventTakeOddnum);
                    } catch (Exception e) {
                        EventBus.getDefault().post(new EventTakeOddnum(position, false));
                    }
                }
            });
        }

        @Subscribe(threadMode = ThreadMode.MainThread)
        public void onEvent(EventAcceptOddnum event) {
            netCommandCount.decrementAndGet();
            if (!event.isSucceed) {
                mAllDatas.get(event.position).isAccept = false;
                notifyItemChanged(event.position);
            } else {
                if (event.state == 1) {
                    nickAcceptBeans.add(mAllDatas.get(event.position));
                } else {
                    nickAcceptBeans.remove(mAllDatas.get(event.position));
                }
            }
            ((Main2Activity) mContext).updateButtonUi();
        }

        @Subscribe(threadMode = ThreadMode.MainThread)
        public void onEvent(EventTakeOddnum event) {
            netCommandCount.decrementAndGet();
            if (!event.isSucceed) {
                mAllDatas.get(event.position).isTake = false;
                notifyItemChanged(event.position);
            } else {
                if (event.state == 2) {
                    nickTakeBeans.add(mAllDatas.get(event.position));
                } else {
                    nickTakeBeans.remove(mAllDatas.get(event.position));
                }
            }
            ((Main2Activity) mContext).updateButtonUi();
        }

        /**
         * 返回所有已取单的单号
         */
        public ArrayList<DeliveryserviceBean> getAllAcceptOddnum() {
            ArrayList<DeliveryserviceBean> beans = new ArrayList<>();
            for (DeliveryserviceBean bean : mAllDatas) {
                if (bean.isAccept) {
                    beans.add(bean);
                }
            }
            return beans;
        }

        /**
         * 返回所有已取货的单号
         */
        public ArrayList<DeliveryserviceBean> getAllTakeOddnum() {
            ArrayList<DeliveryserviceBean> beans = new ArrayList<>();
            for (DeliveryserviceBean bean : mAllDatas) {
                if (bean.isAccept && bean.isTake) {
                    beans.add(bean);
                }
            }
            return beans;
        }
    }
}
