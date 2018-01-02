package com.sysu.pro.fade.home.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;

public class OtherActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other);
		int userId = getIntent().getIntExtra(Const.USER_ID, -1);
		Toast.makeText(this, String.valueOf(userId), Toast.LENGTH_SHORT).show();
	}
}
