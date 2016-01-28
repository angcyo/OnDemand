package com.angcyo.ondemand.base;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.angcyo.ondemand.OdApplication;
import com.angcyo.ondemand.R;
import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.util.PopupTipWindow;
import com.angcyo.ondemand.view.ProgressFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import me.drakeet.materialdialog.MaterialDialog;

public abstract class BaseActivity extends AppCompatActivity {

    public static Handler handler;
    protected ProgressFragment progressFragment = null;
    protected MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        initBefore();
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        initView(savedInstanceState);
        initAfter();
        initEvent();
        initViewData();

        initWindowAnim();
    }

    protected abstract int getContentView();

    //设置窗口动画
    private void initWindowAnim() {
        getWindow().setWindowAnimations(R.style.WindowAnim);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏
    }

    //初始化
    private void init() {
        handler = new StaticHandler(this);
    }


    /**
     * Init before _.
     */
    protected void initBefore() {

    }

    protected void initEvent() {

    }

    protected void initViewData() {

    }

    /**
     * Init view.
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * Init after.
     */
    protected abstract void initAfter();

    public void launchActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    public void launchActivity(Class c, Bundle args) {
        Intent intent = new Intent(this, c);
        intent.putExtras(args);
        startActivity(intent);
    }

    public void showDialogTip(String tip) {
        if (progressFragment != null) {
            progressFragment.updateText(tip);
            return;
        }
        progressFragment = ProgressFragment.newInstance(tip);
        progressFragment.show(getSupportFragmentManager(), "dialog_tip");
    }

    public void showDialogTip(String tip, boolean cancel) {
        if (progressFragment != null) {
            progressFragment.updateText(tip);
            return;
        }
        progressFragment = ProgressFragment.newInstance(tip);
        progressFragment.show(getSupportFragmentManager(), "dialog_tip");
    }

    public void hideDialogTip() {
        if (progressFragment != null) {
            progressFragment.dismiss();
            progressFragment = null;
        }
    }

    @TargetApi(19)
    protected void initWindow(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//导航栏
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setTintResource(color);//设置状态栏颜色
            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setNavigationBarTintResource(R.color.dark_green);//设置导航栏颜色
//            tintManager.setNavigationBarTintEnabled(false);
        }
    }

    @TargetApi(19)
    protected void setStateBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(color);//设置状态栏颜色
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    protected void handMessage(Message msg, int what, Object obj) {

    }

    public void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public void sendMessage(int what, Object obj) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    public void sendRunnable(Runnable runnable) {
        handler.post(runnable);
    }

    public void sendDelayRunnable(Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }


    public void removeCallbacks(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }

    protected OdApplication getApp() {
        return ((OdApplication) getApplication());
    }

    protected void showMaterialDialog(String title, String message,
                                final View.OnClickListener positiveListener, final View.OnClickListener negativeListener,
                                DialogInterface.OnDismissListener onDismissListener) {
        mMaterialDialog = new MaterialDialog(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确认", positiveListener)
                .setNegativeButton("取消", negativeListener)
                .setOnDismissListener(onDismissListener);
        mMaterialDialog.setCanceledOnTouchOutside(false);
        mMaterialDialog.show();
        try {
            Field mPositiveButton = mMaterialDialog.getClass().getDeclaredField("mPositiveButton");
            mPositiveButton.setAccessible(true);
            ((Button) mPositiveButton.get(mMaterialDialog)).setTextColor(getResources().getColor(R.color.colorAccent));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void noNet(EventNoNet event) {
        hideDialogTip();
        PopupTipWindow.showTip(this, "请检查网络连接");
    }

    static class StaticHandler extends Handler {
        BaseActivity context;

        public StaticHandler(BaseActivity context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (context != null && msg != null) {
                context.handMessage(msg, msg.what, msg.obj);
            }
        }
    }
}
