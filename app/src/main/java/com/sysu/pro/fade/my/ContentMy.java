package com.sysu.pro.fade.my;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.my.adapter.MyFragmentAdapter;
import com.sysu.pro.fade.my.fragment.MyFadeFragment;
import com.sysu.pro.fade.my.fragment.TempFragment;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
    private ImageView ivShowHead;
    private TextView tvShowNickname;
    private TextView tvShowSummary; //个性签名
    private RelativeLayout mySetting;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private User user;
    private String[] allNums;
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
        ivShowHead =  (ImageView) rootview.findViewById(R.id.ivShowHead);
        tvShowNickname = (TextView) rootview.findViewById(R.id.tvShowNickname);
        tvShowSummary = (TextView) rootview.findViewById(R.id.tvShowSummary);
        tvFadeName = (TextView) rootview.findViewById(R.id.tvShowUserId);

        user = new UserUtil(activity).getUer();
        loadData();

        //隐藏backbar的返回按钮
        RelativeLayout back = rootview.findViewById(R.id.back_bar_back);
        back.setVisibility(View.GONE);
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
        loadFragment();
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
                        //loadFragment();
                    }
                });
    }

    public  void loadData(){
        //获取本地用户信息举例
        //重新加载本地user数据

        String login_type = sharedPreferences.getString(Const.LOGIN_TYPE,"");
        String image_url = user.getHead_image_url();
        String nickname = user.getNickname();
        String summary = user.getSummary();
        String fade_name = user.getFade_name();
        // TODO: 2018/1/27 第一项是动态数量，暂时没搞，下面有个地方也是先设成了1 
        allNums = new String[]{"1", Integer.toString(user.getFade_num())
                , Integer.toString(user.getConcern_num()), Integer.toString(user.getFans_num())};
        Log.d("loadData", "loadData: "+user.getNickname());
        if(login_type.equals("") || image_url == null || image_url.equals("")){
//            ivShowHead.setImageResource(R.drawable.default_head);
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
            tvShowSummary.setText("暂无个签，点击设置图标进行编辑");
        }else{
            tvShowSummary.setText(summary);
        }
        tvFadeName.setText(fade_name);
    }

    private void loadFragment() {
        String[] mTitles = new String[]{"动态","Fade", "关注", "粉丝"};
        Fragment dongTai = new TempFragment();
        Fragment fade = new MyFadeFragment();
        Fragment concern = new TempFragment();
        Fragment fans = new TempFragment();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(dongTai);
        fragments.add(fade);
        fragments.add(concern);
        fragments.add(fans);
        MyFragmentAdapter adapter = new MyFragmentAdapter(activity.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.my_tablayout_item);
            TextView text1 = (TextView) tab.getCustomView().findViewById(R.id.my_tab_layout_text1);
            TextView text2 = (TextView) tab.getCustomView().findViewById(R.id.my_tab_layout_text2);
            text1.setText(mTitles[i]);
            text2.setText(allNums[i]);
        }
	}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onGetUser(User user) {
        //更新个人信息
        Glide.with(context).load(Const.BASE_IP + user.getHead_image_url()).into(ivShowHead);
        tvShowNickname.setText(user.getNickname());
        tvShowSummary.setText(user.getSummary());
        tvFadeName.setText(user.getFade_name());
        allNums = new String[] {"1", user.getFade_num().toString(), user.getConcern_num().toString()
                ,user.getFans_num().toString()};
        loadFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetNewNote(Note note) {
        requestUser();
    }

}
