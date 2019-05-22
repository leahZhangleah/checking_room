package com.example.checking_room;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private List<T> list;

    public MyAdapter() {
        list = new ArrayList<>();
    }

    public void setList(List<T> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_smooth_scroll, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(list.get(position % list.size()));
    }

    @Override
    public int getItemCount() {
        return list.size() > 0 ? Integer.MAX_VALUE : 0;
    }
}

class ViewHolder<T> extends RecyclerView.ViewHolder {
    private TextView numberTv,nameTv,roomTv;

    public ViewHolder(View itemView) {
        super(itemView);
        numberTv = (TextView) itemView.findViewById(R.id.waiting_number);
        nameTv = itemView.findViewById(R.id.waiting_name);
        roomTv = itemView.findViewById(R.id.waiting_room);
    }

    public void bindData(T msg) {
        if(msg instanceof Waitmsg){
            Waitmsg waitmsg = (Waitmsg) msg;
            numberTv.setText(waitmsg.getPdhm()+"号");
            nameTv.setText(waitmsg.getBrxm());//todo
            roomTv.setText(waitmsg.getFjmc());
        }else if(msg instanceof Ghmsg){
            Ghmsg ghmsg = (Ghmsg) msg;
            numberTv.setText(ghmsg.getPdhm()+"号");
            nameTv.setText(ghmsg.getBrxm());
            roomTv.setText("过号");
        }

    }
}