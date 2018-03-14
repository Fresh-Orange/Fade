package com.sysu.pro.fade.my.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginActivitiesCollector;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.TokenModel;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.PhotoUtils;
import com.sysu.pro.fade.utils.RetrofitUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*
用户名+密码的注册界面
 */

public class AddContentActivity extends LoginBaseActivity {

    protected static final int TAKE_PICTURE = 1;
    protected static final int CHOOSE_PICTURE = 0;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private TextView tvToRegister;
    private ImageView iv_personal_icon;
    private EditText edUserName;
    private ImageView btnRegister;
    private RadioGroup radioGroup;
    private static final int PERMISSION_REQUEST_CODE = 0X00000060;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);
        sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        progressDialog = new ProgressDialog(AddContentActivity.this);
        iv_personal_icon = (ImageView) findViewById(R.id.ivRegisterUserHead);
        edUserName = (EditText) findViewById(R.id.edRegisterNickname);
        btnRegister = (ImageView) findViewById(R.id.btnRegister);
        radioGroup = (RadioGroup) findViewById(R.id.radioSex);
        tvToRegister = (TextView) findViewById(R.id.tvOfBackBar);
        tvToRegister.setText("欢迎来到FADE");

        user = new User();
        user.setPassword(getIntent().getStringExtra(Const.PASSWORD));
        user.setTelephone(getIntent().getStringExtra(Const.TELEPHONE));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.male){
                    user.setSex("男");
                }else{
                    user.setSex("女");
                }
            }
        });


        iv_personal_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtils.showChoosePicDialog(AddContentActivity.this);
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = edUserName.getText().toString();
                if(nickname.equals("")){
                    Toast.makeText(AddContentActivity.this,"输入昵称不能为空",Toast.LENGTH_SHORT).show();;
                }
                else{
                    progressDialog .show();
                    user.setNickname(nickname);
                    MultipartBody.Builder builder= new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("user", JSON.toJSONString(user));
                    if(PhotoUtils.imagePath != null){
                        File file = new File(PhotoUtils.imagePath);
                        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                    } else {
                        //如果用户没用选择头像，则使用默认头像
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_head);
                        File file = new File(Environment.getExternalStorageDirectory()
                                + "/Fade/Photo/Fade", String.valueOf(System.currentTimeMillis())
                                + ".jpg");
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                            outputStream.flush();
                            outputStream.close();
                            builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                        } catch (Exception e) {
                            Log.e("create image file", e.toString());
                        }
                    }
                    RequestBody body = builder.build();
                    Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, null);
                    final UserService userService = retrofit.create(UserService.class);
                    userService.registerByName(body)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<ResponseBody>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e("register","上传注册资料失败");
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(ResponseBody responseBody) {
                                    try {
                                        progressDialog.dismiss();
                                        SimpleResponse simpleResponse = JSON.parseObject(responseBody.string(),SimpleResponse.class);
                                        if(simpleResponse.getErr() == null){
                                            Log.d("register","上传注册资料成功");
                                            Toast.makeText(AddContentActivity.this,"注册并登录成功",Toast.LENGTH_SHORT).show();
                                            Map<String,Object>extra = simpleResponse.getExtra();
                                            if(extra != null){
                                                JSONObject jsonObject = (JSONObject) extra.get("tokenModel");
                                                if(jsonObject != null){
                                                    TokenModel tokenModel = jsonObject.toJavaObject(TokenModel.class);
                                                    user.setTokenModel(tokenModel);
                                                    if(tokenModel != null){
                                                        user.setUser_id(tokenModel.getUser_id());
                                                    }
                                                }
                                                user.setFade_name((String) extra.get("fade_name"));
                                                user.setRegister_time((String) extra.get("register_time"));
                                                user.setHead_image_url((String) extra.get("head_image_url"));
                                                user.setConcern_num(0);
                                                user.setFans_num(0);
                                                user.setFade_num(0);
                                                user.setDynamicNum(0);  //本地初始为0
                                            }
                                            //存储用户信息
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("user",JSON.toJSONString(user));
                                            //最后设置登陆类型 为账号密码登陆
                                            editor.putString(Const.LOGIN_TYPE,"0");
                                            editor.apply();
                                            startActivity(new Intent(AddContentActivity.this,MainActivity.class));
                                            LoginActivitiesCollector.finishAll();
                                        }else {
                                            Log.e("register","注册失败");
                                            Toast.makeText(AddContentActivity.this,"注册失败："+ simpleResponse.getErr(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    PhotoUtils.startPhotoZoom(tempUri, this); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    PhotoUtils.startPhotoZoom(data.getData(), this); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        PhotoUtils.setImageToView(data, iv_personal_icon); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 处理权限申请的回调。
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，加载图片。
                PhotoUtils.loadImageForSDCard(this);
            } else {
                //拒绝权限，弹出提示框。
                PhotoUtils.showExceptionDialog(this);
            }
        }
    }

}