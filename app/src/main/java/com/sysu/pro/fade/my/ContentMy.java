package com.sysu.pro.fade.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private User user;


    public ContentMy(final Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;
        Toast.makeText(context,"我的",Toast.LENGTH_SHORT).show();
        //初始化用户信息
        user = ((MainActivity) activity).getCurrentUser();
        sharedPreferences = activity.getSharedPreferences(Const.USER_SHARE,Context.MODE_PRIVATE);
        ivShowHead = (ImageView) rootview.findViewById(R.id.ivShowHead);
        tvShowNickname = (TextView) rootview.findViewById(R.id.tvShowNickname);
        loadData();

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
