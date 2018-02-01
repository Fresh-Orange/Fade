package com.sysu.pro.fade.discover.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by 12194 on 2018/1/1.
 */

public class DiscoverFragmentAdapter extends FragmentPagerAdapter {

    private String[] mTitleList = {"用户", "Fade"};//页卡标题集合
    private List<Fragment> mFragments;

    public DiscoverFragmentAdapter(FragmentManager fm , List<Fragment> fragmentList) {
        super(fm);
        mFragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList[position];
    }
}
