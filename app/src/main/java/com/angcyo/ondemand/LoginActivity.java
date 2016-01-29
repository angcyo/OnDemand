package com.angcyo.ondemand;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.InputType;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.ondemand.base.BaseActivity;
import com.angcyo.ondemand.base.BaseFragment;
import com.angcyo.ondemand.components.RConstant;
import com.angcyo.ondemand.components.RWorkService;
import com.angcyo.ondemand.components.RWorkThread;
import com.angcyo.ondemand.control.RTableControl;
import com.angcyo.ondemand.event.EventLogin;
import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.model.TableCompany;
import com.angcyo.ondemand.model.TableMember;
import com.angcyo.ondemand.model.TableSellerIndex;
import com.angcyo.ondemand.model.UserInfo;
import com.angcyo.ondemand.util.MD5;
import com.angcyo.ondemand.util.PopupTipWindow;
import com.angcyo.ondemand.util.Util;
import com.angcyo.ondemand.view.RegisterFragment;
import com.orhanobut.hawk.Hawk;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by angcyo on 15-09-27-027.
 */
public class LoginActivity extends BaseActivity implements View.OnLongClickListener {
    public static String KEY_USER_NAME = "user_name";
    public static String KEY_USER_PW = "user_pw";
    public static String KEY_REMEMBER_PW = "remember_pw";
    public static String KEY_SHOW_PW = "show_pw";
    public static String KEY_USER_COMPANY = "user_company";
    public static boolean useSellerLogin = false;//使用商家匹配登录, 默认使用物流公司登录
    @Bind(R.id.phone)
    EditText phone;
    @Bind(R.id.pw)
    EditText pw;
    @Bind(R.id.company)
    EditText company;
    @Bind(R.id.login)
    Button login;
    @Bind(R.id.app_ver)
    TextView appVer;
    String strPhone, strPw, strCompany;
    UserInfo userInfo;
    @Bind(R.id.cbUseSeller)
    AppCompatCheckBox cbUseSeller;
    @Bind(R.id.remember)
    AppCompatCheckBox remember;
    @Bind(R.id.show)
    AppCompatCheckBox show;
    @Bind(R.id.logo_new)
    ImageView logoNew;
    @Bind(R.id.control_layout)
    View controlLayout;

    int controlLayoutHeight;//保存高度,用来检测键盘是否弹起

    /**
     * 判断是否是手机号码
     */
    public static boolean isPhone(CharSequence input) {
        Pattern pattern = Pattern.compile("^[1][358][0-9]{9}$");
        return pattern.matcher(input).matches();
    }

    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(getContentView());
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initAfter() {
        appVer.setText("版本:" + RConstant.VERSION);

        phone.setText(((String) Hawk.get(KEY_USER_NAME)));
        pw.setText(Hawk.get(KEY_USER_PW, ""));
        company.setText(((String) Hawk.get(KEY_USER_COMPANY)));

        cbUseSeller.setVisibility(View.GONE);
        company.setVisibility(View.GONE);

        controlLayout.post(new Runnable() {
            @Override
            public void run() {
                controlLayoutHeight = controlLayout.getMeasuredHeight();
            }
        });

        //v2 版本修改
        if (OdApplication.userInfo != null && Hawk.get(KEY_REMEMBER_PW, false)) {
            launchActivity(Main2Activity.class);
            super.onBackPressed();
        }
    }



    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        initSoftInput();

        phone.setOnLongClickListener(this);
        pw.setOnLongClickListener(this);
        company.setOnLongClickListener(this);

