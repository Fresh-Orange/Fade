package com.sysu.pro.fade.message.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.CommentMessage;
import com.sysu.pro.fade.message.Utils.DateUtils;
import com.sysu.pro.fade.utils.DisplayUtil;
import com.sysu.pro.fade.view.DisplayParams;
import com.sysu.pro.fade.view.MyTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yellow on 2017/12/30.
 */

public class CommentAdapter extends RecyclerView.Adapter< CommentAdapter.MyHolder>
        implements View.OnClickListener,View.OnLongClickListener{

    private List<CommentMessage> data = new ArrayList<>();
    private Context mContext;

    public View VIEW_FOOTER;
    public View VIEW_HEADER;

    //Type
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;
    private int TYPE_FOOTER = 1002;

    public CommentAdapter(List<CommentMessage> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public CommentAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new  CommentAdapter.MyHolder(VIEW_FOOTER);
        } else if (viewType == TYPE_HEADER) {
            return new  CommentAdapter.MyHolder(VIEW_HEADER);
        } else {
            View normalView = getLayout( R.layout.item_notification_comment);
            normalView.setOnClickListener(this);
            normalView.setOnLongClickListener(this);
            return new CommentAdapter.MyHolder(normalView);
        }
    }

    @Override
    public void onBindViewHolder( CommentAdapter.MyHolder holder, int position) {
        if (data.get(position).getViewType() == null) {
            CommentMessage commentMessage = data.get(position);
            ImageView user_icon = (ImageView)holder.itemView.findViewById(R.id.comment_icon);
            TextView user_id = (TextView) holder.itemView.findViewById(R.id.comment_user_id);
            ImageView user_image = (ImageView) holder.itemView.findViewById(R.id.comment_image);
            TextView user_time = (TextView) holder.itemView.findViewById(R.id.comment_time);
            TextView user_content = (TextView)holder.itemView.findViewById(R.id.comment_content);
            TextView reply = (TextView)holder.itemView.findViewById(R.id.reply);
            LinearLayout user_text = holder.itemView.findViewById(R.id.comment_text);

            String content = commentMessage.getComment_content(); //回复内容
            //String time = DateUtils.changeToDate(comment.getComment_time().substring(0,comment.getComment_time().length() - 2));
            Glide.with(mContext).load(Const.BASE_IP + commentMessage.getFrom_head()).into(user_icon);
            user_id.setText(commentMessage.getFrom_nickname());
           // user_time.setText(time);
            user_content.setText(content);
            user_time.setText(DateUtils.changeToDate(commentMessage.getComment_time().substring(0,
                    commentMessage.getComment_time().length() - 2)));
            if (commentMessage.getExampleImage() == null) {
                //TODO:article
                user_image.setVisibility(View.GONE);
                user_text.setVisibility(View.VISIBLE);
                String result = commentMessage.getNote_content().substring(0, 1);
                Log.d("yellow", "result: " + result);
                Log.d("yellow", "getNote_content: " + commentMessage.getNote_content());
                DisplayParams displayParams = DisplayParams.getInstance(mContext);
                if (!commentMessage.getNote_content().isEmpty()) {
                    MyTextView myTextView1 = new MyTextView(mContext);
                    myTextView1.setText(result);
                    myTextView1.setTextSize(18);
                    myTextView1.setTextAlign(MyTextView.TEXT_ALIGN_CENTER_HORIZONTAL |
                            MyTextView.TEXT_ALIGN_CENTER_VERTICAL);
                    myTextView1.setTextColor(Color.DKGRAY);
                    myTextView1.setBackgroundColor(Color.LTGRAY);
                    user_text.addView(myTextView1, LinearLayout.
                            LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(30, displayParams.scale));
                }
            }
            else {
                Glide.with(mContext).load(Const.BASE_IP +
                        commentMessage.getExampleImage()).into(user_image);
            }

            if (commentMessage.getComment_id() != null) {
                Log.d("yellow", "一级！");
                reply.setVisibility(View.GONE);
            }
            else if (commentMessage.getSecond_id() != null) {
                Log.d("yellow", "二级！");
                reply.setVisibility(View.VISIBLE);
            }
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return  data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getViewType() == null) {
            return TYPE_NORMAL;
        } else if (data.get(position).getViewType() == TYPE_FOOTER) {
            return TYPE_FOOTER;
        } else {
            return TYPE_HEADER;
        }
    }

    private View getLayout(int layoutId) {
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        public MyHolder(View itemView) {
            super(itemView);
        }
    }

    private CommentAdapter.onItemClickListener mOnItemClickListener = null;
    private CommentAdapter.onItemLongClickListener mOnItemLongClickListener = null;

    //定义item点击监听器接口
    public static interface onItemClickListener {
        void onItemClick(View view, int position);
    }
    public static interface onItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(CommentAdapter.onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(CommentAdapter.onItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(v, (int)v.getTag());
        }
        return true;
    }

}
