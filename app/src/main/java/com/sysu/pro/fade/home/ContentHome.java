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

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.home.adapter.RecycleAdapter;
import com.sysu.pro.fade.home.animator.FadeItemAnimator;
import com.sysu.pro.fade.home.beans.ContentBean;
import com.sysu.pro.fade.home.beans.RelayBean;
import com.sysu.pro.fade.home.listener.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;

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

    private Activity activity;
    private Context context;
    private View rootView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public ContentHome(Activity activity, Context context, View rootView){
        this.activity = activity;
        this.context = context;
        this.rootView = rootView;

		initData();
		initViews();


        //测试
//        NoteTool.addNote(handler,"8","黄路","https://sysufade.cn/Fade/image/head/8af431_1500709261912.png",
//                "31号安卓端发帖测试","0","标签1,标签2,标签3");
        //NoteTool.getSectionDiscoverRecommond(handler,"0");
//        NoteTool.getTwentyGood(handler,"332","0");

    }

	/**
	 * 初始化数据
	 */
	private void initData(){
		contentBeans = new ArrayList<ContentBean>();
		ArrayList<String> imgUrls3 = new ArrayList<String>();
		ContentBean cb3;

		int offset = (int)Math.round(Math.random()*200)*5;
		for(int i = 0; i < 5; i++){
			imgUrls3 = new ArrayList<String>();
			int tempID = 2000 + offset + i;
			String sId = String.valueOf(tempID);
			for(int k = 1; k <= 6; k++){
				imgUrls3.add("http://img1.mm131.com/pic/"+sId+"/"+String.valueOf(k)+".jpg");
			}
			cb3 = new ContentBean(3,"刘德华","今天天气真好呀哈哈哈很好很好哈哈哈哈哈哈哈哈",imgUrls3,new ArrayList<RelayBean>());
			contentBeans.add(cb3);


		}

		offset = (int)Math.round(Math.random()*200)*5;
		for(int i = 0; i < 3; i++){
			imgUrls3 = new ArrayList<String>();
			int tempID = 2000 + offset + i;
			String sId = String.valueOf(tempID);
			for(int k = 1; k < i+2; k++){
				imgUrls3.add("http://img1.mm131.com/pic/"+sId+"/"+String.valueOf(k)+".jpg");
			}
			cb3 = new ContentBean(3,"刘德华","今天天气真好呀哈哈哈很好很好哈哈哈哈哈哈哈哈",imgUrls3,new ArrayList<RelayBean>());
			contentBeans.add(cb3);
		}
		cb3 = new ContentBean(3,"刘德华","",imgUrls3,new ArrayList<RelayBean>());
		contentBeans.add(cb3);
		cb3 = new ContentBean(3,"刘德华","今天天气真好呀哈哈哈很好很好哈哈哈哈哈哈哈哈",new ArrayList<String>(),new ArrayList<RelayBean>());
		contentBeans.add(cb3);

	}

    private void initViews(){
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleAdapter(context,contentBeans);
        recyclerView.setAdapter(adapter);
        swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.light_blue);
		/*刷新数据*/
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshImage();
            }
        });
        scrollListener = new EndlessRecyclerOnScrollListener(context, layoutManager, contentBeans) {
            @Override
            public void onLoadMore(int currentPage) {
                addImage();
            }
        };
        recyclerView.setOnScrollListener(scrollListener);
        FadeItemAnimator fadeItemAnimator = new FadeItemAnimator();
        fadeItemAnimator.setRemoveDuration(400);
        recyclerView.setItemAnimator(fadeItemAnimator);
    }

    public void loadData(){
        //refreshImage();
    }

    private void addImage() {
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
                        addData();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(context,"加载完成",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    private void refreshImage() {
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
                        changeData();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void changeData(){
        contentBeans.clear();
        ArrayList<String> imgUrls3;
        ContentBean cb3;

        int offset = (int)Math.round(Math.random()*200)*5;
        for(int i = 0; i < 5; i++){
            imgUrls3 = new ArrayList<>();
            int tempID = 2000 + offset + i;
            String sId = String.valueOf(tempID);
            for(int k = 1; k <= 6; k++){
                imgUrls3.add("http://img1.mm131.com/pic/"+sId+"/"+String.valueOf(k)+".jpg");
            }
            cb3 = new ContentBean(3,"刘德华","今天天气真好呀哈哈哈很好很好哈哈哈哈哈哈哈哈",imgUrls3,new ArrayList<RelayBean>());
            contentBeans.add(cb3);
        }


    }

    private void addData(){
        ArrayList<String> imgUrls3;
        ContentBean cb3;
        List<RelayBean> relayBeans = new ArrayList<RelayBean>();
        RelayBean relayBean = new RelayBean("张艺兴","快看啊，好厉害");
        relayBeans.add(relayBean);
        relayBean = new RelayBean("","可不是嘛");
        relayBeans.add(relayBean);
        relayBean = new RelayBean("王迅",":渤哥就是厉害");
        relayBeans.add(relayBean);
        relayBean = new RelayBean("黄磊",":其实我早就看出来了");
        relayBeans.add(relayBean);

        int offset = (int)Math.round(Math.random()*200)*5;
        for(int i = 0; i < 1; i++){
            imgUrls3 = new ArrayList<>();
            int tempID = 2000 + offset + i;
            String sId = String.valueOf(tempID);
            for(int k = 1; k < 16; k++){
                imgUrls3.add("http://images11.app.happyjuzi.com/content/201707/31/00ab6c2d-5ca2-4db5-a88a-1a250e04676a.jpeg");
            }
            cb3 = new ContentBean(3,"刘德华","今天天气真好呀哈哈哈很好很好哈哈哈哈哈哈哈哈"+sId,imgUrls3,relayBeans);
            contentBeans.add(cb3);

        }

        offset = (int)Math.round(Math.random()*200)*5;
        for(int i = 0; i < 10; i++){
            imgUrls3 = new ArrayList<>();
            int tempID = 2000 + offset + i;
            String sId = String.valueOf(tempID);
            for(int k = 1; k < 9; k++){
                if (i == 3)
                    imgUrls3.add("http://images11.app.happyjuzi.com/content/201707/31/00ab6c2d-5ca2-4db5-a88a-1a250e04676a.jpeg");
                imgUrls3.add("http://img1.mm131.com/pic/"+sId+"/"+String.valueOf(k)+".jpg");
            }
            cb3 = new ContentBean(3,"刘德华","今天天气真好呀哈哈哈很好很好哈哈哈哈哈哈哈哈"+sId,imgUrls3,relayBeans);
            contentBeans.add(cb3);

        }

        offset = (int)Math.round(Math.random()*200)*5;
        for(int i = 0; i < 2; i++){
            imgUrls3 = new ArrayList<>();
            int tempID = 2000 + offset + i;
            String sId = String.valueOf(tempID);
            for(int k = 1; k < 9; k++){
                imgUrls3.add("http://img1.mm131.com/pic/"+sId+"/"+String.valueOf(k)+".jpg");
            }
            cb3 = new ContentBean(3,"刘德华","今天天气真好呀哈哈哈很好很好哈哈哈哈哈哈哈哈"+sId,imgUrls3,relayBeans);
            contentBeans.add(cb3);

        }

    }
}
