package com.sysu.pro.fade.home;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.adapter.RecycleAdapter;
import com.sysu.pro.fade.home.animator.FadeItemAnimator;
import com.sysu.pro.fade.home.listener.EndlessRecyclerOnScrollListener;
import com.sysu.pro.fade.home.listener.JudgeRemoveOnScrollListener;
import com.sysu.pro.fade.home.listener.SoftKeyboardStateWatcher;
import com.sysu.pro.fade.tool.NoteTool;
import com.sysu.pro.fade.utils.BeanConvertUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.sysu.pro.fade.R.id.edit_comment;

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
    private int start;                 //大请求起始点
    private List<Integer> id_list;      //大请求带来的id，上限180条，用于小请求,20条一次拿去请求数据
    private int flag = 0;              //代表小请求索要第几段（20条一段，初始为0）

    private Integer current_user_id;   //登录注册以后，当前使用者的user_id
    private User user;               //登录用户的全部信息

    private List<Integer>now_note_id;         //要发给服务器的note_id
    private List<Integer>latest_good_nums;   //更新之后的good_nums数组  对应列表的展示顺序


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x1){
                //大请求，调用NoteTool的getBigSectionHome
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                String err = (String) map.get(Const.ERR);
                if(err != null){
                    Toast.makeText(context,"往下没有啦",Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                    start = -1;
                }
                else{
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                    if(notes == null)   notes = new ArrayList<>();
                    id_list = (List<Integer>) map.get(Const.ID_LIST);
                    if(start == 0){
                        Toast.makeText(context,"大请求：首次加载",Toast.LENGTH_SHORT).show();
                        notes.clear();
                        for(Map<String,Object> one_note : result){
                            Note note = BeanConvertUtil.convert2Note(one_note);
                            notes.add(note);
                            now_note_id.add(note.getNote_id());
                        }
                        initViews();
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(context,"加载成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context,"大请求：继续加载数据",Toast.LENGTH_SHORT).show();
                        for(Map<String,Object> one_note : result){
                            Note note = BeanConvertUtil.convert2Note(one_note);
                            notes.add(note);
                            now_note_id.add(note.getNote_id());
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
                        Note note = BeanConvertUtil.convert2Note(one_note);
                        notes.add(note);
                        now_note_id.add(note.getNote_id());
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context,"加载成功",Toast.LENGTH_SHORT).show();
                }
            }
            else if (msg.what == 0x3){
                //续一秒，调用NoteTool的addSecond
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                Integer good_num = (Integer) map.get(Const.GOOD_NUM);  //得到更新后的帖子续一秒数量
                if(good_num == null){
                    //帖子被删除了 或者 不存在
                    Toast.makeText(context,"帖子被删除或者原贴不存在",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"成功续一秒",Toast.LENGTH_SHORT).show();
                    int position = msg.arg1;
                    notes.get(position).setGood_num(good_num);
                    notes.get(position).setFetchTime(System.currentTimeMillis());
					Log.d("refreshGood", "good: "+good_num);
					adapter.notifyItemChanged(position);
                }
            }

            else if(msg.what == 0x4){
                //发评论，调用CommentTool的addComment
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                Integer comment_id = (Integer) map.get(Const.COMMENT_ID); //得到发送的评论的id
            }

            else if(msg.what == 0x5){
                //顶部下拉刷新
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                latest_good_nums = (List<Integer>) map.get(Const.GOOD_NUM_LIST);
                Collections.reverse(latest_good_nums);
                Log.d("refreshGood", "latest_good_nums.size()"+latest_good_nums.size()+"\nnotes.size()"+notes.size());
                for (int i = 0; i < latest_good_nums.size(); i++) {
                    notes.get(i).setGood_num(latest_good_nums.get(i));
                    notes.get(i).setFetchTime(System.currentTimeMillis());
                }
                String err = (String) map.get(Const.ERR);
                if(err != null){
                    Toast.makeText(context,"没有新的fade",Toast.LENGTH_SHORT).show();
                }
                else{
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                    Toast.makeText(context,"更新了"+result.size()+"个fade",Toast.LENGTH_SHORT).show();
                    if(notes == null)   notes = new ArrayList<>();
                    for(Map<String,Object> one_note_map : result){
                        Note note = BeanConvertUtil.convert2Note(one_note_map); //把note加到notes的最前面
                        notes.add(0,note);
                        now_note_id.add(0,note.getNote_id());//同时，已加载的也要更新
                    }
                }
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                judgeRemoveScrollListener.judgeAndRemoveItem(recyclerView);

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
        now_note_id = new ArrayList<>();

        NoteTool.getBigSectionHome(handler,current_user_id.toString(),"0"); //第一次大请求，handler里面调用initViews加载数据,暂时用user_id=8用户的测试一下 start=0
        flag = 0;
    }

    private void initViews(){
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleAdapter(context, handler, notes);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.light_blue);
		/*刷新数据*/
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        loadMoreScrollListener = new EndlessRecyclerOnScrollListener(context, layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                addItems();
            }
        };
        //TODO 监听器分离
        judgeRemoveScrollListener = new JudgeRemoveOnScrollListener(context, notes, now_note_id);
        recyclerView.addOnScrollListener(loadMoreScrollListener);
        recyclerView.addOnScrollListener(judgeRemoveScrollListener);
        FadeItemAnimator fadeItemAnimator = new FadeItemAnimator();
        fadeItemAnimator.setRemoveDuration(400);
        recyclerView.setItemAnimator(fadeItemAnimator);
		SoftKeyboardStateWatcher watcher = new SoftKeyboardStateWatcher(rootView.getRootView(), context);
		final TabLayout tabLayout = (TabLayout) rootView.getRootView().findViewById(R.id.tab_layout_menu);
		watcher.addSoftKeyboardStateListener(
				new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
					@Override
					public void onSoftKeyboardOpened(int keyboardHeightInPx) {
					}
					@Override
					public void onSoftKeyboardClosed() {
                        loadMoreScrollListener.setKeyBoardOpen(false);
						tabLayout.setVisibility(View.VISIBLE);
                        View focusView = recyclerView.getLayoutManager().getFocusedChild();
                        LinearLayout linearLayout = null;
                        if (focusView != null)
                            linearLayout = (LinearLayout)focusView.findViewById(edit_comment);
                        if (linearLayout != null)
                            linearLayout.setVisibility(View.GONE);
					}
				}
		);
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
                                NoteTool.getBigSectionHome(handler,current_user_id.toString(),String.valueOf(start));
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
                                NoteTool.getSmallSectionHome(handler,current_user_id.toString(),sb.toString());
                                if(count < 20){
                                    start = -1;  //说明后面再也没有了
                                    flag = 0;
                                }else {
                                    flag++;
                                }
                            }
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
                        //顶部下拉刷新，使用大请求 start = 0
