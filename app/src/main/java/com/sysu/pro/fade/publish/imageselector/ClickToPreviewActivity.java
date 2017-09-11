package com.sysu.pro.fade.publish.imageselector;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.publish.PublishActivity;
import com.sysu.pro.fade.publish.imageselector.adapter.ImagePagerAdapter;
import com.sysu.pro.fade.publish.imageselector.constant.Constants;
import com.sysu.pro.fade.publish.imageselector.entry.Image;
import com.sysu.pro.fade.publish.imageselector.view.MyViewPager;

import java.util.ArrayList;

import static android.animation.ObjectAnimator.ofFloat;

public class ClickToPreviewActivity extends Activity {

    private MyViewPager vpImage;    //ViewPager容器，用于显示侧滑效果
    private TextView tvIndicator;   //当前位置/总数量，位于左上角
//    private TextView tvConfirm;     //(X/9)，位于右上角
//    private FrameLayout btnConfirm; //确定，如果选择图片为0则会变暗
//    private TextView tvSelect;      //右下角，是否选择了该图片
    private RelativeLayout rlTopBar;    //上面的状态栏
//    private RelativeLayout rlBottomBar; //下面的状态栏

    //tempImages和tempSelectImages用于图片列表数据的页面传输。
    //之所以不要Intent传输这两个图片列表，因为要保证两位页面操作的是同一个列表数据，同时可以避免数据量大时，
    // 用Intent传输发生的错误问题。
    private static ArrayList<Image> tempImages;
//    private static ArrayList<String> tempPath;
//    private static ArrayList<Image> tempSelectImages;

    private ArrayList<Image> mImages;   //已经选中的图片
//    private ArrayList<String> mPath;    //图片路径
//    private ArrayList<Image> mSelectImages; //待选图片
    private boolean isShowBar = true;
    private boolean isDeleted = false;
    private boolean isAllDeleted = false;
    //    private boolean isSingle;
    private int mMaxCount;
    private int deleteCount;
    private int currentPosition;
    private int[] clickPosition = new int[15];
    private int clickPositionSize = 0;

    private BitmapDrawable mSelectDrawable;
    private BitmapDrawable mUnSelectDrawable;

    private ImagePagerAdapter adapter;
    public static void openActivity(Activity activity, ArrayList<String> paths,
                                    int maxSelectCount, int position) {
        ArrayList<Image> images = new ArrayList<Image>();
        if (paths.size() > 0) {
            for (String path : paths) {
                if (path != null) {
                    Image image = new Image(path, 0, " ");
                    images.add(image);
//                    tempPath.add(path);
                }
            }
        }
        tempImages = images;
//        tempSelectImages = selectImages;
        Intent intent = new Intent(activity, ClickToPreviewActivity.class);
        intent.putExtra(Constants.MAX_SELECT_COUNT, maxSelectCount);
//        intent.putExtra(Constants.IS_SINGLE, isSingle);
        intent.putExtra(Constants.CLICK_POSITION, position);
        activity.startActivityForResult(intent, Constants.CLICK_RESULT_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_preview);
        setStatusBarVisible(true);
        mImages = tempImages;
        tempImages = null;
//        mPath = tempPath;
//        tempPath = null;
//        mSelectImages = tempSelectImages;
//        tempSelectImages = null;

        Intent intent = getIntent();
        mMaxCount = intent.getIntExtra(Constants.MAX_SELECT_COUNT, 0);
        currentPosition = intent.getIntExtra(Constants.CLICK_POSITION, 0);
//        isSingle = intent.getBooleanExtra(Constants.IS_SINGLE, false);
        deleteCount = 0;
        Resources resources = getResources();
        Bitmap selectBitmap = BitmapFactory.decodeResource(resources, com.sysu.pro.fade.R.drawable.icon_image_select);
        mSelectDrawable = new BitmapDrawable(resources, selectBitmap);
        mSelectDrawable.setBounds(0, 0, selectBitmap.getWidth(), selectBitmap.getHeight());

        Bitmap unSelectBitmap = BitmapFactory.decodeResource(resources, com.sysu.pro.fade.R.drawable.icon_image_un_select);
        mUnSelectDrawable = new BitmapDrawable(resources, unSelectBitmap);
        mUnSelectDrawable.setBounds(0, 0, unSelectBitmap.getWidth(), unSelectBitmap.getHeight());

        setStatusBarColor();
        initView();
        initListener();
        initViewPager();

        tvIndicator.setText(1 + "/" + mImages.size());
//        changeSelect(mImages.get(0));
        vpImage.setCurrentItem(intent.getIntExtra(Constants.CLICK_POSITION, 0));
    }

