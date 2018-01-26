package com.sysu.pro.fade.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.DetailPage;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.SecondComment;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.adapter.CommonAdapter;
import com.sysu.pro.fade.home.view.ClickableProgressBar;
import com.sysu.pro.fade.home.view.imageAdaptiveIndicativeItemLayout;
import com.sysu.pro.fade.service.CommentService;
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

public class DetailActivity extends MainBaseActivity{

    private User user;
    public Integer note_id;
    private boolean is_Comment;
    private Integer commentType;
    private Retrofit retrofit;
    private RelativeLayout detailSetting;    //三个点按钮
    private TextView commentNum;
    private EditText writeComment;
    private Button sendComment;
    private RecyclerView recyclerView;
    private LinearLayout loadMore;
    private CommonAdapter<Comment> commentAdapter;
    private List<Comment> commentator = new ArrayList<>();  //第一评论者列表

    private InputMethodManager imm; //管理软键盘

    /* ******** 帖子展示部分 by 赖贤城 *******/
    Note note;//首页传入的帖子
    private imageAdaptiveIndicativeItemLayout imageLayout;
    private TextView tvName, tvBody;    //name为用户名，body为正文
    private ImageView userAvatar;
    private TextView tvPostTime;
    private TextView tvCount;
    private ClickableProgressBar clickableProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        commentNum = (TextView) findViewById(R.id.detail_comment_num);
        writeComment = (EditText) findViewById(R.id.detail_write_comment);
        sendComment = (Button) findViewById(R.id.detail_send_comment);
        loadMore = findViewById(R.id.detail_load_more);
        //设置back bar
        detailSetting = findViewById(R.id.back_bar_menu);
        detailSetting.setVisibility(View.VISIBLE);
        imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //软键盘管理器

        /* ******** 帖子展示部分 by 赖贤城 *******/
        note = (Note)getIntent().getSerializableExtra(Const.COMMENT_ENTITY);
        imageLayout = (imageAdaptiveIndicativeItemLayout)findViewById(R.id.image_layout);
        userAvatar = (ImageView) findViewById(R.id.civ_avatar);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvBody = (TextView) findViewById(R.id.tv_title);
        tvCount = (TextView) findViewById(R.id.tv_comment_add_count);
        tvPostTime = (TextView) findViewById(R.id.tv_post_time);
        clickableProgressBar = (ClickableProgressBar) findViewById(R.id.clickable_progressbar);
        initNoteView();
        //USELESS!! ConstraintLayout rootView = (ConstraintLayout)findViewById(R.id.detail_root_view);

        //初始化note_id
        note_id = getIntent().getIntExtra(Const.NOTE_ID,0);
        is_Comment = getIntent().getBooleanExtra(Const.IS_COMMENT, false);
        commentType = note.getAction();
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
                        //Note tempNote = new Note();
                        note.setAdd_num(detailPage.getAdd_num());
                        note.setComment_num(detailPage.getComment_num());
                        note.setFetchTime(detailPage.getFetchTime());
                        setCommentAndAddCountText(DetailActivity.this, note);
                        setTimeLeftTextAndProgress(DetailActivity.this, note);

