package com.sysu.pro.fade.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.CommentQuery;
import com.sysu.pro.fade.beans.DetailPage;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.SecondComment;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.adapter.CommonAdapter;
import com.sysu.pro.fade.home.view.ClickableProgressBar;
import com.sysu.pro.fade.home.view.imageAdaptiveIndicativeItemLayout;
import com.sysu.pro.fade.message.Utils.DateUtils;
import com.sysu.pro.fade.service.CommentService;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sysu.pro.fade.home.ContentHome.getNoteAndPostEvent;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setAddOrMinusListener;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setAddress;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setCommentAndAddCountText;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setCommentListener;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setOnUserClickListener;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setTimeBar;
import static com.sysu.pro.fade.home.view.ImageOnlyHolder.setImagePager;
import static com.sysu.pro.fade.message.Utils.StatusBarUtil.TintBar;

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
    private TextView commentNumText;
    private int realHeight;
    private ConstraintLayout writeCommentWrapper;
    private EditText writeComment;
    private Button sendComment;
    private List<Note> forwardList = new ArrayList<>(); //续秒列表，用来获取头像
    private CommonAdapter<Note> forwardAdapter;
    private RecyclerView forwardRecycler;   //续秒列表
    private RecyclerView recyclerView;      //评论列表
    private RefreshLayout refreshLayout;
    private int loadMoreFlag;
    private CommonAdapter<Comment> commentAdapter;
    private List<Comment> commentator = new ArrayList<>();  //第一评论者列表

    private InputMethodManager imm; //管理软键盘

    private ProgressBar detailLoading;
    private CoordinatorLayout allContent;

    /* ******** 帖子展示部分 by 赖贤城 *******/
    Note note;//首页传入的帖子
    private imageAdaptiveIndicativeItemLayout imageLayout;
    private TextView tvName, tvBody;    //name为用户名，body为正文
    private ImageView userAvatar;
    private TextView tvPostTime;
    private TextView tvCount;
    private TextView tvAddress;
    private ClickableProgressBar clickableProgressBar;

    /*add by hl*/
    private Boolean getFull = false;
    private Integer commentStart; //评论分页查询的起点

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TintBar(this);//状态栏白底黑字

        EventBus.getDefault().register(this);

        detailLoading = findViewById(R.id.detail_loading);
        allContent = findViewById(R.id.detail_coordinator_layout);

        commentNum = (TextView) findViewById(R.id.detail_comment_num);
        commentNumText = findViewById(R.id.detail_text1);
        writeCommentWrapper = findViewById(R.id.detail_comment_editor);
        writeComment = (EditText) findViewById(R.id.detail_write_comment);
        sendComment = (Button) findViewById(R.id.detail_send_comment);

        //设置back bar
        detailSetting = findViewById(R.id.back_bar_menu);
        detailSetting.setVisibility(View.VISIBLE);
        imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //软键盘管理器
        setupUI(findViewById(R.id.detail_root_view));   //关闭键盘处理

        //设置上拉加载
        refreshLayout = findViewById(R.id.detail_comment_refresh);
        refreshLayout.setEnableRefresh(false);  //不需要下拉刷新功能
        refreshLayout.setEnableAutoLoadmore(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));

        /* ******** 帖子展示部分 by 赖贤城 *******/
        note = (Note)getIntent().getSerializableExtra(Const.COMMENT_ENTITY);
        imageLayout = (imageAdaptiveIndicativeItemLayout)findViewById(R.id.image_layout);
        userAvatar = (ImageView) findViewById(R.id.civ_avatar);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvBody = (TextView) findViewById(R.id.tv_title);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvCount = (TextView) findViewById(R.id.tv_comment_add_count);
        tvPostTime = (TextView) findViewById(R.id.tv_post_time);
        clickableProgressBar = (ClickableProgressBar) findViewById(R.id.clickable_progressbar);
        getFull = getIntent().getBooleanExtra("getFull",false); //如果getFull为true的话，还要重新下载完整note信息
        if(!getFull) initNoteView();
        //USELESS!! ConstraintLayout rootView = (ConstraintLayout)findViewById(R.id.detail_root_view);

        Log.e("YellowMain", "Detaillll!");

        //初始化note_id
        note_id = getIntent().getIntExtra(Const.NOTE_ID,0);
        is_Comment = getIntent().getBooleanExtra(Const.IS_COMMENT, false);//是否是点击评论进来的？
        commentType = note == null ? 0 : note.getAction();
        UserUtil util = new UserUtil(this);
        user = util.getUer();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, user.getTokenModel());


        final NoteService noteService = retrofit.create(NoteService.class);
        noteService.getNotePage(Integer.toString(note_id),user.getUser_id().toString(),(getFull?"1":"0"))
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
                        //1.评论加载的流程，首先详情页加载头十条（通过请求getNotePage），CommentQuery中包含有十条数据list以及下次查询起点start
                        // 十条以后，后面的评论需要分段加载（通过请求CommentService的getTenComment），每次10条，start填的是上次CommentQuery返回的
                        //2.续一秒列表的数据为noteQuery中的list，单项为一个Note
                        //3.如果getFull为true，则等到下载完整note后才加载界面
                        CommentQuery commentQuery = detailPage.getCommentQuery();
                        if(commentQuery != null){
                            commentator.addAll(commentQuery.getList());
                            commentStart = commentQuery.getStart(); //下次第一次用CommentService的getTenComment请求获取评论start就填这个
                        }
                        Note downloadNote = detailPage.getNote();
                        if(getFull){
                            if(note != null && note.getOrigin() != null) note = downloadNote.getOrigin();
                            else {
                                note = downloadNote;
                                commentType = note.getAction();
                            }
                            initNoteView();
                        }
                        commentNum.setText(Integer.toString(downloadNote.getComment_num()));//改成了从DetailPage里面拿数据，这才是实时的
                        //更新续秒和评论数量
                        //Note tempNote = new Note();
                        note.setAdd_num(downloadNote.getAdd_num());
                        note.setComment_num(downloadNote.getComment_num());
                        note.setFetchTime(downloadNote.getFetchTime());
                        setCommentAndAddCountText(DetailActivity.this, tvCount, note);
                        setTimeBar(clickableProgressBar,DetailActivity.this, note);

                        NoteQuery noteQuery = detailPage.getNoteQuery();
                        forwardList.addAll(noteQuery.getList());

                        initForwardList();  //初始化续秒列表
                        initialComment();   //初始化评论区
                        //是评论的话显示输入框
                        if (is_Comment) {
                            Log.d("bug", "进来了");
                            showDirectComment();
                        }

                        detailLoading.setVisibility(View.GONE);
                        allContent.setVisibility(View.VISIBLE);
                    }
                });
    }


    /**
     * 帖子展示部分的初始设置，使用的是旧数据，等rxJava返回后才会再更新界面 ---by 赖贤城
     */
    private void initNoteView(){
        checkAndSetOriginalNote();
        tvName.setText(note.getNickname());
        tvBody.setText(Html.fromHtml(note.getNote_content()));
        String time = note.getOriginalPost_time().substring(0, note.getOriginalPost_time().length()-2);
        tvPostTime.setText(DateUtils.changeToDate(time));
        setImagePager(note, imageLayout);
        setCommentAndAddCountText(this, tvCount, note);
        setTimeBar(clickableProgressBar, this, note);
        setAddress(this, tvAddress, note);
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
        setCommentListener(this, clickableProgressBar, note, new ClickableProgressBar.onCommentClickListener() {
            @Override
            public void onClick() {
                showDirectComment();
            }
        });
        setAddOrMinusListener(this, clickableProgressBar, new UserUtil(this), note);
        setOnUserClickListener(this, tvName, userAvatar, null, note);
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

    //如果在详情页里面对帖子进行了加减秒，更新type和续秒列表
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void typeChangedEvent(Integer newType) {
        commentType = newType;
        int forwardSize = forwardList.size();
        if (forwardSize < 10) {
            //建一个临时用的续秒头像
            Note tmpNote = new Note();
            tmpNote.setUser_id(user.getUser_id());
            tmpNote.setType(newType);
            tmpNote.setHead_image_url(user.getHead_image_url());
            forwardList.add(tmpNote);
            forwardAdapter.notifyItemInserted(forwardSize);
        }
    }

    //初始化续秒头像列表
    private void initForwardList() {
        forwardAdapter = new CommonAdapter<Note>(forwardList) {
            @Override
            public int getLayoutId(int ViewType) {
                return R.layout.item_forward_head;
            }

            @Override
            public void convert(ViewHolder holder, final Note data, int position) {
                holder.setCircleImage(R.id.detail_forward_head, Const.BASE_IP+data.getHead_image_url());
                holder.setGoodImage(R.id.detail_forward_good, data.getType());
                holder.onWidgetClick(R.id.detail_forward_head, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(DetailActivity.this, OtherActivity.class);
                        i.putExtra(Const.USER_ID, data.getUser_id());
                        startActivity(i);
                    }
                });
            }
        };
        forwardRecycler = findViewById(R.id.detail_forward_list);
        forwardRecycler.setAdapter(forwardAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        forwardRecycler.setLayoutManager(manager);
        ImageView forwardMore = findViewById(R.id.detail_forward_more);
        if (forwardList.size() > 0) {
            findViewById(R.id.detail_commentator).setVisibility(View.VISIBLE);
        }
        if (forwardList.size() < 10) {
            forwardMore.setVisibility(View.GONE);
        } else {
//            Glide.with(this).load(R.drawable.forward_more).into(forwardMore);
            //跳转续秒详情
            forwardMore.setVisibility(View.VISIBLE);
            forwardMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DetailActivity.this, ForwardActivity.class);
                    i.putExtra("USER_ID", note.getUser_id());
                    i.putExtra("NOTE_ID", note_id);
                    startActivity(i);
                }
            });
        }
    }

    //初始化评论区
    private void initialComment() {
        recyclerView = (RecyclerView) findViewById(R.id.detail_comment);
        //放一级评论的adapter
        commentAdapter = new CommonAdapter<Comment>(commentator) {
            @Override
            public int getLayoutId(int ViewType) {
                return R.layout.item_comment;
            }

            @Override
            public void convert(final CommonAdapter.ViewHolder holder, final Comment data, final int position) {
                holder.setGoodImage(R.id.comment_detail_good, data.getType());  //1为续秒，2为减秒，0为无操作
                holder.setCircleImage(R.id.comment_detail_head, Const.BASE_IP+data.getHead_image_url());
                holder.setText(R.id.comment_detail_name, data.getNickname());
                String time = data.getComment_time().substring(0, data.getComment_time().length()-2);
                holder.setText(R.id.comment_detail_date, DateUtils.changeToDate(time));
                holder.setText(R.id.comment_detail_content, data.getComment_content());
                //如果是第一项，不需要显示分割线
                if (position == 0) {
                    holder.setWidgetVisibility(R.id.item_comment_divide_line, View.GONE);
                } else {
                    holder.setWidgetVisibility(R.id.item_comment_divide_line, View.VISIBLE);
                }
                //头像点击事件
                holder.onWidgetClick(R.id.comment_detail_head, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(DetailActivity.this, OtherActivity.class);
                        i.putExtra(Const.USER_ID, data.getUser_id());
                        startActivity(i);
                    }
                });
                //一级评论内容的点击事件，点击之后弹出输入框
                holder.onWidgetClick(R.id.comment_detail_content, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        recyclerView.smoothScrollBy(0, getRecyclerViewScrollDistance(position));
                        showSecondComment(commentator.get(position), holder, data.getUser_id());
                    }
                });
                //"查看更多"点击事件
                holder.onWidgetClick(R.id.comment_detail_more, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.unlimitReplyHeight(R.id.comment_detail_reply_wrapper);
                        holder.setWidgetVisibility(R.id.comment_detail_more, View.GONE);
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
                        realHeight = 0;
                        for(SecondComment reply : data.getComments()) {
                            View view = createReplyItemView(reply, data.getUser_id(), holder);
                            holder.addView(R.id.comment_detail_reply_wrapper, view);
                        }
                        //设置是否隐藏内容
                        int maxHeight = commentNumText.getHeight() * 4;
                        holder.setHeightMask(holder, R.id.comment_detail_reply_wrapper, maxHeight);
                    }
                } else {
                    holder.setWidgetVisibility(R.id.comment_detail_reply_wrapper, View.GONE);
                    holder.setWidgetVisibility(R.id.comment_detail_more, View.GONE);
                }
            }
        };

        loadMoreFlag = 1;

        recyclerView.setAdapter(commentAdapter);
        recyclerView.setItemViewCacheSize(20);  // TODO: 2018/5/2 暂时解决回滚的时候view状态错乱的问题
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //上拉加载
        if (commentator.size() < 10) {
            refreshLayout.setEnableLoadmore(false);
        }
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (commentator.size()%10==0 && loadMoreFlag == 1) {
                    loadMoreFlag = 0;
                    loadTenMoreComment();
                }
            }
        });
    }

    // TODO: 2018/2/1 回复的内容移动到软键盘和输入框之上，
    // 现在只能做到，在recyclerView高度够的情况下，某个position 滑到最上
    private int getRecyclerViewScrollDistance(int position) {
        int dis = 0;
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        for (int i = 0; i < position; i++) {
            dis += manager.getChildAt(i).getHeight();
        }
        return dis;
    }

    //上拉加载多10条评论
    private void loadTenMoreComment() {
        CommentService service = retrofit.create(CommentService.class);
        service.getTenComment(note_id.toString(), commentStart.toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentQuery>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("loadMoreErr", "onError: "+e.toString());
                    }

                    @Override
                    public void onNext(CommentQuery commentQuery) {
                        if(commentQuery.getList().size() > 0) {
                            commentStart = commentQuery.getStart(); //更新start
                            int oldSize = commentator.size();
                            int addSize = commentQuery.getList().size();
                            commentator.addAll(commentQuery.getList());
                            //部分更新就行了
                            commentAdapter.notifyItemRangeInserted(oldSize, addSize);
                            refreshLayout.finishLoadmore();
                            if (commentQuery.getList().size() == 10) {
                                loadMoreFlag = 1;
                                refreshLayout.setEnableLoadmore(true);
                            } else {
                                refreshLayout.setEnableLoadmore(false);
                            }
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
        String time = reply.getComment_time().substring(0, reply.getComment_time().length()-2);
        date.setText(DateUtils.changeToDate(time));
        content.setText(reply.getComment_content());
        view.findViewById(R.id.reply_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reply.getUser_id() != user.getUser_id()) {
                    showSecondReply(reply, holder, userId);
                } else {
                    Toast.makeText(DetailActivity.this, "暂时不能删除", Toast.LENGTH_SHORT).show();
                    // TODO: 2018/1/28 自己不能回复自己的二级评论，弹出删除、复制选项
                }
            }
        });
        return view;
    }

    //显示一级评论输入框
    private void showDirectComment() {
        writeCommentWrapper.setVisibility(View.VISIBLE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//弹出软件盘
        writeComment.requestFocus();
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = writeComment.getText().toString();
                if (content.isEmpty() || content.trim().isEmpty()) {
                    Toast.makeText(DetailActivity.this, "输入不能为空或全为空格", Toast.LENGTH_SHORT).show();
                } else {
                    //收起软键盘
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                    writeCommentWrapper.setVisibility(View.GONE);
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
                                    Log.d("firstBugErr", "onError: " + e.toString());
                                }

                                @Override
                                public void onNext(SimpleResponse simpleResponse) {
                                    Map<String, Object> map = simpleResponse.getExtra();
                                    userComment.setComment_id((Integer) map.get("comment_id"));
                                    userComment.setComment_time((String) map.get("comment_time"));
                                    //如果评论没有的话，直接添加，如果评论等于10条的话，等上拉加载再加载出来吧
                                    int oldSize = commentator.size();
                                    if (oldSize == 0 || oldSize % 10 != 0) {
                                        commentator.add(userComment);
                                        commentAdapter.notifyItemInserted(oldSize);
                                    } else {
                                        refreshLayout.setEnableLoadmore(true);
                                    }
                                    writeComment.setText("");
                                    commentNum.setText(Integer.toString(Integer.parseInt(commentNum.getText().toString()) + 1));
                                }
                            });
                }
            }
        });
    }
    //显示二级评论输入框
    private void showSecondComment(final Comment toComment, final CommonAdapter.ViewHolder holder, final int userId) {
        writeCommentWrapper.setVisibility(View.VISIBLE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//弹出软件盘
        writeComment.requestFocus();
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String content = writeComment.getText().toString();
                if (content.isEmpty() || content.trim().isEmpty()) {
                    Toast.makeText(DetailActivity.this, "输入不能为空或全为空格", Toast.LENGTH_SHORT).show();
                } else {
                    //收起软键盘
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                    writeCommentWrapper.setVisibility(View.GONE);
                    final SecondComment secondComment = new SecondComment();
                    secondComment.setComment_content(content);
                    secondComment.setNickname(user.getNickname());
                    secondComment.setNote_id(note_id);
                    secondComment.setUser_id(user.getUser_id());
                    secondComment.setComment_id(toComment.getComment_id());
//                secondComment.setTo_nickname(toComment.getNickname());
//                secondComment.setTo_user_id(toComment.getUser_id());
                    secondComment.setFirst_id(toComment.getUser_id());
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
                                    Log.d("secondBugErr", "onError: " + e.toString());
                                }

                                @Override
                                public void onNext(SimpleResponse simpleResponse) {
                                    Map<String, Object> map = simpleResponse.getExtra();
                                    secondComment.setSecond_id((Integer) map.get("second_id"));
                                    secondComment.setComment_time((String) map.get("comment_time"));
                                    View view = createReplyItemView(secondComment, userId, holder);
                                    holder.addView(R.id.comment_detail_reply_wrapper, view);
                                    holder.setWidgetVisibility(R.id.comment_detail_reply_wrapper, View.VISIBLE);
//                                holder.setWidgetVisibility(R.id.comment_detail_more, View.VISIBLE);
                                    writeComment.setText("");
                                    Log.d("NumberChange", Integer.toString(Integer.parseInt(commentNum.getText().toString()) + 1));
                                    commentNum.setText(Integer.toString(Integer.parseInt(commentNum.getText().toString()) + 1));
                                }
                            });
                }
            }
        });
    }

    //显示二级回复输入框
    private void showSecondReply(final SecondComment toComment, final CommonAdapter.ViewHolder holder, final int userId) {
        writeCommentWrapper.setVisibility(View.VISIBLE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//弹出软件盘
        writeComment.requestFocus();
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //收起软键盘
                if(imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                writeCommentWrapper.setVisibility(View.GONE);
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
                                Log.d("NumberChange", Integer.toString(Integer.parseInt(commentNum.getText().toString())+1));
                                commentNum.setText(Integer.toString(Integer.parseInt(commentNum.getText().toString())+1));
                            }
                        });
            }
        });
    }

    //按返回键时，如果输入框还显示着，用户很大可能是想关掉这个输入框而不是想返回上一个界面
    @Override
    public void onBackPressed() {
        if (writeCommentWrapper.getVisibility() == View.VISIBLE) {
            writeComment.setText("");
            writeCommentWrapper.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        getNoteAndPostEvent(note_id, user);//详情页可能有修改帖子，因此通知首页更新
        super.finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    //给所有非输入框控件设置触摸监听，以收起软键盘收起输入框
    private void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if(imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                                0);
                    }
                    if (writeCommentWrapper.getVisibility() == View.VISIBLE) {
                        writeComment.setText("");
                        writeCommentWrapper.setVisibility(View.GONE);
                    }
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}