package com.sysu.pro.fade.home.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.DetailPage;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.adapter.CommonAdapter;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*
 * rebuild by VJ 2017.12.30
 */

public class DetailActivity extends AppCompatActivity{

    public Integer note_id;
    private ImageView detailBack;   //返回按钮
    private ImageView detailSetting;    //三个点按钮
    private RecyclerView recyclerView;
    private CommonAdapter<Comment> commentAdapter;
    private List<Comment> allComment = new ArrayList<>();  //所有评论的列表
    private List<Comment> commentator = new ArrayList<>();  //第一评论者列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //初始化note_id
        note_id = getIntent().getIntExtra(Const.NOTE_ID,0);
        UserUtil util = new UserUtil(this);
        User user = util.getUer();
        Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, user.getTokenModel());
        NoteService noteService = retrofit.create(NoteService.class);
        noteService.getNotePage(Integer.toString(note_id))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DetailPage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("bug", e.toString());
                    }

                    @Override
                    public void onNext(DetailPage detailPage) {
                        allComment.addAll(detailPage.getComment_list());
                        getDirectComment();
                        initialComment();
                    }
                });
    }

    //获取直接评论的评论
    private void getDirectComment() {
        for(int i = 0; i < allComment.size(); i++) {
            if (allComment.get(i).getTo_comment_id() == 0) {
                commentator.add(allComment.get(i));
            }
        }
    }

    private void initialComment() {
        Log.d("bug", "initialComment: "+allComment.size());
        //放直接评论的adapter
        commentAdapter = new CommonAdapter<Comment>(commentator) {
            @Override
            public int getLayoutId(int ViewType) {
                return R.layout.item_comment;
            }

            @Override
            public void convert(CommonAdapter.ViewHolder holder, Comment data, int position) {
                final Comment comment = data;
                holder.setGoodImage(R.id.comment_detail_good, data.getComment_isGood());
                holder.setImage(R.id.comment_detail_head, data.getHead_image_url());
                holder.setText(R.id.comment_detail_name, data.getNickname());
                holder.setText(R.id.comment_detail_date, data.getComment_time().toString());
                holder.setText(R.id.comment_detail_content, data.getComment_content());

                List<Comment> respondent = getReplys(position); //评论者对应的回复者列表
                //放回复内容的adapter
                CommonAdapter<Comment> replyAdapter = new CommonAdapter<Comment>(respondent) {
                    @Override
                    public int getLayoutId(int ViewType) {
                        return R.layout.item_reply;
                    }

                    @Override
                    public void convert(ViewHolder holder, Comment data, int position) {
                        holder.setText(R.id.reply_name, data.getNickname());
                        holder.setText(R.id.reply_comment_name, data.getOriginComment().getNickname());
                        holder.setText(R.id.reply_date, data.getComment_time().toString());
                        holder.setText(R.id.reply_content, data.getComment_content());
                        if (data.getOriginComment().getUser_id() == comment.getUser_id()) {
                            holder.setWidgetVisibility(R.id.reply_reply, View.GONE);
                            holder.setWidgetVisibility(R.id.reply_comment_name, View.GONE);
                        }
                    }
                };
                holder.setReplyAdapter(R.id.comment_detail_reply, replyAdapter);
            }
        };

        recyclerView = (RecyclerView) findViewById(R.id.detail_comment);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //获取第pos个评论的所有回复
    private List<Comment> getReplys(int pos) {
        List<Comment> replys = new ArrayList<>();
        for(int i = 0; i < allComment.size(); i++) {
            if (allComment.get(i).getTo_comment_id() == commentator.get(pos).getComment_id()) {
                replys.add(allComment.get(i));
            }
        }
        return replys;
    }

}