                        initialComment();
                        //是评论的话显示输入框
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
        tvPostTime.setText(note.getPost_time());
        setImageView();
        setCommentAndAddCountText(this, note);
        setTimeLeftTextAndProgress(this, note);
        if (note.getImages() == null || note.getImages().isEmpty()) {
            imageLayout.setVisibility(View.GONE);
        }
        if (note.getNote_content() == null || note.getNote_content().equals("")) {
            tvBody.setVisibility(View.GONE);
        }
        Glide.with(this)
                .load(Const.BASE_IP+note.getHead_image_url())
                .fitCenter()
                .dontAnimate()
                .into(userAvatar);
        setCommentListener(this, note);
        setAddOrMinusListener(this, note);
        setOnUserClickListener(this, note);
    }


    /**
     * 如果是转发帖，那么将原贴（Origin）的信息转移到note里面，方便后续操作 ---by 赖贤城
     */
    private void checkAndSetOriginalNote(){
        if (note.getType() != 0){
            note.setPost_time(note.getOrigin().getPost_time());
            note.setNickname(note.getOrigin().getNickname());
            note.setHead_image_url(note.getOrigin().getHead_image_url());
            note.setUser_id(note.getOrigin().getUser_id());
        }
    }

    private void setOnUserClickListener(final Context context, final Note bean) {
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OtherActivity.class);
                i.putExtra(Const.USER_ID, bean.getUser_id());
                context.startActivity(i);
            }
        });
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OtherActivity.class);
                i.putExtra(Const.USER_ID, bean.getUser_id());
                context.startActivity(i);
            }
        });
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
            clickableProgressBar.setProgress((int)Math.max((halfProgress+(5.0/6)*minuteLeft), halfProgress));
        }
        else
            clickableProgressBar.setProgress(clickableProgressBar.getMaxProgress());
    }

    /**
     * 续秒或者减秒
     */
    private void setAddOrMinusListener(final Context context, final Note bean) {
        UserUtil userUtil = new UserUtil(this);
        final User curUser = userUtil.getUer();
        clickableProgressBar.setAddClickListener(new ClickableProgressBar.onAddClickListener() {
            @Override
            public void onClick() {
                Note note = getNewNote(context, bean);
                note.setType(1); // 1表示 续秒
                sendAddOrMinusToServer(note, curUser, bean);
                clickableProgressBar.showCommentButton(1);
                bean.setAction(1);
                int curProgress = clickableProgressBar.getProgress();
                int maxProgress = clickableProgressBar.getMaxProgress();
                clickableProgressBar.setProgress(Math.min(curProgress+5, maxProgress));
            }
        });
        clickableProgressBar.setMinusClickListener(new ClickableProgressBar.onMinusClickListener() {
            @Override
            public void onClick() {
                Note note = getNewNote(context, bean);
                note.setType(2); // 2表示 减秒
                sendAddOrMinusToServer(note, curUser, bean);
                clickableProgressBar.showCommentButton(0);
                bean.setAction(2);
                int curProgress = clickableProgressBar.getProgress();
                int halfProgress = clickableProgressBar.getMaxProgress() / 2;
                clickableProgressBar.setProgress(Math.max(curProgress-5, halfProgress));
            }
        });
    }

    private void setCommentListener(final Context context, final Note bean) {
        if (bean.getAction() == 1)
            clickableProgressBar.showCommentButton(1);
        else if (bean.getAction() == 2)
            clickableProgressBar.showCommentButton(0);
        clickableProgressBar.setCommentClickListener(new ClickableProgressBar.onCommentClickListener() {
            @Override
            public void onClick() {
                showDirectComment();
            }
        });
    }

    /**
     * 将包装好的note对象发到服务器，并更新本地的界面
     * @param note 准备发往服务器的note
     * @param curUser 当前用户
     * @param bean 当前holder对应的bean
     */
    private void sendAddOrMinusToServer(Note note, User curUser, final Note bean) {
        Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, curUser.getTokenModel());
        NoteService noteService = retrofit.create(NoteService.class);
        noteService
                .changeSecond(JSON.toJSONString(note))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SimpleResponse>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        //TODO:帖子死掉
                    }

                    @Override
                    public void onNext(SimpleResponse simpleResponse) {
                        if (simpleResponse.getErr() == null){
                            //USELESS!! Integer newNoteId = (Integer) simpleResponse.getExtra().get("note_id");
                            Integer comment_num = (Integer) simpleResponse.getExtra().get("comment_num");    //评论数量
                            Integer sub_num = (Integer) simpleResponse.getExtra().get("sub_num");    //评论数量
                            Integer add_num = (Integer) simpleResponse.getExtra().get("add_num");    //评论数量
                            Long fetchTime = (Long) simpleResponse.getExtra().get("fetchTime");
                            bean.setComment_num(comment_num);
                            bean.setSub_num(sub_num);
                            bean.setAdd_num(add_num);
                            //TODO:fetchTime更新之后进度条更新
                        }
                    }
                });
    }

    @NonNull
    private Note getNewNote(Context context, Note bean) {
        UserUtil userUtil = new UserUtil(this);
        final User curUser = userUtil.getUer();
        Note note = new Note();
        note.setNickname(curUser.getNickname());
        note.setUser_id(curUser.getUser_id());
        note.setNote_content(bean.getNote_content());
        note.setTarget_id(bean.getNote_id());
        note.setHead_image_url(curUser.getHead_image_url());
        return note;
    }

    private void initialComment() {
        //放一级评论的adapter
        commentAdapter = new CommonAdapter<Comment>(commentator) {
            @Override
            public int getLayoutId(int ViewType) {
                return R.layout.item_comment;
            }

            @Override
            public void convert(final CommonAdapter.ViewHolder holder, final Comment data, final int position) {
                holder.setGoodImage(R.id.comment_detail_good, data.getType()==1);
                holder.setImage(R.id.comment_detail_head, Const.BASE_IP+data.getHead_image_url());
                holder.setText(R.id.comment_detail_name, data.getNickname());
                holder.setText(R.id.comment_detail_date, data.getComment_time());
                holder.setText(R.id.comment_detail_content, data.getComment_content());
                //一级评论内容的点击事件，点击之后弹出输入框
                holder.onWidgetClick(R.id.comment_detail_content, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSecondComment(commentator.get(position), holder, data.getUser_id());
                    }
                });

                List<SecondComment> respondent = new ArrayList<>(); //评论者对应的回复者列表
                if(data.getComments() != null) {
                    respondent.addAll(data.getComments());
                    if (respondent.size() == 0) {
                        holder.setWidgetVisibility(R.id.comment_detail_reply_wrapper, View.GONE);
                        holder.setWidgetVisibility(R.id.comment_detail_more, View.GONE);
                    } else {
                        //先清空线型布局中的所有View
                        holder.removeAllViews(R.id.comment_detail_reply_wrapper);
                        //放回复内容
                        for(SecondComment reply : data.getComments()) {
                            View view = createReplyItemView(reply, data.getUser_id(), holder);
                            holder.addView(R.id.comment_detail_reply_wrapper, view);
                        }
                    }
                } else {
                    holder.setWidgetVisibility(R.id.comment_detail_reply_wrapper, View.GONE);
                    holder.setWidgetVisibility(R.id.comment_detail_more, View.GONE);
                }
            }
        };

        recyclerView = (RecyclerView) findViewById(R.id.detail_comment);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerView.canScrollVertically(1)) {
                    loadMore.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //创建回复的View
    private View createReplyItemView(final SecondComment reply, final int userId, final CommonAdapter.ViewHolder holder) {
        View view = LayoutInflater.from(DetailActivity.this).inflate(R.layout.item_reply, null);
        TextView name = (TextView) view.findViewById(R.id.reply_name);
        TextView word = (TextView) view.findViewById(R.id.reply_reply);
        TextView toName = (TextView) view.findViewById(R.id.reply_comment_name);
        TextView date = (TextView) view.findViewById(R.id.reply_date);
        TextView content = (TextView) view.findViewById(R.id.reply_content);
        name.setText(reply.getNickname());
        if (reply.getTo_user_id() == null) {
            word.setVisibility(View.GONE);
            toName.setVisibility(View.GONE);
        }
        else toName.setText(reply.getTo_nickname());
        date.setText(reply.getComment_time());
        content.setText(reply.getComment_content());
        view.findViewById(R.id.reply_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSecondReply(reply, holder, userId);
            }
        });
        return view;
    }

    //显示评论输入框
    private void showDirectComment() {
        writeComment.setVisibility(View.VISIBLE);
        sendComment.setVisibility(View.VISIBLE);
        writeComment.requestFocus();
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收起软键盘
                if(imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                writeComment.setVisibility(View.GONE);
                sendComment.setVisibility(View.GONE);
                final Comment userComment = new Comment();
                userComment.setUser_id(user.getUser_id());
                userComment.setNickname(user.getNickname());
                userComment.setComment_content(writeComment.getText().toString());
                userComment.setHead_image_url(user.getHead_image_url());
                userComment.setNote_id(note_id);
                userComment.setType(commentType);
                CommentService send = retrofit.create(CommentService.class);
                //提交到服务器，并返回评论的id等内容
                send.addComment(JSON.toJSONString(userComment))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<SimpleResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("firstBugErr", "onError: "+e.toString());
                            }

                            @Override
                            public void onNext(SimpleResponse simpleResponse) {
                                Map<String, Object> map = simpleResponse.getExtra();
                                userComment.setComment_id((Integer) map.get("comment_id"));
                                userComment.setComment_time((String) map.get("comment_time"));
                                commentator.add(userComment);
                                commentAdapter.notifyDataSetChanged();
                                writeComment.setText("");
                                commentNum.setText(Integer.parseInt(commentNum.getText().toString())+1);
                            }
                        });

            }
        });
    }
    //显示二级评论输入框
    private void showSecondComment(final Comment toComment, final CommonAdapter.ViewHolder holder, final int userId) {
        writeComment.setVisibility(View.VISIBLE);
        sendComment.setVisibility(View.VISIBLE);
        writeComment.requestFocus();
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //收起软键盘
                if(imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                writeComment.setVisibility(View.GONE);
                sendComment.setVisibility(View.GONE);
                final SecondComment secondComment = new SecondComment();
                secondComment.setComment_content(writeComment.getText().toString());
                secondComment.setNickname(user.getNickname());
                secondComment.setNote_id(note_id);
                secondComment.setUser_id(user.getUser_id());
                secondComment.setComment_id(toComment.getComment_id());
//                secondComment.setTo_nickname(toComment.getNickname());
//                secondComment.setTo_user_id(toComment.getUser_id());
                CommentService send = retrofit.create(CommentService.class);
                //提交到服务器，并返回评论的id等内容
                send.addSecondComment(JSON.toJSONString(secondComment))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<SimpleResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("secondBugErr", "onError: "+e.toString());
                            }

                            @Override
                            public void onNext(SimpleResponse simpleResponse) {
                                Map<String, Object> map = simpleResponse.getExtra();
                                secondComment.setSecond_id((Integer) map.get("second_id"));
                                secondComment.setComment_time((String) map.get("comment_time"));
                                View view = createReplyItemView(secondComment, userId, holder);
                                holder.addView(R.id.comment_detail_reply_wrapper, view);
                                holder.setWidgetVisibility(R.id.comment_detail_reply_wrapper, View.VISIBLE);
                                holder.setWidgetVisibility(R.id.comment_detail_more, View.VISIBLE);
                                writeComment.setText("");
                                commentNum.setText(Integer.parseInt(commentNum.getText().toString())+1);
                            }
                        });
            }
        });
    }

    //显示二级回复输入框
    private void showSecondReply(final SecondComment toComment, final CommonAdapter.ViewHolder holder, final int userId) {
        writeComment.setVisibility(View.VISIBLE);
        sendComment.setVisibility(View.VISIBLE);
        writeComment.requestFocus();
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //收起软键盘
                if(imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                writeComment.setVisibility(View.GONE);
                sendComment.setVisibility(View.GONE);
                final SecondComment secondComment = new SecondComment();
                secondComment.setComment_content(writeComment.getText().toString());
                secondComment.setNickname(user.getNickname());
                secondComment.setNote_id(note_id);
                secondComment.setUser_id(user.getUser_id());
                secondComment.setComment_id(toComment.getComment_id());
                secondComment.setTo_nickname(toComment.getNickname());
                secondComment.setTo_user_id(toComment.getUser_id());
                CommentService send = retrofit.create(CommentService.class);
                //提交到服务器，并返回评论的id等内容
                send.addSecondComment(JSON.toJSONString(secondComment))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<SimpleResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("secondReplyBugErr", "onError: "+e.toString());
                            }

                            @Override
                            public void onNext(SimpleResponse simpleResponse) {
                                Map<String, Object> map = simpleResponse.getExtra();
                                secondComment.setSecond_id((Integer) map.get("second_id"));
                                secondComment.setComment_time((String) map.get("comment_time"));
                                View view = createReplyItemView(secondComment, userId, holder);
                                holder.addView(R.id.comment_detail_reply_wrapper, view);
                                holder.setWidgetVisibility(R.id.comment_detail_reply_wrapper, View.VISIBLE);
                                holder.setWidgetVisibility(R.id.comment_detail_more, View.VISIBLE);
                                writeComment.setText("");
                                commentNum.setText(Integer.parseInt(commentNum.getText().toString())+1);
                            }
                        });
            }
        });
    }

}