        cbUseSeller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                useSellerLogin = isChecked;
            }
        });

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Hawk.put(KEY_REMEMBER_PW, isChecked);
                if (!isChecked) {
                    Hawk.put(KEY_USER_PW, "");
                }
            }
        });

        show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Hawk.put(KEY_SHOW_PW, isChecked);
                if (isChecked) {
                    pw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    pw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    @Override
    protected void initViewData() {
        super.initViewData();
        remember.setChecked(Hawk.get(KEY_REMEMBER_PW, true));
        show.setChecked(Hawk.get(KEY_SHOW_PW, false));
    }

    @OnClick(R.id.login)
    public void login() {
        if (verifyEdit()) {
            showDialogTip("登录中...");
            RWorkService.addTask(new RWorkThread.TaskRunnable() {
                @Override
                public void run() {
                    EventLogin eventLogin = new EventLogin();
                    if (Util.isNetOk(LoginActivity.this)) {
//                        RTableControl.getAllMember();
//                        RTableControl.getAllCompany();
//                        RTableControl.getAllPlatform();
//                        RTableControl.getAllSellerIndexes();
                        TableMember member = RTableControl.getLoginMember(phone.getText().toString(), MD5.toMD5(pw.getText().toString()));
                        eventLogin.isSucceed = true;
                        eventLogin.member = member;
                        EventBus.getDefault().post(eventLogin);
                    } else {
                        EventBus.getDefault().post(new EventNoNet());
                    }
                }
            });
        } else {

        }
    }

    @OnClick(R.id.register)
    public void register() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
            hideSoftKeyboard(this, (EditText) currentFocus);
        }

        addFragment(RegisterFragment.newInstance());
    }

    public void addFragment(BaseFragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.alpha_0to1, R.anim.alpha_1to0);
        fragmentTransaction.add(R.id.container, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }

    private boolean verifyEdit() {
        strPhone = phone.getText().toString();
        strPw = pw.getText().toString();
        strCompany = company.getText().toString();

        if (Util.isEmpty(strPhone) || strPhone.length() != 11 || !isPhone(strPhone)) {
            phone.setError("请输入有效手机号码");
            phone.requestFocus();
            return false;
        }
        if (Util.isEmpty(strPw)) {
            pw.setError("请输入密码");
            pw.requestFocus();
            return false;
        }
//        if (Util.isEmpty(strCompany)) {
//            company.setError("请输入服务商家");
//            company.requestFocus();
//            return false;
//        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventLogin event) {
        hideDialogTip();
        if (event.member != null) {
            onLoginSucceed(event.member, null, null);
            return;
        }

//        for (TableMember member : RTableControl.members) {
//            if (member.getPhone().trim().equalsIgnoreCase(strPhone) && member.getPsw().trim().equals(MD5.toMD5(strPw))) {//手机号 和密码 相等
//                for (TableSellerIndex sellerIndex : RTableControl.sellerIndexes) {
//                    if (sellerIndex.getCompany().trim().equalsIgnoreCase(strCompany)) {//含有登录的 服务商家
        //登录成功
//                        return;
//                    }
//                }
//                company.setError("不存在此服务商家");
//                company.requestFocus();
//                return;

//                if (useSellerLogin) {
//                    for (TableSellerIndex sellerIndex : RTableControl.sellerIndexes) {
//                        if (sellerIndex.getSid() == member.getId_company() && sellerIndex.getCompany().trim().equalsIgnoreCase(strCompany)) {//所属 服务商家 相等
//                            //登录成功
//                            onLoginSucceed(member, null, sellerIndex);
//                            return;
//                        }
//                    }
//                } else {
//                    for (TableCompany company : RTableControl.companys) {
//                        if (company.getSid() == member.getId_company() && company.getCaption().trim().equalsIgnoreCase(strCompany)) {//所属xx 物流 相等
//                            //登录成功
//                            onLoginSucceed(member, company, null);
//                            return;
//                        }
//                    }
//                }
//            }
//        }
        PopupTipWindow.showTip(this, "登录失败");
    }

    private void onLoginSucceed(TableMember member, TableCompany tableCompany, TableSellerIndex tableSellerIndex) {
        userInfo = new UserInfo();
        userInfo.member = member;
        userInfo.company = tableCompany;
        userInfo.sellerIndex = tableSellerIndex;
        OdApplication.userInfo = userInfo;

        //保存登录信息
        Hawk.put(KEY_USER_NAME, strPhone);
        if (remember.isChecked()) {
            Hawk.put(KEY_USER_PW, strPw);
        }
        Hawk.put(KEY_USER_COMPANY, strCompany);

        RWorkService.addTask(new RWorkThread.TaskRunnable() {
            @Override
            public void run() {
                if (Util.isNetOk(LoginActivity.this)) {
                    RTableControl.updateOnline(OdApplication.userInfo.member.getSid());
                } else {
                    EventBus.getDefault().post(new EventNoNet());
                }
            }
        });
//        launchActivity(MainActivity.class);
        // v2 版本修改
        launchActivity(Main2Activity.class);
        finish();
    }

    @Override
    public boolean onLongClick(View v) {
        if (v instanceof EditText) {
            ((EditText) v).setText("");
        }
        return false;
    }

    /**
     * 监听软键盘事件
     */
    private void initSoftInput() {
        controlLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = controlLayout.getHeight();
                if (controlLayoutHeight - height > 100) {
                    onSoftInputEvent(true);
                } else {
                    onSoftInputEvent(false);
                }
            }
        });
//        mAppbarLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                if (bottom > getStatusBarHeight(LoginActivity.this)) {//键盘弹出
//                    onSoftInputEvent(true);
//                } else {
//                    onSoftInputEvent(false);
//                }
//            }
//        });
    }

    private void onSoftInputEvent(boolean isShow) {
//        mAppbarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(this)));
        logoNew.setVisibility(isShow ? View.GONE : View.VISIBLE);
    }
}
