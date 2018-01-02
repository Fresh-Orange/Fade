package com.sysu.pro.fade.discover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchFilter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.beans.UserQuery;
import com.sysu.pro.fade.discover.adapter.UserAdapter;
import com.sysu.pro.fade.discover.adapter.UserPagerAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DBaseRecyclerViewAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DRecyclerViewAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DRecyclerViewScrollListener;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * Created by road on 2017/7/14.
 */
public class ContentDiscover {
    private Activity activity;
    private Context context;
    private View rootview;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private View view_user, view_fade;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合

    private LinearLayoutManager userLinearManager; //用户搜索的LinearLayoutmanager
    private RecyclerView recyclerView_user;
    private UserAdapter userAdapter;
    private DRecyclerViewAdapter dRecyclerViewAdapter;
    private List<User>userList; //用户数据

    //搜索控件
    private SearchHistoryTable mHistoryDatabase;
    private SearchView mSearchView;

    //网络请求有关
    private Retrofit retrofit;
    private UserService userService;
    private User user;
    private ProgressBar progressBar;
    private Integer start;
    public ContentDiscover(Activity activity, final Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;
        progressBar = rootview.findViewById(R.id.progress_search_user);
        progressBar.setVisibility(View.INVISIBLE);
        initViewPager();
        initRecyclerView();
        initSearchView();
        initLoadMore();
        user = new UserUtil(activity).getUer();
        start = 0;
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        userService = retrofit.create(UserService.class);
        EventBus.getDefault().register(this);
    }

    /**
     * 添加全部的Views 中间插入的广告栏 等等
     */
    private void addRandomView() {
        for(int i=0 ; i<userList.size();i++){
            if(userList.get(i).getUser_id() % 2==1){
                View view = LayoutInflater.from(context).inflate(R.layout.random_item, recyclerView_user, false);
                dRecyclerViewAdapter.addRandomView(view,i);
            }
        }
    }

    /**
     * add HeadView
     */
/*    private void addHeadViews() {
        View head = LayoutInflater.from(this).inflate(R.layout.head, rcv_list, false);
        View head1 = LayoutInflater.from(this).inflate(R.layout.head, rcv_list, false);
        dRecyclerViewAdapter.addHeadView(head);
        dRecyclerViewAdapter.addHeadView(head1);

    }*/

    /**
     * add Footview
     */
/*    private void addFootviews() {
        View foot = LayoutInflater.from(this).inflate(R.layout.foot, rcv_list, false);
        View foot1 = LayoutInflater.from(this).inflate(R.layout.foot, rcv_list, false);

        dRecyclerViewAdapter.addFootView(foot);
        dRecyclerViewAdapter.addFootView(foot1);
    }*/

   private void initRecyclerView(){
       //可插入中间文字的recyclerView的初始化
       recyclerView_user = (RecyclerView) view_user.findViewById(R.id.recyclerView_user);
       userList = new ArrayList<>();
       userAdapter = new UserAdapter(userList,context);
       dRecyclerViewAdapter = new DRecyclerViewAdapter(userAdapter);
       dRecyclerViewAdapter.setAdapter(userAdapter);
       userLinearManager = new LinearLayoutManager(context);
       recyclerView_user.setLayoutManager(userLinearManager);
       userAdapter.notifyDataSetChanged();
       //addRandomView();
       userAdapter.setOnClickItemListsner(new DBaseRecyclerViewAdapter.OnClickItemListsner() {
           @Override
           public void onClick(int poisiton) {
               //Toast.makeText(context, poisiton + "", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(context,OtherActivity.class);
               intent.putExtra("user_id",userList.get(poisiton).getUser_id());
               activity.startActivity(intent);
           }
       });
       recyclerView_user.setAdapter(dRecyclerViewAdapter);
       recyclerView_user.addOnScrollListener(new DRecyclerViewScrollListener() {
           @Override
           public void onLoadNextPage(RecyclerView view) {
               // Toast.makeText(context,"底部",Toast.LENGTH_SHORT).show();
           }
       });

   }

