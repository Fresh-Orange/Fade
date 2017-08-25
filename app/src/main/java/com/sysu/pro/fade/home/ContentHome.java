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
import com.sysu.pro.fade.domain.User;
import com.sysu.pro.fade.home.adapter.RecycleAdapter;
import com.sysu.pro.fade.home.animator.FadeItemAnimator;
import com.sysu.pro.fade.home.beans.ContentBean;
import com.sysu.pro.fade.home.beans.RelayBean;
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
    private List<ContentBean> contentBeans;
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
                /**
                 * 大请求测试
                 */
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                Integer ans = (Integer) map.get(Const.ANS);
                if( start == -1 || ans == 0){
                    Toast.makeText(context,"往下没有啦",Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                    start = -1;
                }
                else{
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                    if(contentBeans == null)   contentBeans = new ArrayList<>();
                    id_list = (List<Integer>) map.get(Const.LIST);
                    if(start == 0){
                        Toast.makeText(context,"大请求：首次加载 或 顶部刷新加载数据",Toast.LENGTH_SHORT).show();
                        contentBeans.clear();
                        for(Map<String,Object> one_note : result){
                            contentBeans.add(convert2ContentBean(one_note));
                        }
                        initViews();
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(context,"加载成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context,"大请求：继续加载数据",Toast.LENGTH_SHORT).show();
                        for(Map<String,Object> one_note : result){
                            contentBeans.add(convert2ContentBean(one_note));
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
                /**
                 * 小请求测试
                 */
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                Integer ans = (Integer) map.get(Const.ANS);
                if(ans == 0){
                    Toast.makeText(context,"往下没有啦",Toast.LENGTH_SHORT).show();
                    setLoadingMore(false);
                    swipeRefresh.setRefreshing(false);
                    start = -1;
                }
                else{
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                    if(contentBeans == null)   contentBeans = new ArrayList<>();
                    for(Map<String,Object> one_note : result){
                        contentBeans.add(convert2ContentBean(one_note));
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context,"加载成功",Toast.LENGTH_SHORT).show();
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
        adapter = new RecycleAdapter(context,contentBeans);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.light_blue);
		/*刷新数据*/
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        scrollListener = new EndlessRecyclerOnScrollListener(context, layoutManager, contentBeans) {
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


    public ContentBean convert2ContentBean(Map<String,Object> map ){
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

        //加入转发链
       // List<RelayBean>relayBeans = null;
        List<RelayBean> relayBeans = new ArrayList<>();
        if(isRelay != 0){
            List<Map<String,Object>>relay_list = (List<Map<String, Object>>) map.get(Const.RELAY_LIST);
            relayBeans  = new ArrayList<>();
            //加入原贴
            RelayBean origin_relayBean = new RelayBean();
            origin_relayBean.setUser_id(user_id);
            origin_relayBean.setContent(text);
            origin_relayBean.setName(name);
            relayBeans.add(origin_relayBean);
            for(Map<String,Object> one_relay_note : relay_list){
                RelayBean relayBean = new RelayBean();
                relayBean.setUser_id((Integer) one_relay_note.get(Const.USER_ID));
                relayBean.setContent((String) one_relay_note.get(Const.NOTE_CONTENT));
                relayBean.setName((String) one_relay_note.get(Const.NICKNAME));
                relayBeans.add(relayBean);
            }
            //最后反转一下
            Collections.reverse(relayBeans);
        }
        /**
         * 赋值给contentBean
         */
        ContentBean contentBean = new ContentBean();
        contentBean.setName(name);
        contentBean.setUser_id(user_id);
        contentBean.setComment_num(comment_num);
        contentBean.setGood_num(good_num);
        contentBean.setRelay_num(relay_num);
        contentBean.setHead_image_url(head_image_url);
        contentBean.setIsRelay(isRelay);
        contentBean.setPost_time(post_time);
        contentBean.setText(text);
//        contentBean.setText(String.valueOf(note_id));
        contentBean.setNote_id(note_id);

        contentBean.setImgUrls(image_url);
        contentBean.setImgSizes(image_size);
        contentBean.setTag_list(tag_list);
        contentBean.setRelayBeans(relayBeans);
        contentBean.setFetchTime(System.currentTimeMillis());
        System.out.println(contentBean);
        return  contentBean;
    }
}
