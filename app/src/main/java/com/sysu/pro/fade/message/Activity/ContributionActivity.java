package com.sysu.pro.fade.message.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.activity.DetailActivity;
import com.sysu.pro.fade.message.Adapter.ContributeAdapter;
import com.sysu.pro.fade.message.Event.ContributeEvent;
import com.sysu.pro.fade.service.MessageService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContributionActivity extends MainBaseActivity {
    private RecyclerView notification_Rv;
    private List<Note> notes = new ArrayList<Note>();
    private User user;
    private Retrofit retrofit;
    private MessageService messageService;
    private Integer start = 0;
    private RefreshLayout refreshLayout;
    private ContributeAdapter adapter;
    private String point; //时间点，分段请求需要记录的
    private Boolean isEnd = false;
    private View footView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribution);
        notification_Rv = (RecyclerView) findViewById(R.id.contribution_recycler);
        notification_Rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContributeAdapter(notes,this);
        notification_Rv.setAdapter(adapter);

        user = new UserUtil(this).getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        messageService = retrofit.create(MessageService.class);
        initLoadAddMore();
        messageService.getAddContribute(user.getUser_id().toString(), "0","null")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NoteQuery>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(NoteQuery noteQuery) {
                        start = noteQuery.getStart();
                        point = noteQuery.getPoint();
                        List<Note>list = noteQuery.getList();
                        Log.i("收到贡献" , "" + list.size());
                        if(list.size() != 0){
                            notes.addAll(list);
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
                            //显示footView
                            addFootView();
                            footViewListen(footView);
                        }
                    }
                });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ContributeEvent("back", true));
                finish();
            }
        });
        //单项点击跳转监听
        adapter.setOnItemClickListener(new ContributeAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(notes.get(position).getViewType() == null){
                    Note temp = notes.get(position);
                    Intent intent = new Intent(ContributionActivity.this, DetailActivity.class);
                    intent.putExtra(Const.NOTE_ID,temp.getTarget_id());
                    intent.putExtra(Const.IS_COMMENT,false);
                    intent.putExtra(Const.COMMENT_NUM, temp.getComment_num());
                    intent.putExtra(Const.COMMENT_ENTITY, temp);
                    intent.putExtra("getFull",true);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK )
            EventBus.getDefault().post(new ContributeEvent("back", true));
        return super.onKeyDown(keyCode, event);
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
                messageService.getAddContribute(user.getUser_id().toString(), start.toString(),point)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<NoteQuery>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(NoteQuery noteQuery) {
                                start = noteQuery.getStart();
                                List<Note>list = noteQuery.getList();
                                Log.i("收到贡献" , "" + list.size());
                                if(list.size() != 0){
                                    notes.addAll(list);
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
        refreshLayout.setEnableRefresh(false);    //取消下拉刷新功能
        refreshLayout.setEnableAutoLoadmore(true);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                messageService.getOldContribute(user.getUser_id().toString(), start.toString())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<NoteQuery>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                            @Override
                            public void onNext(NoteQuery noteQuery) {
                                start = noteQuery.getStart();
                                List<Note>list = noteQuery.getList();
                                Log.i("收到贡献" , "" + list.size());
                                if(list.size() != 0){
                                    notes.addAll(list);
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
                   notes.remove(notes.size() - 1);
                   adapter.notifyItemRemoved(notes.size() - 1);
                   refreshLayout.setEnableLoadmore(true);
                   refreshLayout.setEnableAutoLoadmore(true);
                   isEnd = false;
                   initLoadOldMore();
                    Log.i("查看更多start=", start.toString());
                    messageService.getOldContribute(user.getUser_id().toString(),start.toString())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<NoteQuery>() {
                                @Override
                                public void onCompleted() {
                                }
                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(NoteQuery noteQuery) {
                                    start = noteQuery.getStart();
                                    List<Note>list = noteQuery.getList();
                                    Log.i("收到贡献" , "" + list.size());
                                    if(list.size() != 0){
                                        notes.addAll(list);
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
        footView = LayoutInflater.from(ContributionActivity.this).
                inflate(R.layout.foot,null);
        adapter.VIEW_FOOTER = footView;
        Note note = new Note();
        note.setViewType(ContributeAdapter.TYPE_FOOTER);
        notes.add(note);
        adapter.notifyDataSetChanged();
    }


}
