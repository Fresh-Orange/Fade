package com.sysu.pro.fade.home.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.home.view.ObservableScrollView;

public class DetailActivity extends AppCompatActivity implements ObservableScrollView.OnObservableScrollViewScrollChanged{

    //详情页上下滑动
    private ObservableScrollView sv_contentView;
    private LinearLayout ll_topView;
    private LinearLayout ll_nav;
    private LinearLayout ll_fixedView;
    //用来记录内层固定布局到屏幕顶部的距离
    private int mHeight;
    //配置viewpager相关

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        sv_contentView = (ObservableScrollView) findViewById(R.id.sv_contentView);
        ll_topView = (LinearLayout) findViewById(R.id.ll_topView);
        ll_nav = (LinearLayout) findViewById(R.id.ll_nav);
        ll_fixedView = (LinearLayout) findViewById(R.id.ll_fixedView);

        sv_contentView.setOnObservableScrollViewScrollChanged(this);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            //获取HeaderView的高度，当滑动大于等于这个高度的时候，需要把topView移除当前布局，放入到外层布局
            mHeight=ll_topView.getTop();
        }
    }

    /**
     * @param l Current horizontal scroll origin. 当前滑动的x轴距离
     * @param t Current vertical scroll origin. 当前滑动的y轴距离
     * @param oldl Previous horizontal scroll origin. 上一次滑动的x轴距离
     * @param oldt Previous vertical scroll origin. 上一次滑动的y轴距离
     */
    @Override
    public void onObservableScrollViewScrollChanged(int l, int t, int oldl, int oldt) {
        if(t>=mHeight){
            if(ll_nav.getParent()!=ll_fixedView){
                ll_topView.removeView(ll_nav);
                ll_fixedView.addView(ll_nav);
            }
        }else{
            if(ll_nav.getParent()!=ll_topView){
                ll_fixedView.removeView(ll_nav);
                ll_topView.addView(ll_nav);
            }
        }
    }
}