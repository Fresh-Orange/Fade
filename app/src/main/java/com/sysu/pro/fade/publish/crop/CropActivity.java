package com.sysu.pro.fade.publish.crop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.publish.Event.CropToClickEvent;
import com.sysu.pro.fade.publish.Event.ImageSelectorToPublish;
import com.sysu.pro.fade.publish.crop.util.NoScrollView;
import com.sysu.pro.fade.publish.imageselector.ImageSelectorActivity;
import com.sysu.pro.fade.publish.imageselector.constant.Constants;
import com.sysu.pro.fade.publish.imageselector.utils.BitmapUtils;
import com.sysu.pro.fade.publish.imageselector.utils.ImageSelectorUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.sysu.pro.fade.publish.PublishActivity.imageX;
import static com.sysu.pro.fade.publish.PublishActivity.imageY;

public class CropActivity extends AppCompatActivity{

    private int newCount = 9;
    private int mMaxCount;
    private int cutSize;

    public static NoScrollView scrollView;
    private ArrayList<String> newimages;
    static NoScrollView outScroll;
    private BitmapScrollPicker mPickerHorizontal;
    private TextView publishTextView;
    public static int current_position = 0;
    private CropImageView cropImageView;
    public static float[] left = new float[10];
    public static float[] right = new float[10];
    public static float[] top = new float[10];
    public static float[] bottom = new float[10];
    public static boolean[] isSet = new boolean[10];

    public static int screenWidth;
    private boolean isOriginalHeight = true;
    private Bitmap[] bms = new Bitmap[10];


    static int Two_Div_One = 0;
    static int Three_Div_Four = 1;
    static int normalSize = 2;
    static int One_Div_One = 3;

    private ImageView addButton;
    private static float currentRatio;
    private static int currentWidth;
    private static int currentHeight;
    public static void openActivity(Activity activity, int requestCode,
                                    int maxSelectCount, ArrayList<String> images,
                                    int newCount) {
        boolean flag = false;
        if (determineSize(images.get(0)) == Two_Div_One)
            flag = true;
        else
            flag = false;
        Intent intent = new Intent(activity, CropActivity.class);
        intent.putStringArrayListExtra(ImageSelectorUtils.CROP_LAST, images);
        intent.putExtra(Constants.CROP_COUNT, newCount);
        intent.putExtra(Constants.FLAG,flag);
        activity.startActivityForResult(intent, requestCode);
    }

    public static int determineSize(String image) {
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
        currentWidth = (int) options.outWidth;
        currentHeight = (int) options.outHeight;
        currentRatio = (float)options.outWidth / (float)options.outHeight;
        if (currentRatio <= 2f && currentRatio >= 0.75f)
            return normalSize;
        else if (currentRatio > 2f)
            return Two_Div_One;
        else
            return Three_Div_Four;
    }

    // Activity Methods ////////////////////////////////////////////////////////////////////////////

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop);
        initLayout();
        initListener();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        // 初始化滑动
        mPickerHorizontal.setSelectedPosition(current_position);
        bms[current_position] = BitmapFactory.decodeFile(newimages.get(current_position));
        cropImageView.setImageBitmap(bms[current_position]);
        cropImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        showPics(current_position);
        Log.d("Yellow", "You On create");
        mPickerHorizontal.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {
                showPics(position);
            }
        });



    }

    private void showPics(int position) {
        //反正就是改变一下这个VIEW以便调用onLayout，都是泪。。
        ViewGroup.LayoutParams lp = cropImageView.getLayoutParams();
        if (isOriginalHeight) {
            lp.height += 0.1;
            isOriginalHeight = false;
        }
        else {
            lp.height -= 0.1;
            isOriginalHeight = true;
        }
        cropImageView.setLayoutParams(lp);
        current_position = position;
        for(int i = 0; i < bms.length; i++){
            //回收bitmap避免OOM
            if(bms[i] != null && !bms[i].isRecycled()){
                Log.e("recycle", "recycled" + i);
                bms[i].recycle();
            }
        }
        bms[current_position] = BitmapFactory.decodeFile(newimages.get(position));
        cropImageView.setImageBitmap(bms[current_position]);
    }

    private void initListener() {
        publishTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < newimages.size(); i++) {
                    Log.e("yellowsss",i + ": X: " + imageX[i] + " Y: " + imageY[i]);
                }
