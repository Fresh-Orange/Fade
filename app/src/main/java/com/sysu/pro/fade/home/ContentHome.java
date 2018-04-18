package com.sysu.pro.fade.home;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.adapter.NotesAdapter;
import com.sysu.pro.fade.home.animator.FadeItemAnimator;
import com.sysu.pro.fade.home.event.NoteChangeEvent;
import com.sysu.pro.fade.home.listener.EndlessRecyclerOnScrollListener;
import com.sysu.pro.fade.home.listener.JudgeRemoveOnScrollListener;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by road on 2017/7/14.
 */
public class ContentHome {
    /*图片URL数组*/
    private List<Note> notes;//当前加载的帖子
    /*信息流适配器*/
    private NotesAdapter adapter;
    /*刷新控件*/
    private SwipeRefreshLayout swipeRefresh;
    /*上拉加载滑动监听*/
    private EndlessRecyclerOnScrollListener loadMoreScrollListener;
    /*检测是否删除的滑动监听*/
    private JudgeRemoveOnScrollListener judgeRemoveScrollListener;
    /*列表*/
    private RecyclerView recyclerView;

    private Activity activity;
    private Context context;
    private View rootView;

    /**
     * add By 黄路 2017/8/18
     */
    private Integer start;
    private User user;           //登录用户的全部信息
    private List<Note>updateList;  //已加载帖子，用于发给服务器，更新帖子情况(每一项仅仅包含note_id 和 target_id)
    private List<Note>checkList;   //顶部下拉查询返回的帖子，根据这个来判断和更新已加载帖子的情况
    private Retrofit retrofit;
    private UserService userService;
    private NoteService noteService;
    private Boolean isEnd; //记录向下是否到了结尾
    private Boolean isLoading;

    public ContentHome(Activity activity, final Context context, View rootView){
        this.activity = activity;
        this.context = context;
        this.rootView = rootView;
        //EventBus订阅
        EventBus.getDefault().register(this);
        swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setRefreshing(false);
        //初始化用户信息
        user = ((MainActivity) activity).getCurrentUser();
        notes = new ArrayList<>();
        updateList = new ArrayList<>();
        checkList = new ArrayList<>();
        isEnd = false;
        isLoading = true;
        initViews();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        userService = retrofit.create(UserService.class);
        noteService = retrofit.create(NoteService.class);
        noteService.getTenNoteByTime(user.getUser_id().toString(),"0",user.getConcern_num().toString(), JSON.toJSONString(updateList))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NoteQuery>() {
                    @Override
                    public void onCompleted() {
                        isLoading = false;
                        setLoadingMore(false);
                        swipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("初次加载","失败");
                        e.printStackTrace();
                        isLoading = false;
                        setLoadingMore(false);
                        swipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onNext(NoteQuery noteQuery) {
                        Log.i("首次加载","成功");
                        notes.clear();
                        if(noteQuery.getList() != null && noteQuery.getList().size() != 0){
                            addToListTail(noteQuery.getList());
                        }
                        //更新start
                        start = noteQuery.getStart();
                        Toast.makeText(context,"加载成功",Toast.LENGTH_SHORT).show();
                    }
                });
        start = 0;

    }

