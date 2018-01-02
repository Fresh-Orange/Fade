package com.sysu.pro.fade.message.Adapter;

import android.content.Context;
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

import java.util.List;

/**
 * Created by yellow on 2017/12/30.
 */

public class FansAdapter extends RecyclerView.Adapter<FansAdapter.FansViewHolder>{
    private List<User> userList;
    private Context context;

    static class FansViewHolder extends RecyclerView.ViewHolder {
        View userView;
        ImageView user_icon;    //头像
        TextView user_id;       //用户名字
        TextView user_summary;     //聊天时间
        TextView follow_status_no;  //未关注
        TextView follow_status_yes;  //已关注
        public FansViewHolder(View view) {
            super(view);
            userView = view;
            user_icon = (ImageView) view.findViewById(R.id.follow_user_icon);
            user_id = (TextView) view.findViewById(R.id.follow_user_id);
            user_summary = (TextView) view.findViewById(R.id.follow_user_summary);
            follow_status_no = (TextView) view.findViewById(R.id.follow_status_no);
            follow_status_yes = (TextView) view.findViewById(R.id.follow_status_yes);
        }
    }

    public FansAdapter(Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
    }
    @Override
    public FansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_follow, parent, false);
        final FansViewHolder holder = new FansViewHolder(view);
        holder.userView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到聊天界面
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final FansViewHolder holder, int position) {
        //对RecyclerView子项的数据进行赋值，在每个子项被滚动到屏幕内的时候执行
        //获得当前项的实例
        User user = userList.get(position);
        String user_icon = Const.BASE_IP + user.getHead_image_url();    //头像
        String user_id = user.getNickname();       //用户名字
        String user_summary = user.getSummary();     //聊天时间

        Glide.with(context).load(user_icon).into(holder.user_icon);
        holder.user_id.setText(user_id);
        holder.user_summary.setText(user_summary);
        Integer status = 1;    //1是已关注2是未关注
        if (status == 1) {
            holder.follow_status_yes.setVisibility(View.VISIBLE);
            holder.follow_status_no.setVisibility(View.GONE);
        }
        else {
            holder.follow_status_no.setVisibility(View.VISIBLE);
            holder.follow_status_yes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
