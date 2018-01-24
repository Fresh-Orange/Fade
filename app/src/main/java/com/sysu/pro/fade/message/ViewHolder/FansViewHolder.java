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

public class FansViewHolder extends DBaseRecyclerViewHolder<User> implements View.OnClickListener {

    View userView;
    ImageView user_icon;    //头像
    TextView user_id;       //用户名字
    TextView user_summary;     //聊天时间
    TextView follow_status_no;  //未关注
    TextView follow_status_yes;  //已关注
    Context mContext;
    public FansViewHolder(Context mContext, ViewGroup parent, int res, DBaseRecyclerViewAdapter dBaseRecyclerViewAdapter) {
        super(parent, res, dBaseRecyclerViewAdapter);
        this.mContext = mContext;
        user_icon = (ImageView) $(R.id.follow_user_icon);
        user_id = (TextView) $(R.id.follow_user_id);
        user_summary = (TextView) $(R.id.follow_user_summary);
        follow_status_no = (TextView) $(R.id.follow_status_no);
        follow_status_yes = (TextView) $(R.id.follow_status_yes);
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
        //对RecyclerView子项的数据进行赋值，在每个子项被滚动到屏幕内的时候执行
        //获得当前项的实例
        String user_icon = Const.BASE_IP + user.getHead_image_url();    //头像
        String user_id = user.getNickname();       //用户名字
        String user_summary = user.getSummary();     //聊天时间

        Glide.with(mContext).load(user_icon).into(this.user_icon);
        this.user_id.setText(user_id);
        this.user_summary.setText(user_summary);
        Integer status = 1;    //1是已关注2是未关注
        if (status == 1) {
            follow_status_yes.setVisibility(View.VISIBLE);
            follow_status_no.setVisibility(View.GONE);
        }
        else {
            follow_status_no.setVisibility(View.VISIBLE);
            follow_status_yes.setVisibility(View.GONE);
        }
    }
}
