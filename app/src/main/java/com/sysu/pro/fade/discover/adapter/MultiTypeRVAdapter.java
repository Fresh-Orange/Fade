package com.sysu.pro.fade.discover.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sysu.pro.fade.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LaiXiancheng on 2018/1/27.
 * Email: lxc.sysu@qq.com
 * 多类型的RecyclerView适配器
 * 对于每一种在该RecyclerView中出现的item类型，都需要调用registerType函数进行注册
 */

public class MultiTypeRVAdapter extends RecyclerView.Adapter<MultiTypeRVAdapter.MTypeViewHolder> {
    private List<Object> itemList = new ArrayList<>();
    private Map<String, Integer> name2type = new HashMap<>();
    private List<BinderInfo> binderInfos= new ArrayList<>();
    private ViewBinder defaultBinder;
    private int defaultViewId = R.layout.multitype_default_item;

    public MultiTypeRVAdapter(Context context, List<Object> itemList) {
        this.itemList = itemList;
        defaultBinder = new ViewBinder() {
            @Override
            public void bindView(View itemView, Object ob, int position) {
                //空！
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        String name = itemList.get(position).getClass().getName();
        Integer type = name2type.get(name);
        if (type == null){
            name2type.put(name, binderInfos.size());
            binderInfos.add(new BinderInfo(defaultBinder, defaultViewId));
            type = name2type.get(name);
        }
        return type;
    }

    /**
     * 注册item类型，对于每一种在该RecyclerView中出现的item类型，都需要调用这个函数进行注册
     * @param rClass item类型的class
     * @param binder 待用户实现的数据绑定类
     * @param viewId 这种item对应的layoutID
     */
    public void registerType(Class rClass, ViewBinder binder, int viewId){
        String className = rClass.getName();
        name2type.put(className, binderInfos.size());
        binderInfos.add(new BinderInfo(binder, viewId));
    }


    @Override
    public MTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        MTypeViewHolder viewHolder;
        ViewBinder binder = binderInfos.get(viewType).binder;
        int viewId = binderInfos.get(viewType).id;
        view = LayoutInflater.from(parent.getContext()).inflate(viewId, parent, false);
        viewHolder = new MTypeViewHolder(binder, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MTypeViewHolder holder, int position) {
        holder.bindView();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    class MTypeViewHolder extends RecyclerView.ViewHolder {
        ViewBinder binder;
        public MTypeViewHolder(ViewBinder binder, View itemView) {
            super(itemView);
            this.binder = binder;
        }
        public void bindView(){
            Object ob = itemList.get(getAdapterPosition());
            //调用外部传入的binder的绑定数据的接口
            binder.bindView(itemView, ob, getAdapterPosition());
        }
    }

    /**
     * 留给外部进行实现的数据与view的绑定类
     */
    abstract static public class ViewBinder {
        abstract public void bindView(View itemView, Object ob, int position);
    }

    private class BinderInfo{
        ViewBinder binder;
        int id;
        BinderInfo(ViewBinder binder, int id) {
            this.binder = binder;
            this.id = id;
        }
    }
}


