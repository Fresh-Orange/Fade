package com.sysu.pro.fade.message.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewHolder;
import com.sysu.pro.fade.discover.viewHoler.UserViewholder;
import com.sysu.pro.fade.message.Class.NotificationUser;
import com.sysu.pro.fade.message.Utils.DateUtils;
import com.sysu.pro.fade.message.ViewHolder.ContributeViewHolder;

import java.util.List;

/**
 * Created by yellow on 2017/12/30.
 */

public class ContributionAdapter extends DBaseRecyclerViewAdapter<Note> {
    private List<Note> userList;
    private Context context;


    public ContributionAdapter(Context context,List<Note> userList) {
        super(userList, context);
        this.userList = userList;
        this.context = context;
    }
//    @Override
//    public ContributeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
////        context = parent.getContext();
//        View view = LayoutInflater.from(context)
//                .inflate(R.layout.item_contribution, parent, false);
//        final ContributeViewHolder holder = new ContributeViewHolder(view);
//        holder.userView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //跳转到聊天界面
//            }
//        });
//        return holder;
//    }

    @Override
    protected DBaseRecyclerViewHolder onCreateViewHolder1(ViewGroup parent, int viewType) {
        return new ContributeViewHolder(context,parent, R.layout.item_contribution, this);
    }

//    @Override
//    public void onBindViewHolder(final ContributeViewHolder holder, int position) {
//        //对RecyclerView子项的数据进行赋值，在每个子项被滚动到屏幕内的时候执行
//        //获得当前项的实例
//        Note user = userList.get(position);
//        String user_icon = Const.BASE_IP + user.getHead_image_url();    //头像
//        String user_id = user.getNickname();       //用户名字
//
//        String user_time = DateUtils.changeToDate(user.getPost_time().substring(0,user.getPost_time().length() - 2));     //聊天时间
//
//        Integer status = user.getType();    //1是增2是减
//        Glide.with(context).load(user_icon).into(holder.user_icon);
//        holder.user_id.setText(user_id);
//        holder.user_time.setText(user_time);
//        if (status == 1) {
//            holder.user_status_text.setText("续");
//            holder.user_image.setImageResource(R.drawable.add);
//        }
//        else {
//            holder.user_status_text.setText("减");
//            holder.user_image.setImageResource(R.drawable.minus);
//        }
//    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
