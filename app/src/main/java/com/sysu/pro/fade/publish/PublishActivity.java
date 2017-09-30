package com.sysu.pro.fade.publish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import android.widget.ViewSwitcher;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.publish.adapter.ImageAdapter;
import com.sysu.pro.fade.publish.adapter.MyAdapter;
import com.sysu.pro.fade.publish.adapter.MyCallBack;
import com.sysu.pro.fade.publish.adapter.MyGridView;
import com.sysu.pro.fade.publish.adapter.PostArticleImgAdapter;
import com.sysu.pro.fade.publish.adapter.imageAdaptiveIndicativeLayout;
import com.sysu.pro.fade.emotionkeyboard.fragment.EmotionMainFragment;
import com.sysu.pro.fade.publish.imageselector.ImageSelectorActivity;
import com.sysu.pro.fade.publish.imageselector.constant.Constants;
import com.sysu.pro.fade.publish.imageselector.utils.BitmapUtils;
import com.sysu.pro.fade.publish.imageselector.utils.ImageSelectorUtils;
import com.sysu.pro.fade.publish.utils.ImageUtils;
import com.sysu.pro.fade.tool.NoteTool;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.utils.UserUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private boolean isFull = false;
    private MyAdapter adapter;

    private MyGridView mGridView;
    private int newCount = 9;
    private RecyclerView rvImage;
    private ImageAdapter mAdapter;
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

    private imageAdaptiveIndicativeLayout pager;

    private LinearLayout choose_view;


    private ViewSwitcher viewSwitcher;

    public PostArticleImgAdapter postArticleImgAdapter;
    private ItemTouchHelper itemTouchHelper;
    private RecyclerView rcvImg;
    private TextView tv;//删除区域提示
    private Context mContext;
    private ArrayList<String> dragImages;//压缩长宽后图片
    public static final String FILE_DIR_NAME = "com.kuyue.wechatpublishimagesdrag";//应用缓存地址
    public static final String FILE_IMG_NAME = "images";//放置图片缓存

    //2017.9.13 hl
    public Integer have_compress_num = 0;
    public String image_size_list;
    public Integer note_id; //发送文本成功后得到的
    //add by hl
    private User user;
    private TextView publishTextView;
    private ProgressDialog progressDialog;
    private List<File> images_files;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x1){
                //发送文本得到的相应
                Map<String, Object> map = (Map<String, Object>) msg.obj;
                //Toast.makeText(PublishActivity.this,map.toString(),Toast.LENGTH_LONG).show();
                note_id = (Integer) map.get(Const.NOTE_ID);
                String err = (String) map.get(Const.ERR);
                if(err == null && note_id != null){
                    if(images.size() == 0 || images == null){
                        Toast.makeText(PublishActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        setResult(1,getIntent());
                        finish();
                    }else {
                        image_size_list = dealWithImagesToSend(images);
                    }
                }else{
                    Toast.makeText(PublishActivity.this,err,Toast.LENGTH_SHORT).show();
                    setResult(0,getIntent());
                    progressDialog.dismiss();
                    finish();
                }
            }
            else if(msg.what == 0x22){
                //发送图片得到的响应
                Map<String, Object> map = (Map<String, Object>) msg.obj;
                String err = (String) map.get(Const.ERR);
                if(err != null){
                    Toast.makeText(PublishActivity.this,err,Toast.LENGTH_SHORT).show();
                    setResult(0,getIntent());
                    progressDialog.dismiss();
                    finish();
                }else{
                   // List<String>url_list = (List<String>) map.get(Const.IMAGE_LIST);
                    Toast.makeText(PublishActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                    setResult(1,getIntent());
                    progressDialog.dismiss();
                    finish();
                }
                //最后要将所有缓存图片删除
                for(File chache_file : images_files){
                    if(chache_file.exists()){
                        chache_file.delete();
                    }
                }

            }

            super.handleMessage(msg);
        }
    };

    private String dealWithImagesToSend(final List<String>images){
        if(images_files == null) images_files = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        File sd=Environment.getExternalStorageDirectory();
        String cache_path_root = sd.getPath() + "/chache_pic";
        File rootFile = new File(cache_path_root);
        if(!rootFile.exists())  rootFile.mkdir();
        for(String image_path : images){
            Bitmap bitmap_temp = ImageUtils.getBitmap(image_path);
            //获得宽高比
            Double size = new Integer(bitmap_temp.getWidth()).doubleValue()/ new Integer(bitmap_temp.getHeight()).doubleValue();
            sb.append(size.toString());
            sb.append(",");
            Luban.with(this)
                    .load(new File(image_path))
                    .setCompressListener(new OnCompressListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(File file) {
                    images_files.add(file);
                    have_compress_num++;
                    if(have_compress_num == images.size()){
                        //直到这里，所有图片才生成本地的压缩文件，才能发送图片
                        //TODO : 后面两个参数为 coordinate_list, cut_size_list
                        //coordinate_list为坐标用逗号连成的字符串，例如:"1:2,1:2,2:2"  横纵坐标之间用冒号隔开
                        //cut_size_list为裁剪比例用逗号连成的字符串，0代表0代表不裁剪，1代表长图4:5, 2代表宽图15:8，例："0,1,2"
                        //顺序与images的顺序相同
                        //NoteTool.uploadNoteImage(handler,note_id,images_files,image_size_list,null,null);
                    }
                }
                @Override
                public void onError(Throwable e) {
                    Toast.makeText(PublishActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }).launch();
           // File cache_file = ImageUtils.saveBitmapFileByCompress(cache_path_root,bitmap_temp,50);
        }
        //测试
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish);
        user = new UserUtil(PublishActivity.this).getUer();//从本地存储初始化用户信息
        progressDialog = new ProgressDialog(PublishActivity.this);
//        rvImage = (RecyclerView) findViewById(R.id.rv_image);
//        rvImage.setLayoutManager(new GridLayoutManager(this, 3));
//        rvImage.getLayoutManager();
//        mAdapter = new ImageAdapter(this);
//        rvImage.setAdapter(mAdapter);

//        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_addpic);
//        bitmaps.add(bp);

        InitView();

        et_emotion= (EditText) findViewById(R.id.my_et_emotion);
        //设置焦点，可被操作
        et_emotion.setFocusable(true);
        et_emotion.setFocusableInTouchMode(true);
        et_emotion.requestFocus();
        InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        im.showSoftInput(et_emotion, 0);


        InitListener();
        initEmotionMainFragment();

//        mAdapter.setOnItemClickLisitenter(new RecycleViewLisitenter.onItemClickLisitenter() {
//            @Override
//            public void onItemClick(View v, int position) {
//                if (bitmaps.size() == 10) {
//                    Toast.makeText(PublishActivity.this, "图片数9张已满", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (position == bitmaps.size() - 1) {
//                        Toast.makeText(PublishActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
//                        // 选择图片
//                        ImageSelectorUtils.openPhoto(PublishActivity.this, REQUEST_CODE, 9, images, newCount);
//                    } else {
//                        Toast.makeText(PublishActivity.this, "点击第" + (position + 1) + " 号图片", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                ArrayList<String> selectImage = new ArrayList<String>();
//                selectImage.add(images.get(position));
//            }
//        });
//        findViewById(R.id.btn_limit).setOnClickListener(this);
    }

    private void InitListener() {
        imageButton = (ImageButton) findViewById(R.id.add_button);
        publishTextView = (TextView) findViewById(R.id.tv_confirm);
        publishTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先发送文字，收到note_id后再接收图片
                if(edit_temp.getText().toString().equals("") || edit_temp == null){
                    Toast.makeText(PublishActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show();
                    NoteTool.addNote(handler,user.getUser_id(),user.getNickname(),
                            user.getHead_image_url(),edit_temp.getText().toString(),0,"1,2");
                }

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
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
                            if (images.size() == 0) {
                                choose_view.setVisibility(View.VISIBLE);
                                viewSwitcher.setVisibility(View.GONE);
                            }
                            if (images.size() == 1) {
                                images.clear();
                                newCount = maxCount;
                                choose_view.setVisibility(View.VISIBLE);
                                viewSwitcher.setVisibility(View.GONE);
//                                postArticleImgAdapter.notifyDataSetChanged();
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

//        pager.addOnItemTouchListener
        // 设置点击监听事件
//        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == bitmaps.size() - 1 && !isFull) {
//                    showListDialog();
////                    Toast.makeText(PublishActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
//                    // 选择图片
//                } else {
////                    Toast.makeText(PublishActivity.this, "点击第" + (position + 1) + " 号图片",
////                            Toast.LENGTH_SHORT).show();
////                    if (images != null)
//                    ClickToPreviewActivity.openActivity(PublishActivity.this, images,
//                            newCount, position);
////                    images = getIntent().getStringArrayListExtra(Constants.NEW_PATH);
//                }
//            }
//        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        isHidden = EmotionMainFragment.isHidden;
        activityRootView = findViewById(R.id.activity_publish);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(PublishActivity.this, 200)) {
//                    Toast.makeText(PublishActivity.this, "显示", Toast.LENGTH_SHORT).show();
//                    findViewById(R.id.vp_image).setVisibility(View.GONE);
                    findViewById(R.id.rl_editbar_bg).setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(PublishActivity.this, "隐藏", Toast.LENGTH_SHORT).show();
//                    findViewById(R.id.vp_image).setVisibility(View.VISIBLE);
                    if (frameLayout.getVisibility() == View.GONE)
                      findViewById(R.id.rl_editbar_bg).setVisibility(View.GONE);
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
        // 设置默认图片为加号
//        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_addpic);
//        bitmaps.add(bp);
        // 找到控件ID
//        mGridView = (MyGridView) findViewById(R.id.gridView1);
        // 绑定Adapter
//        adapter = new MyAdapter(getApplicationContext(), bitmaps, mGridView);
//        mGridView.setAdapter(adapter);

        publishActivity = this;
        mContext = getApplicationContext();
        dragImages = new ArrayList<>();
        dragImages.addAll(images);
//        new Thread(new MyRunnable(dragImages, images, dragImages, myHandler, false)).start();


        pager = (imageAdaptiveIndicativeLayout) findViewById(R.id.image_viewpager);
        choose_view = (LinearLayout) findViewById(R.id.choose_view);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher);
        rcvImg = (RecyclerView) findViewById(R.id.rcv_img);
        tv = (TextView) findViewById(R.id.tv);

        postArticleImgAdapter = new PostArticleImgAdapter(mContext, dragImages);
        rcvImg.setLayoutManager(new GridLayoutManager(mContext, 3));
        rcvImg.setAdapter(postArticleImgAdapter);
        MyCallBack myCallBack = new MyCallBack(postArticleImgAdapter, dragImages, images);
        itemTouchHelper = new ItemTouchHelper(myCallBack);
        itemTouchHelper.attachToRecyclerView(rcvImg);//绑定RecyclerView

        pager.setViewSwitcher(viewSwitcher);
//
    }

//        image_viewpager = (LinearLayout) findViewById(R.id.image_viewpager);

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
//        isHidden = emotionMainFragment.getIsHidden();
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
            pager.setVisibility(View.VISIBLE);
            viewSwitcher.setVisibility(View.VISIBLE);
            viewSwitcher.setDisplayedChild(0);
            float maxRatio = 0;
            for (String image : images)
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                /**
                 * 最关键在此，把options.inJustDecodeBounds = true;
                 * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
                 */
 //                options.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(image, options); // 此时返回的bitmap为null
//                Bitmap bitmap = BitmapFactory.decodeFile(image);
                /**
                 *options.outHeight为原始图片的高
                 */
                float currentRatio = (float)options.outHeight / (float)options.outWidth;
//                float currentRatio = bitmap.getHeight() / bitmap.getWidth();
                if (currentRatio > maxRatio)
                    maxRatio = currentRatio;
            }
