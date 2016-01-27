package com.angcyo.ondemand.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.angcyo.ondemand.Main2Activity;
import com.angcyo.ondemand.OdApplication;
import com.angcyo.ondemand.R;
import com.angcyo.ondemand.components.RWorkService;
import com.angcyo.ondemand.components.RWorkThread;
import com.angcyo.ondemand.control.RTableControl;
import com.angcyo.ondemand.event.EventGetTradingArea;
import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.event.EventRegister;
import com.angcyo.ondemand.model.TableMember;
import com.angcyo.ondemand.model.TableTradingArea;
import com.angcyo.ondemand.model.UserInfo;
import com.angcyo.ondemand.util.MD5;
import com.angcyo.ondemand.util.PhoneUtil;
import com.angcyo.ondemand.util.PopupTipWindow;
import com.angcyo.ondemand.util.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by angcyo on 16-01-17-017.
 */
public class RegisterFragment extends BaseFragment {

    public ArrayList<TableTradingArea> tableTradingAreas;//所有商圈
    @Bind(R.id.name)
    AppCompatEditText name;
    @Bind(R.id.card_num)
    AppCompatEditText cardNum;
    @Bind(R.id.family_addrs)
    AppCompatEditText familyAddrs;
    @Bind(R.id.now_addrs)
    AppCompatEditText nowAddrs;
    @Bind(R.id.trading_area)
    AppCompatSpinner tradingArea;
    @Bind(R.id.phone)
    AppCompatEditText phone;
    @Bind(R.id.pw)
    AppCompatEditText pw;
    @Bind(R.id.pw_rp)
    AppCompatEditText pwRp;
    @Bind(R.id.register_member)
    PathButton register;
    ArrayAdapter<String> adapter;
    private TableMember member;
    private boolean isMemberExist = false;

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();

        return fragment;
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {

    }

    @Override
    protected View loadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    protected void initView(View rootView) {
        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String str = phone.getText().toString();
                if (!hasFocus) {//丢失焦点
                    if (!TextUtils.isEmpty(str)) {//不为空
                        if (str.length() < 11) {
                            phone.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    phone.requestFocus();
                                    phone.setError("请输入11位手机号码");
                                }
                            }, 100);
                            return;
                        }
                        verifyPhone(str);
                    }
                }
            }
        });

        adapter = new ArrayAdapter<>(mBaseActivity, R.layout.spinner_item_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        adapter.add("请选择商圈");
        tradingArea.setAdapter(adapter);
    }

    @Override
    protected void initAfter() {
        super.initAfter();
        loadTradingAreaData();
    }

    /**
     * 获取商圈信息
     */
    private void loadTradingAreaData() {
        RWorkService.addTask(new RWorkThread.TaskRunnable() {
            @Override
            public void run() {
                if (Util.isNetOk(mBaseActivity)) {
                    EventGetTradingArea eventGetTradingArea = new EventGetTradingArea();
                    eventGetTradingArea.tableTradingAreas = RTableControl.getAllTradingArea();
                    EventBus.getDefault().post(eventGetTradingArea);
                } else {
                    EventBus.getDefault().post(new EventNoNet());
                }
            }
        });
    }

    @OnClick(R.id.register_member)
    public void onRegister(View view) {
        if (verifyEdit()) {
            mBaseActivity.showDialogTip("注册中...");
            RWorkService.addTask(new RWorkThread.TaskRunnable() {
                @Override
                public void run() {
                    if (Util.isNetOk(mBaseActivity)) {
                        EventRegister eventRegister = new EventRegister();
                        try {
                            if (!RTableControl.isMemberExist(phone.getText().toString())/*验证手机号码是否已注册}*/) {
                                RTableControl.addMember(member);//注册用户
                                eventRegister.isSuccess = true;
                            } else {
                                eventRegister.isSuccess = false;
                            }
                        } catch (Exception e) {
                            eventRegister.isSuccess = false;
                        }
                        EventBus.getDefault().post(eventRegister);
                    } else {
                        EventBus.getDefault().post(new EventNoNet());
                    }
                }
            });
        } else {

        }
    }

    private void verifyPhone(final String phone) {
        RWorkService.addTask(new RWorkThread.TaskRunnable() {
            @Override
            public void run() {
                if (Util.isNetOk(mBaseActivity)) {
                    EventRegister eventRegister = new EventRegister();
                    eventRegister.isMemberExist = RTableControl.isMemberExist(phone);//验证手机号码是否已注册
                    eventRegister.isVerify = true;
                    EventBus.getDefault().post(eventRegister);
                }
            }
        });
    }

    /**
     * 验证输入框信息
     */
    private boolean verifyEdit() {
        member = new TableMember();

        member.setName_real(name.getText().toString());
        if (TextUtils.isEmpty(member.getName_real())) {
            name.requestFocus();
            name.setError("请输入真实姓名.");
            return false;
        }
        member.setName_login(cardNum.getText().toString());
        if (TextUtils.isEmpty(member.getName_login()) || member.getName_login().length() < 17) {
            cardNum.requestFocus();
            cardNum.setError("请输入有效省份证号.");
            return false;
        }

        member.setPhone(phone.getText().toString());
        if (TextUtils.isEmpty(member.getPhone()) || !PhoneUtil.isPhone(member.getPhone())) {
            phone.requestFocus();
            phone.setError("请输入有效手机号码.");
            return false;
        }

        String pwStr = pw.getText().toString();
        if (TextUtils.isEmpty(pwStr)) {
            pw.requestFocus();
            pw.setError("请输入密码.");
            return false;
        }

        String pwRpStr = pwRp.getText().toString();
        if (TextUtils.isEmpty(pwRpStr)) {
            pwRp.requestFocus();
            pwRp.setError("请确认密码.");
            return false;
        }

        if (!pwStr.equals(pwRpStr)) {
            pw.requestFocus();
            pw.setError("两次密码不一致.");
            return false;
        }

        member.setPsw(MD5.toMD5(pwStr));
        if (tradingArea.getSelectedItemPosition() != 0 && tableTradingAreas != null && tableTradingAreas.size() > 0) {
            member.setId_tradingarea(tableTradingAreas.get(tradingArea.getSelectedItemPosition() - 1).getSid());//商圈id
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventRegister event) {
        if (event.isVerify) {
            isMemberExist = event.isMemberExist;
            if (event.isMemberExist) {
                phone.requestFocus();
                phone.setError("手机号码已被注册");
                return;
            }
        } else {
            if (event.isSuccess) {
                PopupTipWindow.showTip(mBaseActivity, "注册成功");
                OdApplication.userInfo = new UserInfo();
                OdApplication.userInfo.member = member;//保存登录信息
                mBaseActivity.launchActivity(Main2Activity.class);
                mBaseActivity.finish();
            } else {
                PopupTipWindow.showTip(mBaseActivity, "注册失败");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventGetTradingArea event) {
        tableTradingAreas = event.tableTradingAreas;
        ArrayList<String> items = new ArrayList<>();

        items.add("请选择商圈...");
        for (TableTradingArea area : tableTradingAreas) {
            items.add(area.getDscrp());
        }
        adapter.clear();
        adapter.addAll(items);
    }

    @Override
    public void onResume() {
        super.onResume();
        e(new Exception().getStackTrace()[0].getMethodName());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onPause() {
        super.onPause();
        e(new Exception().getStackTrace()[0].getMethodName());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        e(new Exception().getStackTrace()[0].getMethodName());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        e(isVisibleToUser + "-->" + new Exception().getStackTrace()[0].getMethodName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        e(new Exception().getStackTrace()[0].getMethodName());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
