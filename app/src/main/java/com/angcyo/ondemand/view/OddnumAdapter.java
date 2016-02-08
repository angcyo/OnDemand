package com.angcyo.ondemand.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.ondemand.R;
import com.angcyo.ondemand.base.BaseActivity;
import com.angcyo.ondemand.components.RWorkService;
import com.angcyo.ondemand.components.RWorkThread;
import com.angcyo.ondemand.control.RTableControl;
import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.event.EventOddnumOk;
import com.angcyo.ondemand.model.DeliveryserviceBean;
import com.angcyo.ondemand.util.PhoneUtil;
import com.angcyo.ondemand.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by angcyo on 15-09-27-027.
 */
public class OddnumAdapter extends RecyclerView.Adapter<OddnumAdapter.ViewHolder> {

    public ArrayList<DeliveryserviceBean> datas;
    public AtomicInteger netCommandCount;//网络请求操作的次数,当大于零时,所有请求处理未完成
    Context context;
    MaterialDialog mMaterialDialog;

    public OddnumAdapter(Context context) {
        this.context = context;
        datas = new ArrayList<>();
        netCommandCount = new AtomicInteger();
    }

    public OddnumAdapter(Context context, ArrayList<DeliveryserviceBean> datas) {
        this.datas = datas;
        this.context = context;
        netCommandCount = new AtomicInteger();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = View.inflate(parent.getContext(), R.layout.adapter_oddnum_item, null);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DeliveryserviceBean oddnumBean = datas.get(position);

        holder.platform.setText(oddnumBean.getName());
        holder.oddnum.setText(oddnumBean.getAddress());

        //拨打客户电话
        if (Util.isEmpty(oddnumBean.getPhone())) {
            holder.customerPhone.setText("未识别号码");
            holder.customerPhone.setClickable(false);
        } else {
            holder.customerPhone.setText(oddnumBean.getPhone());
            holder.customerPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhoneUtil.call(context, oddnumBean.getPhone());
                }
            });
        }

        //撤销订单
        holder.cancel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (datas.get(position).getStatus() == 9) {//订单已送达,无法修改
                    holder.cancel.setChecked(!isChecked);
                    return;
                }

                int status;
                if (isChecked) {//撤销订单
                    status = 4;
                } else {//派送中
                    status = 2;
                }
                updateStatus(position, status);
            }
        });

        //送达订单
        holder.ok.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {//
                    mMaterialDialog = new MaterialDialog(context)
                            .setTitle("请注意")
                            .setMessage("订单送达之后,将不可修改")
                            .setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                    ((BaseActivity) context).showDialogTip("订单送达确认中,请稍等...");
                                    updateStatus(position, 9);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                    holder.ok.setChecked(!isChecked);
                                }
                            });
                    mMaterialDialog.show();
                } else {//已送达, 无法取消
                    if (datas.get(position).getStatus() == 9) {//订单已送达,无法修改
                        holder.ok.setChecked(true);
                    }
                }
            }
        });
    }

    private void updateStatus(final int position, final int status) {
        RWorkService.addTask(new RWorkThread.TaskRunnable() {
            @Override
            public void run() {
                if (Util.isNetOk(context)) {
                    EventOddnumOk eventOddnumOk = new EventOddnumOk();
                    eventOddnumOk.state = status;
                    try {
                        netCommandCount.addAndGet(1);
                        RTableControl.updateOddnumState(String.valueOf(datas.get(position).getSeller_order_identifier()), status);
                        eventOddnumOk.isSuccess = true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    datas.get(position).setStatus(status);
                    ((BaseActivity) context).hideDialogTip();
                    EventBus.getDefault().post(eventOddnumOk);
                } else {
                    EventBus.getDefault().post(new EventNoNet());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.platform)
        TextView platform;
        @Bind(R.id.oddnum)
        TextView oddnum;
        @Bind(R.id.cancel)
        CheckBox cancel;
        @Bind(R.id.ok)
        CheckBox ok;
        @Bind(R.id.layout)
        LinearLayout layout;
        @Bind(R.id.customerPhone)
        TextView customerPhone;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
