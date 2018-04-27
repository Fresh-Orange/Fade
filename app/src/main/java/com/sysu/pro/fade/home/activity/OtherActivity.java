package com.sysu.pro.fade.home.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.PersonPage;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.fragment.OtherFadeFragment;
import com.sysu.pro.fade.home.fragment.OtherFadeFragment;
import com.sysu.pro.fade.home.fragment.OtherLiveFragment;
import com.sysu.pro.fade.my.adapter.MyFragmentAdapter;
import com.sysu.pro.fade.my.fragment.ConcernFragment;
import com.sysu.pro.fade.my.fragment.FansFragment;
import com.sysu.pro.fade.my.fragment.TempFragment;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OtherActivity extends MainBaseActivity {

    private User myself;
    private User other;
    private Retrofit retrofit;
    private String[] allNums;

    private CoordinatorLayout rootView;
    private ProgressBar loading;
    private TextView backBarTitle;
    private CircleImageView ivShowHead;
    private TextView tvShowNickname;
    private TextView tvFadeName;//fade_id
    private TextView tvShowSummary; //个性签名
    private TextView schoolName;
    private TextView schoolDot;
    private TextView departmentName;
    private TextView tvUnConcern;  //点击后关注
    private ImageView tvConcernOk;  //点击后取消关注
    private TextView tvContact;     //私信按钮
    private TabLayout tabLayout;
    private ViewPager viewPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        RelativeLayout setting = findViewById(R.id.back_bar_menu);  //三个点的菜单按钮
        setting.setVisibility(View.VISIBLE);
        rootView = findViewById(R.id.other_root_layout);
        loading = findViewById(R.id.other_loading);
        ivShowHead =  findViewById(R.id.ivShowHead);
        tvShowNickname = (TextView) findViewById(R.id.tvShowNickname);
        tvShowSummary = (TextView) findViewById(R.id.tvShowSummary);
        tvFadeName = (TextView) findViewById(R.id.tvShowUserId);
        schoolName = findViewById(R.id.school_name);
        schoolDot = findViewById(R.id.school_dot);
        departmentName = findViewById(R.id.department_name);
        tvConcernOk = findViewById(R.id.other_concern_ok);
        tvUnConcern = findViewById(R.id.other_text1);
        tvContact = findViewById(R.id.other_text2);
        tabLayout = findViewById(R.id.other_tab_layout);
        viewPager = findViewById(R.id.other_view_pager);
        Picasso.with(OtherActivity.this).load(R.id.other_concern_ok).into(tvConcernOk);
        
        Integer user_id = getIntent().getIntExtra(Const.USER_ID, -1);
        Log.d("OtherActivity", user_id.toString());
        myself = new UserUtil(this).getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, myself.getTokenModel());
        UserService service = retrofit.create(UserService.class);
        service.getPersonPage(user_id.toString(), myself.getUser_id().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PersonPage>() {
                    @Override
                    public void onCompleted() {
                        
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("otherErr", "onError: "+ e.toString());
                    }

                    @Override
                    public void onNext(PersonPage personPage) {
                        //获取他人主页用户
                        other = personPage.getUser();
                        if (personPage.getIsConcern() != null) {
                            //0为没关注
                            if (personPage.getIsConcern() == 1) {
                                tvConcernOk.setVisibility(View.VISIBLE);
                                tvContact.setVisibility(View.VISIBLE);
                            } else {
                                tvUnConcern.setVisibility(View.VISIBLE);
                            }
                        }
                        loadData();
                        loadFragment();
                        loading.setVisibility(View.GONE);
                        rootView.setVisibility(View.VISIBLE);
                    }
                });
    }

    public  void loadData(){
        String image_url = other.getHead_image_url();
        String nickname = other.getNickname();
        String summary = other.getSummary();
        String fade_name = other.getFade_name();
        String school_name = other.getSchool_name();
        String department_name = other.getDepartment_name();
        //获取用户的关注、粉丝等的数量
        String fade_num = (other.getFade_num()>999?(other.getFade_num()/1000+"K"):other.getFade_num().toString());
        String fans_num = (other.getFans_num()>999?(other.getFans_num()/1000+"K"):other.getFans_num().toString());
        String concern_num = (other.getConcern_num()>999?(other.getConcern_num()/1000+"K"):other.getConcern_num().toString());
        String live_num = (other.getDynamicNum()>999?(other.getDynamicNum()/1000+"K"):other.getDynamicNum().toString());
        allNums = new String[]{live_num, fade_num, fans_num, concern_num};

        Picasso.with(this).load(Const.BASE_IP + image_url).into(ivShowHead);
        tvShowNickname.setText(nickname);
        if(summary == null || summary.equals("")){
            tvShowSummary.setText("该用户没有个人简介");
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
        tvUnConcern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myself.getUser_id() == other.getUser_id()) {
                    Toast.makeText(OtherActivity.this, "不能关注自己", Toast.LENGTH_SHORT).show();
                } else {
                    UserService service = retrofit.create(UserService.class);
                    service.concern(myself.getUser_id().toString(), other.getUser_id().toString())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d("关注bug", "onError: " + e.toString());
                                }

                                @Override
                                public void onNext(SimpleResponse simpleResponse) {
                                    if (simpleResponse.getErr() == null) {
                                        tvUnConcern.setVisibility(View.GONE);
                                        tvConcernOk.setVisibility(View.VISIBLE);
                                        tvContact.setVisibility(View.VISIBLE);
//                                        loadFragment();
                                    }
                                }
                            });
                }
            }
        });
        tvConcernOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService service = retrofit.create(UserService.class);
                service.cancelConcern(myself.getUser_id().toString(), other.getUser_id().toString())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<SimpleResponse>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("取关bug", "onError: " + e.toString());
                            }

                            @Override
                            public void onNext(SimpleResponse simpleResponse) {
                                if (simpleResponse.getErr() == null) {
                                    tvUnConcern.setVisibility(View.VISIBLE);
                                    tvConcernOk.setVisibility(View.GONE);
                                    tvContact.setVisibility(View.GONE);
//                                    loadFragment();
                                }
                            }
                        });
            }
        });
        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongIM.getInstance().startPrivateChat(OtherActivity.this,
                        other.getUser_id().toString(), other.getNickname());
            }
        });

        //设置AppBarLayout,当不可见时，顶栏显示用户名
        backBarTitle = findViewById(R.id.tvOfBackBar);
        backBarTitle.setText(other.getNickname());
        backBarTitle.setVisibility(View.INVISIBLE);
        AppBarLayout appBar = findViewById(R.id.my_app_bar_layout);
        final LinearLayout otherMessage = findViewById(R.id.other_all_message);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset + otherMessage.getHeight() == 0) {
                    backBarTitle.setVisibility(View.VISIBLE);
                } else {
                    if (backBarTitle.getVisibility() != View.INVISIBLE) {
                        backBarTitle.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void loadFragment() {
        tabLayout.clearOnTabSelectedListeners();
        String[] mTitles = new String[]{"动态","Fade", "粉丝", "关注"};
        Fragment liveFade = OtherLiveFragment.newInstance(other.getUser_id());
        Fragment fade = OtherFadeFragment.newInstance(other.getUser_id());
        Fragment concern = ConcernFragment.newInstance(other.getUser_id());
        Fragment fans = FansFragment.newInstance(other.getUser_id());
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(liveFade);
        fragments.add(fade);
        fragments.add(fans);
        fragments.add(concern);
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
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
    
}
