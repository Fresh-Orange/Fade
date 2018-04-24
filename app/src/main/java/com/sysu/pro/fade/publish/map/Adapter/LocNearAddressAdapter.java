package com.sysu.pro.fade.publish.map.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.message.Adapter.ContributeAdapter;
import com.sysu.pro.fade.publish.PublishActivity;
import com.sysu.pro.fade.publish.map.Activity.SearchPlaceActivity;

import java.util.List;

/**
 * Created by yellow on 2018/1/25.
 */

public class LocNearAddressAdapter extends RecyclerView.Adapter<LocNearAddressAdapter.ViewHolder>
    implements View.OnClickListener{
    private List<PoiItem> retailList;
    private Context context;
    private String address;
    private String detail;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View retailView;
        TextView city;
        TextView address;
        public ViewHolder(View view) {
            super(view);
            retailView = view;
            city = (TextView) view.findViewById(R.id.city);
            address = (TextView) view.findViewById(R.id.address);
        }
    }


    public LocNearAddressAdapter(List<PoiItem> retailList, Context context) {
        //初始化Adapter
        this.retailList = retailList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //将LayoutInflater来为子项加载传入的布局
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_map, parent, false);
        //创建ViewHolder实例，并把加载出来的布局传入到构造函数当中
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        //返回ViewHolder实例
        return holder;
    }

    private onItemClickListener mOnItemClickListener = null;

    //定义item点击监听器接口
    public static interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //对RecyclerView子项的数据进行赋值，在每个子项被滚动到屏幕内的时候执行
        //获得当前项的实例
        PoiItem retail = retailList.get(position);
        //将数据设置到ViewHolder中
        address = retail.getTitle();
        detail = retail.getSnippet();
        //设置城市
        holder.city.setText(address);
        //设置信息
        holder.address.setText(detail);
        holder.itemView.setTag(position);
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int getItemCount() {
        //获得当前List的size
        return retailList.size();
    }
}
