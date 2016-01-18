package com.angcyo.ondemand.view;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.angcyo.ondemand.Main2Activity;
import com.angcyo.ondemand.R;
import com.angcyo.ondemand.components.RWorkService;
import com.angcyo.ondemand.components.RWorkThread;
import com.angcyo.ondemand.control.RTableControl;
import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.event.EventRegister;
import com.angcyo.ondemand.model.TableMember;
import com.angcyo.ondemand.util.PopupTipWindow;
import com.angcyo.ondemand.util.Util;

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
    AppCompatButton register;

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
    //
        //
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
                            RTableControl.addMember(new TableMember());
                            EventBus.getDefault().post(eventRegister);
                        } catch (Exception e) {
                            eventRegister.isSuccess = false;
                            EventBus.getDefault().post(eventRegister);
                        }
                    } else {
                        EventBus.getDefault().post(new EventNoNet());
                    }
                }
            });
        } else {

        }
    }

    private boolean verifyEdit() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(EventRegister event) {
        if (event.isSuccess) {
            mBaseActivity.launchActivity(Main2Activity.class);
            mBaseActivity.finish();
        } else {
            PopupTipWindow.showTip(mBaseActivity, "注册失败");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}
