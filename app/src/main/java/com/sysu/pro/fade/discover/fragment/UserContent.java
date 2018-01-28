package com.sysu.pro.fade.discover.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.beans.UserQuery;
import com.sysu.pro.fade.discover.adapter.HintTypeItem;
import com.sysu.pro.fade.discover.adapter.MultiTypeRVAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DRecyclerViewScrollListener;
import com.sysu.pro.fade.discover.event.ClearListEvent;
import com.sysu.pro.fade.home.activity.OtherActivity;
import com.sysu.pro.fade.service.UserService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;


/**
 * Created by road on 2017/7/14.
 */
public class UserContent {
    private FragmentActivity activity;
    private Context context;
    private View rootView;

    private LinearLayoutManager userLinearManager; //用户搜索的LinearLayoutmanager
    private RecyclerView recyclerView;
    private MultiTypeRVAdapter userAdapter;
    private List<Object>userList; //用户数据

    //网络请求有关
    private Retrofit retrofit;
    private UserService userService;
    private User user;

    public UserContent(FragmentActivity activity, final Context context, View rootView){
        this.activity = activity;
        this.context = context;
        this.rootView = rootView;
        //EventBus订阅
        EventBus.getDefault().register(this);

        //初始化用户信息
        user = ((MainActivity) activity).getCurrentUser();
        initRecyclerView();



    }

    private void initLoadMore(){
        //设置底部加载刷新
        RefreshLayout refreshLayout = (RefreshLayout) rootView.findViewById(R.id.refreshLayout_user);
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
                userAdapter.notifyDataSetChanged();
                refreshlayout.finishLoadmore();
            }
        });
    }




    private void initRecyclerView(){
        //可插入中间文字的recyclerView的初始化
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_user);
        userList = new ArrayList<>();
        userList.add(new HintTypeItem("感兴趣的用户"));
        userAdapter = new MultiTypeRVAdapter(context, userList);
        userLinearManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(userLinearManager);
        userAdapter.notifyDataSetChanged();
        userAdapter.setNormalItemClickListener(new MultiTypeRVAdapter.NormalItemClickListener() {
            @Override
            public void onClick(User user) {
                Intent intent = new Intent(context,OtherActivity.class);
                intent.putExtra("user_id",user.getUser_id());
                activity.startActivity(intent);
            }
        });
        recyclerView.setAdapter(userAdapter);
        recyclerView.addOnScrollListener(new DRecyclerViewScrollListener() {
            @Override
            public void onLoadNextPage(RecyclerView view) {
                // Toast.makeText(context,"底部",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onGetUserQuery(UserQuery userQuery){
        List<User>addUsers = userQuery.getList();
        if(addUsers.size() != 0){
            userList.addAll(addUsers);
            userAdapter.notifyDataSetChanged();
        }
        userList.add(new HintTypeItem("感兴趣的用户"));
        if(addUsers.size() != 0){
            userList.addAll(addUsers);
            userAdapter.notifyDataSetChanged();
        }
        Log.d("搜索用户","成功");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onGetUserQuery(ClearListEvent event){
        if(userList != null) userList.clear();
    }

}
