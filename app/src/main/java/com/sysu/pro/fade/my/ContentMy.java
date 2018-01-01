package com.sysu.pro.fade.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by road on 2017/7/14.
 */
public class ContentMy {
    private Activity activity;
    private Context context;
    private View rootview;
    private SharedPreferences sharedPreferences;
    private ImageView ivShowHead;
    private TextView tvShowNickname;
    private TextView tvShowSummary; //个性签名
    private ImageView mySetting;
    private User user;

    private TextView tvFadeName;//fade_id
    private TextView tvFadeNum;
    private TextView tvFansNum;
    private TextView tvConcernNum;

    public ContentMy(final Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;
        //注册EventBus
        EventBus.getDefault().register(this);
        //获得本地存储的用户信息
        sharedPreferences = activity.getSharedPreferences(Const.USER_SHARE,Context.MODE_PRIVATE);
        ivShowHead =  rootview.findViewById(R.id.ivShowHead);
        tvShowNickname = rootview.findViewById(R.id.tvShowNickname);
        tvShowSummary = rootview.findViewById(R.id.tvShowSummary);
        tvFadeName = rootview.findViewById(R.id.tvShowUserId);
        tvConcernNum = rootview.findViewById(R.id.tv_concern_num);
        tvFansNum = rootview.findViewById(R.id.tv_fans_num);
        tvFadeNum = rootview.findViewById(R.id.tv_fade_num);
        loadData();

        //设置
        mySetting = (ImageView)  rootview.findViewById(R.id.MySetting);
        mySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MySetting.class);
                activity.startActivity(intent);
            }
        });



    }

    public  void loadData(){
        //获取本地用户信息举例
        user = new UserUtil(activity).getUer(); //重新加载本地user数据
        String login_type = sharedPreferences.getString(Const.LOGIN_TYPE,"");
        String image_url = user.getHead_image_url();
        String nickname = user.getNickname();
        String summary = user.getSummary();
        String fade_name = user.getFade_name();
        Integer fade_num = user.getFade_num();
        Integer concern_num = user.getConcern_num();
        Integer fans_num = user.getFans_num();
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
        tvFadeNum.setText(fade_num.toString());
        tvFansNum.setText(fans_num.toString());
        tvConcernNum.setText(concern_num.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onGetUser(User user){
        //更新个人信息
        Glide.with(context).load(Const.BASE_IP + user.getHead_image_url()).into(ivShowHead);
        tvShowNickname.setText(user.getNickname());
        tvShowSummary.setText(user.getSummary());
        tvFadeName.setText(user.getFade_name());
        tvFadeNum.setText(user.getFade_num().toString());
        tvFansNum.setText(user.getFans_num().toString());
        tvConcernNum.setText(user.getConcern_num().toString());
    }

}
