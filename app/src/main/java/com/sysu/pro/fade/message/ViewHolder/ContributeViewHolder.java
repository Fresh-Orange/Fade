package com.sysu.pro.fade.message.ViewHolder;
import android.content.Context;
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
import com.sysu.pro.fade.message.Utils.DateUtils;

/**
 * Created by road on 2017/12/31.
 */

public class ContributeViewHolder extends DBaseRecyclerViewHolder<Note> implements View.OnClickListener {

    View userView;
    ImageView user_icon;    //头像
    TextView user_id;       //用户名字
    TextView user_status_text;  //减/续文字
    ImageView user_image;   //详情图片
    TextView user_time;     //聊天时间
    Context mContext;
    public ContributeViewHolder(Context mContext, ViewGroup parent, int res, DBaseRecyclerViewAdapter dBaseRecyclerViewAdapter) {
        super(parent, res, dBaseRecyclerViewAdapter);
        this.mContext = mContext;
        user_icon = (ImageView) $(R.id.contribution_icon);
        user_id = (TextView) $(R.id.contribution_user_id);
        user_status_text = (TextView) $(R.id.contribution_status);
        user_image = (ImageView) $(R.id.contribution_status_image);
        user_time = (TextView) $(R.id.contribution_time);
        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (getOnClickItemListsner() != null) {
            getOnClickItemListsner().onClick(getAdapterItemPosition());
        }
    }

    @Override
    public void setData(Note user, int position) {
        //对RecyclerView子项的数据进行赋值，在每个子项被滚动到屏幕内的时候执行
        //获得当前项的实例
        String user_icon = Const.BASE_IP + user.getHead_image_url();    //头像
        String user_id = user.getNickname();       //用户名字

        String user_time = DateUtils.changeToDate(user.getPost_time().substring(0,user.getPost_time().length() - 2));     //聊天时间

        Integer status = user.getType();    //1是增2是减
        Glide.with(mContext).load(user_icon).into(this.user_icon);
        this.user_id.setText(user_id);
        this.user_time.setText(user_time);
        if (status == 1) {
            this.user_status_text.setText("续");
            this.user_image.setImageResource(R.drawable.add);
        }
        else {
            this.user_status_text.setText("减");
            this.user_image.setImageResource(R.drawable.minus);
        }
    }
}
