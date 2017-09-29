package com.sysu.pro.fade.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.DetailGood;
import com.sysu.pro.fade.home.view.GoodListViewHolder;

import java.util.List;

/**
 * Created by road on 2017/9/4.
 */
public class GoodListAdapter extends RecyclerView.Adapter<GoodListViewHolder>{
    private List<DetailGood>detailGoods;
    private Context context;
    public GoodListAdapter(Context context, List<DetailGood>detailGoods){
        this.detailGoods = detailGoods;
        this.context = context;
    }
    @Override
    public GoodListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.good_item,viewGroup,false);
        GoodListViewHolder goodListViewHolder = new GoodListViewHolder(view);
        return goodListViewHolder;
    }

    @Override
    public void onBindViewHolder(GoodListViewHolder holder, int position) {
        Picasso.with(context).load(detailGoods.get(position).getHead_image_url()).into(holder.iv_good_head);
        String x = detailGoods.get(position).getGood_time().toString();
        holder.good_list_good_time.setText(detailGoods.get(position).getGood_time());
        holder.good_list_nickname.setText(detailGoods.get(position).getNickname());
    }

    @Override
    public int getItemCount() {
        return detailGoods.size();
    }
}
