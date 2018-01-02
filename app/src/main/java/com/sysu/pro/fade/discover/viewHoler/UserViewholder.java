package com.sysu.pro.fade.discover.viewHoler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewHolder;

/**
 * Created by road on 2017/12/31.
 */

public class UserViewholder extends DBaseRecyclerViewHolder<User> implements View.OnClickListener {

    public TextView tv_nickname;
    public TextView tv_fade_name;
    public ImageView iv_header;
    private Context mContext;
    public UserViewholder(Context mContext, ViewGroup parent, int res, DBaseRecyclerViewAdapter dBaseRecyclerViewAdapter) {
        super(parent, res, dBaseRecyclerViewAdapter);
        this.mContext = mContext;
        tv_nickname = $(R.id.tv_nickname);
        tv_fade_name = $(R.id.tv_fade_name);
        iv_header = $(R.id.iv_header);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (getOnClickItemListsner() != null) {
            getOnClickItemListsner().onClick(getAdapterItemPosition());
        }
    }

    @Override
    public void setData(User user, int position) {
        //每次默认初始化 因为Stagge会改变高度
        ViewGroup.LayoutParams layoutParams1 = tv_nickname.getLayoutParams();
        layoutParams1.height = 80;
        tv_nickname.setLayoutParams(layoutParams1);
        tv_nickname.setText(user.getNickname());
        tv_fade_name.setText(user.getFade_name());
        Glide.with(mContext).load(Const.BASE_IP + user.getHead_image_url()).into(iv_header);
    }
}
