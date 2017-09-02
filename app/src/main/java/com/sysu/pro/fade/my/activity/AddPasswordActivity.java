package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.Const;

public class AddPasswordActivity extends AppCompatActivity {

    private EditText edAddPassword;
    private Button btnToSubmitPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        edAddPassword = (EditText) findViewById(R.id.edAddPassword);
        btnToSubmitPwd = (Button) findViewById(R.id.btnToSubmitPwd);

        btnToSubmitPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telephone = getIntent().getStringExtra(Const.TELEPHONE);
                String password = edAddPassword.getText().toString();
                Intent intent = new Intent(AddPasswordActivity.this,AddContentActivity.class);
                intent.putExtra(Const.TELEPHONE,telephone);
                intent.putExtra(Const.PASSWORD,password);
                startActivity(intent);
                finish();
            }
        });
    }
}
