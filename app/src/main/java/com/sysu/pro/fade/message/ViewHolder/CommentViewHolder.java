package com.sysu.pro.fade.message.ViewHolder;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewHolder;
import com.sysu.pro.fade.message.Utils.DateUtils;

/**
 * Created by road on 2017/12/31.
 */

public class CommentViewHolder extends DBaseRecyclerViewHolder<Comment> implements View.OnClickListener {

    View userView;
    ImageView user_icon;    //头像
    TextView user_id;       //用户名字
    ImageView user_image;   //详情图片
    TextView user_time;     //回复时间
    TextView user_content;  //回复内容
    Context mContext;
    public CommentViewHolder(Context mContext, ViewGroup parent, int res, DBaseRecyclerViewAdapter dBaseRecyclerViewAdapter) {
        super(parent, res, dBaseRecyclerViewAdapter);
        this.mContext = mContext;
        user_icon = (ImageView) $(R.id.comment_icon);
        user_id = (TextView) $(R.id.comment_user_id);
        user_image = (ImageView) $(R.id.comment_image);
        user_time = (TextView) $(R.id.comment_time);
        user_content = (TextView) $(R.id.comment_content);
        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (getOnClickItemListsner() != null) {
            getOnClickItemListsner().onClick(getAdapterItemPosition());
        }
    }

    @Override
    public void setData(Comment comment, int position) {
        //对RecyclerView子项的数据进行赋值，在每个子项被滚动到屏幕内的时候执行
        //获得当前项的实例
        String user_icon = Const.BASE_IP + comment.getHead_image_url();    //头像
        String user_id = comment.getNickname();       //用户名字
//        String user_time = comment.getComment_time();     //回复时间
        String user_content = comment.getComment_content(); //回复内容
        String user_time = DateUtils.changeToDate(comment.getComment_time().substring(0,comment.getComment_time().length() - 2));
        Glide.with(mContext).load(user_icon).into(this.user_icon);
        this.user_id.setText(user_id);
        this.user_time.setText(user_time);
        this.user_content.setText(user_content);
    }
}
