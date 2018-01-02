package com.sysu.pro.fade.message.Activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.beans.UserQuery;
import com.sysu.pro.fade.message.Adapter.ContributionAdapter;
import com.sysu.pro.fade.message.Adapter.FansAdapter;
import com.sysu.pro.fade.service.MessageService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FansActivity extends MainBaseActivity {
    private RecyclerView notification_Rv;
    private FansAdapter adapter;
    private List<User> users = new ArrayList<User>();
    private User user;
    private Retrofit retrofit;
    private MessageService messageService;
    private Integer start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans);
        notification_Rv = (RecyclerView) findViewById(R.id.fans_recycler);
        adapter = new FansAdapter(FansActivity.this,users);
        notification_Rv.setLayoutManager(new LinearLayoutManager(this));
        notification_Rv.setAdapter(adapter);
        user = new UserUtil(this).getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        messageService = retrofit.create(MessageService.class);
        messageService.getAddFans(user.getUser_id().toString(), "0")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserQuery>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UserQuery userQuery) {
                        start = userQuery.getStart();
                        List<User>list = userQuery.getList();
                        if(list.size() != 0){
                            users.addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }

}
