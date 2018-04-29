package com.sysu.pro.fade.discover;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchFilter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.beans.UserQuery;
import com.sysu.pro.fade.discover.adapter.DiscoverFragmentAdapter;
import com.sysu.pro.fade.discover.event.ClearListEvent;
import com.sysu.pro.fade.discover.fragment.FadeFragment;
import com.sysu.pro.fade.discover.fragment.UserFragment;
import com.sysu.pro.fade.my.activity.ValidationActivity;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lapism.searchview.SearchView.TAG;

/**
 * Created by road on 2017/7/14.
 */
public class ContentDiscover {
    private FragmentActivity activity;
    private Context context;
    private View rootview;
    private TabLayout mTabLayout;//
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private View view_user, view_fade;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合

    private List<User>userList; //用户数据

    //搜索控件
    //private SearchHistoryTable mHistoryDatabase;
    //private SearchView mSearchView;
    private RelativeLayout searchView;
    private ImageView fangdajing;
    private EditText searchView_edittext;
    private EditText searchView_edittext1;
    private TextView searchButton;
    private String query;
    private int flag;

    TextWatcher mTextWatchr = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //网络请求有关
    private Retrofit retrofit;
    private UserService userService;
    private User user;
    private ProgressBar progressBar;
    private Integer start;
    public ContentDiscover(Activity activity, final Context context, View rootview){
        this.activity = (FragmentActivity) activity;
        this.context = context;
        this.rootview = rootview;
        progressBar = rootview.findViewById(R.id.progress_search_user);
        progressBar.setVisibility(View.INVISIBLE);
        /*用以解决输入评论时底部导航栏被顶起的问题*/
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initViewPager();
        initSearchView();
        user = new UserUtil(activity).getUer();
        start = 0;
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        userService = retrofit.create(UserService.class);
        EventBus.getDefault().register(this);
    }



   private void initViewPager(){
       //viewpager的初始化
       mViewPager = (ViewPager) activity.findViewById(R.id.vp_view);
       mTabLayout = (TabLayout) activity.findViewById(R.id.tabs);

       Fragment userFragment = new UserFragment();
       Fragment fadeFragment = new FadeFragment();
       List<Fragment> fragments = new ArrayList<>();
       fragments.add(userFragment);
       fragments.add(fadeFragment);
       DiscoverFragmentAdapter adapter = new DiscoverFragmentAdapter(activity.getSupportFragmentManager(), fragments);

       mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
       mViewPager.setAdapter(adapter);//给ViewPager设置适配器
       mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
   }