   private void initViewPager(){
       //viewpager的初始化
       mViewPager = (ViewPager) activity.findViewById(R.id.vp_view);
       mTabLayout = (TabLayout) activity.findViewById(R.id.tabs);
       mInflater = LayoutInflater.from(context);
       view_user = mInflater.inflate(R.layout.discover_user, null);
       view_fade = mInflater.inflate(R.layout.discover_fade, null);
       //添加页卡视图
       mViewList.add(view_user);
       mViewList.add(view_fade);
       //添加页卡标题
       mTitleList.add("用户");
       mTitleList.add("Fade");
       mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
       mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡
       mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
       UserPagerAdapter mAdapter = new UserPagerAdapter(mViewList,mTitleList);
       mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
       mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
   }

   private void initSearchView(){
       //搜索控件的初始化
       mHistoryDatabase = new SearchHistoryTable(context);
       mSearchView = rootview.findViewById(R.id.searchView);
       if (mSearchView != null) {
           mSearchView.setVersionMargins(SearchView.VersionMargins.TOOLBAR_SMALL);
           mSearchView.setHint("搜索");
           mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
               @Override
               public boolean onQueryTextSubmit(final String query) {
                  // mHistoryDatabase.addItem(new SearchItem(query));
                   mSearchView.close(false);
                   progressBar.setVisibility(View.VISIBLE);
                   if(start == 0) userList.clear();
                   if(userList != null) userList.clear();
                   //rxjava遇到玄学问题，改用简单的okhttp
                   new Thread(){
                       @Override
                       public void run() {
                           Request.Builder builder = new Request.Builder();
                           String url = Const.BASE_IP + "searchUser/" + query + "/" + start.toString();
                           builder.url(url);
                           Request request = builder.build();
                           try {
                               Response response = new OkHttpClient().newCall(request).execute();
                               String response_str = response.body().string();
                               Log.i("搜索结果",response_str);
                               UserQuery userQuery = JSON.parseObject(response_str,UserQuery.class);
                               if(userQuery != null){
                                   EventBus.getDefault().post(userQuery);
                               }
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                           super.run();
                       }
                   }.start();
                   return true;
               }

               @Override
               public boolean onQueryTextChange(String newText) {
                   return false;
               }
           });

           List<SearchItem> suggestionsList = new ArrayList<>();
           suggestionsList.add(new SearchItem("search1"));
           suggestionsList.add(new SearchItem("search2"));
           suggestionsList.add(new SearchItem("search3"));

           SearchAdapter searchAdapter = new SearchAdapter(context, suggestionsList);
           searchAdapter.setOnSearchItemClickListener(new SearchAdapter.OnSearchItemClickListener() {
               @Override
               public void onSearchItemClick(View view, int position, String text) {
/*                   mHistoryDatabase.addItem(new SearchItem(text));
                   mSearchView.close(false);*/
               }
           });
           mSearchView.setAdapter(searchAdapter);

           suggestionsList.add(new SearchItem("search1"));
           suggestionsList.add(new SearchItem("search2"));
           suggestionsList.add(new SearchItem("search3"));
           searchAdapter.notifyDataSetChanged();

           List<SearchFilter> filter = new ArrayList<>();
           // filter.add(new SearchFilter("Filter1", true));
           //filter.add(new SearchFilter("Filter2", true));
           mSearchView.setFilters(filter);
           //use mSearchView.getFiltersStates() to consider filter when performing search
       }
   }

   private void initLoadMore(){
       //设置底部加载刷新
       RefreshLayout refreshLayout = (RefreshLayout) rootview.findViewById(R.id.refreshLayout_user);
       refreshLayout.setEnableRefresh(false);	//取消下拉刷新功能
       refreshLayout.setEnableAutoLoadmore(false);
       refreshLayout.setRefreshFooter(new ClassicsFooter(context));
       //.setProgressResource(R.drawable.progress)
       // .setArrowResource(R.drawable.arrow));
       refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
           @Override
           public void onLoadmore(RefreshLayout refreshlayout) {
               User user = new User();
               user.setUser_id(1);
               user.setNickname("新加进来的");
               userList.add(user);
               dRecyclerViewAdapter.notifyDataSetChanged();
               refreshlayout.finishLoadmore();
           }
       });
   }

    @Subscribe(threadMode = ThreadMode.MAIN)
   public  void onGetUserQuery(UserQuery userQuery){
        start = userQuery.getStart();
        List<User>addUsers = userQuery.getList();
        if(addUsers.size() != 0){
            userList.addAll(addUsers);
            dRecyclerViewAdapter.notifyDataSetChanged();
        }
        Log.d("搜索用户","成功");
        progressBar.setVisibility(View.INVISIBLE);
   }

}
