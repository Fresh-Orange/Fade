package com.sysu.pro.fade.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.AddMessage;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.message.Activity.CommentActivity;
import com.sysu.pro.fade.message.Activity.ContributionActivity;
import com.sysu.pro.fade.message.Activity.FansActivity;
import com.sysu.pro.fade.message.Adapter.ChatAdapter;
import com.sysu.pro.fade.message.Class.NotificationUser;
import com.sysu.pro.fade.service.MessageService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private int contributionCount;
    private int newFanCount;
    private int commentCount;

    private TextView processCountTv;
    private TextView newFanCountTv;
    private TextView commentCountTv;

    private User user;
    private Retrofit retrofit;
    private MessageService messageService;
    private Integer start;

    public ContentMessage(Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;

        initNotification();
        initLayout();
        initListener();
        setNotification();  //设置消息数量
    }

    private void initNotification() {
        user = new UserUtil(activity).getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        messageService = retrofit.create(MessageService.class);
        messageService.getAddMessage(user.getUser_id().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AddMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AddMessage addMessage) {
                        contributionCount = addMessage.getAddContributeNum();
                        newFanCount = addMessage.getAddFansNum();
                        commentCount = addMessage.getAddCommentNum();
                        Log.d("yellow", "contribution: " + contributionCount);
                    }
                });
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
//        contributionCount = 33;
//        newFanCount = 29;
//        commentCount = 99;
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
        if (contributionCount > 0) {
            processCountTv.setVisibility(View.VISIBLE);
            processCountTv.setText(String.valueOf(contributionCount));
        }
        if (newFanCount > 0) {
            newFanCountTv.setVisibility(View.VISIBLE);
            newFanCountTv.setText(String.valueOf(newFanCount));
        }
        if (commentCount > 0) {
            commentCountTv.setVisibility(View.VISIBLE);
            commentCountTv.setText(String.valueOf(commentCount));
        }
    }

    private void initListener() {
        rootview.findViewById(R.id.notification_process_contribution).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进度贡献查看
                Intent intent = new Intent(context, ContributionActivity.class);
                activity.startActivity(intent);
                contributionCount = 0;
            }
        });

        rootview.findViewById(R.id.notification_new_fan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新的粉丝查看
                Intent intent = new Intent(context, FansActivity.class);
                activity.startActivity(intent);
                newFanCount = 0;
            }
        });

        rootview.findViewById(R.id.notification_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //评论查看
                Intent intent = new Intent(context, CommentActivity.class);
                activity.startActivity(intent);
                commentCount = 0;
            }
        });
    }

}
