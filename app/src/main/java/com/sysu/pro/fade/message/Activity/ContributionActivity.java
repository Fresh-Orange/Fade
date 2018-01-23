package com.sysu.pro.fade.message.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.discover.adapter.UserAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DRecyclerViewAdapter;
import com.sysu.pro.fade.home.activity.OtherActivity;
import com.sysu.pro.fade.message.Adapter.ContributionAdapter;
import com.sysu.pro.fade.service.MessageService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContributionActivity extends MainBaseActivity {
    private RecyclerView notification_Rv;
    private ContributionAdapter adapter;
    private List<Note> notes = new ArrayList<Note>();
    private User user;
    private Retrofit retrofit;
    private MessageService messageService;
    private Integer start = 0;
    private RefreshLayout refreshLayout;
    private Boolean isEnd = false;

    private Boolean isLoad = false;
    private UserAdapter userAdapter;

    private DRecyclerViewAdapter dRecyclerViewAdapter;

    private View foot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribution);
        notification_Rv = (RecyclerView) findViewById(R.id.contribution_recycler);
        adapter = new ContributionAdapter(ContributionActivity.this,notes);
        notification_Rv.setLayoutManager(new LinearLayoutManager(this));
        dRecyclerViewAdapter = new DRecyclerViewAdapter(adapter);
        dRecyclerViewAdapter.setAdapter(adapter);
        notification_Rv.setAdapter(dRecyclerViewAdapter);

        adapter.notifyDataSetChanged();

        adapter.setOnClickItemListsner(new DBaseRecyclerViewAdapter.OnClickItemListsner() {
            @Override
            public void onClick(int position) {
                Toast.makeText(ContributionActivity.this, "" + position,
                        Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(this,OtherActivity.class);
//                intent.putExtra("user_id",userList.get(poisiton).getUser_id());
//                activity.startActivity(intent);
            }
        });

        initLoadMore();
        user = new UserUtil(this).getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        messageService = retrofit.create(MessageService.class);
        messageService.getAddContribute(user.getUser_id().toString(), "0")
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
                            addFootviews();
                            dRecyclerViewAdapter.notifyDataSetChanged();
                            isLoad = true;
                        }
                        start = noteQuery.getStart();
                    }
                });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initLoadMore() {
        //设置底部加载刷新
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);    //取消下拉刷新功能
        refreshLayout.setEnableAutoLoadmore(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        //.setProgressResource(R.drawable.progress)
        // .setArrowResource(R.drawable.arrow));
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                messageService.getAddContribute(user.getUser_id().toString(), start.toString())
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
                                    dRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
            }
        });
    }

    private void addFootviews() {
        foot = LayoutInflater.from(this).inflate(R.layout.foot, notification_Rv, false);
        dRecyclerViewAdapter.addFootView(foot);
//        dRecyclerViewAdapter.notifyDataSetChanged();
    }
}
