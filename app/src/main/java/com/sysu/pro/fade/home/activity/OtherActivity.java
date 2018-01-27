package com.sysu.pro.fade.home.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.PersonPage;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.fragment.OtherFadeFragment;
import com.sysu.pro.fade.my.adapter.MyFragmentAdapter;
import com.sysu.pro.fade.my.fragment.TempFragment;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

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

    private ImageView ivShowHead;
    private TextView tvShowNickname;
    private TextView tvFadeName;//fade_id
    private TextView tvShowSummary; //个性签名
    private TextView tvUnConcern;
    private ImageView tvConcernOk;
    private TextView tvContact;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        RelativeLayout backBar = findViewById(R.id.back_bar_menu);  //三个点的菜单按钮
        backBar.setVisibility(View.VISIBLE);
        ivShowHead =  (ImageView) findViewById(R.id.ivShowHead);
        tvShowNickname = (TextView) findViewById(R.id.tvShowNickname);
        tvShowSummary = (TextView) findViewById(R.id.tvShowSummary);
        tvFadeName = (TextView) findViewById(R.id.tvShowUserId);
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
                        //0为没关注
                        if (personPage.getIsConcern() == 1) {
                            tvConcernOk.setVisibility(View.VISIBLE);
                        } else {
                            tvUnConcern.setVisibility(View.VISIBLE);
                        }
                        loadData();
                        loadFragment();
                    }
                });
    }

    public  void loadData(){
        String image_url = other.getHead_image_url();
        String nickname = other.getNickname();
        String summary = other.getSummary();
        String fade_name = other.getFade_name();
        allNums = new String[]{Integer.toString(other.getFade_num())
                , Integer.toString(other.getConcern_num()), Integer.toString(other.getFans_num())};
        
        Picasso.with(this).load(Const.BASE_IP + image_url).into(ivShowHead);
        tvShowNickname.setText(nickname);
        if(summary == null || summary.equals("")){
            tvShowSummary.setText("该用户没有个人简介");
        }else{
            tvShowSummary.setText(summary);
        }
        tvFadeName.setText(fade_name);
        tvUnConcern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                Log.d("关注bug", "onError: "+e.toString());
                            }

                            @Override
                            public void onNext(SimpleResponse simpleResponse) {
                                if (simpleResponse.getErr() == null) {
                                    tvUnConcern.setVisibility(View.GONE);
                                    tvConcernOk.setVisibility(View.VISIBLE);
                                    loadFragment();
                                }
                            }
                        });
            }
        });
        tvConcernOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService service = retrofit.create(UserService.class);
                service.cancelConcern(myself.getUser_id().toString(),other.getUser_id().toString() )
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<SimpleResponse>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("取关bug", "onError: "+e.toString());
                            }

                            @Override
                            public void onNext(SimpleResponse simpleResponse) {
                                if (simpleResponse.getErr() == null) {
                                    tvUnConcern.setVisibility(View.VISIBLE);
                                    tvConcernOk.setVisibility(View.GONE);
                                    loadFragment();
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
    }

    private void loadFragment() {
        String[] mTitles = new String[]{"Fade", "关注", "粉丝"};
        Fragment fade = OtherFadeFragment.newInstance(other.getUser_id());
        Fragment concern = new TempFragment();
        Fragment fans = new TempFragment();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(fade);
        fragments.add(concern);
        fragments.add(fans);
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
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
    
}
