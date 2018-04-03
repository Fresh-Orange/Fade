package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginActivitiesCollector;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.beans.UserQuery;
import com.sysu.pro.fade.publish.imageselector.entry.Image;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.PhotoUtils;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huanggzh5 on 2018/3/11.
 */

public class SetInitAttentionActivity extends LoginBaseActivity {
    private ImageView backbtn;
    private ImageView into_fade;
    private ImageView another;
    private ImageView user1;
    private ImageView choosed1;
    private ImageView user2;
    private ImageView choosed2;
    private ImageView user3;
    private ImageView choosed3;
    private ImageView user4;
    private ImageView choosed4;
    private ImageView user5;
    private ImageView choosed5;
    private ImageView user6;
    private ImageView choosed6;
    private ImageView user7;
    private ImageView choosed7;
    private ImageView user8;
    private ImageView choosed8;
    private ImageView user9;
    private ImageView choosed9;
    private User user;
    private UserQuery userQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_attention);
        backbtn = (ImageView) findViewById(R.id.back_btn);
        into_fade = (ImageView) findViewById(R.id.into_fade);
        another = (ImageView) findViewById(R.id.another_one);
        user1 = (ImageView) findViewById(R.id.user1);
        user2 = (ImageView) findViewById(R.id.user2);
        user3 = (ImageView) findViewById(R.id.user3);
        user4 = (ImageView) findViewById(R.id.user4);
        user5 = (ImageView) findViewById(R.id.user5);
        user6 = (ImageView) findViewById(R.id.user6);
        user7 = (ImageView) findViewById(R.id.user7);
        user8 = (ImageView) findViewById(R.id.user8);
        user9 = (ImageView) findViewById(R.id.user9);
        choosed1 = (ImageView) findViewById(R.id.choosed1);
        choosed2 = (ImageView) findViewById(R.id.choosed2);
        choosed3 = (ImageView) findViewById(R.id.choosed3);
        choosed4 = (ImageView) findViewById(R.id.choosed4);
        choosed5 = (ImageView) findViewById(R.id.choosed5);
        choosed6 = (ImageView) findViewById(R.id.choosed6);
        choosed7 = (ImageView) findViewById(R.id.choosed7);
        choosed8 = (ImageView) findViewById(R.id.choosed8);
        choosed9 = (ImageView) findViewById(R.id.choosed9);
        user = user = new UserUtil(this).getUer();

        Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, null);
        final UserService userService = retrofit.create(UserService.class);
        userService.getRecommendUser(user.getUser_id() + "", 0 +"")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserQuery>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e("获取用户","失败");
                        //progressDialog.dismiss();
                        e.printStackTrace();
                    }
                    @Override
                    public void onNext(UserQuery userlist) {
                        userQuery = userlist;
                        List<User> list = userlist.getList();
                        Glide.with(SetInitAttentionActivity.this)
                                .load(Const.BASE_IP + list.get(0).getHead_image_url())
                                .fitCenter()
                                .dontAnimate()
                                .into(user1);
                        Glide.with(SetInitAttentionActivity.this)
                                .load(Const.BASE_IP + list.get(1).getHead_image_url())
                                .fitCenter()
                                .dontAnimate()
                                .into(user2);
                        Glide.with(SetInitAttentionActivity.this)
                                .load(Const.BASE_IP + list.get(2).getHead_image_url())
                                .fitCenter()
                                .dontAnimate()
                                .into(user3);
                        Glide.with(SetInitAttentionActivity.this)
                                .load(Const.BASE_IP + list.get(3).getHead_image_url())
                                .fitCenter()
                                .dontAnimate()
                                .into(user4);
                        Glide.with(SetInitAttentionActivity.this)
                                .load(Const.BASE_IP + list.get(4).getHead_image_url())
                                .fitCenter()
                                .dontAnimate()
                                .into(user5);
                        Glide.with(SetInitAttentionActivity.this)
                                .load(Const.BASE_IP + list.get(5).getHead_image_url())
                                .fitCenter()
                                .dontAnimate()
                                .into(user6);
                        Glide.with(SetInitAttentionActivity.this)
                                .load(Const.BASE_IP + list.get(6).getHead_image_url())
                                .fitCenter()
                                .dontAnimate()
                                .into(user7);
                        Glide.with(SetInitAttentionActivity.this)
                                .load(Const.BASE_IP + list.get(7).getHead_image_url())
                                .fitCenter()
                                .dontAnimate()
                                .into(user8);
                        Glide.with(SetInitAttentionActivity.this)
                                .load(Const.BASE_IP + list.get(8).getHead_image_url())
                                .fitCenter()
                                .dontAnimate()
                                .into(user9);
                    }
                });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userService.getRecommendUser(user.getUser_id() + "", userQuery.getStart() +"")
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<UserQuery>() {
                            @Override
                            public void onCompleted() {

                            }
                            @Override
                            public void onError(Throwable e) {
                                Log.e("获取用户","失败");
                                //progressDialog.dismiss();
                                e.printStackTrace();
                            }
                            @Override
                            public void onNext(UserQuery userlist) {
                                userQuery = userlist;
                                List<User> list = userlist.getList();
                                Glide.with(SetInitAttentionActivity.this)
                                        .load(Const.BASE_IP + list.get(0).getHead_image_url())
                                        .fitCenter()
                                        .dontAnimate()
                                        .into(user1);
                                Glide.with(SetInitAttentionActivity.this)
                                        .load(Const.BASE_IP + list.get(1).getHead_image_url())
                                        .fitCenter()
                                        .dontAnimate()
                                        .into(user2);
                                Glide.with(SetInitAttentionActivity.this)
                                        .load(Const.BASE_IP + list.get(2).getHead_image_url())
                                        .fitCenter()
                                        .dontAnimate()
                                        .into(user3);
                                Glide.with(SetInitAttentionActivity.this)
                                        .load(Const.BASE_IP + list.get(3).getHead_image_url())
                                        .fitCenter()
                                        .dontAnimate()
                                        .into(user4);
                                Glide.with(SetInitAttentionActivity.this)
                                        .load(Const.BASE_IP + list.get(4).getHead_image_url())
                                        .fitCenter()
                                        .dontAnimate()
                                        .into(user5);
                                Glide.with(SetInitAttentionActivity.this)
                                        .load(Const.BASE_IP + list.get(5).getHead_image_url())
                                        .fitCenter()
                                        .dontAnimate()
                                        .into(user6);
                                Glide.with(SetInitAttentionActivity.this)
                                        .load(Const.BASE_IP + list.get(6).getHead_image_url())
                                        .fitCenter()
                                        .dontAnimate()
                                        .into(user7);
                                Glide.with(SetInitAttentionActivity.this)
                                        .load(Const.BASE_IP + list.get(7).getHead_image_url())
                                        .fitCenter()
                                        .dontAnimate()
                                        .into(user8);
                                Glide.with(SetInitAttentionActivity.this)
                                        .load(Const.BASE_IP + list.get(8).getHead_image_url())
                                        .fitCenter()
                                        .dontAnimate()
                                        .into(user9);
                            }
                        });
            }
        });

        into_fade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosed1.getVisibility() == View.VISIBLE){
                    userService.concern(user.getUser_id() + "", userQuery.getList().get(0).getUser_id() + "")
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.e("关注","失败");
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(SimpleResponse response) {
                                }
                            });
                }
                if (choosed2.getVisibility() == View.VISIBLE){
                    userService.concern(user.getUser_id() + "", userQuery.getList().get(1).getUser_id() + "")
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.e("关注","失败");
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(SimpleResponse response) {
                                }
                            });
                }
                if (choosed3.getVisibility() == View.VISIBLE){
                    userService.concern(user.getUser_id() + "", userQuery.getList().get(2).getUser_id() + "")
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.e("关注","失败");
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(SimpleResponse response) {
                                }
                            });
                }
                if (choosed4.getVisibility() == View.VISIBLE){
                    userService.concern(user.getUser_id() + "", userQuery.getList().get(3).getUser_id() + "")
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.e("关注","失败");
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(SimpleResponse response) {
                                }
                            });
                }
                if (choosed5.getVisibility() == View.VISIBLE){
                    userService.concern(user.getUser_id() + "", userQuery.getList().get(4).getUser_id() + "")
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.e("关注","失败");
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(SimpleResponse response) {
                                }
                            });
                }
                if (choosed6.getVisibility() == View.VISIBLE){
                    userService.concern(user.getUser_id() + "", userQuery.getList().get(5).getUser_id() + "")
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.e("关注","失败");
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(SimpleResponse response) {
                                }
                            });
                }
                if (choosed7.getVisibility() == View.VISIBLE){
                    userService.concern(user.getUser_id() + "", userQuery.getList().get(6).getUser_id() + "")
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.e("关注","失败");
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(SimpleResponse response) {
                                }
                            });
                }
                if (choosed8.getVisibility() == View.VISIBLE){
                    userService.concern(user.getUser_id() + "", userQuery.getList().get(7).getUser_id() + "")
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.e("关注","失败");
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(SimpleResponse response) {
                                }
                            });
                }
                if (choosed9.getVisibility() == View.VISIBLE){
                    userService.concern(user.getUser_id() + "", userQuery.getList().get(8).getUser_id() + "")
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.e("关注","失败");
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(SimpleResponse response) {
                                }
                            });
                }
                startActivity(new Intent(SetInitAttentionActivity.this,MainActivity.class));
                LoginActivitiesCollector.finishAll();
            }
        });

        user1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosed1.getVisibility() == View.VISIBLE){
                    choosed1.setVisibility(View.INVISIBLE);
                }else {
                    choosed1.setVisibility(View.VISIBLE);
                }
            }
        });

        user2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosed2.getVisibility() == View.VISIBLE){
                    choosed2.setVisibility(View.INVISIBLE);
                }else {
                    choosed2.setVisibility(View.VISIBLE);
                }
            }
        });

        user3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosed3.getVisibility() == View.VISIBLE){
                    choosed3.setVisibility(View.INVISIBLE);
                }else {
                    choosed3.setVisibility(View.VISIBLE);
                }
            }
        });

        user4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosed4.getVisibility() == View.VISIBLE){
                    choosed4.setVisibility(View.INVISIBLE);
                }else {
                    choosed4.setVisibility(View.VISIBLE);
                }
            }
        });

        user5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosed5.getVisibility() == View.VISIBLE){
                    choosed5.setVisibility(View.INVISIBLE);
                }else {
                    choosed5.setVisibility(View.VISIBLE);
                }
            }
        });

        user6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosed6.getVisibility() == View.VISIBLE){
                    choosed6.setVisibility(View.INVISIBLE);
                }else {
                    choosed6.setVisibility(View.VISIBLE);
                }
            }
        });

        user7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosed7.getVisibility() == View.VISIBLE){
                    choosed7.setVisibility(View.INVISIBLE);
                }else {
                    choosed7.setVisibility(View.VISIBLE);
                }
            }
        });

        user8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosed8.getVisibility() == View.VISIBLE){
                    choosed8.setVisibility(View.INVISIBLE);
                }else {
                    choosed8.setVisibility(View.VISIBLE);
                }
            }
        });

        user9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosed9.getVisibility() == View.VISIBLE){
                    choosed9.setVisibility(View.INVISIBLE);
                }else {
                    choosed9.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}
