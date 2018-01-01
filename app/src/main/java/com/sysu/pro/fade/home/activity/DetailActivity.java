package com.sysu.pro.fade.home.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.DetailPage;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.SecondComment;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.adapter.CommonAdapter;
import com.sysu.pro.fade.home.view.ClickableProgressBar;
import com.sysu.pro.fade.home.view.imageAdaptiveIndicativeItemLayout;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.TimeUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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

    /* ******** 帖子展示部分 by 赖贤城 *******/
    Note note;//首页传入的帖子
    private imageAdaptiveIndicativeItemLayout imageLayout;
    private TextView tvName, tvBody;    //name为用户名，body为正文
    private ImageView userAvatar;
    private TextView tvCount;
    private ClickableProgressBar clickableProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final int num = getIntent().getIntExtra(Const.COMMENT_NUM, 0);
        commentNum = (TextView) findViewById(R.id.detail_comment_num);
        writeComment = (EditText) findViewById(R.id.detail_write_comment);

        /* ******** 帖子展示部分 by 赖贤城 *******/
        note = (Note)getIntent().getSerializableExtra(Const.COMMENT_ENTITY);
        imageLayout = (imageAdaptiveIndicativeItemLayout)findViewById(R.id.image_layout);
        userAvatar = (ImageView) findViewById(R.id.civ_avatar);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvBody = (TextView) findViewById(R.id.tv_title);
        tvCount = (TextView) findViewById(R.id.tv_comment_add_count);
        clickableProgressBar = (ClickableProgressBar) findViewById(R.id.clickable_progressbar);
        initNoteView();
        //USELESS!! ConstraintLayout rootView = (ConstraintLayout)findViewById(R.id.detail_root_view);

        //初始化note_id
        note_id = getIntent().getIntExtra(Const.NOTE_ID,0);
        is_Comment = getIntent().getBooleanExtra(Const.IS_COMMENT, false);
        int action = note.getAction();//TODO:伟杰，你要的加减秒类型
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
                        commentNum.setText(Integer.toString(detailPage.getComment_num()));//改成了从DetailPage里面拿数据，这才是实时的

                        //更新续秒和评论数量
                        Note tempNote = new Note();
                        tempNote.setAdd_num(detailPage.getAdd_num());
                        tempNote.setComment_num(detailPage.getComment_num());
                        setCommentAndAddCountText(DetailActivity.this, tempNote);
                        //TODO:fetchTime更新之后进度条更新

                        initialComment();
                        //是评论的话显示直接评论框
                        if (is_Comment) {
                            Log.d("bug", "进来了");
                            showDirectComment();
                        }
                    }
                });
    }

    /**
     * 帖子展示部分的续秒数和评论数显示 ---by 赖贤城
     */
    private void setCommentAndAddCountText(Context context, Note bean) {
        DecimalFormat decimalFormat = new DecimalFormat(",###");
        String sAddCount = decimalFormat.format(bean.getAdd_num());
        String addCntText = context.getString(R.string.add_count_text, sAddCount);
        String sCommentCount = decimalFormat.format(bean.getComment_num());
        String commentCntText = context.getString(R.string.comment_count_text, sCommentCount);
        tvCount.setText(commentCntText + "   "+addCntText);
    }

    /**
     * 帖子展示部分的初始设置，使用的是旧数据，等rxJava返回后才会再更新界面 ---by 赖贤城
     */
    private void initNoteView(){
        checkAndSetOriginalNote();
        tvName.setText(note.getNickname());
        tvBody.setText(note.getNote_content());
        setImageView();
        setCommentAndAddCountText(this, note);
        setTimeLeftTextAndProgress(this, note);
        Glide.with(this)
                .load(Const.BASE_IP+note.getHead_image_url())
                .fitCenter()
                .dontAnimate()
                .into(userAvatar);
    }


    /**
     * 如果是转发帖，那么将原贴（Origin）的信息转移到note里面，方便后续操作 ---by 赖贤城
     */
    private void checkAndSetOriginalNote(){
        if (note.getType() != 0){
            note.setNickname(note.getOrigin().getNickname());
            note.setHead_image_url(note.getOrigin().getHead_image_url());
            note.setUser_id(note.getOrigin().getUser_id());
        }
    }

    private double getNoteRatio(Note bean) {
        double ratio;
        int cutSize = bean.getImgCutSize();
        if (cutSize == 1)
            ratio = 5.0/4;
        else if (cutSize == 2)
            ratio = 8.0/15;
        else{//USELESS!!
            ratio = 999;
            for (double d:bean.getImgSizes()) {
                Log.d("Ratio", "out "+d);
                ratio = ratio < d ? ratio : d;
            }
        }
        return ratio;
    }

    private void setImageView(){
        double ratio = getNoteRatio(note);
        imageLayout.setViewPagerMaxHeight(600);
        //imageLayout.setHeightByRatio(((float) (1.0/ratio)));
        imageLayout.setImgCoordinates(note.getImgCoordinates());
        imageLayout.setHeightByRatio((float)ratio);
        imageLayout.setPaths(Const.BASE_IP, note.getImgUrls());
    }

    private void setTimeLeftTextAndProgress(Context context, Note bean) {
        Date dateNow = new Date(bean.getFetchTime());
        Date datePost = TimeUtil.getTimeDate(bean.getPost_time());
        //floor是为了防止最后半秒的计算结果就为0,也就是保证了时间真正耗尽之后计算结果才为0
        long minuteLeft = (long) (Const.HOME_NODE_DEFAULT_LIFE
                + 5 * bean.getAdd_num()
                - bean.getSub_num()
                - Math.floor(((double) (dateNow.getTime() - datePost.getTime())) / (1000 * 60)));
        String sTimeLeft;
        if (minuteLeft < 60)
            sTimeLeft = String.valueOf(minuteLeft) + "分钟";
        else if (minuteLeft < 1440)
            sTimeLeft = String.valueOf(Math.round(((double) minuteLeft) / 60)) + "小时";
        else
            sTimeLeft = String.valueOf(Math.round(((double) minuteLeft) / 1440)) + "天";

        clickableProgressBar.setTimeText(context.getString(R.string.time_left_text, sTimeLeft));

        if (minuteLeft < 60){
            int halfProgress = clickableProgressBar.getMaxProgress() / 2;
            clickableProgressBar.setProgress((int)(halfProgress+(5.0/6)*minuteLeft));
        }
        else
            clickableProgressBar.setProgress(clickableProgressBar.getMaxProgress());
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