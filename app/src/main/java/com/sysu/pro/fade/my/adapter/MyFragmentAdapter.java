package com.sysu.pro.fade.my.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by 12194 on 2018/1/1.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {

//    private String[] mTitles = new String[]{"Fade", "关注", "粉丝"};
    private List<Fragment> mFragments;

    public MyFragmentAdapter(FragmentManager fm ,List<Fragment> fragmentList) {
        super(fm);
        mFragments = fragmentList;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return mTitles[position];
//    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
