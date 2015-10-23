package com.angcyo.ondemand;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.angcyo.ondemand.components.RWorkService;
import com.angcyo.ondemand.components.RWorkThread;
import com.angcyo.ondemand.control.RTableControl;
import com.angcyo.ondemand.event.EventException;
import com.angcyo.ondemand.event.EventLoadData;
import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.event.EventOddnumOk;
import com.angcyo.ondemand.event.EventUpdateAdapter;
import com.angcyo.ondemand.model.OddnumBean;
import com.angcyo.ondemand.model.TableDeliveryservice;
import com.angcyo.ondemand.model.TablePlatform;
import com.angcyo.ondemand.util.PhoneUtil;
import com.angcyo.ondemand.util.PopupTipWindow;
import com.angcyo.ondemand.util.Util;
import com.angcyo.ondemand.view.AddItemAdapter;
import com.angcyo.ondemand.view.RsenRadioGroup;
import com.angcyo.ondemand.view.SparseExitFragment;
import com.orhanobut.hawk.Hawk;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends BaseActivity {


    public static List<OddnumBean> oddnums;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rg_platform)
    RsenRadioGroup rgPlatform;
    @Bind(R.id.oddnum)
    AutoCompleteTextView oddnum;
    @Bind(R.id.ok)
    Button ok;
    @Bind(R.id.num1)
    TextView num1;
    @Bind(R.id.num2)
    TextView num2;
    @Bind(R.id.num3)
    TextView num3;
    @Bind(R.id.num4)
    TextView num4;
    @Bind(R.id.num5)
    TextView num5;
    @Bind(R.id.num6)
    TextView num6;
    @Bind(R.id.num7)
    TextView num7;
    @Bind(R.id.num8)
    TextView num8;
    @Bind(R.id.num9)
    TextView num9;
    @Bind(R.id.num10)
    TextView num10;
    @Bind(R.id.num0)
    TextView num0;
    @Bind(R.id.num11)
    TextView num11;
    @Bind(R.id.add)
    Button add;
    @Bind(R.id.recycle)
    RecyclerView recycle;
    @Bind(R.id.text_input_layout)
    TextInputLayout text_input_layout;

    List<String> des;//数据库中的所有订单//需要过滤, 只显示今天的订单
    MaterialDialog mMaterialDialog;
    private boolean isSendSms = false;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initWindow(R.color.colorAccent);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setTitle("当前用户:" + OdApplication.userInfo.member.getName_real());
        recycle.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void initAfter() {
        oddnums = new ArrayList<>();//所有订单
        List<String> captions = new ArrayList<>();
        for (TablePlatform platform : RTableControl.platforms) {
            captions.add(platform.getCaption());
        }
        rgPlatform.addView(captions);
        recycle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycle.setAdapter(new AddItemAdapter(oddnums));
        oddnum.setThreshold(1);//从第一个字符开始匹配
        oddnum.setDropDownHeight(600);//下拉框的高度
//        rgPlatform.setVisibility(View.GONE);

        sendDelayRunnable(new Runnable() {
            @Override
            public void run() {
                loadData(false);
            }
        }, 100);
    }

    private void loadData(final boolean isRefresh) {
        showDialogTip("初始化中...");
        RWorkService.addTask(new RWorkThread.TaskRunnable() {
            @Override
            public void run() {
                if (Util.isNetOk(MainActivity.this)) {
                    if (isRefresh) {
                        RTableControl.getAllMember();
                        RTableControl.getAllCompany();
                        RTableControl.getAllPlatform();
                        RTableControl.getAllSellerIndexes();
                    }
                    RTableControl.getAllDeliveryservice();//获取所有订单
                    RTableControl.getAllCustomer();//获取所有消费者
                    EventBus.getDefault().post(new EventLoadData());
                } else {
                    EventBus.getDefault().post(new EventNoNet());
                }
            }
        });
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        oddnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                oddnum.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        oddnum.setInputType(InputType.TYPE_NULL);
        oddnum.requestFocus();

        num11.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                oddnum.setText("");
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            loadData(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.ok)
    public void onOk() {
        if (oddnums.size() < 1) {
            PopupTipWindow.showTip(this, "未添加配送单号");
            return;
        }
        showDialogTip("准备派送,请等待...");
        RWorkService.addTask(new RWorkThread.TaskRunnable() {
            @Override
            public void run() {
                if (Util.isNetOk(MainActivity.this)) {
                    for (OddnumBean bean : oddnums) {
                        try {
//                            RTableControl.executeOddnum(bean.oddnum, bean.platformId, bean.sid_seller, bean.sid_customer, bean.memberId, bean.status);//不创建订单
//                            RTableControl.updateOddnumState(bean.oddnum, 1);//更新订单状态//订单状态（0待命 1锁单 2派送中 3派送丢失 4客户拒收 9客户已收）
                            RTableControl.updateOddnum(bean.memberId, 1, bean.sid);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new EventException());
                            return;
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new EventException());
                            return;
                        }
                    }
                    EventBus.getDefault().post(new EventOddnumOk());
                } else {
                    EventBus.getDefault().post(new EventNoNet());
                }
            }
        });
    }

    @OnClick(R.id.add)
    public void onAdd() {
        String num = oddnum.getText().toString();
        if (Util.isEmpty(num)) {
            oddnum.setError("请输入有效订单号");
            oddnum.requestFocus();
            return;
        }
        if (rgPlatform.getCheckedRadioButtonIndex() < 0) {
            PopupTipWindow.showTip(this, "平台信息无效");
            return;
        }
        if (!des.contains(num)) {
            oddnum.setError("无效订单,请重新输入");
            oddnum.requestFocus();
            return;
        }

        OddnumBean bean = new OddnumBean();
        bean.caption = RTableControl.platforms.get(rgPlatform.getCheckedRadioButtonIndex()).getCaption();
        bean.platformId = RTableControl.platforms.get(rgPlatform.getCheckedRadioButtonIndex()).getSid();
        bean.oddnum = num;
        bean.sid = RTableControl.getSidWithPOddnum(bean.oddnum, bean.platformId);
        bean.memberId = OdApplication.userInfo.member.getSid();

        //在今天的订单里面,判断输入的订单号和对应的选择的平台是否相同
        for (TableDeliveryservice deliveryservice : RTableControl.deliveryservicesToday) {
            if (deliveryservice.getSeller_order_identifier().equalsIgnoreCase(bean.oddnum)
                    && (deliveryservice.getSid_ec() == RTableControl.getSidWithPlatform(bean.caption))) {//订单号相同,并且平台相同

                if (oddnums.contains(bean)) {
                    PopupTipWindow.showTip(this, "请勿重复添加");
                    return;
                } else {
                    bean.sid_seller = deliveryservice.getSid_seller();
                    ((AddItemAdapter) recycle.getAdapter()).addItem(bean);
                    recycle.smoothScrollToPosition(recycle.getAdapter().getItemCount());
                    oddnum.setText("");
                    return;
                }
            }
        }

        PopupTipWindow.showTip(this, "平台\"" + bean.caption + "\"无此订单号:" + bean.oddnum);
    }

    @Override
    public void onBackPressed() {
        SparseExitFragment.show(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(SparseExitFragment.ExitEvent event) {
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventUpdateAdapter event) {
        recycle.getAdapter().notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventException event) {
        hideDialogTip();
        PopupTipWindow.showTip(this, "遇到错误,请重试");
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventOddnumOk event) {
        hideDialogTip();

        //短信提醒
        final StringBuffer phones = new StringBuffer();
        String phone, sellerCaption = "--";////商户名称
        for (OddnumBean odd : oddnums) {
            phone = RTableControl.getPhoneWithOddnum(odd.oddnum);
            if (!TextUtils.isEmpty(phone)) {
                phones.append(phone + ";");
            }
            sellerCaption = RTableControl.getCaptionWithSellerSid(odd.sid_seller);//商户名称
        }
        final String content = String.format("昂递科技提醒:\n您好，你刚才在 %s 预订的外卖由我(%s)正在火速配送中，\n联系方式：%s，请稍等，谢谢!",
                sellerCaption, OdApplication.userInfo.member.getName_real(), OdApplication.userInfo.member.getPhone());

        phone = phones.toString();
        if (TextUtils.isEmpty(phone)) {
            launchActivity(DetailActivity.class);
        } else {
            mMaterialDialog = new MaterialDialog(this)
                    .setTitle("发送至:" + phone)
                    .setMessage(content + "\n\n是否,发短信通知?")
                    .setPositiveButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PhoneUtil.sendSMSTo(MainActivity.this, phones.toString(), content);
                            isSendSms = true;
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isSendSms = false;
                            mMaterialDialog.dismiss();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            launchActivity(DetailActivity.class);
                        }
                    });
            mMaterialDialog.show();
        }
