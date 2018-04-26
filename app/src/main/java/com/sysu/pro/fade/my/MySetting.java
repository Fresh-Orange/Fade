package com.sysu.pro.fade.my;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainActivitiesCollector;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.my.activity.RegisterActivity;
import com.sysu.pro.fade.my.setting.About;
import com.sysu.pro.fade.my.setting.Personal;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.GlideCatchUtil;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MySetting extends MainBaseActivity {

    private ListView settingList;   //用于展示各个设置选项
    private String[] list = {"修改个人信息", "账号安全", "关于Fade", "有话对Fade说", "清理图片缓存"};
    private String signature;       //存储签名内容，用于个人界面的签名更新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        settingList = (ListView) findViewById(R.id.setting_list);
        list[4] = list[4] + "("+GlideCatchUtil.getInstance().getCacheSize()+")";
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
                    case 4:
                        if (GlideCatchUtil.getInstance().clearCacheDiskSelf())
                            Toast.makeText(MySetting.this, "清理完成", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MySetting.this, "清理出错，请重试", Toast.LENGTH_SHORT).show();
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
                //发起退出登录请求
                User user = new UserUtil((MySetting.this)).getUer();
                Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
                UserService service = retrofit.create(UserService.class);
                service.logoutUserByToken(JSON.toJSONString(user.getTokenModel()))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<SimpleResponse>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("退出登录","失败");
                                e.printStackTrace();
                            }
                            @Override
                            public void onNext(SimpleResponse simpleResponse) {
                                Toast.makeText(MySetting.this,"退出登录成功",Toast.LENGTH_SHORT).show();
                                //设置loginType
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Const.LOGIN_TYPE,"");//重置LOGIN_TYPE
                                editor.remove("user");
                                editor.apply();
                                startActivity(new Intent(MySetting.this, RegisterActivity.class));
                                // TODO: 2017/12/31 这里应该要把MainActivity也结束掉
                                MainActivitiesCollector.finishAll();
                            }
                        });
            }
        });
    }

}
