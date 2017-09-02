package com.sysu.pro.fade.my.setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.sysu.pro.fade.R;

public class About extends AppCompatActivity {

    private ListView aboutList;
    private String[] list = {"联系我们", "官方微博"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        aboutList = (ListView) findViewById(R.id.about_list);
        aboutList.setAdapter(adapter);
    }
}
