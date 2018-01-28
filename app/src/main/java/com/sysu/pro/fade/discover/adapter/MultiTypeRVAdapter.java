package com.sysu.pro.fade.discover.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;

import java.util.ArrayList;
import java.util.List;



public class MultiTypeRVAdapter extends RecyclerView.Adapter<MultiTypeRVAdapter.MTypeViewHolder> {
    List<Object> itemList = new ArrayList<>();
    public static final int NORMAL_TYPE = 0;
    public static final int HINT_TYPE = 1;
    NormalItemClickListener normalItemClickListener;
    Context mContext;

    public MultiTypeRVAdapter(Context mContext, List<Object> itemList) {
        this.itemList = itemList;
        this.mContext = mContext;
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position) instanceof HintTypeItem)
            return HINT_TYPE;
        return NORMAL_TYPE;
    }


    @Override
    public MTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        MTypeViewHolder viewHolder;
        if (viewType == HINT_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.random_item, parent, false);
            viewHolder = new MTypeViewHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            viewHolder = new MTypeViewHolder(view);
        }
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


    static class DRecyclerViewHolder extends RecyclerView.ViewHolder {

        public DRecyclerViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MTypeViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nickname;
        public TextView tv_fade_name;
        public ImageView iv_header;
        public MTypeViewHolder(View itemView) {
            super(itemView);
        }
        public void bindView(){
            final Object ob = itemList.get(getAdapterPosition());
            if (getItemViewType() == MultiTypeRVAdapter.HINT_TYPE){
                //如果是提示信息
                TextView textView = itemView.findViewById(R.id.tv_hint);
                textView.setText(((HintTypeItem)ob).getHint());
            }
            else if (getItemViewType() == MultiTypeRVAdapter.NORMAL_TYPE){
                //如果是正常的item
                tv_nickname = itemView.findViewById(R.id.tv_nickname);
                tv_fade_name = itemView.findViewById(R.id.tv_fade_name);
                iv_header = itemView.findViewById(R.id.iv_header);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (normalItemClickListener != null)
                            normalItemClickListener.onClick((User)ob);
                    }
                });

                User user = (User)itemList.get(getAdapterPosition());
                ViewGroup.LayoutParams layoutParams1 = tv_nickname.getLayoutParams();
                layoutParams1.height = 80;
                tv_nickname.setLayoutParams(layoutParams1);
                tv_nickname.setText(Html.fromHtml(user.getNickname()));
                tv_fade_name.setText(Html.fromHtml(user.getFade_name()));
                Glide.with(mContext).load(Const.BASE_IP + user.getHead_image_url()).into(iv_header);
            }
        }
    }

    public void setNormalItemClickListener(NormalItemClickListener clickListener){
        this.normalItemClickListener = clickListener;
    }

    static public interface NormalItemClickListener{
        void onClick(User user);
    }


}


