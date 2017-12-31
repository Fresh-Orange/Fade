package com.sysu.pro.fade.publish;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Image;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.emotionkeyboard.fragment.EmotionMainFragment;
import com.sysu.pro.fade.publish.Event.ImageSelectorToPublish;
import com.sysu.pro.fade.publish.Event.PublishToImageSelector;
import com.sysu.pro.fade.publish.adapter.PostArticleImgAdapter;
import com.sysu.pro.fade.publish.adapter.imageAdaptiveIndicativeLayout;
import com.sysu.pro.fade.publish.imageselector.ImageSelectorActivity;
import com.sysu.pro.fade.publish.imageselector.constant.Constants;
import com.sysu.pro.fade.publish.imageselector.utils.BitmapUtils;
import com.sysu.pro.fade.publish.imageselector.utils.ImageSelectorUtils;
import com.sysu.pro.fade.publish.utils.ImageUtils;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class PublishActivity extends AppCompatActivity {

    public static PublishActivity publishActivity;
    private static final int REQUEST_CODE = 0x00000011;

    private EditText edit_temp = null;
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();

    private String path = null;

    private LinearLayout rl_editbar_bg;
    private View activityRootView;
    private int newCount = 9;
    private ArrayList<String> images = new ArrayList<String>();
    private ArrayList<String> newDataList = new ArrayList<String>();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private final int maxCount = 9;
    private EditText et_emotion; //编辑器
    private EmotionMainFragment emotionMainFragment;

    private boolean isHidden = true;

    private FrameLayout frameLayout;

    private ImageButton imageButton;

    private imageAdaptiveIndicativeItemLayout pager;

    private RelativeLayout pagerContainer;

    private boolean flag = true;

    private LinearLayout choose_view;
    public PostArticleImgAdapter postArticleImgAdapter;
    private TextView tv;//删除区域提示
    private Context mContext;
    private int show;
    private static int CHOOSE = 0;
    private static int PAGER = 1;

    //2017.9.29
    public static float[] imageX = new float[10];
    public static float[] imageY = new float[10];
    private int crop_size = 1;

    //2017.9.13 hl
    public Integer have_compress_num = 0;
    //public String image_size_list;
    public Integer note_id; //发送文本成功后得到的
    //add by hl
    private User user;
    private TextView publishTextView;
    private ProgressDialog progressDialog;
    private static List<File> images_files;
    /*add By huanglu 2017.2.30，统一到java bean里处理*/
    private Note note;
    private List<Image>imageArray;
    //rxjava接入
    private Retrofit retrofit;
    private NoteService noteService;



    private void dealWithImagesToSend(final List<String>images){
        //发送帖子的最后操作在这里
        //收集要发送的图片数据，包装一下,压缩好图片后，发送帖子
        if(images_files == null) images_files = new ArrayList<>();
        final File sd=Environment.getExternalStorageDirectory();
        String cache_path_root = sd.getPath() + "/chache_pic";
        File rootFile = new File(cache_path_root);
        if(!rootFile.exists())  rootFile.mkdir();

        for(int i = 0; i < images.size(); i++){
            final Image image = new Image();
            //获得坐标
            int x = (int) (imageX[i] * 1000);
            String xStr = "" + x;
            int y = (int) (imageY[i] * 1000);
            String yStr = "" + y;
            image.setImage_coordinate(xStr + ":" + yStr);
            image.setImage_cut_size(crop_size + "");

            String image_path = images.get(i);
            //然后压缩图片
            Luban.with(this)
                    .load(new File(image_path))
                    .setCompressListener(new OnCompressListener() {
                @Override
                public void onStart() {
                    Log.i("压缩图片","开始");
                }
                @Override
                public void onSuccess(File file) {
                    Bitmap bitmap_temp = ImageUtils.getBitmap(file.getPath());
                    //获得宽高比
                    final Double size = new Integer(bitmap_temp.getWidth()).doubleValue()/ new Integer(bitmap_temp.getHeight()).doubleValue();
                    bitmap_temp.recycle();
                    image.setImage_size(size.toString());
                    //添加到图片队列
                    imageArray.add(image);
                    images_files.add(new File(file.getPath()));
                    have_compress_num++;
                    Log.i("压缩图片","成功");
                    if(have_compress_num == images.size()){
                        //直到这里，所有图片才生成本地的压缩文件，才能发送图片
                        //TODO : 后面两个参数为 coordinate_list, cut_size_list
                        //cut_size为裁剪比例，1代表宽图4:5, 2代表长图15:8
                        //全部图片压缩完毕，发送帖子
                        note.setImages(imageArray);
                        MultipartBody.Builder builder= new MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("note", new Gson().toJson(note));
                        for(File temp : images_files){
                            builder.addFormDataPart("file", temp.getName(), RequestBody.create(MediaType.parse("image/*"), temp));
                        }
                        RequestBody body = builder.build();
                        noteService.addNote(body)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<SimpleResponse>() {
                                    @Override
                                    public void onCompleted() {
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("发送帖子","出错");
                                        Toast.makeText(PublishActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        finish();
                                    }

                                    @Override
                                    public void onNext(SimpleResponse simpleResponse) {
                                        if(simpleResponse.getErr() == null){
                                            Toast.makeText(PublishActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            //添加一些服务器返回来的参数
                                            Map<String,Object> extra = simpleResponse.getExtra();
                                            List<String>imageUrls = (List<String>) extra.get("imageUrls");
                                            if(imageUrls != null){
                                                for(int k = 0; k < imageUrls.size(); k++ ){
                                                    imageArray.get(k).setImage_url(imageUrls.get(k));
                                                }
                                            }
                                            note.setImages(imageArray);
                                            Integer note_id = (Integer) extra.get("note_id");
                                            String post_time = (String) extra.get("post_time");
                                            note.setNote_id(note_id);
                                            note.setPost_time(post_time);
                                            //通知主界面（ContentHome）更新
                                            EventBus.getDefault().post(note);
                                            finish();
                                        }else {
                                            Toast.makeText(PublishActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
                                        }
                                        //最后要将所有缓存图片删除
                                        for(File chache_file : images_files){
                                            if(chache_file.exists()){
                                                chache_file.delete();
                                            }
                                        }
                                    }
                                });
                    }
                }
                @Override
                public void onError(Throwable e) {
                    Log.i("压缩图片","失败");
                    //Toast.makeText(PublishActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }).launch();
           // File cache_file = ImageUtils.saveBitmapFileByCompress(cache_path_root,bitmap_temp,50);
        }
        if(images.size() == 0){
            //说明是纯文字帖，另外处理
            MultipartBody.Builder builder= new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("note", new Gson().toJson(note));
            RequestBody body = builder.build();
            noteService.addNote(body)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SimpleResponse>() {
                        @Override
                        public void onCompleted() {
                        }
                        @Override
                        public void onError(Throwable e) {
                            Log.e("发送帖子","出错");
                            Toast.makeText(PublishActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                        @Override
                        public void onNext(SimpleResponse simpleResponse) {
                            if(simpleResponse.getErr() == null){
                                Toast.makeText(PublishActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                //添加一些服务器返回来的参数
                                Map<String,Object> extra = simpleResponse.getExtra();
                                Integer note_id = (Integer) extra.get("note_id");
                                String post_time = (String) extra.get("post_time");
                                note.setNote_id(note_id);
                                note.setPost_time(post_time);
                                //通知主界面（ContentHome）更新
                                EventBus.getDefault().post(note);
                                finish();
                            }else {
                                Toast.makeText(PublishActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish);
        //注册
        EventBus.getDefault().register(this);
        user = new UserUtil(PublishActivity.this).getUer();//从本地存储初始化用户信息
        note = new Note();//本页面的note对象
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        noteService = retrofit.create(NoteService.class);
        imageArray = new ArrayList<>();//图片对象数组
        progressDialog = new ProgressDialog(PublishActivity.this);
        show = CHOOSE;
        InitView();
        if (flag) {
            et_emotion= (EditText) findViewById(R.id.my_et_emotion);
            //设置焦点，可被操作
            et_emotion.setFocusable(true);
            et_emotion.setFocusableInTouchMode(true);
            et_emotion.requestFocus();
            InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
            im.showSoftInput(et_emotion, 0);
            InitListener();
            initEmotionMainFragment();
        }

    }

    private String getCoordinateString() {
        String str = "";
        for (int i = 0; i < images.size(); i++) {
            int x = (int) (imageX[i] * 1000);
            String xStr = "" + x;
            int y = (int) (imageY[i] * 1000);
            String yStr = "" + y;
            str += xStr + ":" + yStr;
            if (i < images.size() - 1)
                str += ",";
        }
        Log.d("upload_size", str);
        return str;
    }
    private void InitListener() {
        publishTextView = (TextView) findViewById(R.id.tv_confirm);
        publishTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送帖子
                progressDialog.show();
                //设置Note对象的一些属性
                note.setUser_id(user.getUser_id());
                note.setNickname(user.getNickname());
                note.setNote_content(edit_temp.getText().toString());
                note.setHead_image_url(user.getHead_image_url());
                //处理图片，发送后的回调处理
                dealWithImagesToSend(images);
            }
        });

        findViewById(R.id.choose_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });

        ImageButton icon_add_pic = (ImageButton) findViewById(R.id.icon_add_pic);
        icon_add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });

        ImageButton icon_sub_pic = (ImageButton) findViewById(R.id.icon_sub_pic);
        icon_sub_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PublishActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("确定要删除这张照片吗?");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (images.size() == 1) {
                                images.clear();
                                newCount = maxCount;
                                choose_view.setVisibility(View.VISIBLE);
                                setHiddenPager(true);
                                show = CHOOSE;
                            }
                            else {
                                int currentItem = pager.getPosition();
                                Log.d("Hey", "" + currentItem);
                                removeImg(currentItem);
                                newCount = maxCount - images.size();
                                pager.setPaths(images,currentItem);
//                                pager.removeViewAt(currentItem);
                            }
                            pager.notifyChanged();
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
            }
        });


        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PublishActivity.this);
                builder.setTitle("退出此次编辑?");
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
            }
        });

        activityRootView = findViewById(R.id.activity_publish);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(PublishActivity.this, 200)) {
                    findViewById(R.id.rl_editbar_bg).setVisibility(View.VISIBLE);
                    findViewById(R.id.emotion_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.keyboard_button).setVisibility(View.GONE);
                    choose_view.setVisibility(View.GONE);
                    setHiddenPager(true);
                }
                else{
                    findViewById(R.id.emotion_button).setVisibility(View.GONE);
                    findViewById(R.id.keyboard_button).setVisibility(View.VISIBLE);
                    if (frameLayout.getVisibility() == View.GONE)
                      findViewById(R.id.rl_editbar_bg).setVisibility(View.GONE);
                    if (show == CHOOSE)
                        choose_view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (findViewById(R.id.rl_editbar_bg).getVisibility() == View.GONE)
                             choose_view.setVisibility(View.VISIBLE);
                        }
                    }, 50);
                    if (show == PAGER)
                        pager.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (findViewById(R.id.rl_editbar_bg).getVisibility() == View.GONE)
                                    choose_view.setVisibility(View.VISIBLE);
                            }
                        }, 3000);
                }

            }
        });

        edit_temp = (EditText) findViewById(R.id.my_et_emotion);

    }


    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private void InitView() {
        pager = (imageAdaptiveIndicativeItemLayout) findViewById(R.id.image_layout);
        choose_view = (LinearLayout) findViewById(R.id.choose_view);
        tv = (TextView) findViewById(R.id.tv);
        pagerContainer = (RelativeLayout) findViewById(R.id.pager_container);
        setHiddenPager(true);
    }

    /**
     * 隐藏或是显示“Pager和增加和删除按钮”
     * @param isHidden true为隐藏 ，false为显示
     */
    private void setHiddenPager(boolean isHidden){
        if (isHidden)
            pagerContainer.setVisibility(View.GONE);
        else
            pagerContainer.setVisibility(View.VISIBLE);
    }

    private void initEmotionMainFragment() {
        //构建传递参数
        Bundle bundle = new Bundle();
        //绑定主内容编辑框
        bundle.putBoolean(EmotionMainFragment.BIND_TO_EDITTEXT,false);
        //隐藏控件
        bundle.putBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN,true);

        bundle.putBoolean(EmotionMainFragment.EMOTION_HIDE,isHidden);
        //替换fragment
        //创建修改实例
        frameLayout = (FrameLayout) findViewById(R.id.fl_memotionview_main);
        rl_editbar_bg = (LinearLayout) findViewById(R.id.rl_editbar_bg);
        emotionMainFragment = EmotionMainFragment.newInstance(EmotionMainFragment.class,bundle);
        emotionMainFragment.bindToContentView(et_emotion);
        emotionMainFragment.bindToFramelayout(frameLayout);
        emotionMainFragment.bindToRl_editbar_bg(rl_editbar_bg);
        emotionMainFragment.bindToEmotion((ImageView)findViewById(R.id.emotion_button));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in thefragment_container view with this fragment,
        // and add the transaction to the backstack
        transaction.replace(R.id.fl_memotionview_main,emotionMainFragment);
        //返回栈
        transaction.addToBackStack(null);
        //提交修改
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        /**
         * 判断是否拦截返回键操作
         */
        if (!emotionMainFragment.isInterceptBackPress()) {
            super.onBackPressed();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        ShowThumbnail();
//        ShowViewPager();
        }

    private void ShowViewPager() {
        if (images != null) {
            choose_view.setVisibility(View.GONE);
            setHiddenPager(false);
            show = PAGER;
            float maxRatio = 0;
            pager.setViewPagerMaxHeight(280);
            double ratio;
            int cutSize = determineSize(images.get(0));
            if (cutSize == 1)
                ratio = 5.0/4;
            else
                ratio = 8.0/15;
            pager.setHeightByRatio((float)ratio);

            String coordinateString = getCoordinateString();
            String[] coordinates = coordinateString.split(",");
            pager.setImgCoordinates(Arrays.asList(coordinates));
            pager.setPaths(images,images.size() - 1);
        }
    }

    private void ShowThumbnail() {
        if (images != null) {
            Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_addpic);
            bitmaps.clear();
            for (String image : images) {
                Bitmap newBp = BitmapUtils.decodeSampledBitmapFromFd(image, 200, 200);
                bitmaps.add(newBp);
            }
        }
    }

    private static int determineSize(String image) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        Bitmap bitmap = BitmapFactory.decodeFile(image, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        float currentRatio = (float)options.outWidth / (float)options.outHeight;
        if (currentRatio > 1.3375f)
            return 0;
        else
            return 1;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            newCount = data.getIntExtra(Constants.NEW_COUNT, maxCount);
            imageX = data.getFloatArrayExtra(Constants.IMAGEX);
            imageY = data.getFloatArrayExtra(Constants.IMAGEY);
            crop_size = data.getIntExtra(Constants.CUT_SIZE, 1);
            newDataList = new ArrayList<String>();
            if (data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT) != null)
            {
                newDataList = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
            }
            if (newDataList != null)
            {
                images = (newDataList);
            }


            ShowViewPager();
            flag = true;
            Log.d("My","string: " + getCoordinateString());
            Log.d("My", "size: " + crop_size);
        }
        //仅剩一张还删了
