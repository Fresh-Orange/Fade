package com.sysu.pro.fade.my.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.tool.UserTool;
import com.sysu.pro.fade.utils.PhotoUtils;

import java.util.Map;

import static com.sysu.pro.fade.utils.PhotoUtils.tempUri;

/*
用户名+密码的注册界面
 */

public class AddContentActivity extends AppCompatActivity {

    protected static final int TAKE_PICTURE = 1;
    protected static final int CHOOSE_PICTURE = 0;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private TextView tvToRegister;
    private ImageView iv_personal_icon;
    private EditText edUserName;
    private ImageView btnRegister;
    private RadioGroup radioGroup;
    private String sex;

    private String password;
    private String telephone;

    private static final int PERMISSION_REQUEST_CODE = 0X00000060;
    private boolean isToSettings = false;
    private int ifSucess = 0;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    Map<String,Object>ans_map = (Map<String, Object>) msg.obj;
                    String fade_name = (String) ans_map.get(Const.FADE_NAME);
                    Integer user_id = (Integer) ans_map.get(Const.USER_ID);
                    String register_time = (String) ans_map.get(Const.REGISTER_TIME);
                    String err = (String) ans_map.get(Const.ERR);
                    if(err == null){
                        Toast.makeText(AddContentActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        //成功则发送图片,并存储昵称  fade号  电话  性别  密码 user_id head_image_url 注册时间
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Const.NICKNAME,edUserName.getText().toString());
                        editor.putInt(Const.USER_ID,user_id);
                        editor.putString(Const.SEX,sex);
                        editor.putString(Const.PASSWORD,password);
                        editor.putString(Const.TELEPHONE,telephone);
                        editor.putString(Const.FADE_NAME,fade_name);
                        editor.putString(Const.REGISTER_TIME,register_time);
                        //最后设置登陆类型 为账号密码登陆
                        editor.putString(Const.LOGIN_TYPE,"0");
                        editor.commit();
                        //如果本地头像不为空的话，则上传到服务器
                        if(PhotoUtils.imagePath != null)
                            UserTool.uploadHeadImage(handler,user_id,PhotoUtils.imagePath);
                        else{
                            progressDialog.dismiss();
                            startActivity(new Intent(AddContentActivity.this,MainActivity.class));
                            finish();
                        }

                    }else {
                        Toast.makeText(AddContentActivity.this,err,Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
                break;

                case 2:{
                    Map<String,Object>ans_map = (Map<String, Object>) msg.obj;
                    String head_image_url = (String) ans_map.get(Const.HEAD_IMAGE_URL);
                    String err = (String) ans_map.get(Const.ERR);
                    if(err == null){
                        SharedPreferences.Editor editor2 = sharedPreferences.edit();
                        editor2.putString(Const.HEAD_IMAGE_URL,head_image_url);
                        editor2.commit();
                        progressDialog.dismiss();
                        //发送头像成功
                        startActivity(new Intent(AddContentActivity.this,MainActivity.class));
                        finish();
                        progressDialog.dismiss();
                    }else{
                        Toast.makeText(AddContentActivity.this,err,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddContentActivity.this,MainActivity.class));
                        finish();
                        progressDialog.dismiss();
                    }
                }
                break;
            }
            super.handleMessage(msg);
        }
    };

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

        password = getIntent().getStringExtra(Const.PASSWORD);
        telephone = getIntent().getStringExtra(Const.TELEPHONE);
//        telephone = "189026675";
//        password = "hhh";

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.male)  sex = "男";
                else sex = "女";
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
                progressDialog .show();
                String nickname = edUserName.getText().toString();
                if(nickname.equals("")){
                    Toast.makeText(AddContentActivity.this,"输入昵称不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    UserTool.sendToRegister(handler,nickname,password,sex,telephone);
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