package com.sysu.pro.fade.message;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.message.Adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by road on 2017/7/14.
 */
public class ContentMessage {
    private Activity activity;
    private Context context;
    private View rootview;

    private RecyclerView notification_Rv;
    private ChatAdapter adapter;
    private List<NotificationUser> userList = new ArrayList<NotificationUser>();

    private int processCount;
    private int newFanCount;
    private int commentCount;

    private TextView processCountTv;
    private TextView newFanCountTv;
    private TextView commentCountTv;

    public ContentMessage(Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;

        initLayout();
        initListener();
        setNotification();  //设置消息数量
    }



    private void initLayout() {
        processCountTv = (TextView) rootview.findViewById(R.id.notification_process_count);
        newFanCountTv = (TextView) rootview.findViewById(R.id.notification_new_fan_count);
        commentCountTv = (TextView) rootview.findViewById(R.id.notification_comment_count);
        notification_Rv = (RecyclerView) rootview.findViewById(R.id.notification_recyclerView);
    }

    private void setNotification() {
        Uri uri = Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514632966065&di=c3b195646" +
                "3daaa6431d4cc26c04083a3&imgtype=0&src=http%3A%2F%2Fwww.ghost64.com%2" +
                "Fqqtupian%2FzixunImg%2Flocal%2F2017%2F03%2F09%2F14890377211618.jpg");
        int user_count = 22;
        String user_id = "黄路";
        String user_content =  "美国的小雷，比你们不知道高到哪里去了，你们还是太年轻";
        String user_time = "16:22";
        processCount = 33;
        newFanCount = 29;
        commentCount = 99;
        userList.add(new NotificationUser(uri, user_count, user_id, user_content, user_time));
        Uri uri2 = Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514632966065&di=c3b195646" +
                "3daaa6431d4cc26c04083a3&imgtype=0&src=http%3A%2F%2Fwww.ghost64.com%2" +
                "Fqqtupian%2FzixunImg%2Flocal%2F2017%2F03%2F09%2F14890377211618.jpg");
        int user_count2 = 0;
        String user_id2 = "胡文浩";
        String user_content2 =  "中国的小云，比你们不知道高到哪里去了，你们还是太年轻";
        String user_time2 = "2017-2-28";
        userList.add(new NotificationUser(uri2, user_count2, user_id2, user_content2, user_time2));
        adapter = new ChatAdapter(userList);
        notification_Rv.setLayoutManager(new LinearLayoutManager(context));
        notification_Rv.setAdapter(adapter);
        processCountTv.setVisibility(View.VISIBLE);
        newFanCountTv.setVisibility(View.VISIBLE);
        commentCountTv.setVisibility(View.VISIBLE);
        processCountTv.setText(String.valueOf(processCount));
        newFanCountTv.setText(String.valueOf(newFanCount));
        commentCountTv.setText(String.valueOf(commentCount));
    }

    private void initListener() {
        rootview.findViewById(R.id.notification_process_contribution).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进度贡献查看
            }
        });

        rootview.findViewById(R.id.notification_new_fan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新的粉丝查看
            }
        });

        rootview.findViewById(R.id.notification_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //评论查看
            }
        });
    }

}
