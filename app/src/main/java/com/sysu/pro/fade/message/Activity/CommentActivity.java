package com.sysu.pro.fade.message.Activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.CommentQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DRecyclerViewAdapter;
import com.sysu.pro.fade.message.Adapter.CommentAdapter;
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
    private List<Comment> comments = new ArrayList<Comment>();
    private User user;
    private Retrofit retrofit;
    private MessageService messageService;
    private Integer start;
    private DRecyclerViewAdapter dRecyclerViewAdapter;
    private RefreshLayout refreshLayout;
    private String point; //时间点，分段请求需要记录的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        notification_Rv = (RecyclerView) findViewById(R.id.comment_recycler);
        adapter = new CommentAdapter(CommentActivity.this,comments);
        notification_Rv.setLayoutManager(new LinearLayoutManager(this));
        dRecyclerViewAdapter = new DRecyclerViewAdapter(adapter);
        dRecyclerViewAdapter.setAdapter(adapter);
        notification_Rv.setAdapter(dRecyclerViewAdapter);
//        notification_Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnClickItemListsner(new DBaseRecyclerViewAdapter.OnClickItemListsner() {
            @Override
            public void onClick(int position) {
                Toast.makeText(CommentActivity.this, "" + position,
                        Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(this,OtherActivity.class);
//                intent.putExtra("user_id",userList.get(poisiton).getUser_id());
//                activity.startActivity(intent);
            }
        });

        addFootviews();
        initLoadMore();
        user = new UserUtil(this).getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        messageService = retrofit.create(MessageService.class);
        messageService.getAddComment(user.getUser_id().toString(), "0", "null")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentQuery>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CommentQuery commentQuery) {
                        start = commentQuery.getStart();
                        point = commentQuery.getPoint();
                        List<Comment>list = commentQuery.getList();
                        if(list.size() != 0){
                            comments.addAll(list);
                            dRecyclerViewAdapter.notifyDataSetChanged();
                        }
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
        refreshLayout.setEnableAutoLoadmore(true);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        //.setProgressResource(R.drawable.progress)
        // .setArrowResource(R.drawable.arrow));
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                messageService.getAddComment(user.getUser_id().toString(), start.toString(),point)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<CommentQuery>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(CommentQuery commentQuery) {
                                start = commentQuery.getStart();
                                List<Comment>list = commentQuery.getList();
                                Log.i("收到贡献" , "" + list.size());
                                refreshlayout.finishLoadmore();
                                if(list.size() != 0){
                                    comments.addAll(list);
                                    dRecyclerViewAdapter.notifyDataSetChanged();
                                    if(list.size() < 10){
                                       // refreshlayout.setEnableLoadmore(false);
                                        //addFootviews();
                                    }
                                }else {
                                   // refreshlayout.setEnableLoadmore(false);
                                    //addFootviews();
                                }

                            }
                        });
            }
        });
    }

    private void addFootviews() {
        View foot = LayoutInflater.from(this).inflate(R.layout.foot, notification_Rv, false);
        dRecyclerViewAdapter.addFootView(foot);
        //dRecyclerViewAdapter.notifyDataSetChanged();
    }

}