//                Intent intent = new Intent();
//                intent.putExtra(Constants.IMAGEX,imageX);
//                intent.putExtra(Constants.IMAGEY,imageY);
//                intent.putExtra(Constants.CUT_SIZE,cut_size);
//                intent.putExtra(Constants.IS_CROP, true);
//                setResult(Constants.CROP_RESULT_CODE, intent);
                EventBus.getDefault().post(new CropToClickEvent("Success!", current_position));
                finish();
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启相册选取活动
                Intent intent = new Intent(CropActivity.this, ImageSelectorActivity.class);
                intent.putExtra(Constants.MAX_SELECT_COUNT, 9);
                intent.putStringArrayListExtra(ImageSelectorUtils.SELECT_LAST, newimages);
                intent.putExtra(Constants.NEW_COUNT, newCount);
                startActivity(intent);
            }
        });
    }

    private void initLayout() {
        //注册
        EventBus.getDefault().register(this);
        publishTextView = (TextView) findViewById(R.id.tv_confirm);
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        outScroll = (NoScrollView) findViewById(R.id.scrollview);
        addButton = (ImageView) findViewById(R.id.picker_add);
        for (int i = 0; i < isSet.length; i++)
            isSet[i] = false;
        Intent intent = getIntent();
        //默认15:8
        current_position = intent.getIntExtra(Constants.CURRENT_CROP_POSITION, 0);
        newCount = intent.getIntExtra(Constants.CROP_COUNT, 9);
        newimages = new ArrayList<String>();
        if (intent.getStringArrayListExtra(ImageSelectorUtils.CROP_LAST) != null)
        {
            newimages = intent.getStringArrayListExtra(ImageSelectorUtils.CROP_LAST);
        }
        if (newimages.size() == 9)
            addButton.setVisibility(View.GONE);
        if (newimages.size() > 1) {
            //多于一张
            cropImageView.setAspectRatio(1, 1);
        }
        else {
            cutSize = determineSize(newimages.get(0));
            if (cutSize == normalSize) {
                cropImageView.setAspectRatio(currentWidth, currentHeight);
            }
            if (cutSize == Two_Div_One) {
                cropImageView.setAspectRatio(2, 1);
            }
            if (cutSize == Three_Div_Four) {
                cropImageView.setAspectRatio(3, 4);
            }
        }

        final CopyOnWriteArrayList<Bitmap> bitmaps = new CopyOnWriteArrayList<Bitmap>();
        for (int i = 0; i < newimages.size(); i++) {
            Bitmap newBp = BitmapUtils.decodeSampledBitmapFromFd(newimages.get(i), 100, 100);
            bitmaps.add(newBp);
        }
        mPickerHorizontal = (BitmapScrollPicker) findViewById(R.id.picker_04_horizontal);
        mPickerHorizontal.setData(bitmaps);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImageSelectorToPublish event) {
        newimages = event.getImages();
        newCount = event.getNewCount();
        current_position = newimages.size() - 1;
        Log.d("yellow", "current: " + current_position);
        updateView();
    }

    private void updateView() {
        if (newimages.size() == 9)
            addButton.setVisibility(View.GONE);
        final CopyOnWriteArrayList<Bitmap> bitmaps = new CopyOnWriteArrayList<Bitmap>();
        for (int i = 0; i < newimages.size(); i++) {
            Bitmap newBp = BitmapUtils.decodeSampledBitmapFromFd(newimages.get(i), 50, 50);
            bitmaps.add(newBp);
        }
        mPickerHorizontal.setData(bitmaps);

        // 初始化滑动
        mPickerHorizontal.setSelectedPosition(current_position);
        bms[current_position] = BitmapFactory.decodeFile(newimages.get(current_position));
        cropImageView.setImageBitmap(bms[current_position]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注册
        EventBus.getDefault().unregister(this);
    }
}
