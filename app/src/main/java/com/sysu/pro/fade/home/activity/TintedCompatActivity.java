package com.sysu.pro.fade.home.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.message.Utils.StatusBarUtil;

/**
 * 状态栏为白底黑字的activity
 */
public class TintedCompatActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StatusBarUtil.setStatusBarColor(this, R.color.white);
		StatusBarUtil.StatusBarLightMode(this);
	}

	/**
	 * 设置 app 不随着系统字体的调整而变化
	 */
	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		Configuration config = new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config, res.getDisplayMetrics());
		return res;
	}
}
