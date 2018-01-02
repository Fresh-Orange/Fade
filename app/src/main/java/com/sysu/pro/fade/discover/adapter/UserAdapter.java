package com.sysu.pro.fade.discover.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewHolder;
import com.sysu.pro.fade.discover.viewHoler.UserViewholder;

import java.util.List;

/**
 * Created by road on 2017/12/31.
 */

public class UserAdapter extends DBaseRecyclerViewAdapter<User> {

    private Context mContext;
    private List<User> mDatas;
    public UserAdapter(List<User> mDatas, Context mContext) {
        super(mDatas, mContext);
        this.mDatas = mDatas;
        this.mContext = mContext;
    }

    @Override
    protected DBaseRecyclerViewHolder onCreateViewHolder1(ViewGroup parent, int viewType) {
        return new UserViewholder(mContext,parent, R.layout.item_user, this);
    }
}
