package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huanggzh5 on 2018/3/9.
 */

public class SetPasswordActivity extends LoginBaseActivity{
    private ImageView nextbtn;
    private EditText first_password;
    private EditText second_password;
    private int fp_flag;
    private String fp;
    private String sp;
    private String mobilePhoneNumber;
    private ImageView backbtn;
    private LinearLayout red_wrong_password;
    private TextView red_text;
    private int number_flag = 0;
    private int zifu_flag = 0;
    private User user;

    /*private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {

            }else if (msg.what == 2){
                red_text.setText("密码太长");
                red_wrong_password.setVisibility(View.VISIBLE);
            }else if (msg.what == 3){
                red_text.setText("密码需包含英文与数字");
                red_wrong_password.setVisibility(View.VISIBLE);
            }else if (msg.what == 4){
                red_wrong_password.setVisibility(View.INVISIBLE);
            }else if (msg.what == 5){
                red_text.setText("密码包含非法字符");
                red_wrong_password.setVisibility(View.VISIBLE);
            }
        }
    };*/

    TextWatcher mTextWatchr = new TextWatcher() {
        private CharSequence temp;
        //private int editStart ;
        //private int editEnd ;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            //editStart = first_password.getSelectionStart();
            //editEnd = first_password.getSelectionEnd();
            judgePassword(temp.toString());
            if (temp.length() < 6) {
                if (temp.length() == 0){
                    red_wrong_password.setVisibility(View.INVISIBLE);
                }else{
                    red_text.setText("密码太短");
                    red_wrong_password.setVisibility(View.VISIBLE);
                    nextbtn.setImageResource(R.drawable.next_gray);
                }
            }else  if (temp.length() > 16){
                red_text.setText("密码太长");
                red_wrong_password.setVisibility(View.VISIBLE);
                nextbtn.setImageResource(R.drawable.next_gray);
            }else if(fp_flag == 1){
                red_text.setText("密码需包含英文与数字");
                red_wrong_password.setVisibility(View.VISIBLE);
                nextbtn.setImageResource(R.drawable.next_gray);
            }else if (fp_flag == 2){
                red_wrong_password.setVisibility(View.INVISIBLE);
            }else{
                red_text.setText("密码包含非法字符");
                red_wrong_password.setVisibility(View.VISIBLE);
                nextbtn.setImageResource(R.drawable.next_gray);
            }
        }
    };

    TextWatcher mTextWatchr1 = new TextWatcher() {
        private CharSequence temp;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (temp.length() > 0 && fp_flag == 2){
                red_wrong_password.setVisibility(View.INVISIBLE);
                nextbtn.setImageResource(R.drawable.next_blue);
            }else{
                nextbtn.setImageResource(R.drawable.next_gray);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        nextbtn = (ImageView) findViewById(R.id.next);
        first_password = (EditText) findViewById(R.id.my_telephone);
        second_password = (EditText) findViewById(R.id.my_valid);
        mobilePhoneNumber = getIntent().getStringExtra("mobilePhoneNumber");
        backbtn = (ImageView) findViewById(R.id.back_btn);
        red_wrong_password = (LinearLayout) findViewById(R.id.red_wrong_password);
        red_text = (TextView) findViewById(R.id.red_text);
        first_password.addTextChangedListener(mTextWatchr);
        second_password.addTextChangedListener(mTextWatchr1);
        user = new User();

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(SetPasswordActivity.this, SetContentActivity.class));
                fp = first_password.getText().toString();
                sp = second_password.getText().toString();
                if (fp_flag == 2){
                    if (fp.equals(sp)){
                        user.setTelephone(mobilePhoneNumber);
                        user.setPassword(fp);
                        Intent intent = new Intent(SetPasswordActivity.this, SetSchoolActivity.class);
                        Bundle mbundle = new Bundle();
                        mbundle.putSerializable("user", user);
                        intent.putExtras(mbundle);
                        startActivity(intent);
                        finish();
                    }else {
                        red_text.setText("密码不相同");
                        red_wrong_password.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void judgePassword(String account){
        /*Pattern p1 = Pattern.compile("/^(?![0-9]+$)/");
        Pattern p2 = Pattern.compile("/^(?![a-zA-Z]+$)");
        Pattern p3 = Pattern.compile("^[0-9A-Za-z]$");
        Matcher m = p1.matcher(account);
        Matcher n = p2.matcher(account);
        Matcher k = p3.matcher(account);
        if(!m.matches()){
            fp_flag = 0;
        }else if (!n.matches()){
            fp_flag = 1;
        }else if (k.matches()){
            fp_flag = 2;
        }else {
            fp_flag = 3;
        }*/
        fp_flag = 0;
        number_flag = 0;
        zifu_flag = 0;
        for (int i = 0; i < account.length(); i++){
            if ((account.charAt(i) > '9' || account.charAt(i) < '0')
                    && (account.charAt(i) > 'z' || account.charAt(i) < 'a')
                    && (account.charAt(i) > 'Z' || account.charAt(i) < 'A')){
                fp_flag = 3;
                break;
            }else if (account.charAt(i) <= '9' && account.charAt(i) >= '0'){
                number_flag = 1;
            }else if ((account.charAt(i) <= 'z' && account.charAt(i) >= 'a')
                    || (account.charAt(i) <= 'Z' && account.charAt(i) >= 'A')){
                zifu_flag = 1;
            }
        }
        if (fp_flag != 3){
            if (number_flag == 1 && zifu_flag == 1){
                fp_flag = 2;
            }else {
                fp_flag = 1;
            }
        }

    }
}
