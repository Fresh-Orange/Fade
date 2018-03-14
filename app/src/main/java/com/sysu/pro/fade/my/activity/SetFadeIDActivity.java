package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;

/**
 * Created by huanggzh5 on 2018/3/9.
 */

public class SetFadeIDActivity extends LoginBaseActivity {
    private ImageView nextbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fade_id);
        nextbtn = (ImageView) findViewById(R.id.next);
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetFadeIDActivity.this, SetInitAttentionActivity.class));
            }
        });
    }
}
