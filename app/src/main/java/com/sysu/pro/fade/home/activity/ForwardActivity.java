package com.sysu.pro.fade.home.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.home.fragment.ForwardFragment;
import com.sysu.pro.fade.my.adapter.MyFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class ForwardActivity extends MainBaseActivity {

    private RelativeLayout back;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
        int userId = getIntent().getIntExtra("USER_ID", -1);
        int noteId = getIntent().getIntExtra("NOTE_ID", -1);
        back = findViewById(R.id.forward_back);
        tabLayout = findViewById(R.id.forward_tab_layout);
        viewPager = findViewById(R.id.forward_view_pager);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Fragment good = ForwardFragment.newInstance(userId, noteId, 1);
        Fragment bad = ForwardFragment.newInstance(userId, noteId, 2);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(good);
        fragments.add(bad);
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        String[] titles = {"续一秒","减一秒"};
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.forward_tablayout_item);
            TextView tv = tab.getCustomView().findViewById(R.id.forward_tab_layout_text);
            tv.setText(titles[i]);
            if (i != 0) {
                tab.getCustomView().findViewById(R.id.forward_divide_line).setVisibility(View.GONE);
            }
        }
        //设置下划线的颜色变化、文字颜色变化
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.forward_tab_layout_blue_line).setVisibility(View.VISIBLE);
                tab.getCustomView().findViewById(R.id.forward_tab_layout_text).setAlpha(1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.forward_tab_layout_blue_line).setVisibility(View.INVISIBLE);
                tab.getCustomView().findViewById(R.id.forward_tab_layout_text).setAlpha((float) 0.5);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.forward_tab_layout_blue_line).setVisibility(View.VISIBLE);
                tab.getCustomView().findViewById(R.id.forward_tab_layout_text).setAlpha(1);
            }
        });
        tabLayout.getTabAt(0).select();
    }

}
