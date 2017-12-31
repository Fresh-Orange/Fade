package com.sysu.pro.fade.publish.crop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
import com.sysu.pro.fade.publish.crop.util.AspectRatioUtil;
import com.sysu.pro.fade.publish.crop.util.NoScrollView;
import com.sysu.pro.fade.publish.imageselector.constant.Constants;
import com.sysu.pro.fade.publish.imageselector.utils.BitmapUtils;
import com.sysu.pro.fade.publish.imageselector.utils.ImageSelectorUtils;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class CropActivity extends AppCompatActivity{

    private int newCount = 9;
    private int mMaxCount;

    public static NoScrollView scrollView;
    private ArrayList<String> newimages;
    static NoScrollView outScroll;
    private BitmapScrollPicker mPickerHorizontal;
    private TextView publishTextView;
    public static int current_position = 0;
    private CropImageView cropImageView;
    public static float[] imageX = new float[10];
    public static float[] imageY = new float[10];
    public static float[] left = new float[10];
    public static float[] right = new float[10];
    public static float[] top = new float[10];
    public static float[] bottom = new float[10];
    public static boolean[] isSet = new boolean[10];
    private int cut_size = 1;

    public static int screenWidth;
    private boolean isOriginalHeight = true;
    private Bitmap[] bms = new Bitmap[10];

    private boolean isFifteenToEight = false;

    static int FIFTEEN_DIV_EIGHT = 0;
    static int FOUR_DIV_FIVE = 1;
    public static void openActivity(Activity activity, int requestCode,
                                    int maxSelectCount, ArrayList<String> images,
                                    int newCount) {
        boolean flag = false;
        if (determineSize(images.get(0)) == FIFTEEN_DIV_EIGHT)
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
            return FIFTEEN_DIV_EIGHT;
        else
            return FOUR_DIV_FIVE;
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
        current_position = 0;
        bms[current_position] = BitmapFactory.decodeFile(newimages.get(0));
        cropImageView.setImageBitmap(bms[current_position]);
        cropImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        mPickerHorizontal.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {
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
        });

    }

    private void initListener() {
        publishTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < newimages.size(); i++) {
                    Log.e("yellowsss",i + ": X: " + imageX[i] + " Y: " + imageY[i]);
                }
                Intent intent = new Intent();
                intent.putExtra(Constants.IMAGEX,imageX);
                intent.putExtra(Constants.IMAGEY,imageY);
                intent.putExtra(Constants.CUT_SIZE,cut_size);
                intent.putExtra(Constants.IS_CROP, true);
                setResult(Constants.CROP_RESULT_CODE, intent);
                finish();
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.picker_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initLayout() {
        publishTextView = (TextView) findViewById(R.id.tv_confirm);
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        outScroll = (NoScrollView) findViewById(R.id.scrollview);
        for (int i = 0; i < isSet.length; i++)
            isSet[i] = false;
        Intent intent = getIntent();
        newCount = intent.getIntExtra(Constants.CROP_COUNT, 9);
        //默认15:8
        isFifteenToEight = intent.getBooleanExtra(Constants.FLAG,true);
        newimages = new ArrayList<String>();
        if (intent.getStringArrayListExtra(ImageSelectorUtils.CROP_LAST) != null)
        {
            newimages = intent.getStringArrayListExtra(ImageSelectorUtils.CROP_LAST);
        }
        if (isFifteenToEight) {
            cropImageView.setAspectRatio(15, 8);
            cut_size = 2;
        }
        else {
            cropImageView.setAspectRatio(4, 5);
            cut_size = 1;
        }
        final CopyOnWriteArrayList<Bitmap> bitmaps = new CopyOnWriteArrayList<Bitmap>();
        for (int i = 0; i < newimages.size(); i++) {
            Bitmap newBp = BitmapUtils.decodeSampledBitmapFromFd(newimages.get(i), 50, 50);
            bitmaps.add(newBp);
        }
        mPickerHorizontal = (BitmapScrollPicker) findViewById(R.id.picker_04_horizontal);
        mPickerHorizontal.setData(bitmaps);
    }


}
