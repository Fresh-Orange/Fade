package com.sysu.pro.fade.my.activity;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import java.io.File;
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

/**
 * Created by huanggzh5 on 2018/3/9.
 */

public class SetContentActivity extends LoginBaseActivity {
    protected static final int TAKE_PICTURE = 1;
    protected static final int CHOOSE_PICTURE = 0;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private static final int PERMISSION_REQUEST_CODE = 0X00000060;
    private ImageView nextbtn;
    private ImageView myhead;
    private EditText edRegisterNickname;
    private RadioButton male;
    private RadioButton female;
    private User user;
    private Bundle mbundle;
    private SharedPreferences sharedPreferences;
    private ImageView backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_content);
        sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        backbtn = (ImageView) findViewById(R.id.back_btn);
        nextbtn = (ImageView) findViewById(R.id.next);
        myhead = (ImageView) findViewById(R.id.ivRegisterUserHead);
        edRegisterNickname = (EditText) findViewById(R.id.edRegisterNickname);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        user = new User();
        mbundle = new Bundle();

        mbundle = getIntent().getExtras();
        user = (User) mbundle.getSerializable("user");
        //Toast.makeText(SetContentActivity.this, user.getTelephone(), Toast.LENGTH_SHORT).show();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(SetContentActivity.this, SetSchoolActivity.class));
                String nickname = edRegisterNickname.getText().toString();
                if (PhotoUtils.imagePath == null){
                    Toast.makeText(SetContentActivity.this,"必须选择头像",Toast.LENGTH_SHORT).show();
                }else if(nickname.equals("")){
                    Toast.makeText(SetContentActivity.this,"必须输入昵称",Toast.LENGTH_SHORT).show();
                }else if (!male.isChecked() && !female.isChecked()){
                    Toast.makeText(SetContentActivity.this,"必须选择性别",Toast.LENGTH_SHORT).show();
                }else{
                    //progressDialog .show();
                    user.setNickname(nickname);
                    String sex = male.isChecked() ? "男" : "女";
                    user.setSex(sex);
                    MultipartBody.Builder builder= new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("user", JSON.toJSONString(user));
                    /*if(PhotoUtils.imagePath != null){
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
                    }*/
                    File file = new File(PhotoUtils.imagePath);
                    builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
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
                                        //progressDialog.dismiss();
                                        SimpleResponse simpleResponse = JSON.parseObject(responseBody.string(),SimpleResponse.class);
                                        if(simpleResponse.getErr() == null){
                                            Log.d("register","上传注册资料成功");
                                            Toast.makeText(SetContentActivity.this,"注册并登录成功",Toast.LENGTH_SHORT).show();
                                            Map<String,Object> extra = simpleResponse.getExtra();
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
                                            startActivity(new Intent(SetContentActivity.this,SetFadeIDActivity.class));
                                            LoginActivitiesCollector.finishAll();
                                        }else {
                                            Log.e("register","注册失败");
                                            Toast.makeText(SetContentActivity.this,"注册失败："+ simpleResponse.getErr(),
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

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male.setChecked(true);
                female.setChecked(false);
                //user.setSex("男");
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male.setChecked(false);
                female.setChecked(true);
                //user.setSex("女");
            }
        });

        myhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoUtils.showChoosePicDialog(SetContentActivity.this);
            }
        });
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
                        PhotoUtils.setImageToView(data, myhead); // 让刚才选择裁剪得到的图片显示在界面上
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
