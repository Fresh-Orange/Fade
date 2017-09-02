package com.sysu.pro.fade;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.pro.fade.discover.ContentDiscover;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.ContentHome;
import com.sysu.pro.fade.message.ContentMessage;
import com.sysu.pro.fade.my.ContentMy;
import com.sysu.pro.fade.publish.PublishActivity;
import com.sysu.pro.fade.utils.UserUtil;
import com.sysu.pro.fade.view.CustomViewPager;
import com.sysu.pro.fade.view.SectionsPagerAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;
    private TabLayout mTabLayoutMenu;
    public Toolbar mToolbar;
    private User user;


    /*
    上次back的时间，用于双击退出判断
    当双击 back 键在此间隔内是直接触发 onBackPressed
     */
    private static long lastBackTime = 0;
    private final int BACK_INTERVAL = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化用户信息
        user = new UserUtil(this).getUer();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        /*
        项目中该activity默认背景总是处于被覆盖状态。去除背景可以优化GPU绘制，减少一层绘制 --- by 赖贤城
         */
        getWindow().setBackgroundDrawable(null);

        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //设置底部导航栏以及监听
        mTabLayoutMenu = (TabLayout) findViewById(R.id.tab_layout_menu);
        bindPagerAndTab();
        setupTabIcon();
        TabLayout.Tab publishTab = mTabLayoutMenu.getTabAt(2);
        View publishTabView = publishTab.getCustomView();
        publishTabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PublishActivity.class);
                startActivityForResult(intent,Const.PUBLISH_REQUEST_CODE);
                //跳转到发布页
            }
        });
    }

    //设置底部导航栏图片
    private void setupTabIcon(){
        Resources res = getResources();
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.scenery_normal), "首页")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.community_normal), "发现")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.add), "发布")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.route_normal), "消息")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.my_normal), "我的")));
    }

    private View createView(Drawable icon, String tab) {
        View view = getLayoutInflater().inflate(R.layout.tab_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        TextView title = (TextView) view.findViewById(R.id.title);
        imageView.setImageDrawable(icon);

        title.setText(tab);
        return view;
    }

    //设置滑动事件
    private void bindPagerAndTab() {
        mTabLayoutMenu.setSelectedTabIndicatorHeight(0);//去除指示器
        mTabLayoutMenu.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**
             * 选中tab后触发
             * @param tab 选中的tab
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //与pager 关联
//                mViewPager.setCurrentItem(tab.getPosition(), true);
                changeTabSelect(tab);
            }

            /**
             * 退出选中状态时触发
             * @param tab 退出选中的tab
             */
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabNormal(tab);
            }

            /**
             * 重复选择时触发
             * @param tab 被 选择的tab
             */
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    //设置选择tab图标
    private void changeTabSelect(TabLayout.Tab tab) {
        Resources res = getResources();
        View view = tab.getCustomView();
        ImageView img_title = (ImageView) view.findViewById(R.id.icon);
        TextView txt_title = (TextView) view.findViewById(R.id.title);
        if (tab.getPosition() == Const.HOME-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.scenery_selected));
            mViewPager.setCurrentItem(Const.HOME-1,false);
        } else if (tab.getPosition()==Const.DISCOVER-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.community_selected));
            mViewPager.setCurrentItem(Const.DISCOVER-1,false);
        }else if (tab.getPosition() == Const.MESSAGE-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.route_selected));
            mViewPager.setCurrentItem(Const.MESSAGE-1,false);
        } else if(tab.getPosition() == Const.MY-1){
            img_title.setImageDrawable(res.getDrawable(R.drawable.my_selected));
            mViewPager.setCurrentItem(Const.MY-1,false);
        }
    }

    //设置还原tab图标
    private void changeTabNormal(TabLayout.Tab tab) {
        Resources res = getResources();
        View view = tab.getCustomView();
        ImageView img_title = (ImageView) view.findViewById(R.id.icon);
        TextView txt_title = (TextView) view.findViewById(R.id.title);
        txt_title.setTextColor(Color.GRAY);
        if (tab.getPosition() == Const.HOME -1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.scenery_normal));
        }  else if (tab.getPosition() == Const.DISCOVER-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.community_normal));
        }else if (tab.getPosition() == Const.MESSAGE-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.route_normal));
        }else if(tab.getPosition() == Const.MY-1){
            img_title.setImageDrawable(res.getDrawable(R.drawable.my_normal));
        }
    }


    public static class PlaceHolderFragment extends Fragment{

        //四大模块
        private ContentDiscover contentDiscover = null;
        private ContentHome contentHome = null;
        private ContentMessage contentMessage = null;
        private ContentMy contentMy = null;

        private static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public void onResume() {
            //返回时重新加载数据
            super.onResume();
            if(contentHome != null && getArguments().getInt(ARG_SECTION_NUMBER) == Const.HOME){
               // contentHome.loadData();
            }
            if(contentDiscover != null && getArguments().getInt(ARG_SECTION_NUMBER) == Const.DISCOVER){
                //contentDiscover.loadData();
            }

            if(contentMessage != null && getArguments().getInt(ARG_SECTION_NUMBER) == Const.MESSAGE){
                //contentMessage.loadData();
            }

            if(contentMy != null && getArguments().getInt(ARG_SECTION_NUMBER) == Const.MY){
                //contentMy.loadData();
            }


        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        public static PlaceHolderFragment newInstance(int sectionNumber) {
            //用于构建一个fragment实例
            PlaceHolderFragment fragment = new PlaceHolderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView =null;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case Const.HOME:
                    rootView = inflater.inflate(R.layout.fragment_home,container,false);
                    contentHome = new ContentHome(getActivity(),getContext(),rootView);
                    break;

                case Const.DISCOVER:
                    rootView = inflater.inflate(R.layout.fragment_discover,container,false);
                    contentDiscover = new ContentDiscover(getActivity(),getContext(),rootView);
                    break;

                case Const.MESSAGE:
                    rootView = inflater.inflate(R.layout.fragment_message,container,false);
                    contentMessage = new ContentMessage(getActivity(),getContext(),rootView);
                    break;

                case Const.MY:
                    rootView = inflater.inflate(R.layout.fragment_my,container,false);
                    contentMy = new ContentMy(getActivity(),getContext(),rootView);
                    break;
            }
            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode){
                case Const.PUBLISH_REQUEST_CODE:{
                    if(resultCode == 1){
                        //发布成功的话则刷新
                        MainActivity activity = (MainActivity) getActivity();
                        contentHome.reload(activity.getCurrentUser().getUser_id());
                    }
                }
                break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackTime < BACK_INTERVAL) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "双击 back 退出", Toast.LENGTH_SHORT).show();
        }
        lastBackTime = currentTime;
    }

    public User getCurrentUser(){
        //用于在fragment中，获取当前的用户对象
        return  user;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(MainActivity.this,"接收到回应"+requestCode,Toast.LENGTH_SHORT).show();
        //为fragment赋值
        List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        Fragment fragmentHome = fragments.get(0);
        if(requestCode == Const.PUBLISH_REQUEST_CODE){
            //转交给fragmentHome处理
            fragmentHome.onActivityResult(requestCode,resultCode,data);
        }
    }
}