    private void initView() {
        vpImage = (MyViewPager) findViewById(com.sysu.pro.fade.R.id.vp_image);
        tvIndicator = (TextView) findViewById(com.sysu.pro.fade.R.id.tv_indicator);
//        tvConfirm = (TextView) findViewById(com.sysu.pro.fade.R.id.tv_confirm);
//        btnConfirm = (FrameLayout) findViewById(com.sysu.pro.fade.R.id.btn_confirm);
//        tvSelect = (TextView) findViewById(com.sysu.pro.fade.R.id.tv_select);
        rlTopBar = (RelativeLayout) findViewById(com.sysu.pro.fade.R.id.rl_top_bar);
//        rlBottomBar = (RelativeLayout) findViewById(com.sysu.pro.fade.R.id.rl_bottom_bar);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlTopBar.getLayoutParams();
        lp.topMargin = getStatusBarHeight(this);
        rlTopBar.setLayoutParams(lp);
    }

    private void initListener() {
        //返回
        findViewById(com.sysu.pro.fade.R.id.btn_back).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //右上角的选择
//        btnConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isConfirm = true;
//                finish();
//            }
//        });
        //右下角的选择
//        if (mMaxCount > 0)
//            tvSelect.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clickSelect();
//                }
//            });
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        //作用是显示滑动切换效果
        //选择mImages放到vpImage里面

        //删除
        findViewById(R.id.photo_bt_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ClickToPreviewActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("确定要删除这张照片吗?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mImages.size() <= 1) {
                            isAllDeleted = true;
                            mImages.clear();
                            finish();
                        }
                        else {
                            isDeleted = true;
                            deleteCount++;
                            removeImg(currentPosition);
                            clickPosition[clickPositionSize++] = currentPosition;
                            adapter.removeView(currentPosition);
                            tvIndicator.setText(currentPosition + 1 + "/" + mImages.size());
                            adapter.notifyDataSetChanged();
                        }
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
        adapter = new ImagePagerAdapter(this, mImages);
        vpImage.setAdapter(adapter);
        adapter.setOnItemClickListener(new ImagePagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Image image) {
                if (isShowBar) {
                    hideBar();
                } else {
                    showBar();
                }
            }
        });
        vpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //设置左上角
                tvIndicator.setText(position + 1 + "/" + mImages.size());
                currentPosition = position;
//                changeSelect(mImages.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });



    }

    /**
     * 修改状态栏颜色
     */
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#373c3d"));
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 显示和隐藏状态栏
     * @param show
     */
    private void setStatusBarVisible(boolean show) {
        if (show) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    /**
     * 显示头部和尾部栏
     */
    private void showBar() {
        isShowBar = true;
        setStatusBarVisible(true);
        //添加延时，保证StatusBar完全显示后再进行动画。
        rlTopBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ofFloat(rlTopBar, "translationY",
                        rlTopBar.getTranslationY(), 0).setDuration(300);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        rlTopBar.setVisibility(View.VISIBLE);
                    }
                });
                animator.start();
//                ofFloat(rlBottomBar, "translationY", rlBottomBar.getTranslationY(), 0)
//                        .setDuration(300).start();
            }
        }, 100);
    }

    /**
     * 隐藏头部和尾部栏
     */
    private void hideBar() {
        isShowBar = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(rlTopBar, "translationY",
                0, -rlTopBar.getHeight()).setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rlTopBar.setVisibility(View.GONE);
                //添加延时，保证rlTopBar完全隐藏后再隐藏StatusBar。
                rlTopBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setStatusBarVisible(false);
                    }
                }, 5);
            }
        });
        animator.start();
    }


    private void removeImg(int location)
    {
        if (location + 1 <= mImages.size())
        {
            mImages.remove(location);
        }
    }
    public void finish() {
        //Activity关闭时，通过Intent把用户的操作(确定/返回)传给ImageSelectActivity。
        Intent intent = new Intent(ClickToPreviewActivity.this, PublishActivity.class);
        intent.putExtra(Constants.IS_DELETED, isDeleted);
        intent.putExtra(Constants.IS_ALL_DELETED, isAllDeleted);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.CURRENT_POSITION, clickPosition);
        intent.putExtras(bundle);
        intent.putExtra(Constants.CURRENT_POSITION_SIZE, clickPositionSize);
        setResult(RESULT_OK,intent);
        super.finish();
    }
}
