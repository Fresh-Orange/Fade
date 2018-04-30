package com.sysu.pro.fade.my.setting;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Department;
import com.sysu.pro.fade.beans.DepartmentQuery;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.my.activity.SetSchoolActivity;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.PhotoUtils;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sysu.pro.fade.Const.USER_SHARE;

public class Personal extends AppCompatActivity {

    private User user;
    private TextView saveChange;    //点击之后保存所有修改到数据库
    private ImageView settingHead;
    private EditText settingName;
    private EditText settingSummary;
    private TextView settingSex;
    private TextView settingSchool;
    private ImageView out1;
    private TextView settingDepartment;
    private ImageView out2;
    private EditText settingArea;
    private String[] choice = new String[] {"男", "女"};
    private static final int CHOOSE_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private Retrofit retrofit;
    private UserService userService;
    private ListView mTypeLv;
    private ListView mTypeLv1;
    private PopupWindow typeSelectPopup;
    private PopupWindow typeSelectPopup1;
    private List<String> testData;
    private List<Department> majorData;
    private ArrayAdapter<String> testDataAdapter;
    private ArrayAdapter<String> majorDataAdapter;
    private String value;
    private Integer school_id;
    private Integer department_id;
    private int flag;
    private Map<String, Integer> school_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        saveChange = (TextView) findViewById(R.id.tvOnRight);
        settingHead = (ImageView) findViewById(R.id.setting_head);
        settingName = (EditText) findViewById(R.id.setting_name);
        settingSummary = (EditText) findViewById(R.id.setting_summary);
        settingSex = (TextView) findViewById(R.id.setting_sex);
        settingSchool = (TextView) findViewById(R.id.setting_school);
        out1 = (ImageView) findViewById(R.id.out1);
        settingDepartment = (TextView) findViewById(R.id.setting_department);
        out2 = (ImageView) findViewById(R.id.out2);
        settingArea = (EditText) findViewById(R.id.setting_area);
        flag = 0;
        school_map = new HashMap<String, Integer>();

        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,null);
        userService = retrofit.create(UserService.class);

        TestData();

        out1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.out1:
                        // 点击控件后显示popup窗口
                        initSelectPopup();
                        // 使用isShowing()检查popup窗口是否在显示状态
                        if (typeSelectPopup != null && !typeSelectPopup.isShowing()) {
                            typeSelectPopup.showAsDropDown(settingSchool, 0, 0);
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
                        if (flag == 0){
                            if (settingSchool.getText().toString().equals("未编辑学校")){
                                Log.d("fuck:", "0");
                                Toast.makeText(Personal.this, "请先选择学校", Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d("fuck:", "1"+school_map.get(settingSchool.getText().toString()));
                                userService.getSchoolDepartment(school_map.get(settingSchool.getText().toString())+"")
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
                                                flag = 1;
                                                // 点击控件后显示popup窗口
                                                initSelectPopup1();
                                                // 使用isShowing()检查popup窗口是否在显示状态
                                                if (typeSelectPopup1 != null && !typeSelectPopup1.isShowing()) {
                                                    typeSelectPopup1.showAsDropDown(settingDepartment, 0, 0);
                                                }
                                            }
                                        });
                            }

                        }else {
                            Log.d("fuck:", "2");
                            // 点击控件后显示popup窗口
                            initSelectPopup1();
                            // 使用isShowing()检查popup窗口是否在显示状态
                            if (typeSelectPopup1 != null && !typeSelectPopup1.isShowing()) {
                                typeSelectPopup1.showAsDropDown(settingDepartment, 0, 0);
                            }
                        }
                        break;
                }
            }
        });

        //获取当前用户
        UserUtil tmp = new UserUtil(Personal.this);
        user = tmp.getUer();

        //修改头像
        String image_url = user.getHead_image_url();
        Picasso.with(Personal.this).load(Const.BASE_IP + image_url).into(settingHead);
        settingHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Personal", "onClick: seems ok");
                PhotoUtils.showChoosePicDialog(Personal.this);
            }
        });

        //修改昵称
        settingName.setText(user.getNickname());
        settingName.setSelection(user.getNickname().length());
        settingName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingName.setCursorVisible(true);
            }
        });
        //修改个性签名
        if (user.getSummary() == null ||  user.getSummary().equals("")) {
            settingSummary.setHint("编辑个性签名");
        } else {
            settingSummary.setText(user.getSummary());
            settingSummary.setSelection(user.getSummary().length());
        }
        //修改性别
        settingSex.setText(user.getSex());
        settingSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseSex();
            }
        });
        //修改地区
        if (user.getArea() == null || user.getArea().equals("")) {
            settingArea.setHint("编辑地区");
        } else {
            settingArea.setText(user.getArea());
            settingArea.setSelection(user.getArea().length());
        }
        //修改学校
        if (user.getSchool_name() == null || user.getSchool_name().equals("")) {
            settingSchool.setText("未编辑学校");
        } else {
            settingSchool.setText(user.getSchool_name());
        }

        //修改院系
        if (user.getDepartment_name() == null || user.getDepartment_name().equals("")){
            settingDepartment.setText("编辑院系");
        }else{
            settingDepartment.setText(user.getDepartment_name());
        }

        //保存修改
        saveChange.setText("保存");
        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setNickname(settingName.getText().toString());
                user.setSummary(settingSummary.getText().toString());
                user.setSex(settingSex.getText().toString());
                user.setArea(settingArea.getText().toString());
                user.setSchool_name(settingSchool.getText().toString());
                user.setDepartment_name(settingDepartment.getText().toString());
                MultipartBody.Builder builder= new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user", JSON.toJSONString(user));
                if(PhotoUtils.imagePath != null){
                    File file = new File(PhotoUtils.imagePath);
                    builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                }
                RequestBody body = builder.build();
                userService.updateUserById(body)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<SimpleResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("保存个人信息","出错了");
                            }

                            @Override
                            public void onNext(SimpleResponse simpleResponse) {
                                if(simpleResponse.getErr() == null){
                                    SharedPreferences.Editor editor = getSharedPreferences
                                            (USER_SHARE, Context.MODE_PRIVATE).edit();
                                    Toast.makeText(Personal.this, "保存修改成功", Toast.LENGTH_SHORT).show();
                                    user.setNickname(settingName.getText().toString());
                                    Map<String,Object>extra = simpleResponse.getExtra();
                                    user.setHead_image_url((String) extra.get(Const.HEAD_IMAGE_URL));
                                    user.setSummary(settingSummary.getText().toString());
                                    user.setSex(settingSex.getText().toString());
                                    user.setArea(settingArea.getText().toString());
                                    user.setSchool_id(school_id);
                                    user.setSchool_name(settingSchool.getText().toString());
                                    user.setDepartment_id(department_id);
                                    user.setDepartment_name(settingDepartment.getText().toString());
                                    editor.putString("user",JSON.toJSONString(user));
                                    editor.apply();
                                    //通知主界面
                                    EventBus.getDefault().post(user);
                                    finish();
                                }else {
                                    Toast.makeText(Personal.this, "保存修改失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }


    private void chooseSex() {
        int selected;
        if (settingSex.getText().equals("男")) selected = 0;
        else selected = 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(Personal.this);
        builder.setSingleChoiceItems(choice, selected, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingSex.setText(choice[which]);
                dialog.dismiss();   //选择之后对话框消失
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    PhotoUtils.startPhotoZoom(PhotoUtils.tempUri, this); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    PhotoUtils.startPhotoZoom(data.getData(), this); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        PhotoUtils.setImageToView(data, settingHead); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    private void initSelectPopup() {
        mTypeLv = new ListView(this);
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
                settingSchool.setText(value);
                user.setSchool_name(value);
                user.setSchool_id(school_map.get(value));
                school_id = school_map.get(value);
                userService.getSchoolDepartment(school_map.get(value)+"")
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
                                flag = 1;
                            }
                        });
                // 选择完后关闭popup窗口
                typeSelectPopup.dismiss();
            }
        });
        typeSelectPopup = new PopupWindow(mTypeLv, settingSchool.getWidth() + out1.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
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
        final List<String> stringList = new ArrayList<String>();
        //if (flag ==1){
        for (int i = 0; i < majorData.size(); i++){
            stringList.add(majorData.get(i).getDepartment_name());
        }
        /*}else{
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
                            for (int i = 0; i < majorData.size(); i++){
                                stringList.add(majorData.get(i).getDepartment_name());
                            }
                        }
                    });
        }*/

        // 设置适配器
        majorDataAdapter = new ArrayAdapter<String>(this, R.layout.popup_text_item, stringList);
        mTypeLv1.setAdapter(majorDataAdapter);

        // 设置ListView点击事件监听
        mTypeLv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 在这里获取item数据
                value = majorData.get(position).getDepartment_name();
                // 把选择的数据展示对应的TextView上
                settingDepartment.setText(value);
                user.setDepartment_name(value);
                user.setDepartment_id(majorData.get(position).getDepartment_id());
                department_id = majorData.get(position).getDepartment_id();
                // 选择完后关闭popup窗口
                typeSelectPopup1.dismiss();
            }
        });
        typeSelectPopup1 = new PopupWindow(mTypeLv1, settingDepartment.getWidth() + out2.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        // 取得popup窗口的背景图片
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
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
        /*for (int i = 0; i < 20; i++) {
            String str = new String("数据" + i);
            testData.add(str);
        }*/
        school_map.put("中山大学", 12002);
        school_map.put("华南理工大学", 12001);
        school_map.put("深圳大学", 12051);
        school_map.put("暨南大学", 12003);
        school_map.put("华南师范大学", 12004);
        school_map.put("华南农业大学", 12006);
        school_map.put("南方医科大学", 12010);
        school_map.put("广东外语外贸大学", 12008);
        school_map.put("广州大学", 12007);
        school_map.put("广东工业大学", 12005);
        school_map.put("汕头大学", 12101);
        school_map.put("广州中医药大学", 12009);

        testData = new ArrayList<>(school_map.keySet());
        /*testData.add("中山大学");
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
        testData.add("广东中医药大学");*/
    }

}
