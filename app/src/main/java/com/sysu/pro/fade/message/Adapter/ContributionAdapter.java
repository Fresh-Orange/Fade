package com.sysu.pro.fade.message.Adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.message.NotificationUser;

import java.util.List;

/**
 * Created by yellow on 2017/12/30.
 */

public class ContributionAdapter extends RecyclerView.Adapter<ContributionAdapter.ViewHolder>{
    private List<NotificationUser> userList;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View userView;
        ImageView user_icon;    //头像
        TextView user_count;    //聊天数
        TextView user_id;       //用户名字
        TextView user_content;  //聊天内容
        TextView user_time;     //聊天时间
        public ViewHolder(View view) {
            super(view);
            userView = view;
            user_icon = (ImageView) view.findViewById(R.id.notification_user_icon);
            user_count = (TextView) view.findViewById(R.id.notification_user_count);
            user_id = (TextView) view.findViewById(R.id.notification_user_id);
            user_content = (TextView) view.findViewById(R.id.notification_user_content);
            user_time = (TextView) view.findViewById(R.id.notification_time);
        }
    }

    public ContributionAdapter(List<NotificationUser> userList) {
        this.userList = userList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contribution, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.userView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到聊天界面
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //对RecyclerView子项的数据进行赋值，在每个子项被滚动到屏幕内的时候执行
        //获得当前项的实例
        NotificationUser user = userList.get(position);
        Uri user_icon = user.getUser_icon();    //头像
        int user_count = user.getUser_count();    //聊天数
        String user_id = user.getUser_id();       //用户名字
        String user_content = user.getUser_content();  //聊天内容
        String user_time = user.getUser_time();     //聊天时间
        holder.user_icon.setImageURI(user_icon);
        if (user_count > 0) {
            holder.user_count.setVisibility(View.VISIBLE);
            holder.user_count.setText(String.valueOf(user_count));
        }
        else {
            holder.user_count.setVisibility(View.GONE);
        }
        holder.user_id.setText(user_id);
        holder.user_content.setText(user_content);
        holder.user_time.setText(user_time);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
