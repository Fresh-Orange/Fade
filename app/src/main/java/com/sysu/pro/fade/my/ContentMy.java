package com.sysu.pro.fade.my;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.event.DoubleClick;
import com.sysu.pro.fade.home.listener.OnDoubleClickListener;
import com.sysu.pro.fade.my.Event.DoubleFade;
import com.sysu.pro.fade.my.adapter.MyFragmentAdapter;
import com.sysu.pro.fade.my.fragment.ConcernFragment;
import com.sysu.pro.fade.my.fragment.FansFragment;
import com.sysu.pro.fade.my.fragment.MyFadeFragment;
import com.sysu.pro.fade.my.fragment.MyLiveFragment;
import com.sysu.pro.fade.my.fragment.TempFragment;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by road on 2017/7/14.
 */
public class ContentMy {
    private FragmentActivity activity;
    private Context context;
    private View rootview;
    private SharedPreferences sharedPreferences;
    private TextView backBarTitle;
    private CircleImageView ivShowHead;
    private TextView tvShowNickname;
    private TextView tvShowSummary; //个性签名
    private TextView schoolName;
    private TextView schoolDot;
    private TextView departmentName;
    private RelativeLayout mySetting;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private User user;
    private String[] allNums;       //所有菜单项的数字
    private TextView tvFadeName;//fade_id

    private Retrofit retrofit;
    private UserService userService;

