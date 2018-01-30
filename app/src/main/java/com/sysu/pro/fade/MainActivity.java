package com.sysu.pro.fade;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.TokenModel;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.discover.ContentDiscover;
import com.sysu.pro.fade.fragment.LazyFragment;
import com.sysu.pro.fade.home.ContentHome;
import com.sysu.pro.fade.message.ContentMessage;
import com.sysu.pro.fade.my.ContentMy;
import com.sysu.pro.fade.publish.PublishActivity;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.Client;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;
import com.sysu.pro.fade.view.CustomViewPager;
import com.sysu.pro.fade.view.SectionsPagerAdapter;

import org.java_websocket.drafts.Draft_6455;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sysu.pro.fade.R.id.container;
import static com.sysu.pro.fade.message.Utils.StatusBarUtil.TintBar;

public class MainActivity extends MainBaseActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;
    private TabLayout mTabLayoutMenu;
    public Toolbar mToolbar;
    private User user;
    private Retrofit retrofit;
    private UserService userService;
    private Client client;
    /*
    上次back的时间，用于双击退出判断
    当双击 back 键在此间隔内是直接触发 onBackPressed
     */
    private static long lastBackTime = 0;
    private final int BACK_INTERVAL = 1000;
    private final int ACCESS_FINE_LOCATION = 101;
    private final int CAMERA = 102;
    private final int PERMISSION_WRITE_EXTERNAL_STORAGE = 103;
    private final int READ_PHONE_STATE = 104;
    private final int RECORD_AUDIO = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFiles();

        /*用以解决输入评论时底部导航栏被顶起的问题*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getPermission();
        //初始化用户信息
        user = new UserUtil(this).getUer();
        TokenModel tokenModel = user.getTokenModel();
        //建立websocket连接
        try {
            String websocketUri = Const.WEBSOCKET_IP + "?user_id=" + user.getUser_id() + "&token=" + tokenModel.getToken();
            client = new Client(new URI(websocketUri), new Draft_6455());
            client.connect();
            Log.i("websocket","websocket连接成功");
        } catch (URISyntaxException e) {
            Log.i("websocket","websocket连接失败");
            e.printStackTrace();
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        /*
        项目中该activity默认背景总是处于被覆盖状态。去除背景可以优化GPU绘制，减少一层绘制 --- by 赖贤城
         */
        getWindow().setBackgroundDrawable(null);

        mViewPager = (CustomViewPager) findViewById(container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        /*改变预加载页的数量*/
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //设置底部导航栏以及监听
        mTabLayoutMenu = (TabLayout) findViewById(R.id.tab_layout_menu);
        bindPagerAndTab();
        setupTabIcon();
        TabLayout.Tab publishTab = mTabLayoutMenu.getTabAt(2);
        View publishTabView = publishTab.getCustomView();
        /*publishTabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("publishTabView", "onClick: publishTabView");
                Intent intent = new Intent(MainActivity.this, PublishActivity.class);
                startActivityForResult(intent,Const.PUBLISH_REQUEST_CODE);
                overridePendingTransition(R.anim.values, R.anim.out_left);
                //跳转到发布页
            }
        });*/
        //初始化retrofit和service，用于上线和下线请求
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        userService = retrofit.create(UserService.class);
        //上线请求
        if(user.getUser_id() != null){
            userService.online(user.getUser_id() + "")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SimpleResponse>() {
                        @Override
                        public void onCompleted() {
                        }
                        @Override
                        public void onError(Throwable e) {
                            Log.e("用户上线","失败" + e.getMessage());
                        }
                        @Override
                        public void onNext(SimpleResponse simpleResponse) {
                            Log.i("用户上线",simpleResponse.getSuccess());
                        }
                    });
        }

        setUserProvider();
        getTokenAndConnect();
        TintBar(this);
    }


    private void createFiles() {
        //创建文件夹
        final File sd= Environment.getExternalStorageDirectory();
        String cache_path_photo = sd.getPath() + "/Fade/Photo/Fade";
        File rootFile_photo = new File(cache_path_photo);
        if(!rootFile_photo.exists())
            rootFile_photo.mkdirs();
        String cache_path_root = sd.getPath() + "/Fade/cache_pic";
        File rootFile = new File(cache_path_root);
        if(!rootFile.exists())
            rootFile.mkdirs();
    }

    /**
     * 设置内容提供器以供融云SDK进行调用获得用户的头像等信息
     */
    private void setUserProvider() {
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                Log.d("setUserProvider-id", s);
                Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, user.getTokenModel());
                UserService userService = retrofit.create(UserService.class);
                userService
                        .getUserById(s)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<User>() {
                            @Override
                            public void onCompleted() {}

                            @Override
                            public void onError(Throwable e) {
                                Log.e("setUserProvider", e.getMessage());
                            }

                            @Override
                            public void onNext(User user) {
                                Log.d("rong-onNext", user.toString());
                                RongIM.getInstance().refreshUserInfoCache(
                                        new UserInfo(user.getUser_id().toString(),
                                                user.getNickname(),
                                                Uri.parse(Const.BASE_IP+user.getHead_image_url())));
                            }
                        });
                return null;
            }
        }, true);
    }

    private void getTokenAndConnect() {
/*        Retrofit rongRetrofit = new Retrofit.Builder()
                .baseUrl(Const.RONG_CLOUD_BASE_IP)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        RongCloudService rongService = rongRetrofit.create(RongCloudService.class);
        String randNum = RongCloudHelper.getRandNum();
        String timeStamp = RongCloudHelper.getCurTime();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("userId", user.getUser_id().toString());
        builder.add("name", user.getNickname());
        builder.add("portraitUri", user.getHead_image_url());
        rongService.getRongCloudToken(randNum, timeStamp,
                RongCloudHelper.getSignature(randNum, timeStamp),
                builder.build())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getTokenAndConnectErr", e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String response = responseBody.string();
                            Log.e("本地获取token", response);
                            //Toast.makeText(MainActivity.this, responseBody.string(), Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = JSON.parseObject(response);
                            if (((int)jsonObject.get("code")) == 200){ //返回码正常
                                String token = (String)jsonObject.get("token");
                                connect(token);
                            }
                            //connect("UJWNywMqxIURSZUVrDvMeYXmBq98FHESTwFkzj26+w5rDw+ZqtUvPybf/6NpKAGYBrqo3wsWf4Jvn4AUx5UbTw==");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });*/
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        userService = retrofit.create(UserService.class);
        userService.getMessageToken(user.getUser_id().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String,Object>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e("获取token失败", e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Map<String,Object>map) {
                        String s = (String) map.get("token");
                        Log.d("getTokenAndConnect", s);
                        connect(s);
                    }
                });
    }

    //设置底部导航栏图片
    private void setupTabIcon(){
        Resources res = getResources();
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.mipmap.home_normal), "首页")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.mipmap.message_normal), "消息")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.mipmap.add), "发布")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.mipmap.discover_normal), "发现")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.mipmap.my_normal), "我的")));
    }

    private View createView(Drawable icon, String tab) {
        View view = getLayoutInflater().inflate(R.layout.tab_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        imageView.setImageDrawable(icon);
        imageView.setAlpha((float)0.5);
        return view;
    }

    //设置滑动事件
    private void bindPagerAndTab() {
        mTabLayoutMenu.setSelectedTabIndicatorHeight(0);//去除指示器
        mTabLayoutMenu.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**
             * 选中tab后触发
             * @param tab 选中的tab
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //与pager 关联
//                mViewPager.setCurrentItem(tab.getPosition(), true);
                changeTabSelect(tab);
                if (tab.getPosition() == Const.PUBLISH - 1){
                    Intent intent = new Intent(MainActivity.this, PublishActivity.class);
                    startActivityForResult(intent,Const.PUBLISH_REQUEST_CODE);
                    overridePendingTransition(R.anim.values, R.anim.out_left);
                }
            }

            /**
             * 退出选中状态时触发
             * @param tab 退出选中的tab
             */
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabNormal(tab);
            }

            /**
             * 重复选择时触发
             * @param tab 被 选择的tab
             */
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == Const.PUBLISH - 1){
                    Intent intent = new Intent(MainActivity.this, PublishActivity.class);
                    startActivityForResult(intent,Const.PUBLISH_REQUEST_CODE);
                    overridePendingTransition(R.anim.values, R.anim.out_left);
                }
            }
        });
    }

    //设置选择tab图标
    private void changeTabSelect(TabLayout.Tab tab) {
        Resources res = getResources();
        View view = tab.getCustomView();
        ImageView img_title = (ImageView) view.findViewById(R.id.icon);
        //TextView txt_title = (TextView) view.findViewById(R.id.title);
        if (tab.getPosition() == Const.HOME-1) {
            mViewPager.setCurrentItem(Const.HOME-1,false);
        } else if (tab.getPosition()==Const.DISCOVER-1) {
            mViewPager.setCurrentItem(Const.DISCOVER-1,false);
        }else if (tab.getPosition() == Const.MESSAGE-1) {
            mViewPager.setCurrentItem(Const.MESSAGE-1,false);
        } else if(tab.getPosition() == Const.MY-1){
            mViewPager.setCurrentItem(Const.MY-1,false);
        }
        img_title.setAlpha((float)1.0);
    }

    //设置还原tab图标
    private void changeTabNormal(TabLayout.Tab tab) {
        Resources res = getResources();
        View view = tab.getCustomView();
        ImageView img_title = (ImageView) view.findViewById(R.id.icon);
        img_title.setAlpha((float)0.5);
    }


    public static class PlaceHolderFragment extends LazyFragment{

        //四大模块
        private ContentDiscover contentDiscover = null;
        private ContentHome contentHome = null;
        private ContentMessage contentMessage = null;
        private ContentMy contentMy = null;

        View rootView;
        FrameLayout frameBar;

        private static final String ARG_SECTION_NUMBER = "section_number";


        //是否已经初始化完成
        private boolean isPrepared;
        //是否已被加载过一次，第二次就不再去请求数据了
        private boolean mHasLoadedOnce;

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        public static PlaceHolderFragment newInstance(int sectionNumber) {
            //用于构建一个fragment实例
            PlaceHolderFragment fragment = new PlaceHolderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView =null;


            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case Const.HOME:
                    rootView = inflater.inflate(R.layout.fragment_home,container,false);
                    break;

                case Const.DISCOVER:
                    rootView = inflater.inflate(R.layout.fragment_discover,container,false);
                    break;

                case Const.MESSAGE:
                    rootView = inflater.inflate(R.layout.fragment_notification,container,false);
                    break;

                case Const.MY:
                    rootView = inflater.inflate(R.layout.fragment_my,container,false);
                    break;
            }

            return rootView;
        }

        /**
         * 当该fragment变为可见时回调的方法，例如从消息页跳回首页，则首页回调这个方法
         * by 赖贤城
         */
        @Override
        protected void lazyLoad() {
            if (!isVisible || !isActivityCreated) {
                Log.d("fragmentLazy", "没显示"+getArguments().getInt(ARG_SECTION_NUMBER));
                return;
            }

            frameBar = (FrameLayout) getActivity().findViewById(R.id.frame_layout);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case Const.HOME:
                    setToolbarShow(true);
                    if (!mHasLoadedOnce)
                        contentHome = new ContentHome(getActivity(),getContext(),rootView);
                    else
                        contentHome.refreshIfUserChange();
                    break;

                case Const.DISCOVER:
                    setToolbarShow(false);
                    if (!mHasLoadedOnce)
                        contentDiscover = new ContentDiscover(getActivity(),getContext(),rootView);
                    break;

                case Const.MESSAGE:
                    setToolbarShow(true);
                    if (!mHasLoadedOnce)
                        contentMessage = new ContentMessage(getActivity(),getContext(),rootView);
                    break;

                case Const.MY:
                    setToolbarShow(false);
                    if (!mHasLoadedOnce)
                        contentMy = new ContentMy(getActivity(),getContext(),rootView);
                    break;
            }
            mHasLoadedOnce = true;
        }

        /**
         * 设置显示或隐藏toolbar
         * @param isShow 是否显示
         * by 赖贤城
         */
        private void setToolbarShow(boolean isShow){
            if (isShow){
                frameBar.setVisibility(View.VISIBLE);
            }
            else{
                frameBar.setVisibility(View.GONE);
            }
        }
    }



    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackTime < BACK_INTERVAL) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "双击 back 退出", Toast.LENGTH_SHORT).show();
        }
        lastBackTime = currentTime;
    }

    public User getCurrentUser(){
        //用于在fragment中，获取当前的用户对象
        return  user;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(MainActivity.this,"接收到回应"+requestCode,Toast.LENGTH_SHORT).show();
        //为fragment赋值
/*        List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        Fragment fragmentHome = fragments.get(0);
        if(requestCode == Const.PUBLISH_REQUEST_CODE){
            //转交给fragmentHome处理
            fragmentHome.onActivityResult(requestCode,resultCode,data);
        }*/
    }

    @Override
    protected void onDestroy() {
        //关闭websocket连接
        if(client != null && client.isOpen()){
            client.close();
            Log.d("连接已关闭", "连接已关闭");
        }
        //下线请求
        if(user.getUser_id() != null){
            userService.offline(user.getUser_id().toString())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SimpleResponse>() {
                        @Override
                        public void onCompleted() {
                        }
                        @Override
                        public void onError(Throwable e) {
                            Log.e("用户下线","失败" + e.getMessage());
                        }

                        @Override
                        public void onNext(SimpleResponse simpleResponse) {
                            Log.i("用户下线",simpleResponse.getSuccess());
                        }
                    });
            super.onDestroy();
        }
    }

    /**
     * 使用本机用户的token连接融云服务器
     * @param token 本机用户的token
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            //Toast.makeText(MainActivity.this, "IN！", Toast.LENGTH_SHORT).show();
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    Toast.makeText(MainActivity.this, "onTokenIncorrect！", Toast.LENGTH_SHORT).show();
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("LoginActivity", "--onSuccess" + userid);
                    //Toast.makeText(MainActivity.this, "成功！", Toast.LENGTH_SHORT).show();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Toast.makeText(MainActivity.this, "失败……", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void getPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
//                    CAMERA);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
//                    READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
//                    RECORD_AUDIO);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "您拒绝了其中一种权限，无法正常完整运行某些功能，请在后台权限管理打开此功能",
                                    Toast.LENGTH_LONG).show();
//                            finish();
                            return;
                        }
                    }
                }
                else {
                    Toast.makeText(this, "发生未知错误",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
//            case ACCESS_FINE_LOCATION:
//                if (grantResults.length > 0) {
//                    for (int result : grantResults) {
//                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(this, "您拒绝了定位权限，无法进行帖子定位，请在后台权限管理打开此功能",
//                                    Toast.LENGTH_SHORT).show();
////                            finish();
//                            return;
//                        }
//                    }
//                }
//                else {
//                    Toast.makeText(this, "发生未知错误",
//                            Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                break;
//            case PERMISSION_WRITE_EXTERNAL_STORAGE:
//                if (grantResults.length > 0) {
//                    for (int result : grantResults) {
//                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(this, "您拒绝了读写内存权限，无法访问内部相册，请在后台权限管理打开此功能",
//                                    Toast.LENGTH_SHORT).show();
////                            finish();
//                            return;
//                        }
//                    }
//                }
//                else {
//                    Toast.makeText(this, "发生未知错误",
//                            Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                break;
//            case READ_PHONE_STATE:
//                if (grantResults.length > 0) {
//                    for (int result : grantResults) {
//                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(this, "您拒绝了手机状态权限，无法读取手机状态，请在后台权限管理打开此功能",
//                                    Toast.LENGTH_SHORT).show();
////                            finish();
//                            return;
//                        }
//                    }
//                }
//                else {
//                    Toast.makeText(this, "发生未知错误",
//                            Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                break;
//            case RECORD_AUDIO:
//                if (grantResults.length > 0) {
//                    for (int result : grantResults) {
//                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(this, "您拒绝了访问麦克风权限，无法进行语音输入，请在后台权限管理打开此功能",
//                                    Toast.LENGTH_SHORT).show();
////                            finish();
//                            return;
//                        }
//                    }
//                }
//                else {
//                    Toast.makeText(this, "发生未知错误",
//                            Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                break;
        }
    }

}
