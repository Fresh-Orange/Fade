package com.sysu.pro.fade.message.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.AddMessage;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.CommentQuery;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.message.Adapter.ChatAdapter;
import com.sysu.pro.fade.message.Adapter.CommentAdapter;
import com.sysu.pro.fade.message.Adapter.ContributionAdapter;
import com.sysu.pro.fade.service.MessageService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView notification_Rv;
    private CommentAdapter adapter;
    private List<Comment> comments = new ArrayList<Comment>();
    private User user;
    private Retrofit retrofit;
    private MessageService messageService;
    private Integer start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        notification_Rv = (RecyclerView) findViewById(R.id.comment_recycler);
        adapter = new CommentAdapter(CommentActivity.this,comments);
        notification_Rv.setLayoutManager(new LinearLayoutManager(this));
        notification_Rv.setAdapter(adapter);
        user = new UserUtil(this).getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        messageService = retrofit.create(MessageService.class);
        messageService.getAddComment(user.getUser_id().toString(), "0")
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
                        if(list.size() != 0){
                            comments.addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }

}
