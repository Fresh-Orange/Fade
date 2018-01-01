package com.sysu.pro.fade.home.activity;

import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.DetailPage;
import com.sysu.pro.fade.beans.SecondComment;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.adapter.CommonAdapter;
import com.sysu.pro.fade.service.CommentService;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*
 * rebuild by VJ 2017.12.30
 */

public class DetailActivity extends AppCompatActivity{

    private User user;
    public Integer note_id;
    private boolean is_Comment;
    private Integer commentType;
    private Retrofit retrofit;
    private ImageView detailBack;   //返回按钮
    private ImageView detailSetting;    //三个点按钮
    private TextView commentNum;
    private EditText writeComment;
    private Button sendComment;
    private RecyclerView recyclerView;
    private CommonAdapter<Comment> commentAdapter;
    private List<Comment> commentator = new ArrayList<>();  //第一评论者列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final int num = getIntent().getIntExtra(Const.COMMENT_NUM, 0);
        commentNum = (TextView) findViewById(R.id.detail_comment_num);
        writeComment = (EditText) findViewById(R.id.detail_write_comment);
        //初始化note_id
        note_id = getIntent().getIntExtra(Const.NOTE_ID,0);
        is_Comment = getIntent().getBooleanExtra(Const.IS_COMMENT, false);
        UserUtil util = new UserUtil(this);
        user = util.getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, user.getTokenModel());

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
                        Log.d("bugErr", e.toString());
                    }

                    @Override
                    public void onNext(DetailPage detailPage) {
                        commentator.addAll(detailPage.getComment_list());
                        commentNum.setText(Integer.toString(num));
                        initialComment();
                        //是评论的话显示直接评论框
                        if (is_Comment) {
                            Log.d("bug", "进来了");
                            showDirectComment();
                        }
                    }
                });
    }

    private void initialComment() {

        //放直接评论的adapter
        commentAdapter = new CommonAdapter<Comment>(commentator) {
            @Override
            public int getLayoutId(int ViewType) {
                return R.layout.item_comment;
            }

            @Override
            public void convert(CommonAdapter.ViewHolder holder, Comment data, int position) {
                holder.setGoodImage(R.id.comment_detail_good, data.getType()==0);
                holder.setImage(R.id.comment_detail_head, Const.BASE_IP+data.getHead_image_url());
                holder.setText(R.id.comment_detail_name, data.getNickname());
                holder.setText(R.id.comment_detail_date, data.getComment_time());
                holder.setText(R.id.comment_detail_content, data.getComment_content());

                List<SecondComment> respondent = new ArrayList<>(); //评论者对应的回复者列表
                respondent.addAll(data.getComments());
                if (respondent.size() == 0) {
                    Log.d("bug", "convert: "+data.getComments().size());
                    holder.setWidgetVisibility(R.id.comment_detail_reply_wrapper, View.GONE);
                    holder.setWidgetVisibility(R.id.comment_detail_more, View.GONE);
                } else {
                    //放回复内容
                    Log.d("bug", "respond: "+respondent.size());
                    for(int i = 0; i < data.getComments().size(); i++) {
                        SecondComment reply = data.getComments().get(i);
                        View view = LayoutInflater.from(DetailActivity.this).inflate(R.layout.item_reply, null);
                        TextView name = (TextView) view.findViewById(R.id.reply_name);
                        TextView word = (TextView) view.findViewById(R.id.reply_reply);
                        TextView toName = (TextView) view.findViewById(R.id.reply_comment_name);
                        TextView date = (TextView) view.findViewById(R.id.reply_date);
                        TextView content = (TextView) view.findViewById(R.id.reply_content);
                        name.setText(reply.getNickname());
                        if (reply.getTo_user_id() == data.getUser_id()) {
                            word.setVisibility(View.GONE);
                            toName.setVisibility(View.GONE);
                        }
                        else toName.setText(reply.getTo_nickname());
                        date.setText(reply.getComment_time());
                        content.setText(reply.getComment_content());
                        holder.addView(R.id.comment_detail_reply_wrapper, view);
                    }
                }
            }
        };

        recyclerView = (RecyclerView) findViewById(R.id.detail_comment);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showDirectComment() {
        writeComment.setVisibility(View.VISIBLE);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment userComment = new Comment();
                userComment.setUser_id(user.getUser_id());
                userComment.setNickname(user.getNickname());
                userComment.setComment_content(writeComment.getText().toString());
                userComment.setHead_image_url(user.getHead_image_url());
                userComment.setNote_id(note_id);
                userComment.setType(commentType);
                CommentService send = retrofit.create(CommentService.class);
                SimpleResponse response = send.addComment(JSON.toJSONString(userComment));
                Map<String, Object> map = response.getExtra();
                userComment.setComment_id(Integer.parseInt((String) map.get("comment_id")));
                userComment.setComment_time((String) map.get("comment_time"));
                commentator.add(userComment);
                commentAdapter.notifyDataSetChanged();
                writeComment.setVisibility(View.GONE);
            }
        });
    }
}