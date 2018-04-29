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
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.CommentMessage;
import com.sysu.pro.fade.beans.CommentMessageQuery;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.activity.DetailActivity;
import com.sysu.pro.fade.message.Adapter.CommentAdapter;
import com.sysu.pro.fade.message.Adapter.ContributeAdapter;
import com.sysu.pro.fade.service.MessageService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentActivity extends MainBaseActivity {
    private RecyclerView notification_Rv;
    private CommentAdapter adapter;
    private List<CommentMessage> comments = new ArrayList<CommentMessage>();
    private User user;
    private Retrofit retrofit;
    private MessageService messageService;
    private Integer start = 0;
    private RefreshLayout refreshLayout;
    private String point; //时间点，分段请求需要记录的
    private Boolean isEnd = false;
    private View footView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        notification_Rv = (RecyclerView) findViewById(R.id.comment_recycler);
        adapter = new CommentAdapter(comments,CommentActivity.this);
        notification_Rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(comments,this);
        notification_Rv.setAdapter(adapter);
        user = new UserUtil(this).getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        messageService = retrofit.create(MessageService.class);
        initLoadAddMore();
        messageService.getAddComment(user.getUser_id().toString(), "0", "null")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentMessageQuery>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(CommentMessageQuery query) {
                        start = query.getStart();
                        point = query.getPoint();
                        List<CommentMessage>list = query.getList();
                        Log.i("收到评论" , "" + list.size());
                        if(list.size() != 0){
                            comments.addAll(list);
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
        //单项点击跳转监听
        adapter.setOnItemClickListener(new CommentAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(comments.get(position).getViewType() == null){
                    CommentMessage temp = comments.get(position);
                    Intent intent = new Intent(CommentActivity.this, DetailActivity.class);
                    intent.putExtra(Const.NOTE_ID,temp.getComment_id());
                    intent.putExtra(Const.IS_COMMENT,true);
//                    intent.putExtra(Const.COMMENT_NUM, temp.);
//                    intent.putExtra(Const.COMMENT_ENTITY, temp);
                    intent.putExtra("getFull",true);
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
                messageService.getAddComment(user.getUser_id().toString(), start.toString(),point)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<CommentMessageQuery>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(CommentMessageQuery query) {
                                start = query.getStart();
                                List<CommentMessage>list = query.getList();
                                Log.i("收到贡献" , "" + list.size());
                                if(list.size() != 0){
                                    comments.addAll(list);
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
                messageService.getOldComment(user.getUser_id().toString(), start.toString())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<CommentMessageQuery>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                            @Override
                            public void onNext(CommentMessageQuery query) {
                                start = query.getStart();
                                List<CommentMessage>list = query.getList();
                                Log.i("收到评论" , "" + list.size());
                                if(list.size() != 0){
                                    comments.addAll(list);
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
                comments.remove(comments.size() - 1);
                adapter.notifyDataSetChanged();
                refreshLayout.setEnableLoadmore(true);
                refreshLayout.setEnableAutoLoadmore(true);
                isEnd = false;
                initLoadOldMore();
                Log.i("查看更多start=", start.toString());
                messageService.getOldComment(user.getUser_id().toString(),start.toString())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<CommentMessageQuery>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onNext(CommentMessageQuery query) {
                                start = query.getStart();
                                List<CommentMessage>list = query.getList();
                                Log.i("收到粉丝" , "" + list.size());
                                if(list.size() != 0){
                                    comments.addAll(list);
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
        footView = LayoutInflater.from(CommentActivity.this).
                inflate(R.layout.foot,null);
        adapter.VIEW_FOOTER = footView;
        CommentMessage comment = new CommentMessage();
        comment.setViewType(ContributeAdapter.TYPE_FOOTER);
        comments.add(comment);
        adapter.notifyDataSetChanged();
    }

}
