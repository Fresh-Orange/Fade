package com.sysu.pro.fade.my.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.PhotoUtils;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Map;

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
    private EditText settingSchool;
    private EditText settingArea;
    private String[] choice = new String[] {"男", "女"};
    private static final int CHOOSE_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private Retrofit retrofit;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        saveChange = (TextView) findViewById(R.id.tvOnRight);
        settingHead = (ImageView) findViewById(R.id.setting_head);
        settingName = (EditText) findViewById(R.id.setting_name);
        settingSummary = (EditText) findViewById(R.id.setting_summary);
        settingSex = (TextView) findViewById(R.id.setting_sex);
        settingSchool = (EditText) findViewById(R.id.setting_school);
        settingArea = (EditText) findViewById(R.id.setting_area);

        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,null);
        userService = retrofit.create(UserService.class);

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
            settingSchool.setHint("编辑学校");
        } else {
            settingSchool.setText(user.getSchool_name());
            settingSchool.setSelection(user.getSchool_name().length());
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
                                    user.setSchool_name(settingSchool.getText().toString());
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



}
