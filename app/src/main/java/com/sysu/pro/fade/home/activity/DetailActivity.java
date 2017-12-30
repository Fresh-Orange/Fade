package com.sysu.pro.fade.home.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.home.adapter.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

/*
 * rebuild by VJ 2017.12.30
 */

public class DetailActivity extends AppCompatActivity{

    public Integer note_id;
    private ImageView detailBack;   //返回按钮
    private ImageView detailSetting;    //三个点按钮
    private RecyclerView recyclerView;
    private CommonAdapter<Comment> commentAdapter;
    private List<Comment> commentator = new ArrayList<>();  //第一评论者列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //初始化note_id
        note_id = getIntent().getIntExtra(Const.NOTE_ID,0);

        //放直接评论的adapter
        commentAdapter = new CommonAdapter<Comment>(commentator) {
            @Override
            public int getLayoutId(int ViewType) {
                return R.layout.item_comment;
            }

            @Override
            public void convert(CommonAdapter.ViewHolder holder, Comment data, int position) {
                Comment comment = commentator.get(position);
                holder.setGoodImage(R.id.comment_detail_good, comment.getComment_isGood());
                holder.setImage(R.id.comment_detail_head, comment.getHead_image_url());
                holder.setText(R.id.comment_detail_name, comment.getNickname());
                holder.setText(R.id.comment_detail_date, comment.getComment_time().toString());
                holder.setText(R.id.comment_detail_content, comment.getComment_content());

                List<Comment> respondent = getReplys(position); //评论者对应的回复者列表
                //放回复内容的adapter
                CommonAdapter<Comment> replyAdapter = new CommonAdapter<Comment>(respondent) {
                    @Override
                    public int getLayoutId(int ViewType) {
                        return R.layout.item_reply;
                    }

                    @Override
                    public void convert(ViewHolder holder, Comment data, int position) {

                    }
                };
                holder.setReplyAdapter(R.id.comment_detail_reply, replyAdapter);
            }
        };

        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    //获取第pos个评论的所有回复
    private List<Comment> getReplys(int pos) {
        List<Comment> replys = new ArrayList<>();
        // TODO: 2017/12/30获取
        return replys;
    }

}