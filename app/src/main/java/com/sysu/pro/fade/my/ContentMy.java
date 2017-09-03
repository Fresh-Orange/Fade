package com.sysu.pro.fade.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.my.activity.GuideActivity;
import com.sysu.pro.fade.Const;

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
        user = ((MainActivity) activity).getCurrentUser();
        sharedPreferences = activity.getSharedPreferences(Const.USER_SHARE,Context.MODE_PRIVATE);
        ivShowHead = (ImageView) rootview.findViewById(R.id.ivShowHead);
        tvShowNickname = (TextView) rootview.findViewById(R.id.tvShowNickname);
        loadData();

        //设置
        mySetting = (ImageView)  rootview.findViewById(R.id.MySetting);
        mySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MySetting.class);
                activity.startActivityForResult(intent, 1);
            }
        });
        //修改壁纸

        //退出登录
        Button btnLogout = (Button) rootview.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置loginType
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Const.LOGIN_TYPE,"");//重置LOGIN_TYPE
                editor.commit();
                activity.startActivity(new Intent(activity, GuideActivity.class));
                activity.finish();
         }
        });

    }

    public  void loadData(){
        //获取本地用户信息举例
        String login_type = sharedPreferences.getString(Const.LOGIN_TYPE,"");
        String image_url = user.getHead_image_url();
        String nickname = user.getNickname();
        if(login_type.equals("") || image_url == null || image_url.equals("")){
            ivShowHead.setImageResource(R.drawable.default_head);
        }else{
            Picasso.with(context).load(image_url).into(ivShowHead);
        }
        if(nickname.equals("")){
            tvShowNickname.setText("未登录");
        }else{
            tvShowNickname.setText(nickname);
        }





    }



}
