package com.sysu.pro.fade.message.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.message.Utils.StatusBarUtil;


public class ConversationActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        TextView titleView = findViewById(R.id.tv_title);
        String title = getIntent().getData().getQueryParameter("title");
        String targetId = getIntent().getData().getQueryParameter("targetId");
        titleView.setText(title);

        findViewById(R.id.conversation_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TintBar();
    }

    public void TintBar() {
        StatusBarUtil.setStatusBarColor(this, R.color.status_color);
        StatusBarUtil.StatusBarLightMode(this);
    }
}
