package com.angcyo.ondemand.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.ondemand.R;
import com.angcyo.ondemand.model.OddnumBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by angcyo on 15-09-27-027.
 */
public class AddItemAdapter extends RecyclerView.Adapter<AddItemAdapter.ViewHolder> {

    List<OddnumBean> datas;

    public AddItemAdapter() {
        datas = new ArrayList<>();
    }

    public AddItemAdapter(List<OddnumBean> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = View.inflate(parent.getContext(), R.layout.adapter_add_item, null);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.platform.setText(datas.get(position).caption);
        holder.oddnum.setText(datas.get(position).oddnum);
        if (position % 2 == 0) {//偶数行
            holder.layout.setBackgroundResource(R.color.colorAccent);
        } else {
            holder.layout.setBackgroundResource(android.R.color.transparent);
        }
    }

    public void addItem(OddnumBean bean) {
        datas.add(bean);
        notifyItemInserted(datas.size() - 1);
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
        @Bind(R.id.layout)
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
