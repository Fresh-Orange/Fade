package com.sysu.pro.fade.my.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginActivitiesCollector;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.Department;
import com.sysu.pro.fade.beans.DepartmentQuery;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.TokenModel;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huanggzh5 on 2018/3/9.
 */

public class SetSchoolActivity extends LoginBaseActivity {
    private ImageView finishbtn;
    private User user;
    private Bundle mbundle;
    private TextView school;
    private TextView major;
    private ImageView out1;
    private ImageView out2;
    private ListView mTypeLv;
    private ListView mTypeLv1;
    private PopupWindow typeSelectPopup;
    private PopupWindow typeSelectPopup1;
    private List<String> testData;
    private List<Department> majorData;
    private ArrayAdapter<String> testDataAdapter;
    private ArrayAdapter<String> majorDataAdapter;
    private View line;
    private View line1;
    private String value;
    private ImageView backbtn;
    private UserService userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_school);
        finishbtn = (ImageView) findViewById(R.id.finish);
        school = (TextView) findViewById(R.id.school);
        major = (TextView) findViewById(R.id.major);
        out1 = (ImageView) findViewById(R.id.out1);
        out2 = (ImageView) findViewById(R.id.out2);
        line = (View) findViewById(R.id.line);
        line1 = (View) findViewById(R.id.line1);
        backbtn = (ImageView) findViewById(R.id.back_btn);
        user = new User();
        mbundle = new Bundle();


        Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, null);
        userService = retrofit.create(UserService.class);
        mbundle = getIntent().getExtras();
        //user.setTelephone((User) mbundle.getSerializable("user"));
        user = (User) mbundle.getSerializable("user");

        //Toast.makeText(SetSchoolActivity.this, user.getTelephone(), Toast.LENGTH_SHORT).show();
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (school.getText().equals("")){
                    Toast.makeText(SetSchoolActivity.this, "必须选择学校", Toast.LENGTH_SHORT).show();
                }else{
                    String my_major = major.getText().toString();
                    user.setDepartment_name(my_major);
                    Intent intent = new Intent(SetSchoolActivity.this, SetContentActivity.class);
                    mbundle.putSerializable("user", user);
                    intent.putExtras( mbundle);
                    startActivity(intent);
                    finish();
                }
            }
        });

        out1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.out1:
                        // 点击控件后显示popup窗口
                        initSelectPopup();
                        // 使用isShowing()检查popup窗口是否在显示状态
                        if (typeSelectPopup != null && !typeSelectPopup.isShowing()) {
                            typeSelectPopup.showAsDropDown(line, 0, 0);
                        }
                        break;
                }
            }
        });

        out2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.out2:
                        // 点击控件后显示popup窗口
                        initSelectPopup1();
                        // 使用isShowing()检查popup窗口是否在显示状态
                        if (typeSelectPopup1 != null && !typeSelectPopup1.isShowing()) {
                            typeSelectPopup1.showAsDropDown(line1, 0, 0);
                        }
                        break;
                }
            }
        });
    }
    private void initSelectPopup() {
        mTypeLv = new ListView(this);
        TestData();
        // 设置适配器
        testDataAdapter = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData);
        mTypeLv.setAdapter(testDataAdapter);

        // 设置ListView点击事件监听
        mTypeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 在这里获取item数据
                value = testData.get(position);
                // 把选择的数据展示对应的TextView上
                school.setText(value);
                user.setSchool_name(value);
                user.setSchool_id(12002);
                userService.getSchoolDepartment("12002")
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<DepartmentQuery>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("department","获取院系失败");
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(DepartmentQuery initlist) {
                                majorData = initlist.getList();
                            }
                        });
                // 选择完后关闭popup窗口
                typeSelectPopup.dismiss();
            }
        });
        typeSelectPopup = new PopupWindow(mTypeLv, line.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        // 取得popup窗口的背景图片
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        typeSelectPopup.setBackgroundDrawable(drawable);
        typeSelectPopup.setFocusable(true);
        typeSelectPopup.setOutsideTouchable(true);
        typeSelectPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 关闭popup窗口
                typeSelectPopup.dismiss();
            }
        });
    }

    private void initSelectPopup1() {
        mTypeLv1 = new ListView(this);
        List<String> stringList = new ArrayList<String>();
        for (int i = 0; i < majorData.size(); i++){
            stringList.add(majorData.get(i).getDepartment_name());
        }
        // 设置适配器
        majorDataAdapter = new ArrayAdapter<String>(SetSchoolActivity.this, R.layout.popup_text_item, stringList);
        mTypeLv1.setAdapter(majorDataAdapter);

        // 设置ListView点击事件监听
        mTypeLv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 在这里获取item数据
                value = majorData.get(position).getDepartment_name();
                // 把选择的数据展示对应的TextView上
                major.setText(value);
                user.setDepartment_name(value);
                user.setDepartment_id(majorData.get(position).getDepartment_id());
                // 选择完后关闭popup窗口
                typeSelectPopup1.dismiss();
            }
        });
        typeSelectPopup1 = new PopupWindow(mTypeLv1, line1.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        // 取得popup窗口的背景图片
        Drawable drawable = ContextCompat.getDrawable(SetSchoolActivity.this, R.drawable.bg_corner);
        typeSelectPopup1.setBackgroundDrawable(drawable);
        typeSelectPopup1.setFocusable(true);
        typeSelectPopup1.setOutsideTouchable(true);
        typeSelectPopup1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 关闭popup窗口
                typeSelectPopup1.dismiss();
            }
        });
    }

    /**
     * 模拟假数据
     */
    private void TestData() {
        testData = new ArrayList<>();
        /*for (int i = 0; i < 20; i++) {
            String str = new String("数据" + i);
            testData.add(str);
        }*/
        testData.add("中山大学");
        testData.add("华南理工大学");
        testData.add("深圳大学");
        testData.add("暨南大学");
        testData.add("华南师范大学");
        testData.add("华南农业大学");
        testData.add("南方医科大学");
        testData.add("广东外语外贸大学");
        testData.add("广州大学");
        testData.add("广东工业大学");
        testData.add("汕头大学");
        testData.add("广东中医药大学");
    }
}
