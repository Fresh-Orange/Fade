package com.sysu.pro.fade.publish.map.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.sysu.pro.fade.R;

import java.util.List;

/**
 * Created by yellow on 2018/1/25.
 */

public class LocNearAddressAdapter extends RecyclerView.Adapter
    implements View.OnClickListener{

    private final static int FOOT_COUNT = 1;

    private final static int TYPE_CONTENT = 1;
    private final static int TYPE_FOOTER = 2;

    private List<PoiItem> retailList;
    private Context context;
    private String city;
    private String address;

    private String keyword;

    boolean shouldSet;

    @Override
    public int getItemCount() {
        //获得当前List的size
        return retailList.size() + getFootCount();
    }

    public static int getFootCount() {
        return FOOT_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        int contentSize = getItemCount();
        Log.d("foot", "position! " + position);
        Log.d("foot", "contentSize! " + contentSize);
        if(position == contentSize - 1){ // 尾部
            Log.d("foot", "here!");
            return TYPE_FOOTER;
        }else{
            return TYPE_CONTENT;
        }
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        View retailView;
        TextView city;
        TextView address;
        LinearLayout footView;
        TextView foot_address;
        public ContentHolder(View view, int viewType) {
            super(view);
            if (viewType == TYPE_CONTENT) {
                retailView = view;
                city = (TextView) view.findViewById(R.id.city);
                address = (TextView) view.findViewById(R.id.address);
            }
            else {
                footView = (LinearLayout) view;
                foot_address = view.findViewById(R.id.suggestion_address_bottom);
            }
        }
    }

    // 尾部
    static class FootHolder extends RecyclerView.ViewHolder{
        View footView;
        TextView address;
        public FootHolder(View view) {
            super(view);
            footView = view;
            address = (TextView) view.findViewById(R.id.suggestion_address_bottom);
        }
    }

    public LocNearAddressAdapter(List<PoiItem> retailList, Context context, String keyword) {
        //初始化Adapter
        this.retailList = retailList;
        this.context = context;
        this.keyword = keyword;
        shouldSet = !keyword.isEmpty();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_CONTENT){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map, parent,
                    false);
            return new ContentHolder(itemView, viewType);
        }
        else{
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_foot, parent,
                    false);
            return new ContentHolder(itemView, viewType);
        }
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
            Log.d("adapter", "getTag: " + (int)v.getTag());
            mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final LocNearAddressAdapter.ContentHolder newHolder = (LocNearAddressAdapter.ContentHolder) holder;
        if (getItemViewType(position) == TYPE_CONTENT) { // 内容
            //对RecyclerView子项的数据进行赋值，在每个子项被滚动到屏幕内的时候执行
            //获得当前项的实例
            PoiItem poiItem = retailList.get(position);
            //将数据设置到ViewHolder中
            city = poiItem.getTitle();
            address = poiItem.getSnippet();
            //设置城市
            newHolder.city.setText(city);
            //设置信息
            newHolder.address.setText(address);
//            newHolder.itemView.setTag(position);
            newHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(newHolder.itemView, position);
                }
            });
//            myHolder.itemText.setText(list.get(position - 1));
        }
        else { // 尾部
            if (shouldSet) {
                //应该可见
                newHolder.footView.setVisibility(View.VISIBLE);
                newHolder.foot_address.setText(keyword);
//                newHolder.footView.setTag(position);
                newHolder.footView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onItemClick(newHolder.itemView, position);
                    }
                });
            }
            else {
                //不可见
                newHolder.footView.setVisibility(View.GONE);
            }
        }
    }

    public void setKeyword(String keyword) {
        if (keyword.isEmpty())
            shouldSet = false;
        else {
            this.keyword = keyword;
            shouldSet = true;
        }
    }
    public String getCity() {
        return city;
    }





}
