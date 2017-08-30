package com.sysu.pro.fade.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.my.activity.GuideActivity;
import com.sysu.pro.fade.utils.Const;

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
    private Button mySetting;


    public ContentMy(final Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;
        sharedPreferences = activity.getSharedPreferences(Const.USER_SHARE,Context.MODE_PRIVATE);
        ivShowHead = (ImageView) rootview.findViewById(R.id.ivShowHead);
        tvShowNickname = (TextView) rootview.findViewById(R.id.tvShowNickname);


        loadData();

        //设置
        mySetting = (Button)  rootview.findViewById(R.id.MySetting);
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
         }
        });

    }

    public  void loadData(){
        //获取本地用户信息举例
        String login_type = sharedPreferences.getString(Const.LOGIN_TYPE,"");
        String image_url = sharedPreferences.getString(Const.IMAGE_URL,"");
        String nickname = sharedPreferences.getString(Const.NICKNAME,"");
        if(login_type.equals("")){
            ivShowHead.setImageResource(R.drawable.default_head);
            tvShowNickname.setText("未登录");
        }else{
            Picasso.with(context).load(image_url).into(ivShowHead);
            tvShowNickname.setText(nickname);
        }



    }



}
