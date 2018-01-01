package com.sysu.pro.fade.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.my.activity.GuideActivity;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.Map;

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
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Map<String,Object>map = (Map<String, Object>) msg.obj;
            String err = (String) map.get(Const.ERR);
            if(msg.what == 1 || msg.what == 3 || msg.what == 4 || msg.what == 5 || msg.what == 6 ){
                if(err != null){
                    Toast.makeText(context,err,Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"修改成功",Toast.LENGTH_SHORT).show();
                }
            }
            else if(msg.what == 2){
                //得到头像
                if(err != null){
                    Toast.makeText(context,err,Toast.LENGTH_SHORT).show();
                }else {
                    //得到最新头像的url，用于显示
                    String latest_head_url = (String) map.get(Const.HEAD_IMAGE_URL);
                }
            }
            else if(msg.what == 7){
                //得到头像
                if(err != null){
                    Toast.makeText(context,err,Toast.LENGTH_SHORT).show();
                }else {
                    //得到最新头像的url，用于显示
                    String latest_wallpaper_url = (String) map.get(Const.WALLPAPER_URL);
                }
            }
            super.handleMessage(msg);
        }
    };

    public ContentMy(final Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;

        //获得本地存储的用户信息
        sharedPreferences = activity.getSharedPreferences(Const.USER_SHARE,Context.MODE_PRIVATE);
        ivShowHead = (ImageView) rootview.findViewById(R.id.ivShowHead);
        tvShowNickname = (TextView) rootview.findViewById(R.id.tvShowNickname);
        tvShowSummary = (TextView) rootview.findViewById(R.id.tvShowSummary);
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

    }


}
