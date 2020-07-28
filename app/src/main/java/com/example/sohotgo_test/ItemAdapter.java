package com.example.sohotgo_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {


    List<ListData> list;
    Context context;


    public ItemAdapter(Context context) {
        this.context = context;
        list=new ArrayList<>();
    }

    public ItemAdapter(List<ListData>list,Context context) {
        this.context = context;
        this.list=list;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_layout, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,  int position) {
        ListData data=list.get(position);
        if(data.getFlag()==MainActivity.LEFT){
            holder.tvLeft.setVisibility(View.VISIBLE);
            holder.tvRight.setVisibility(View.GONE);
            holder.tvLeft.setText(data.getContent());
        }else if(data.getFlag()==MainActivity.RIGHT){
            holder.tvLeft.setVisibility(View.GONE);
            holder.tvRight.setVisibility(View.VISIBLE);
            holder.tvRight.setText(data.getContent());
        }
    }

    //添加子项
    public void addItem(String str, int leftOrRight) {
        list.add(new ListData(str,leftOrRight));
        notifyItemInserted(list.size()-1);
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLeft,tvRight;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvLeft = (TextView) itemView.findViewById(R.id.leftTv);
            tvRight = (TextView) itemView.findViewById(R.id.rightTv);

        }
    }
}