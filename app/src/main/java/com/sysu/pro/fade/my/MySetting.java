package com.sysu.pro.fade.my;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.my.activity.GuideActivity;
import com.sysu.pro.fade.my.setting.About;
import com.sysu.pro.fade.my.setting.Personal;

public class MySetting extends AppCompatActivity {

    private ListView settingList;   //用于展示各个设置选项
    private String[] list = {"修改个人信息", "账号安全", "关于Fade", "有话对Fade说"};
    private String signature;       //存储签名内容，用于个人界面的签名更新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        settingList = (ListView) findViewById(R.id.setting_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MySetting.this, android.R.layout.simple_list_item_1, list);
        settingList.setAdapter(adapter);
        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent0 = new Intent(MySetting.this, Personal.class);
                        startActivity(intent0);
                        break;
                    case 1:
                        Toast.makeText(MySetting.this, "账号安全", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Intent intent1 = new Intent(MySetting.this, About.class);
                        startActivity(intent1);
                        break;
                    case 3:
                        Toast.makeText(MySetting.this, "反馈", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });

        final SharedPreferences sharedPreferences = getSharedPreferences(Const.USER_SHARE, Context.MODE_PRIVATE);
        //退出登录
        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置loginType
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Const.LOGIN_TYPE,"");//重置LOGIN_TYPE
                editor.remove("user");
                editor.commit();
                startActivity(new Intent(MySetting.this, GuideActivity.class));
                // TODO: 2017/12/31 这里应该要把MainActivity也结束掉 
                finish();
            }
        });
    }

}
