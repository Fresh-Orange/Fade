package com.sysu.pro.fade.home.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sysu.pro.fade.home.fragment.DetailPageFragment;

/**
 * Created by road on 2017/9/4.
 */
public class DetailFragmentAdapter extends FragmentPagerAdapter {

    public final int COUNT = 3;
    private String[] titles = new String[]{"续一秒", "评论", "转发"};
    private Context context;

    public DetailFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailPageFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}

