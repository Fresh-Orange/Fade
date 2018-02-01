package com.sysu.pro.fade.discover.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.beans.UserQuery;
import com.sysu.pro.fade.discover.adapter.CountTypeItem;
import com.sysu.pro.fade.discover.adapter.HintTypeItem;
import com.sysu.pro.fade.discover.adapter.MultiTypeRVAdapter;
import com.sysu.pro.fade.discover.drecyclerview.DRecyclerViewScrollListener;
import com.sysu.pro.fade.discover.event.ClearListEvent;
import com.sysu.pro.fade.home.activity.OtherActivity;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;

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
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by LaiXiancheng on 2018/1/27.
 * Email: lxc.sysu@qq.com
 */
public class UserContent {
    private FragmentActivity activity;
    private Context context;
    private View rootView;

    private LinearLayoutManager userLinearManager; //用户搜索的LinearLayoutmanager
    private RecyclerView recyclerView;
    private MultiTypeRVAdapter mtAdapter;
    private List<Object> itemList; //用户数据

    //网络请求有关
    private Retrofit retrofit;
    private UserService userService;
    private User user;

    private String queryKeyWord;
    private Integer start = 0;
    private Integer sum = 0;
    static public enum SearchMode { NORMAL, INTERESTED }
    private SearchMode searchMode = SearchMode.NORMAL;

    public UserContent(FragmentActivity activity, final Context context, View rootView){
        this.activity = activity;
        this.context = context;
        this.rootView = rootView;
        //EventBus订阅
        EventBus.getDefault().register(this);

        //初始化用户信息
        user = ((MainActivity) activity).getCurrentUser();
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        userService = retrofit.create(UserService.class);
        initRecyclerView();
        initLoadMore();
        getRecommendUsers();
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
                if (searchMode == SearchMode.NORMAL){
                    new Thread(){
                        @Override
                        public void run() {
                            Request.Builder builder = new Request.Builder();
                            String url = Const.BASE_IP + "searchUser/" + queryKeyWord + "/"+start+" ";
                            builder.url(url);
                            Request request = builder.build();
                            try {
                                Response response = new OkHttpClient().newCall(request).execute();
                                String response_str = response.body().string();
                                Log.i("搜索结果",response_str);
                                UserQuery userQuery = JSON.parseObject(response_str,UserQuery.class);
                                if(userQuery != null){
                                    userQuery.setQueryKeyWord(queryKeyWord);
                                    EventBus.getDefault().post(userQuery);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            super.run();
                        }
                    }.start();
                }
                else{
                    getRecommendUsers();
                }

                refreshlayout.finishLoadmore();
            }
        });
    }

    public void getRecommendUsers() {
        userService.getRecommendUser(user.getUser_id().toString(), start.toString())
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<UserQuery>() {
					@Override
					public void onCompleted() {}

					@Override
					public void onError(Throwable e) {
						Log.e("searchNote", e.getMessage());
					}

					@Override
					public void onNext(UserQuery noteQuery) {
						noteQuery.setQueryKeyWord(queryKeyWord);
						EventBus.getDefault().post(noteQuery);
					}
				});
    }


    private void initRecyclerView(){
        //可插入中间文字的recyclerView的初始化
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_user);
        itemList = new ArrayList<>();
        itemList.add(new HintTypeItem("感兴趣的用户"));
        mtAdapter = new MultiTypeRVAdapter(context, itemList);

        // ***************  注册item类型  ***************
        mtAdapter.registerType(User.class, new MultiTypeRVAdapter.ViewBinder(){
            //注册User类型的item
            @Override
            public void bindView(View itemView, Object ob, int position) {
                TextView tv_nickname = itemView.findViewById(R.id.tv_nickname);
                TextView tv_fade_name = itemView.findViewById(R.id.tv_fade_name);
                ImageView iv_header = itemView.findViewById(R.id.iv_header);
                final User user = (User)ob;
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,OtherActivity.class);
                        intent.putExtra("user_id",user.getUser_id());
                        activity.startActivity(intent);
                    }
                });
                ViewGroup.LayoutParams layoutParams1 = tv_nickname.getLayoutParams();
                layoutParams1.height = 80;
                tv_nickname.setLayoutParams(layoutParams1);
                tv_nickname.setText(Html.fromHtml(user.getNickname()));
                tv_fade_name.setText(Html.fromHtml(user.getFade_name()));
                Glide.with(context).load(Const.BASE_IP + user.getHead_image_url()).into(iv_header);
            }
        }, R.layout.item_user);

        mtAdapter.registerType(HintTypeItem.class, new MultiTypeRVAdapter.ViewBinder() {
            //注册HintTypeItem类型的item
            @Override
            public void bindView(View itemView, Object ob, int position) {
                TextView textView = itemView.findViewById(R.id.tv_hint);
                textView.setText(((HintTypeItem)ob).getHint());
            }
        }, R.layout.discover_hint_item);

        mtAdapter.registerType(CountTypeItem.class, new MultiTypeRVAdapter.ViewBinder() {
            //注册CountTypeItem类型的item
            @Override
            public void bindView(View itemView, Object ob, int position) {
                TextView textView = itemView.findViewById(R.id.tv_hint);
                textView.setText(((CountTypeItem)ob).getHint());
            }
        }, R.layout.discover_count_item);
        // ***************  结束注册item类型  ***************

        userLinearManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(userLinearManager);
        mtAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(mtAdapter);
        recyclerView.addOnScrollListener(new DRecyclerViewScrollListener() {
            @Override
            public void onLoadNextPage(RecyclerView view) {
                // Toast.makeText(context,"底部",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onGetUserQuery(UserQuery userQuery){
        Log.d("搜索用户","成功");

        queryKeyWord = userQuery.getQueryKeyWord();
        start = userQuery.getStart();
        if (itemList.isEmpty()){
            sum = userQuery.getSum();
            itemList.add(new CountTypeItem(context.getString(R.string.count_hint, userQuery.getSum().toString())));
        }
        List<User>addUsers = userQuery.getList();
        if(addUsers.size() != 0){
            itemList.addAll(addUsers);
            mtAdapter.notifyDataSetChanged();
        }
        if (itemList.size()-1 == sum){
            //搜索完毕，变换成感兴趣用户模式，-1是
            start = 0;
            searchMode = SearchMode.INTERESTED;
            itemList.add(new HintTypeItem("感兴趣的用户"));
            if (itemList.size() < 8)
                getRecommendUsers();
        }
        mtAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserQuery(ClearListEvent event){
        if(itemList != null){
            itemList.clear();
            searchMode = SearchMode.NORMAL;
        }
    }

}
