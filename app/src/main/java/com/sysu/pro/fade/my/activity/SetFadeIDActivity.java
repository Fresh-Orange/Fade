package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.utils.UserUtil;

/**
 * Created by huanggzh5 on 2018/3/9.
 */

public class SetFadeIDActivity extends LoginBaseActivity {
    private Button nextbtn;
    private TextView fade_id;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fade_id);
        nextbtn = (Button) findViewById(R.id.next);
        fade_id = (TextView) findViewById(R.id.fade_id);

        user = new UserUtil(this).getUer();
        fade_id.setText(user.getFade_name());

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetFadeIDActivity.this, SetInitAttentionActivity.class));
                //finish();
            }
        });
    }
}