//        finish();
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

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventLoadData event) {
        hideDialogTip();
        des = RTableControl.getDeliveryservicesToday();
        oddnum.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, des));
        EventBus.getDefault().post(new EventUpdateAdapter());
    }

    private void insert(String text) {
        int index = oddnum.getSelectionStart();//获取光标所在位置
        Editable edit = oddnum.getEditableText();//获取EditText的文字
        try {
            if (index < 0 || index >= edit.length()) {
                edit.append(text);
            } else {
                edit.insert(index, text);//光标所在位置插入文字
            }
        } catch (Exception e) {
        }
    }

    private void delete() {
        int index = oddnum.getSelectionStart();//获取光标所在位置
        Editable edit = oddnum.getEditableText();//获取EditText的文字
        try {
            edit.delete(index - 1, index);//光标所在位置插入文字
        } catch (Exception e) {
        }
    }

    @OnClick(R.id.num1)
    public void onNum1() {
        insert("1");
    }

    @OnClick(R.id.num2)
    public void onNum2() {
        insert("2");
    }

    @OnClick(R.id.num3)
    public void onNum3() {
        insert("3");
    }

    @OnClick(R.id.num4)
    public void onNum4() {
        insert("4");
    }

    @OnClick(R.id.num5)
    public void onNum5() {
        insert("5");
    }

    @OnClick(R.id.num6)
    public void onNum6() {
        insert("6");
    }

    @OnClick(R.id.num7)
    public void onNum7() {
        insert("7");
    }

    @OnClick(R.id.num8)
    public void onNum8() {
        insert("8");
    }

    @OnClick(R.id.num9)
    public void onNum9() {
        insert("9");
    }

    @OnClick(R.id.num0)
    public void onNum0() {
        insert("0");
    }

    @OnClick(R.id.num11)
    public void onNum11() {//删除
        delete();
    }
}