//            maxRatio *= 3;
            pager.setViewPagerMaxHeight(280);
            pager.setHeightByRatio(maxRatio);
            pager.setPaths(images,images.size() - 1);
            if (bitmaps.size() == 9)
                isFull = true;
//            else
//                bitmaps.add(bp);
//            adapter.notifyDataSetChanged();
//            bitmaps.clear();
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
            if (bitmaps.size() == 9)
                isFull = true;
//            else
//                bitmaps.add(bp);
            postArticleImgAdapter.notifyDataSetChanged();
//            bitmaps.clear();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            newCount = data.getIntExtra(Constants.NEW_COUNT, maxCount);
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
//            new Thread(new MyRunnable(images,
//                    images, dragImages, myHandler, true)).start();
//            postArticleImgAdapter.notifyDataSetChanged();
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
                        takePhoto();
                        break;
                    case 1:
//                        Toast.makeText(PublishActivity.this,
//                                "你点击了相册", Toast.LENGTH_SHORT).show();
//                        ImageSelectorUtils.openPhoto(PublishActivity.this, REQUEST_CODE, 9, images, newCount);
                        ImageSelectorActivity.openActivity(PublishActivity.this, REQUEST_CODE, 9, images, newCount);
                        break;
                }
                // which 下标从0开始
                // ...To-do

            }
        });
        listDialog.show();
    }



    public void takePhoto()
    {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

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
        path = vFile.getPath();
        Uri cameraUri = Uri.fromFile(vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(openCameraIntent, Constants.TAKE_PICTURE);
    }

    private void removeImg(int location)
    {
        if (location + 1 <= images.size())
        {
            images.remove(location);
        }
    }



//    static class MyRunnable implements Runnable {
//
//        ArrayList<String> images;
//        ArrayList<String> originImages;
//        ArrayList<String> dragImages;
//        Handler handler;
//        boolean add;//是否为添加图片
//
//        public MyRunnable(ArrayList<String> images,
//                          ArrayList<String> originImages,
//                          ArrayList<String> dragImages,
//                          Handler handler,
//                          boolean add) {
//            this.images = images;
//            this.originImages = originImages;
//            this.dragImages = dragImages;
//            this.handler = handler;
//            this.add = add;
//        }
//
//        @Override
//        public void run() {
//            SdcardUtils sdcardUtils = new SdcardUtils();
//            String filePath;
//            Bitmap newBitmap;
//            int addIndex = originImages.size();
//            int i = 0;
//            for (String image : images) {
//                //压缩
////                newBitmap = ImageUtils.compressScaleByWH(images.get(i),
////                        DensityUtils.dp2px(MyApplication.getInstance().getContext(), 100),
////                        DensityUtils.dp2px(MyApplication.getInstance().getContext(), 100));
//                newBitmap = BitmapUtils.decodeSampledBitmapFromFd(image, 200, 200);
//                //文件地址
//                filePath = sdcardUtils.getSDPATH() + FILE_DIR_NAME + "/"
//                        + FILE_IMG_NAME + "/" + String.format("img_%d.jpg", System.currentTimeMillis());
//                //保存图片
//                ImageUtils.save(newBitmap, filePath, Bitmap.CompressFormat.JPEG, true);
//                //设置值
////                if (!add) {
//                    images.set(i++, filePath);
////                } else {//添加图片，要更新
////                    dragImages.add(addIndex, filePath);
////                    originImages.add(addIndex++, filePath);
////                }
//            }
//            Message message = new Message();
//            message.what = 1;
//            handler.sendMessage(message);
//        }
//    }
//
//    private MyHandler myHandler = new MyHandler(this);
//
//    private static class MyHandler extends Handler {
//        private WeakReference<Activity> reference;
//
//        public MyHandler(Activity activity) {
//            reference = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            PublishActivity activity = (PublishActivity) reference.get();
//            if (activity != null) {
//                switch (msg.what) {
//                    case 1:
//                        activity.postArticleImgAdapter.notifyDataSetChanged();
//                        break;
//                }
//            }
//        }
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        myHandler.removeCallbacksAndMessages(null);
//    }

}
