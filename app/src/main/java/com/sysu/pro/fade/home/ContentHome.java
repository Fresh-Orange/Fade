package com.sysu.pro.fade.home;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.OriginComment;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.adapter.RecycleAdapter;
import com.sysu.pro.fade.home.animator.FadeItemAnimator;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.RelayNote;
import com.sysu.pro.fade.home.listener.EndlessRecyclerOnScrollListener;
import com.sysu.pro.fade.tool.NoteTool;
import com.sysu.pro.fade.utils.Const;
import com.sysu.pro.fade.utils.TimeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.sysu.pro.fade.utils.Const.NICKNAME;

/**
 * Created by road on 2017/7/14.
 */
public class ContentHome {
    /*图片URL数组*/
    private List<Note> notes;
    /*信息流适配器*/
    private RecycleAdapter adapter;
    /*刷新控件*/
    private SwipeRefreshLayout swipeRefresh;
    /*滑动监听*/
    private EndlessRecyclerOnScrollListener scrollListener;
    /*列表*/
    private RecyclerView recyclerView;

    private Activity activity;
    private Context context;
    private View rootView;

    /**
     * add By 黄路 2017/8/18
     */
    private int start;                 //大请求起始点
    private List<Integer> id_list;      //大请求带来的id，上限180条，用于小请求,20条一次拿去请求数据
    private int flag = 0;              //代表小请求索要第几段（20条一段，初始为0）

