package com.sysu.pro.fade.my.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.utils.Const;
import com.sysu.pro.fade.tool.RegisterTool;
import com.sysu.pro.fade.utils.PhotoUtils;

import java.io.File;
import java.util.UUID;


public class RegisterActivity extends AppCompatActivity {

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private ImageView iv_personal_icon;
    private EditText edUserName;
    private EditText edPassword;
    private Button btnRegister;
    private String   imagePath;
    private RadioGroup radioGroup;
    private String sex;
    private String user_id;
    private int ifSucess = 0;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                String ans = (String) msg.obj;
                Toast.makeText(RegisterActivity.this,ans,Toast.LENGTH_SHORT).show();
                if(ans.equals("注册成功")){
                    //成功则发送图片
                    RegisterTool.sendImage(Const.IP,handler,"head",imagePath,user_id);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nickname",edUserName.getText().toString());
                    editor.putString("user_id",user_id);
                    editor.putString("sex",sex);
                    editor.putString("password",edPassword.getText().toString());
                    editor.putString("head_image",imagePath);
                    editor.commit();
                }else{
                    progressDialog.dismiss();
                }
                super.handleMessage(msg);
            }else{
                String rsp = (String) msg.obj;
                Log.d("image",rsp);
                progressDialog.dismiss();
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                finish();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        progressDialog = new ProgressDialog(RegisterActivity.this);
        iv_personal_icon = (ImageView) findViewById(R.id.ivRegisterUserHead);
        edUserName = (EditText) findViewById(R.id.edRegisterNickname);
        edPassword = (EditText) findViewById(R.id.edRegisterPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        radioGroup = (RadioGroup) findViewById(R.id.radioSex);

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
                showChoosePicDialog();
            }
        });



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog .show();
                String nickname = edUserName.getText().toString();
                String password = edPassword.getText().toString();
                if(nickname.equals("") || password.equals("")){
                    Toast.makeText(RegisterActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    user_id = UUID.randomUUID().toString();
                    System.out.println(user_id);

                    RegisterTool.sendToRegister(Const.IP,handler,nickname,password,sex,user_id);


                }
            }
        });
    }

    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "image.jpg"));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = PhotoUtils.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了
            iv_personal_icon.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了

        imagePath = PhotoUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("imagePath", imagePath+"");
        if(imagePath != null){
            // 拿着imagePath上传了
            // ...
        }
    }


}