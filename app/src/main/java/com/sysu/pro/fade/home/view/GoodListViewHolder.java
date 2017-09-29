package com.sysu.pro.fade.home.view;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.pro.fade.R;

import org.w3c.dom.Text;

/**
 * Created by road on 2017/9/4.
 */
public class GoodListViewHolder extends RecyclerView.ViewHolder{

    public ImageView iv_good_head;
    public TextView good_list_nickname;
    public TextView good_list_good_time;
    public GoodListViewHolder(View itemView) {
        super(itemView);
        iv_good_head = (ImageView) itemView.findViewById(R.id.iv_good_head);
        good_list_nickname = (TextView) itemView.findViewById(R.id.good_list_nickname);
        good_list_good_time = (TextView) itemView.findViewById(R.id.good_list_good_time);
    }
}
