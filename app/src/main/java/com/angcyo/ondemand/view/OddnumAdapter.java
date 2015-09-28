package com.angcyo.ondemand.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.ondemand.BaseActivity;
import com.angcyo.ondemand.R;
import com.angcyo.ondemand.components.RWorkService;
import com.angcyo.ondemand.components.RWorkThread;
import com.angcyo.ondemand.control.RTableControl;
import com.angcyo.ondemand.event.EventException;
import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.model.OddnumBean;
import com.angcyo.ondemand.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by angcyo on 15-09-27-027.
 */
public class OddnumAdapter extends RecyclerView.Adapter<OddnumAdapter.ViewHolder> {

    List<OddnumBean> datas;
    Context context;

    public OddnumAdapter(Context context) {
        this.context = context;
        datas = new ArrayList<>();
    }

    public OddnumAdapter(Context context, List<OddnumBean> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = View.inflate(parent.getContext(), R.layout.adapter_oddnum_item, null);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.platform.setText(datas.get(position).caption);
        holder.oddnum.setText(datas.get(position).oddnum);
        holder.cancel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (datas.get(position).status == 9) {//订单已送达,无法修改
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

        holder.ok.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {//
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("请注意");
                    builder.setMessage("订单送达之后,将不可修改");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((BaseActivity) context).showDialogTip("订单送达确认中,请稍等...");
                            updateStatus(position, 9);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            holder.ok.setChecked(!isChecked);
                        }
                    });
                    builder.show();
                } else {//已送达, 无法取消
                    if (datas.get(position).status == 9) {//订单已送达,无法修改
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
                    try {
                        RTableControl.updateOddnumState(datas.get(position).oddnum, status);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new EventException());
                        return;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new EventException());
                        return;
                    }
                    datas.get(position).status = status;
                    ((BaseActivity) context).hideDialogTip();
//                    EventBus.getDefault().post(new EventOddnumOk());
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