    private int current_user_id;   //登录注册以后，当前使用者的user_id
    private User user;               //登录用户的全部信息


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x1){
                //大请求，调用NoteTool的getBigSectionHome
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                //Toast.makeText(context,map.toString(),Toast.LENGTH_SHORT).show();
                String err = (String) map.get(Const.ERR);
                if( start == -1 || err != null){
                    Toast.makeText(context,"往下没有啦",Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                    start = -1;
                }
                else{
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                    if(notes == null)   notes = new ArrayList<>();
                    id_list = (List<Integer>) map.get(Const.LIST);
                    if(start == 0){
                        Toast.makeText(context,"大请求：首次加载 或 顶部刷新加载数据",Toast.LENGTH_SHORT).show();
                        notes.clear();
                        for(Map<String,Object> one_note : result){
                            notes.add(convert2ContentBean(one_note));
                        }
                        initViews();
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(context,"加载成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context,"大请求：继续加载数据",Toast.LENGTH_SHORT).show();
                        for(Map<String,Object> one_note : result){
                            notes.add(convert2ContentBean(one_note));
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(context,"加载成功",Toast.LENGTH_SHORT).show();
                    }
                    if(id_list == null || id_list.size() == 0){
                        start = -1;
                    }else{
                        start = id_list.get(id_list.size() -1);    //start设定为id_list的最后一个id，用于下次大请求
                    }
                }
            }
            else if(msg.what == 0x2){
                //小请求，调用NoteTool的getSmallSectionHome
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                String err = (String) map.get(Const.ERR);
                if(err != null){
                    Toast.makeText(context,"往下没有啦",Toast.LENGTH_SHORT).show();
                    setLoadingMore(false);
                    swipeRefresh.setRefreshing(false);
                    start = -1;
                }
                else{
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                    if(notes == null)   notes = new ArrayList<>();
                    for(Map<String,Object> one_note : result){
                        notes.add(convert2ContentBean(one_note));
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context,"加载成功",Toast.LENGTH_SHORT).show();
                }
            }
            else if (msg.what == 0x3){
                //续一秒，调用NoteTool的addSecond
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                String err = (String) map.get(Const.ERR);
                String good_num = (String) map.get(Const.GOOD_NUM);
                if(err != null){
                    //已经续过，不能再续
                    Toast.makeText(context,err,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"成功续一秒",Toast.LENGTH_SHORT).show();
                    //用返回的good_num更新帖子信息（可选）
                    //...
                }
            }


        }
    };

    public ContentHome(Activity activity, Context context, View rootView){
        this.activity = activity;
        this.context = context;
        this.rootView = rootView;
        swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setRefreshing(true);
        //初始化用户信息
        user = ((MainActivity) activity).getCurrentUser();
        current_user_id = user.getUser_id();
     //   current_user_id = 8;   //user_id=8,测试用
        NoteTool.getBigSectionHome(handler,String.valueOf(current_user_id),"0"); //第一次大请求，handler里面调用initViews加载数据,暂时用user_id=8用户的测试一下 start=0
        flag = 0;
    }

    private void initViews(){
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleAdapter(context, notes);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.light_blue);
		/*刷新数据*/
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        scrollListener = new EndlessRecyclerOnScrollListener(context, layoutManager, notes) {
            @Override
            public void onLoadMore(int currentPage) {
                addItems();
            }
        };
        recyclerView.setOnScrollListener(scrollListener);
        FadeItemAnimator fadeItemAnimator = new FadeItemAnimator();
        fadeItemAnimator.setRemoveDuration(400);
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
                        //addData();
                        //小请求：一次拿20条note_id去请求帖子
                        if(start == -1 || id_list == null || id_list.size() == 0){
                            Toast.makeText(context,"往下没有啦",Toast.LENGTH_SHORT).show();
                            setLoadingMore(false);
                            swipeRefresh.setRefreshing(false);
                        }else{
                            if(flag*20 > id_list.size()){
                                //id_list全部的note_id都请求过，再次发大请求
                                NoteTool.getBigSectionHome(handler,String.valueOf(current_user_id),String.valueOf(start));
                                flag = 0;
                            } else {
                                StringBuilder sb = new StringBuilder();
                                int count = 0;
                                for (int i = flag*20; i < id_list.size(); i++){
                                    sb.append(id_list.get(i)+",");
                                    count++;
                                    if(count == 20) break;
                                }
                                sb.deleteCharAt(sb.length() -1);//把最后一个逗号去掉
                                NoteTool.getSmallSectionHome(handler,sb.toString());
                                if(count < 20){
                                    start = -1;  //说明后面再也没有了
                                    flag = 0;
                                }else {
                                    flag++;
                                }
                            }
                        }
//                        adapter.notifyDataSetChanged();
//                        Toast.makeText(context,"加载完成",Toast.LENGTH_LONG).show();
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
                        scrollListener.resetPreviousTotal();
                        //changeData();
                        //顶部下拉刷新，使用大请求 start = 0
                        start = 0;
                        NoteTool.getBigSectionHome(handler,String.valueOf(current_user_id),"0");
//                      adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(true);

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

    //提供给MainActivity在发完帖子后OnResult的更新
    public void reload(int user_id){
        start = 0;
        NoteTool.getBigSectionHome(handler,String.valueOf(user_id),"0"); //第一次大请求，handler里面调用initViews加载数据,暂时用user_id=8用户的测试一下 start=0
        flag = 0;
    }

    public Note convert2ContentBean(Map<String,Object> map ){
        int note_id = (Integer) map.get(Const.NOTE_ID);
        int user_id = (Integer) map.get(Const.USER_ID);
        String name = (String) map.get(NICKNAME);
        String text = (String) map.get(Const.NOTE_CONTENT);
        String head_image_url = (String) map.get(Const.HEAD_IMAGE_URL);
        Date post_time = TimeUtil.getTimeDate((String)map.get(Const.POST_TIME));
        int good_num = (Integer) map.get(Const.GOOD_NUM);
        int relay_num = (Integer) map.get(Const.RELAY_NUM);
        int comment_num = (Integer) map.get(Const.COMMENT_NUM);
        int isRelay = (Integer) map.get(Const.ISRELAY);
        String post_area = (String) map.get(Const.POST_AREA);

        //加入图片url和图片尺寸数组
        List<Map<String,Object>>image_url_size = (List<Map<String, Object>>) map.get(Const.IMAGE_LIST);
        List<String> image_url = new ArrayList<>();
        List<Double> image_size = new ArrayList<>();
        for(Map<String,Object> one_image : image_url_size){
            image_url.add((String)one_image.get(Const.IMAGE_URL));
            BigDecimal bigDecimal = (BigDecimal) one_image.get(Const.IMAGE_SIZE);
            image_size.add(bigDecimal.doubleValue());
        }

        //加入标签数组
        List<String>tag_list = (List<String>) map.get(Const.TAG_LIST);

        //加入评论数组
        List<Comment>comment_list = new ArrayList<>();
        List<Map<String,Object>>comment_list_map = (List<Map<String, Object>>) map.get(Const.COMMENT_LIST);
        if(comment_list_map != null){
            for(Map<String,Object> one_comment_map : comment_list_map){
                Comment comment = new Comment();
                comment.setComment_id((Integer) one_comment_map.get(Const.COMMENT_ID));
                comment.setUser_id((Integer) one_comment_map.get(Const.USER_ID));
                comment.setNickname((String) one_comment_map.get(Const.NICKNAME));
                comment.setHead_image_url((String) one_comment_map.get(Const.HEAD_IMAGE_URL));
                comment.setTo_comment_id((Integer) one_comment_map.get(Const.TO_COMMENT_ID));
                comment.setNote_id((Integer) one_comment_map.get(Const.NOTE_ID));
                comment.setComment_time(TimeUtil.getTimeDate((String) one_comment_map.get(Const.COMMENT_TIME)));
                comment.setComment_content((String) one_comment_map.get(Const.COMMENT_CONTENT));
                comment.setComment_good_num((Integer) one_comment_map.get(Const.COMMENT_GOOD_NUM));
                if(comment.getTo_comment_id() != 0){
                    //加入comment_origin
                    Map<String,Object> origin_comment_map = (Map<String, Object>) one_comment_map.get(Const.ORIGIN_COMMENT);
                    OriginComment originComment = new OriginComment();
                    originComment.setComment_content((String) origin_comment_map.get(Const.COMMENT_CONTENT));
                    originComment.setNickname((String) origin_comment_map.get(Const.NICKNAME));
                    originComment.setUser_id((Integer) origin_comment_map.get(Const.USER_ID));
                    comment.setOriginComment(originComment);
                }
            }
        }

        //加入转发链
        List<RelayNote> relayNotes = new ArrayList<>();
        if(isRelay != 0){
            List<Map<String,Object>>relay_list = (List<Map<String, Object>>) map.get(Const.RELAY_LIST);
            relayNotes = new ArrayList<>();
            //加入原贴
            RelayNote origin_relayNote = new RelayNote();
            origin_relayNote.setUser_id(user_id);
            origin_relayNote.setContent(text);
            origin_relayNote.setName(name);
            relayNotes.add(origin_relayNote);
            for(Map<String,Object> one_relay_note : relay_list){
                RelayNote relayNote = new RelayNote();
                relayNote.setUser_id((Integer) one_relay_note.get(Const.USER_ID));
                relayNote.setContent((String) one_relay_note.get(Const.NOTE_CONTENT));
                relayNote.setName((String) one_relay_note.get(Const.NICKNAME));
                relayNotes.add(relayNote);
            }
            //最后反转一下
            Collections.reverse(relayNotes);
        }
        /**
         * 赋值给contentBean
         */
        Note note = new Note();
        note.setName(name);
        note.setUser_id(user_id);
        note.setComment_num(comment_num);
        note.setGood_num(good_num);
        note.setRelay_num(relay_num);
        note.setHead_image_url(head_image_url);
        note.setIsRelay(isRelay);
        note.setPost_time(post_time);
        note.setText(text);
        note.setNote_id(note_id);
        note.setPost_aera(post_area);

        note.setImgUrls(image_url);
        note.setImgSizes(image_size);
        note.setTag_list(tag_list);
        note.setRelayNotes(relayNotes);
        return note;
    }



}