    private void initViews(){
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NotesAdapter((MainActivity) context, notes);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.light_blue);
		/*刷新数据*/
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        loadMoreScrollListener = new EndlessRecyclerOnScrollListener(context, layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if(!isLoading){
                    isLoading = true;
                    addItems();
                }
            }
        };
        //TODO 监听器分离
        judgeRemoveScrollListener = new JudgeRemoveOnScrollListener(context, notes, updateList);
        recyclerView.addOnScrollListener(loadMoreScrollListener);
        recyclerView.addOnScrollListener(judgeRemoveScrollListener);

        FadeItemAnimator fadeItemAnimator = new FadeItemAnimator();
        fadeItemAnimator.setRemoveDuration(400);
        fadeItemAnimator.setSupportsChangeAnimations(false);//解决notifyItem时的闪屏问题
        recyclerView.setItemAnimator(fadeItemAnimator);

    }

    /**
     * 加载更多
     */
    private void addItems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isEnd){
                            Toast.makeText(context,"往下没有啦",Toast.LENGTH_SHORT).show();
                            setLoadingMore(false);
                            swipeRefresh.setRefreshing(false);
                        }else {
                            //加载更多
                                isLoading = true;
                                Log.i("加载更多打印start", start.toString());
                                noteService.getTenNoteByTime(user.getUser_id().toString(),
                                        start.toString(),user.getConcern_num().toString(), JSON.toJSONString(updateList))
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<NoteQuery>() {
                                            @Override
                                            public void onCompleted() {
                                                swipeRefresh.setRefreshing(false);
                                                setLoadingMore(false);
                                                isLoading = false;
                                            }
                                            @Override
                                            public void onError(Throwable e) {
                                                Log.e("加载更多","失败");
                                                e.printStackTrace();
                                                swipeRefresh.setRefreshing(false);
                                                setLoadingMore(false);
                                                isLoading = false;
                                            }
                                            @Override
                                            public void onNext(NoteQuery noteQuery) {
                                                Log.i("加载更多","成功");
                                                List<Note>addList = noteQuery.getList();
                                                start = noteQuery.getStart();
                                                if(addList.size() != 0){
                                                    addToListTail(noteQuery.getList());
                                                    Toast.makeText(context,"加载成功",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Toast.makeText(context,"往下没有啦",Toast.LENGTH_SHORT).show();
                                                    isEnd = true;
                                                }
                                                if(addList.size() < 10){
                                                    isEnd = true;
                                                }
                                            }
                                        });
                            }
                        }

                });
            }
        }).start();
    }

    /**
     * 下拉刷新
     */
    private void refreshItems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreScrollListener.resetPreviousTotal();
                        //顶部下拉刷新
                        swipeRefresh.setRefreshing(true);
                        Log.i("updateList",new Gson().toJson(updateList));
                        noteService.getMoreNote(user.getUser_id().toString(), new Gson().toJson(updateList))
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<NoteQuery>() {
                                    @Override
                                    public void onCompleted() {
                                        swipeRefresh.setRefreshing(false);
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("顶部加载","失败");
                                        e.printStackTrace();
                                        swipeRefresh.setRefreshing(false);
                                    }
                                    @Override
                                    public void onNext(NoteQuery noteQuery) {
                                        Log.i("顶部加载","成功");
                                        if(noteQuery.getUpdateList() != null && noteQuery.getUpdateList().size() != 0){
                                             checkList = noteQuery.getUpdateList();
                                             //更新现有的数据
                                            Note origin = null;
                                            Note check = null;
                                             for(int i = 0; i < checkList.size(); i++){
                                                 origin = notes.get(i);
                                                 check = checkList.get(i);
                                                 origin.setAdd_num(check.getAdd_num());
                                                 origin.setSub_num(check.getSub_num());
                                                 origin.setComment_num(check.getComment_num());
                                                 origin.setIs_die(check.getIs_die());
                                                 origin.setFetchTime(check.getFetchTime());
                                             }
                                             addToListHead(noteQuery.getList());
                                            judgeRemoveScrollListener.judgeAndRemoveItem(recyclerView);
                                        }
                                    }
                                });
                    }
                });
            }
        }).start();
    }

    /**
     * 设置“正在加载”是否显示
     * @param isShow 是否显示
     */
    private void setLoadingMore(boolean isShow){
        adapter.setLoadingMore(isShow);
    }


    private void scrollToTOP(){
        recyclerView.smoothScrollToPosition(0);
    }

    /**
     * 如果当前帖子有本机用户自己的帖子，则检查其头像和名字更新
     * 当home变为可见时调用
     */
    public void refreshIfUserChange(){
        boolean isChange = false;
        for (Note note: notes){
            if (note.getUser_id().equals(user.getUser_id())){
                if (!note.getHead_image_url().equals(user.getHead_image_url())
                        || !note.getNickname().equals(user.getNickname())){
                    note.setHead_image_url(user.getHead_image_url());
                    note.setNickname(user.getNickname());
                    isChange = true;
                }
                else{
                    isChange = false;
                    break;
                }
            }
        }
        if (isChange){
            adapter.notifyDataSetChanged();
        }
    }

    private void addToListTail(List<Note>list){
        //下翻加载数据
        for(Note note : list){
            note.setIs_die(1);
            if(note.getComment_num() == null) note.setComment_num(0);
            if(note.getAdd_num() == null) note.setAdd_num(0);
            if(note.getSub_num() == null) note.setSub_num(0);
        }

        Note simpleNote = null;
        for(Note note : list){
            //查重判断
            if(!notes.contains(note)){
                notes.add(note);

                simpleNote = new Note();
                simpleNote.setNote_id(note.getNote_id());
                simpleNote.setTarget_id(note.getTarget_id());
                simpleNote.setType(note.getType());
                updateList.add(simpleNote);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void addToListHead(List<Note>list){
        //顶部下拉刷新加载数据
        Note getNote = null;
        Note simpleNote = null;
        for(Note note : list){
            if(note.getComment_num() == null) note.setComment_num(0);
            if(note.getAdd_num() == null) note.setAdd_num(0);
            if(note.getSub_num() == null) note.setSub_num(0);
        }
        List<Note> newNoteList = new ArrayList<>();//查重之后的新增贴列表，也就是真正添加进来的帖子列表
        for(int i = 0; i < list.size(); i++){
            getNote = list.get(i);
            //查重判断
            if(!notes.contains(getNote)){
                newNoteList.add(getNote);
                notes.add(0,getNote);
                simpleNote = new Note();
                simpleNote.setNote_id(getNote.getNote_id());
                simpleNote.setTarget_id(getNote.getTarget_id());
                simpleNote.setType(getNote.getType());
                updateList.add(0,simpleNote);
            }
        }

        //合并帖子的操作，服务器未完工
        Note oldNote = null;
        SparseIntArray addTargetId2index = new SparseIntArray();
        SparseIntArray subTargetId2index = new SparseIntArray();
        //从newNoteList.size()的位置起，往后全是旧帖
        for(int i = newNoteList.size(); i < notes.size(); i++){
            oldNote = notes.get(i);
            if (oldNote.getType() == 1)  //这条帖子是续秒贴
                addTargetId2index.append(oldNote.getTarget_id(), i);
            else if (oldNote.getType() == 2)
                subTargetId2index.append(oldNote.getTarget_id(), i);
        }

        List<Integer> waitForDelete = new ArrayList<>();
        for(int i = 0; i < newNoteList.size(); i++){
            getNote = newNoteList.get(i);
            //该帖不是转发贴，跳过
            if (getNote.getTarget_id() == 0)
                continue;
            //合并转发贴，也就是不显示旧的转发贴
            //index为旧帖中targetId与该新帖相同的帖子索引
            int index = -1;
            if (getNote.getType() == 1)
               index = addTargetId2index.get(getNote.getTarget_id(), -1);
            else if (getNote.getType() == 2)
                index = subTargetId2index.get(getNote.getTarget_id(), -1);
            if(index != -1){
                waitForDelete.add(index);//先把索引存下来，过后统一删除，不然会导致索引乱套
            }
        }

        Collections.sort(waitForDelete);
        Collections.reverse(waitForDelete);
        for(Integer i: waitForDelete){
            notes.remove(i.intValue());
            updateList.remove(i.intValue());
        }

        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetNewNote(Note note) {
        //接收新的Note，加到头部
        note.setFetchTime(System.currentTimeMillis());
        note.setAction(0);
        if(note.getComment_num() == null) note.setComment_num(0);
        if(note.getAdd_num() == null) note.setAdd_num(0);
        if(note.getSub_num() == null) note.setSub_num(0);
        if (!notes.contains(note)){
            notes.add(0,note);
            Note simpleNote = new Note();
            simpleNote.setNote_id(note.getNote_id());
            simpleNote.setTarget_id(note.getTarget_id());
            simpleNote.setType(note.getType());
            updateList.add(0,simpleNote);
            adapter.notifyDataSetChanged();
        }

        Log.e("onGetNewNote", "note id : "+note.getNote_id());
    }

/*    static public void getNoteFromServer(final Integer noteId, User curUser, final NotesAdapter adapter){
        Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, curUser.getTokenModel());
        NoteService noteService = retrofit.create(NoteService.class);
        noteService.getFullNote(noteId.toString(), curUser.getUser_id().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Note>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Note newNote) {
                        List<Note> notes = adapter.getDataList();
                        for (int i = 0; i < notes.size(); i++) {
                            Note tmpNote = notes.get(i);
                            if (tmpNote.getOriginalId().equals(noteId)){
                                if (tmpNote.isOriginalNote())
                                    notes.set(i, newNote);
                                else
                                    tmpNote.setOrigin(newNote);
                                adapter.notifyItemChanged(i);
                            }
                        }

                    }
                });
    }*/

    static public void getNoteAndPostEvent(final Integer noteId, User curUser){
        Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, curUser.getTokenModel());
        NoteService noteService = retrofit.create(NoteService.class);
        noteService.getFullNote(noteId.toString(), curUser.getUser_id().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Note>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Note newNote) {
                        EventBus.getDefault().post(new NoteChangeEvent(noteId, newNote));
                    }
                });
    }

    /**
     * item发生变化，更新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemChanged(NoteChangeEvent noteChangeEvent) {
        int noteId = noteChangeEvent.getOriginalNoteId();
        Note newNote = noteChangeEvent.getNote();
        for (int i = 0; i < notes.size(); i++) {
            Note tmpNote = notes.get(i);
            if (tmpNote.getOriginalId().equals(noteId)){
                if (tmpNote.isOriginalNote())
                    notes.set(i, newNote);
                else
                    tmpNote.setOrigin(newNote);
                adapter.notifyItemChanged(i);
            }
        }
    }

    /**
     * 修改用户信息，更新主界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onGetUser(User user){
        Integer user_id = user.getUser_id();
        for(Note note : notes){
            if(note.getUser_id().equals(user_id)){
                note.setHead_image_url(Const.BASE_IP + user.getHead_image_url());
                note.setNickname(user.getNickname());
            }
            adapter.notifyDataSetChanged();
        }
    }

}