//                        start = 0;
//                        NoteTool.getBigSectionHome(handler,current_user_id.toString(),"0");
                        if(now_note_id.size() != 0){
                            StringBuilder sb = new StringBuilder();
                            for(int i =now_note_id.size() -1; i >=  0; i--){
                                sb.append(now_note_id.get(i));
                                sb.append(",");
                            }
                            sb.deleteCharAt(sb.length()-1);
                            String bunch = sb.toString();
                            NoteTool.topReload(handler,current_user_id,bunch);
                        }else{
                            //否则就是首次加载
                            NoteTool.getBigSectionHome(handler,current_user_id.toString(),"0");
                            start = 0;
                            flag = 0;
                        }
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
        //直接顶部下拉刷新
        if(now_note_id.size() != 0){
            StringBuilder sb = new StringBuilder();
            for(int i =now_note_id.size() -1; i >=  0; i--){
                sb.append(now_note_id.get(i));
                sb.append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            String bunch = sb.toString();
            NoteTool.topReload(handler,current_user_id,bunch);
        }else{
            //否则就是首次加载
            NoteTool.getBigSectionHome(handler,current_user_id.toString(),"0");
            start = 0;
            flag = 0;
        }
        swipeRefresh.setRefreshing(true);

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
            if (note.getUser_id() == user.getUser_id()){
                if (!note.getHead_image_url().equals(user.getHead_image_url())){
                    note.setHead_image_url(user.getHead_image_url());
                    note.setName(user.getNickname());
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

}
