package com.sysu.pro.fade.my.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.my.activity.AddContentActivity;
import com.sysu.pro.fade.tool.UserTool;
import com.sysu.pro.fade.utils.GsonUtil;
import com.sysu.pro.fade.utils.PhotoUtils;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.Map;

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


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SharedPreferences.Editor editor = getSharedPreferences
                    (USER_SHARE, Context.MODE_PRIVATE).edit();
            //昵称
            if(msg.what == 1){
                String ans_str = (String) msg.obj;
                if(ans_str.equals("{}")){
                    //更新到本地
                    editor.putString(Const.NICKNAME, settingName.getText().toString());
                    editor.apply();
                }else{
                    //提示“服务器异常”
                    Toast.makeText(Personal.this, "昵称保存失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
            //头像
            if(msg.what == 2){
                Map<String,Object> map = (Map<String,Object>)msg.obj;
                String err = (String) map.get(Const.ERR);
                if(err == null){
                    String head_image_url = (String) map.get(Const.HEAD_IMAGE_URL);
                    //修改头像成功，服务器返回头像url，把数据存到本地，更新头像
                    editor.putString(Const.HEAD_IMAGE_URL, head_image_url);
                    editor.apply();
                }else{
                    //打印err
                    Toast.makeText(Personal.this, err, Toast.LENGTH_SHORT).show();
                }
            }
            //个签
            else if(msg.what == 3){
                String ans_str = (String) msg.obj;
                if(ans_str.equals("")){
                    //更新到本地
                    editor.putString(Const.SUMMARY, settingSummary.getText().toString());
                    editor.apply();
                }else{
                    //提示“服务器异常”
                    Toast.makeText(Personal.this, "个签保存失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
            //性别
            else if(msg.what == 4){
                String ans_str = (String) msg.obj;
                if(ans_str.equals("{}")){
                    //更新到本地
                    editor.putString(Const.SEX, settingSex.getText().toString());
                    editor.apply();
                }else{
                    //提示“服务器异常”
                    Toast.makeText(Personal.this, "性别保存失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
            //地区
            else if(msg.what == 5){
                String ans_str = (String) msg.obj;
                if(ans_str.equals("{}")){
                    //更新到本地
                    editor.putString(Const.AREA, settingArea.getText().toString());
                    editor.apply();
                }else{
                    //提示“服务器异常”
                    Toast.makeText(Personal.this, "地区保存失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
            //学校
            else if(msg.what == 6){
                String ans_str = (String) msg.obj;
                if(ans_str.equals("{}")){
                    //更新到本地
                    editor.putString(Const.SCHOOL, settingSchool.getText().toString());
                    editor.apply();
                }else{
                    //提示“服务器异常”
                    Toast.makeText(Personal.this, "学校保存失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

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

        //获取当前用户
        UserUtil tmp = new UserUtil(Personal.this);
        user = tmp.getUer();

        //修改头像
        String image_url = user.getHead_image_url();
        Picasso.with(Personal.this).load(image_url).into(settingHead);
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
        if (user.getSummary().equals("")) {
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
        if (user.getArea().equals("")) {
            settingArea.setHint("编辑地区");
        } else {
            settingArea.setText(user.getArea());
            settingArea.setSelection(user.getArea().length());
        }
        //修改学校
        if (user.getSchool().equals("")) {
            settingSchool.setHint("编辑学校");
        } else {
            settingSchool.setText(user.getSchool());
            settingSchool.setSelection(user.getSchool().length());
        }

        //保存修改
        saveChange.setText("保存");
        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDS(user);     //自定义方法，更新数据库的内容并更新个人主页
                Toast.makeText(Personal.this, "保存修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    //更新数据库的内容
    private void updateDS(User user) {

        // TODO: 2017/9/4 更新头像
        //UserTool.uploadHeadImage(handler, user.getUser_id(), head_path);
        UserTool.editNickname(handler, user.getUser_id(), settingName.getText().toString());
        UserTool.editSummary(handler, user.getUser_id(), settingSummary.getText().toString());
        UserTool.editSex(handler, user.getUser_id(), settingSex.getText().toString());
        UserTool.editArea(handler, user.getUser_id(), settingArea.getText().toString());
        UserTool.editSchool(handler, user.getUser_id(), settingSchool.getText().toString());
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
                        UserTool.uploadHeadImage(handler, user.getUser_id(), PhotoUtils.imagePath);
                    }
                    break;
            }
        }
    }
}