//        if (data != null && data.getBooleanExtra(Constants.IS_ALL_DELETED, false)
//                && requestCode == Constants.CLICK_RESULT_CODE) {
//            images.clear();
//            newCount = maxCount;
//            postArticleImgAdapter.notifyDataSetChanged();
//        }
        //其他删除情况
//        if (data != null && data.getBooleanExtra(Constants.IS_DELETED, false)
//                && requestCode == Constants.CLICK_RESULT_CODE) {
//            //既然删了就没满
//            isFull = false;
//            int clickPositionSize = data.getIntExtra(Constants.CURRENT_POSITION_SIZE,0);
//            if (clickPositionSize >= images.size()) {
//                newCount = maxCount;
//                images.clear();
//            }
//            else if (clickPositionSize > 0) {
//                Bundle bundle = data.getExtras();
//                int[] clickPosition = new int[15];
//                clickPosition = bundle.getIntArray(Constants.CURRENT_POSITION);
//                for (int i = 0; i < clickPositionSize; i++) {
//                    int removePosition = 0;
//                    if (clickPosition != null) {
//                        removePosition = clickPosition[i];
//                    }
//                    Log.d("Yellow", "" + removePosition);
//                    if (removePosition < images.size())
//                        images.remove(removePosition);
//                }
//                newCount = maxCount - images.size();
//            }
////            images.remove(data.getIntExtra(Constants.CURRENT_POSITION, 0));
////            newCount -= newDataList.size();
//            postArticleImgAdapter.notifyDataSetChanged();
//        }

        if (requestCode == Constants.TAKE_PICTURE  && resultCode == RESULT_OK)
        {
            if (path != null) {
                images.add(path);
                newCount--;
                ShowViewPager();
             }
            }
    }

    private void showListDialog() {
        final String[] items = { "拍摄","从相册选择"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(PublishActivity.this);
//        listDialog.setTitle("我是一个列表Dialog");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
//                        Toast.makeText(PublishActivity.this,
//                                "你点击了拍照", Toast.LENGTH_SHORT).show();
                        takePhoto(PublishActivity.this);
                        break;
                    case 1:
//                        Toast.makeText(PublishActivity.this,
//                                "你点击了相册", Toast.LENGTH_SHORT).show();
//                        ImageSelectorUtils.openPhoto(PublishActivity.this, REQUEST_CODE, 9, images, newCount);
//                        emotionMainFragment.bindToContentView(findViewById(R.id.picker_04_horizontal));
//                        ImageSelectorActivity.openActivity(PublishActivity.this, REQUEST_CODE, 9, images, newCount);
                        Intent intent = new Intent(PublishActivity.this, ImageSelectorActivity.class);
                        intent.putExtra(Constants.MAX_SELECT_COUNT, 9);
                        intent.putStringArrayListExtra(ImageSelectorUtils.SELECT_LAST, images);
                        intent.putExtra(Constants.NEW_COUNT, newCount);
                        startActivity(intent);
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                EventBus.getDefault().post(new PublishToImageSelector(9, images, newCount));
//                                Log.d("Yellow", "PublishNewCount: " + newCount);
//                            }
//                        }).start();
                        break;
                }
                // which 下标从0开始
                // ...To-do

            }
        });
        listDialog.show();
    }
    //适配7.0的拍照方法
    private static void takePhoto(Activity activity)
    {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File vFile = new File(Environment.getExternalStorageDirectory()
                + "/myimage/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        if (!vFile.exists())
        {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        }
        else
        {
            if (vFile.exists())
            {
                vFile.delete();
            }
        }
        Uri tempUri;
        if (Build.VERSION.SDK_INT >= 24)
            tempUri = FileProvider.getUriForFile(activity.getApplicationContext(),
                    activity.getApplicationContext().getPackageName() +
                            ".provider", vFile);
        else tempUri = Uri.fromFile(vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        activity.startActivityForResult(openCameraIntent, Constants.TAKE_PICTURE);
    }



    private void removeImg(int location)
    {
        if (location + 1 <= images.size())
        {
            images.remove(location);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImageSelectorToPublish event) {
        images = event.getImages();
        newCount = event.getNewCount();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }

}
