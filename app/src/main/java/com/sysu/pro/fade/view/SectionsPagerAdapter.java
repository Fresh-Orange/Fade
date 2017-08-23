package com.sysu.pro.fade.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.utils.Const;

/**
 * Created by road on 2017/7/14.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MainActivity.PlaceHolderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return Const.PAGE_SIZE;
    }

}