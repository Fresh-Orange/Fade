package com.sysu.pro.fade.publish.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.publish.imageselector.constant.Constants;
import com.sysu.pro.fade.publish.imageselector.utils.BitmapUtils;
import com.sysu.pro.fade.publish.imageselector.utils.ImageSelectorUtils;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class CropActivity extends AppCompatActivity {

    private int newCount = 9;
    private int mMaxCount;

    private ArrayList<String> newimages;
    private BitmapScrollPicker mPickerHorizontal;
    public static int current_position = 0;
    private CropImageView cropImageView;
    public static float[] imageX = new float[10];
    public static float[] imageY = new float[10];
    private int cut_size = 1;

    private boolean flag = false;
    public CropActivity() {
    }

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

    private static int determineSize(String image) {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop);
        Intent intent = getIntent();
        newCount = intent.getIntExtra(Constants.CROP_COUNT, 9);
        flag = intent.getBooleanExtra(Constants.FLAG,true);
        newimages = new ArrayList<String>();
        if (intent.getStringArrayListExtra(ImageSelectorUtils.CROP_LAST) != null)
        {
            newimages = intent.getStringArrayListExtra(ImageSelectorUtils.CROP_LAST);
        }
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        if (flag) {
            cropImageView.setAspectRatio(15, 8);
            cut_size = 2;
        }
        else {
            cropImageView.setAspectRatio(4, 5);
            cut_size = 1;
        }
        final TextView publishTextView = (TextView) findViewById(R.id.tv_confirm);
        publishTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < newimages.size(); i++) {
                    Log.d("yellowsss",i + ": X: " + imageX[i] + " Y: " + imageY[i]);
                }
                Intent intent = new Intent();
                intent.putExtra(Constants.IMAGEX,imageX);
                intent.putExtra(Constants.IMAGEY,imageY);
                intent.putExtra(Constants.CUT_SIZE,cut_size);
                intent.putExtra(Constants.IS_CROP, true);
                setResult(Constants.CROP_RESULT_CODE, intent);
                finish();
//                Toast.makeText(CropActivity.this,
//                        "X: " + imageX[0] + "Y: " + imageY[0],Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final CopyOnWriteArrayList<Bitmap> bitmaps = new CopyOnWriteArrayList<Bitmap>();
        for (int i = 0; i < newimages.size(); i++) {
            Bitmap newBp = BitmapUtils.decodeSampledBitmapFromFd(newimages.get(i), 50, 50);
            bitmaps.add(newBp);
        }

        mPickerHorizontal = (BitmapScrollPicker) findViewById(R.id.picker_04_horizontal);
        mPickerHorizontal.setData(bitmaps);

        initImage();
        cropImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Bitmap bm = BitmapFactory.decodeFile(newimages.get(0));
        cropImageView.setImageBitmap(bm);
        current_position = 0;
        mPickerHorizontal.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {
                Bitmap bm = BitmapFactory.decodeFile(newimages.get(position));
                cropImageView.setImageBitmap(bm);

                current_position = position;
            }
        });

    }

    private void initImage() {
        for (int index = 0; index < newimages.size(); index++) {
//            String image = newimages.get(index);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            /**
//             * 最关键在此，把options.inJustDecodeBounds = true;
//             * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
//             */
//            Bitmap bitmap = BitmapFactory.decodeFile(image, options); // 此时返回的bitmap为null
//            /**
//             *options.outHeight为原始图片的高
//             */
//            float currentRatio = (float)options.outHeight / (float)options.outWidth;
//            if (currentRatio > 1.3375f) {
//                imageX[index] = (float)options.outWidth / 2;
//                imageY[index] = 0;
//            }
//            else {
//                imageX[index] = 0;
//                imageY[index] = (float) options.outHeight / 2;
//            }
            imageX[index] = 0;
            imageY[index] = 0;
        }
    }


}
