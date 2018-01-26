package com.sysu.pro.fade.message.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.beans.UserQuery;
import com.sysu.pro.fade.home.activity.OtherActivity;
import com.sysu.pro.fade.message.Adapter.ContributeAdapter;
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
    private List<User> users = new ArrayList<User>();
    private User user;
    private Retrofit retrofit;
    private MessageService messageService;
    private Integer start = 0;
    private RefreshLayout refreshLayout;
    private FansAdapter adapter;
    private String point; //时间点，分段请求需要记录的
    private Boolean isEnd = false;
    private View footView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans);
        notification_Rv = (RecyclerView) findViewById(R.id.fans_recycler);
        notification_Rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FansAdapter(users,this);
        notification_Rv.setAdapter(adapter);

        user = new UserUtil(this).getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        messageService = retrofit.create(MessageService.class);
        initLoadAddMore();
        messageService.getAddFans(user.getUser_id().toString(), "0","null")
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
                        point = userQuery.getPoint();
                        List<User>list = userQuery.getList();
                        Log.i("收到贡献" , "" + list.size());
                        if(list.size() != 0){
                            users.addAll(list);
                            adapter.notifyDataSetChanged();
                            if(list.size() < 20) isEnd = true;
                            else {
                                refreshLayout.setEnableLoadmore(true);
                            }
                        }else {
                            isEnd = true;
                        }
                        if(isEnd){
                            refreshLayout.setEnableLoadmore(false);
                            addFootView();
                            footViewListen(footView);
                        }
                    }
                });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //单项点击监听
        adapter.setOnItemClickListener(new FansAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                User temp = users.get(position);
                if(temp != null){
                    Intent intent = new Intent(FansActivity.this, OtherActivity.class);
                    intent.putExtra(Const.USER_ID , temp.getUser_id());
                    startActivity(intent);
                }
            }
        });
    }

    private void initLoadAddMore() {
        //设置底部加载刷新,更新新通知
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);    //取消下拉刷新功能
        refreshLayout.setEnableAutoLoadmore(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                messageService.getAddFans(user.getUser_id().toString(), start.toString(),point)
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
                                Log.i("收到贡献" , "" + list.size());
                                if(list.size() != 0){
                                    users.addAll(list);
                                    adapter.notifyDataSetChanged();
                                    if(list.size() < 20) isEnd = true;
                                }else {
                                    isEnd = true;
                                }
                                refreshlayout.finishLoadmore();
                                if(isEnd){
                                    refreshLayout.setEnableLoadmore(false);
                                    refreshlayout.setEnableAutoLoadmore(false);
                                    addFootView();
                                    footViewListen(footView);
                                }
                            }
                        });
            }
        });
    }
    private void initLoadOldMore() {
        //设置底部加载刷新，更新以前的旧通知
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);    //取消下拉刷新功能
        refreshLayout.setEnableAutoLoadmore(true);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                messageService.getOldFans(user.getUser_id().toString(), start.toString())
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
                                Log.i("收到评论" , "" + list.size());
                                if(list.size() != 0){
                                    users.addAll(list);
                                    adapter.notifyDataSetChanged();
                                    if(list.size() < 20) isEnd = true;
                                }else {
                                    isEnd = true;
                                }
                                refreshlayout.finishLoadmore();
                                if(isEnd){
                                    refreshLayout.setEnableLoadmore(false);
                                    refreshlayout.setEnableAutoLoadmore(false);
                                    footViewEnd();
                                }
                            }
                        });
            }
        });
    }

    public void footViewListen(final View footView){
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users.remove(users.size() - 1);
                adapter.notifyDataSetChanged();
                refreshLayout.setEnableLoadmore(true);
                refreshLayout.setEnableAutoLoadmore(true);
                isEnd = false;
                initLoadOldMore();
                Log.i("查看更多start=", start.toString());
                messageService.getOldFans(user.getUser_id().toString(),start.toString())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<UserQuery>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onNext(UserQuery userQuery) {
                                start = userQuery.getStart();
                                List<User>list = userQuery.getList();
                                Log.i("收到粉丝" , "" + list.size());
                                if(list.size() != 0){
                                    users.addAll(list);
                                    adapter.notifyDataSetChanged();
                                    if(list.size() < 20) isEnd = true;
                                }else {
                                    isEnd = true;
                                    footViewEnd();
                                }
                                refreshLayout.finishLoadmore();
                            }
                        });
            }
        });
    }

    private void footViewEnd(){
        TextView tv_foot_end = footView.findViewById(R.id.tv_foot_end);
        tv_foot_end.setText("已经到底部了");
        footView.setOnClickListener(null);
        addFootView();
    }

    public void addFootView(){
        footView = LayoutInflater.from(FansActivity.this).
                inflate(R.layout.foot,null);
        adapter.VIEW_FOOTER = footView;
        User user = new User();
        user.setViewType(ContributeAdapter.TYPE_FOOTER);
        users.add(user);
        adapter.notifyDataSetChanged();
    }
}