   private void initSearchView(){
       searchView = rootview.findViewById(R.id.searchView);
       fangdajing = rootview.findViewById(R.id.fangdajing);
       searchView_edittext = rootview.findViewById(R.id.searchView_edittext);
       searchView_edittext1 = rootview.findViewById(R.id.searchView_edittext1);
       searchButton = rootview.findViewById(R.id.search_button);

       searchView_edittext.setFocusable(false);
       //searchView_edittext1.setFocusable(false);
       searchView_edittext.setCursorVisible(false);
       searchView_edittext1.setCursorVisible(false);
       searchView_edittext.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               //searchView_edittext.setFocusable(false);
               searchView_edittext1.setFocusable(true);
               searchView_edittext1.setCursorVisible(true);
               InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
               inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
               Log.d("edit", "0");
           }
       });
       searchView_edittext1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               searchView_edittext1.setCursorVisible(true);
               //searchView_edittext1.setFocusable(true);
               Log.d("edit", "1");
           }
       });
       searchView_edittext1.addTextChangedListener(mTextWatchr);
       searchView_edittext1.setOnKeyListener(new View.OnKeyListener() {
           @Override
           public boolean onKey(View v, int keyCode, KeyEvent event) {
               //是否是回车键
               if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                   flag = 0;
                   EventBus.getDefault().post(new ClearListEvent());
                   InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                   inputMethodManager.hideSoftInputFromWindow(searchView_edittext1.getWindowToken(), 0);
                   query = searchView_edittext1.getText().toString();
                   if (jundgeqiery(query) == false){
                       //Toast.makeText(activity, "输入不合法！", Toast.LENGTH_LONG).show();
                       Log.d(TAG, "kong");
                       flag = 1;
                       Toast.makeText(context, "输入不合法！", Toast.LENGTH_LONG).show();
                   }else {
                       flag = 2;
                       progressBar.setVisibility(View.VISIBLE);
                       new Thread(){
                           @Override
                           public void run() {
                               Request.Builder builder = new Request.Builder();
                               String url = Const.BASE_IP + "searchUser/" + query + "/0 ";
                               builder.url(url);
                               Request request = builder.build();
                               try {
                                   Response response = new OkHttpClient().newCall(request).execute();
                                   String response_str = response.body().string();
                                   Log.i("搜索结果", response_str);
                                   UserQuery userQuery = JSON.parseObject(response_str, UserQuery.class);
                                   if (userQuery != null) {
                                       userQuery.setQueryKeyWord(query);
                                       EventBus.getDefault().post(userQuery);
                                   }
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                               super.run();
                           }
                       }.start();
                       retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, user.getTokenModel());
                       NoteService noteService = retrofit.create(NoteService.class);
                       noteService.searchNote(query, "0", "1", user.getUser_id().toString())
                               .subscribeOn(Schedulers.newThread())
                               .observeOn(AndroidSchedulers.mainThread())
                               .subscribe(new Subscriber<NoteQuery>() {
                                   @Override
                                   public void onCompleted() {
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       Log.e("searchNote", e.getMessage());
                                   }

                                   @Override
                                   public void onNext(NoteQuery noteQuery) {
                                       noteQuery.setQueryKeyWord(query);
                                       EventBus.getDefault().post(noteQuery);
                                   }
                               });
                   }
               }
               return false;
           }
       });

       searchButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               flag = 0;
               EventBus.getDefault().post(new ClearListEvent());
               InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
               inputMethodManager.hideSoftInputFromWindow(searchView_edittext1.getWindowToken(), 0);
               query = searchView_edittext1.getText().toString();
               if (jundgeqiery(query) == false){
                   //Toast.makeText(activity, "输入不合法！", Toast.LENGTH_LONG).show();
                   Log.d(TAG, "kong");
                   flag = 1;
                   Toast.makeText(context, "输入不合法！", Toast.LENGTH_LONG).show();
               }else {
                   flag = 2;
                   progressBar.setVisibility(View.VISIBLE);
                   new Thread(){
                       @Override
                       public void run() {
                           Request.Builder builder = new Request.Builder();
                           String url = Const.BASE_IP + "searchUser/" + query + "/0 ";
                           builder.url(url);
                           Request request = builder.build();
                           try {
                               Response response = new OkHttpClient().newCall(request).execute();
                               String response_str = response.body().string();
                               Log.i("搜索结果", response_str);
                               UserQuery userQuery = JSON.parseObject(response_str, UserQuery.class);
                               if (userQuery != null) {
                                   userQuery.setQueryKeyWord(query);
                                   EventBus.getDefault().post(userQuery);
                               }
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                           super.run();
                       }
                   }.start();
                   retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, user.getTokenModel());
                   NoteService noteService = retrofit.create(NoteService.class);
                   noteService.searchNote(query, "0", "1", user.getUser_id().toString())
                           .subscribeOn(Schedulers.newThread())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Subscriber<NoteQuery>() {
                               @Override
                               public void onCompleted() {
                               }

                               @Override
                               public void onError(Throwable e) {
                                   Log.e("searchNote", e.getMessage());
                               }

                               @Override
                               public void onNext(NoteQuery noteQuery) {
                                   noteQuery.setQueryKeyWord(query);
                                   EventBus.getDefault().post(noteQuery);
                               }
                           });
               }
               /*if (flag == 1){
                   Toast.makeText(context, "输入不合法！", Toast.LENGTH_LONG).show();
               }*/
           }
       });
   }

   private boolean jundgeqiery(String que){
       /*Pattern p = Pattern.compile("^[a-z0-9A-Z\u4e00-\u9fa5]+$");
       for (int i = 0; i < que.length(); i++){
           String s = que.charAt(i) + "";
           if (p.matcher(s).matches()){
               return true;
           }
       }*/
       for (int i = 0; i < que.length(); i++){
           if (que.charAt(i) != ' ' && que.charAt(i) != '.'){
               return true;
           }
       }
       return false;
   }

   /*private void initSearchView(){
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
                   EventBus.getDefault().post(new ClearListEvent());
                   //rxjava遇到玄学问题，改用简单的okhttp
                   new Thread(){
                       @Override
                       public void run() {
                           Request.Builder builder = new Request.Builder();
                           String url = Const.BASE_IP + "searchUser/" + query + "/0 ";
                           builder.url(url);
                           Request request = builder.build();
                           try {
                               Response response = new OkHttpClient().newCall(request).execute();
                               String response_str = response.body().string();
                               Log.i("搜索结果",response_str);
                               UserQuery userQuery = JSON.parseObject(response_str,UserQuery.class);
                               if(userQuery != null){
                                   userQuery.setQueryKeyWord(query);
                                   EventBus.getDefault().post(userQuery);
                               }
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                           super.run();
                       }
                   }.start();

                   retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
                   NoteService noteService = retrofit.create(NoteService.class);
                   noteService.searchNote(query, "0", "1", user.getUser_id().toString())
                           .subscribeOn(Schedulers.newThread())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Subscriber<NoteQuery>() {
                               @Override
                               public void onCompleted() {}

                               @Override
                               public void onError(Throwable e) {
                                   Log.e("searchNote", e.getMessage());
                               }

                               @Override
                               public void onNext(NoteQuery noteQuery) {
                                   noteQuery.setQueryKeyWord(query);
                                   EventBus.getDefault().post(noteQuery);
                               }
                           });
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
                   //mHistoryDatabase.addItem(new SearchItem(text));
                   //mSearchView.close(false);
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
   }*/



    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onGetUserQuery(UserQuery userQuery){
        start = userQuery.getStart();
        Log.d("搜索用户","成功");
        progressBar.setVisibility(View.INVISIBLE);
    }

}
