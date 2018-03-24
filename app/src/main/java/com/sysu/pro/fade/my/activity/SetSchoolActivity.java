package com.sysu.pro.fade.my.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanggzh5 on 2018/3/9.
 */

public class SetSchoolActivity extends LoginBaseActivity {
    private ImageView finishbtn;
    private User user;
    private Bundle mbundle;
    private TextView school;
    private EditText major;
    private ImageView out1;
    private ImageView out2;
    private ListView mTypeLv;
    private PopupWindow typeSelectPopup;
    private List<String> testData;
    private ArrayAdapter<String> testDataAdapter;
    private View line;
    private String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_school);
        finishbtn = (ImageView) findViewById(R.id.finish);
        school = (TextView) findViewById(R.id.school);
        major = (EditText) findViewById(R.id.major);
        out1 = (ImageView) findViewById(R.id.out1);
        out2 = (ImageView) findViewById(R.id.out2);
        line = (View) findViewById(R.id.line);
        user = new User();
        mbundle = new Bundle();

        mbundle = getIntent().getExtras();
        //user.setTelephone((User) mbundle.getSerializable("user"));
        //user = (User) mbundle.getSerializable("user");

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String my_major = major.getText().toString();
                user.setDepartment_name(my_major);
                Intent intent = new Intent(SetSchoolActivity.this, SetContentActivity.class);
                mbundle.putSerializable("user", user);
                intent.putExtra("user", mbundle);
                startActivity(intent);
                finish();
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
                //user.setSchool_name(value);
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
