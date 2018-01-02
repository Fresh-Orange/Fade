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
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.User;

import java.util.List;

/**
 * Created by yellow on 2017/12/30.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.FansViewHolder>{
    private List<Comment> commentList;
    private Context context;

    static class FansViewHolder extends RecyclerView.ViewHolder {
        View userView;
        ImageView user_icon;    //头像
        TextView user_id;       //用户名字
        ImageView user_image;   //详情图片
        TextView user_time;     //回复时间
        TextView user_content;  //回复内容
        public FansViewHolder(View view) {
            super(view);
            userView = view;
            user_icon = (ImageView) view.findViewById(R.id.comment_icon);
            user_id = (TextView) view.findViewById(R.id.comment_user_id);
            user_image = (ImageView) view.findViewById(R.id.comment_image);
            user_time = (TextView) view.findViewById(R.id.comment_time);
            user_content = (TextView) view.findViewById(R.id.comment_content);
        }
    }

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.commentList = commentList;
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
        Comment comment = commentList.get(position);
        String user_icon = Const.BASE_IP + comment.getHead_image_url();    //头像
        String user_id = comment.getNickname();       //用户名字
        String user_time = comment.getComment_time();     //回复时间
        String user_content = comment.getComment_content(); //回复内容

        Glide.with(context).load(user_icon).into(holder.user_icon);
        holder.user_id.setText(user_id);
        holder.user_time.setText(user_time);
        holder.user_content.setText(user_content);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

}
