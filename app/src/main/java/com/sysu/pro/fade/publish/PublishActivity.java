package com.sysu.pro.fade.publish;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import com.sysu.pro.fade.publish.imageselector.ClickToPreviewActivity;
import com.sysu.pro.fade.publish.imageselector.ImageSelectorActivity;
import com.sysu.pro.fade.publish.imageselector.constant.Constants;
import com.sysu.pro.fade.publish.imageselector.utils.BitmapUtils;
import com.sysu.pro.fade.publish.imageselector.utils.ImageSelectorUtils;
import com.sysu.pro.fade.publish.utils.ImageUtils;
import com.sysu.pro.fade.publish.utils.PhotoUtils;
import com.sysu.pro.fade.tool.NoteTool;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.utils.UserUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PublishActivity extends AppCompatActivity {

    public static PublishActivity publishActivity;
    private static final int REQUEST_CODE = 0x00000011;
    public static int currentItem;

    private EditText edit_temp = null;
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();

    private String path = null;

    private LinearLayout rl_editbar_bg;
    private View activityRootView;

    private int newCount = 9;
    private ArrayList<String> images = new ArrayList<String>();
    private ArrayList<String> newDataList = new ArrayList<String>();
    private final int maxCount = 9;
    private EditText et_emotion; //编辑器
    private EmotionMainFragment emotionMainFragment;
    private FrameLayout frameLayout;
    private ImageButton imageButton;
    private imageAdaptiveIndicativeLayout pager;
    private LinearLayout choose_view;
    private ViewSwitcher viewSwitcher;

    private Context mContext;
    public static final String FILE_DIR_NAME = "com.kuyue.wechatpublishimagesdrag";//应用缓存地址
    public static final String FILE_IMG_NAME = "images";//放置图片缓存

    private final int CHOOSE = 0;
    private final int VIEW = 1;
    private int show;
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
                Integer note_id = (Integer) map.get(Const.NOTE_ID);
                String err = (String) map.get(Const.ERR);
                if(err == null && note_id != null){
                    if(images.size() == 0 || images == null){
                        Toast.makeText(PublishActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        setResult(1,getIntent());
                        finish();
                    }else {
                        String image_size_list = dealWithImagesToSend(images);
//                        List<File>lists = new ArrayList<>();
//                        for(String str : images){
//                            lists.add(new File(str));
//                        }
                        NoteTool.uploadNoteImage(handler,note_id,images_files,image_size_list);
                       // NoteTool.uploadNoteImage(handler,note_id,lists,image_size_list);
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

    private String dealWithImagesToSend(List<String>images){
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
            File cache_file = ImageUtils.saveBitmapFileByCompress(cache_path_root,bitmap_temp,50);
            images_files.add(cache_file);
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
        show = CHOOSE;
        user = new UserUtil(PublishActivity.this).getUer();//从本地存储初始化用户信息
        progressDialog = new ProgressDialog(PublishActivity.this);
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
    }

    private void InitListener() {
        imageButton = (ImageButton) findViewById(R.id.add_button);
        publishTextView = (TextView) findViewById(R.id.tv_confirm);
        publishTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先发送文字，收到note_id后再接收图片
                Log.d("Yellow", edit_temp.getText().toString());
                try {
                    String str = URLDecoder.decode(edit_temp.getText().toString(),"unicode");
                    Toast.makeText(PublishActivity.this,str,Toast.LENGTH_LONG).show();
                    if(str.equals("") || edit_temp == null){
                        Toast.makeText(PublishActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.show();
                        NoteTool.addNote(handler,user.getUser_id(),user.getNickname(),
                                user.getHead_image_url(),str,0,"1,2");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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
                                show = CHOOSE;
                            }
                            if (images.size() == 1) {
                                images.clear();
                                newCount = maxCount;
                                choose_view.setVisibility(View.VISIBLE);
                                viewSwitcher.setVisibility(View.GONE);
                                show = CHOOSE;
//                                postArticleImgAdapter.notifyDataSetChanged();
                            }
                            else {
                                currentItem = pager.getPosition();
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
                finish();
            }
        });
        activityRootView = findViewById(R.id.activity_publish);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(PublishActivity.this, 200)) {
                    findViewById(R.id.rl_editbar_bg).setVisibility(View.VISIBLE);
                    if (show == CHOOSE)
                        choose_view.setVisibility(View.GONE);
                    else
                        viewSwitcher.setVisibility(View.GONE);
                }
                else{
                    if (frameLayout.getVisibility() == View.GONE) {
                        findViewById(R.id.rl_editbar_bg).setVisibility(View.GONE);
                    }
                }
                if (findViewById(R.id.rl_editbar_bg).getVisibility() == View.GONE)
                {
                    if (show == CHOOSE)
                        choose_view.setVisibility(View.VISIBLE);
                    else
                        viewSwitcher.setVisibility(View.VISIBLE);
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
        publishActivity = this;
        mContext = getApplicationContext();
        pager = (imageAdaptiveIndicativeLayout) findViewById(R.id.image_viewpager);
        choose_view = (LinearLayout) findViewById(R.id.choose_view);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher);
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
        }

    private void ShowViewPager() {
        if (images != null) {
            choose_view.setVisibility(View.GONE);
            pager.setVisibility(View.VISIBLE);
            viewSwitcher.setVisibility(View.VISIBLE);
            viewSwitcher.setDisplayedChild(0);
            show = VIEW;
            pager.setImages(images, newCount);
            float maxRatio = 0;
            for (String image : images)
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                /**
                 * 最关键在此，把options.inJustDecodeBounds = true;
                 * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
                 */
                Bitmap bitmap = BitmapFactory.decodeFile(image, options); // 此时返回的bitmap为null
                /**
                 *options.outHeight为原始图片的高
                 */
                float currentRatio = (float)options.outHeight / (float)options.outWidth;
                if (currentRatio > maxRatio)
                    maxRatio = currentRatio;
            }
            pager.setViewPagerMaxHeight(280);
            pager.setHeightByRatio(maxRatio);
            pager.setPaths(images,images.size() - 1);
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
        }
//        仅剩一张还删了
        if (data != null && requestCode == Constants.CLICK_RESULT_CODE)
        {
            if (data.getBooleanExtra(Constants.IS_ALL_DELETED, false)) {
                images.clear();
                newCount = maxCount;
//            pager.setPaths(images,0);
                choose_view.setVisibility(View.VISIBLE);
                viewSwitcher.setVisibility(View.GONE);
                show = CHOOSE;
            }
            //        其他删除情况
            else if (data.getBooleanExtra(Constants.IS_DELETED, false)) {
                int clickPositionSize = data.getIntExtra(Constants.CURRENT_POSITION_SIZE,0);
                if (clickPositionSize >= images.size()) {
                    newCount = maxCount;
                    images.clear();
                }
                else if (clickPositionSize > 0) {
                    Bundle bundle = data.getExtras();
                    int[] clickPosition = new int[15];
                    clickPosition = bundle.getIntArray(Constants.CURRENT_POSITION);
                    for (int i = 0; i < clickPositionSize; i++) {
                        int removePosition = 0;
                        if (clickPosition != null) {
                            removePosition = clickPosition[i];
                        }
//                    Log.d("Yellow", "" + removePosition);
                        if (removePosition < images.size())
                            images.remove(removePosition);
                    }
                    newCount = maxCount - images.size();
                }
                pager.setPaths(images,0);
                pager.notifyChanged();
            }
        }
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
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        takePhoto();
                        break;
                    case 1:
                        ImageSelectorActivity.openActivity(PublishActivity.this, REQUEST_CODE, 9, images, newCount);
                        break;
                }
            }
        });
        listDialog.show();
    }



    public void takePhoto()
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
        path = vFile.getPath();
//        Uri cameraUri = Uri.fromFile(vFile);
        Uri photoURI = FileProvider.getUriForFile(mContext,
                mContext.getApplicationContext().getPackageName() +
                        ".provider", vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        startActivityForResult(openCameraIntent, Constants.TAKE_PICTURE);
    }

    private void removeImg(int location)
    {
        if (location + 1 <= images.size())
        {
            images.remove(location);
        }
    }


}