    public ContentMy(final FragmentActivity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;
        //注册EventBus
        EventBus.getDefault().register(this);
        //获得本地存储的用户信息
        sharedPreferences = activity.getSharedPreferences(Const.USER_SHARE,Context.MODE_PRIVATE);
        ivShowHead =  rootview.findViewById(R.id.ivShowHead);
        tvShowNickname = (TextView) rootview.findViewById(R.id.tvShowNickname);
        tvShowSummary = (TextView) rootview.findViewById(R.id.tvShowSummary);
        tvFadeName = (TextView) rootview.findViewById(R.id.tvShowUserId);
        schoolName = rootview.findViewById(R.id.school_name);
        schoolDot = rootview.findViewById(R.id.school_dot);
        departmentName = rootview.findViewById(R.id.department_name);

        user = new UserUtil(activity).getUer();
        loadData();

        //隐藏backbar的返回按钮
        RelativeLayout back = rootview.findViewById(R.id.back_bar_back);
        back.setVisibility(View.GONE);

        //设置AppBarLayout,当不可见时，顶栏显示用户名
        backBarTitle = rootview.findViewById(R.id.tvOfBackBar);
        backBarTitle.setText(user.getNickname());
        backBarTitle.setVisibility(View.INVISIBLE);
        AppBarLayout appBar = rootview.findViewById(R.id.my_app_bar_layout);
        final LinearLayout myMessage = rootview.findViewById(R.id.my_all_message);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset + myMessage.getHeight() == 0) {
                    backBarTitle.setVisibility(View.VISIBLE);
                } else {
                    if (backBarTitle.getVisibility() != View.INVISIBLE) {
                        backBarTitle.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        backBarTitle.setOnTouchListener(new OnDoubleClickListener(
                new OnDoubleClickListener.DoubleClickCallback() {
                    @Override
                    public void onDoubleClick() {
                        EventBus.getDefault().post(new DoubleFade("click", true));
                    }
                }
        ));
        //进入设置界面的按钮
        mySetting = rootview.findViewById(R.id.back_bar_menu);
        mySetting.setVisibility(View.VISIBLE);
        mySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MySetting.class);
                activity.startActivity(intent);
            }
        });

        tabLayout = (TabLayout) rootview.findViewById(R.id.my_tab_layout);
        viewPager = (ViewPager) rootview.findViewById(R.id.my_view_pager);
        //loadFragment();
        requestUser();


    }

    private void requestUser() {
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        userService = retrofit.create(UserService.class);
        userService.getUserById(user.getUser_id().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(User newUser) {
                        user = newUser;
                        loadData();
                        loadFragment();
                    }
                });
    }

    public void loadData(){
        //获取本地用户信息举例
        //重新加载本地user数据

        String login_type = sharedPreferences.getString(Const.LOGIN_TYPE,"");
        String image_url = user.getHead_image_url();
        String nickname = user.getNickname();
        String summary = user.getSummary();
        String fade_name = user.getFade_name();
        String school_name = user.getSchool_name();
        String department_name = user.getDepartment_name();
        //获取用户的关注、粉丝等的数量
        String fade_num = (user.getFade_num()>999?(user.getFade_num()/1000+"K"):user.getFade_num().toString());
        String fans_num = (user.getFans_num()>999?(user.getFans_num()/1000+"K"):user.getFans_num().toString());
        String concern_num = (user.getConcern_num()>999?(user.getConcern_num()/1000+"K"):user.getConcern_num().toString());
        String live_num = (user.getDynamicNum()>999?(user.getDynamicNum()/1000+"K"):user.getDynamicNum().toString());
        allNums = new String[]{live_num, fade_num, fans_num, concern_num};
        Log.d("loadData", "loadData: "+user.getNickname());
        if(login_type.equals("") || image_url == null || image_url.equals("")){
            Picasso.with(context).load(R.drawable.default_head).into(ivShowHead);
        }else{
            Picasso.with(context).load(Const.BASE_IP + image_url).into(ivShowHead);
        }
        if(nickname == null||nickname.equals("")){
            tvShowNickname.setText("未登录");
        }else{
            tvShowNickname.setText(nickname);
        }
        if(summary == null || summary.equals("")){
            tvShowSummary.setText("暂无个签，点击设置进入编辑");
        }else{
            tvShowSummary.setText(summary);
        }
        tvFadeName.setText(fade_name);
        //学校院系
        if(school_name != null) {
            schoolName.setText(school_name);
        }
        if (department_name != null) {
            schoolDot.setVisibility(View.VISIBLE);
            departmentName.setText(department_name);
        }
    }

    private void loadFragment() {
        tabLayout.clearOnTabSelectedListeners();    //先清空
        String[] mTitles = new String[]{"动态","Fade", "粉丝", "关注"};
        Fragment liveFade = new MyLiveFragment();
        Fragment fade = new MyFadeFragment();
        Fragment concern = ConcernFragment.newInstance(user.getUser_id());
        Fragment fans = FansFragment.newInstance(user.getUser_id());
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(liveFade);
        fragments.add(fade);
        fragments.add(fans);
        fragments.add(concern);
        MyFragmentAdapter adapter = new MyFragmentAdapter(activity.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3); //缓存3个选项卡，这样就不会每次都重新加载
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (i == 0) {
                tab.setCustomView(R.layout.my_tablayout_first_item);
            } else {
                tab.setCustomView(R.layout.my_tablayout_item);
            }
            TextView text1 = (TextView) tab.getCustomView().findViewById(R.id.my_tab_layout_text1);
            TextView text2 = (TextView) tab.getCustomView().findViewById(R.id.my_tab_layout_text2);
            text1.setText(mTitles[i]);
            text2.setText(allNums[i]);
        }
        //设置下划线的颜色变化
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.my_tab_layout_blue_line).setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.my_tab_layout_blue_line).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.my_tab_layout_blue_line).setVisibility(View.VISIBLE);
            }
        });
        tabLayout.getTabAt(0).select();

	}


    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onGetUser(User user) {
        //更新个人信息
        Glide.with(context).load(Const.BASE_IP + user.getHead_image_url()).into(ivShowHead);
        tvShowNickname.setText(user.getNickname());
        backBarTitle.setText(user.getNickname());
        tvShowSummary.setText(user.getSummary());
        tvFadeName.setText(user.getFade_name());
        String fade_num = (user.getFade_num()>999?(user.getFade_num()/1000+"K"):user.getFade_num().toString());
        String fans_num = (user.getFans_num()>999?(user.getFans_num()/1000+"K"):user.getFans_num().toString());
        String concern_num = (user.getConcern_num()>999?(user.getConcern_num()/1000+"K"):user.getConcern_num().toString());
        String live_num = (user.getDynamicNum()>999?(user.getDynamicNum()/1000+"K"):user.getDynamicNum().toString());
        allNums = new String[]{live_num, fade_num, fans_num, concern_num};
        loadFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetNewNote(Note note) {
        requestUser();
    }

}
