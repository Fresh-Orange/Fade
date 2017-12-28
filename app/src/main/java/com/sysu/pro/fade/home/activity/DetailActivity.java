package com.sysu.pro.fade.home.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.home.adapter.DetailFragmentAdapter;
import com.sysu.pro.fade.home.fragment.DetailPageFragment;
import com.sysu.pro.fade.home.view.ObservableScrollView;

public class DetailActivity extends AppCompatActivity implements ObservableScrollView.OnObservableScrollViewScrollChanged{

    //详情页上下滑动
    private ObservableScrollView sv_contentView;
    private LinearLayout ll_topView; //包住悬浮布局的
   // private TextView tv_topView;
    private LinearLayout ll_tab;  //悬浮的布局
    private LinearLayout ll_fixedView;
    //用来记录内层固定布局到屏幕顶部的距离
    private int mHeight;
    //配置viewpager相关
    public Integer note_id;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //初始化note_id
        note_id = getIntent().getIntExtra(Const.NOTE_ID,0);
        sv_contentView= (ObservableScrollView) findViewById(R.id.sv_contentView);
        ll_topView= (LinearLayout) findViewById(R.id.ll_topView);
        ll_tab = (LinearLayout) findViewById(R.id.ll_tab);
        ll_fixedView= (LinearLayout) findViewById(R.id.ll_fixedView);
        ll_fixedView.setVisibility(View.INVISIBLE);

        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Fragment+ViewPager+FragmentViewPager组合的使用
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_detail);
        DetailFragmentAdapter adapter = new DetailFragmentAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(adapter);

        //TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_detail);
        tabLayout.setupWithViewPager(viewPager);

        sv_contentView.setOnObservableScrollViewScrollChanged(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //返回监听
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        if (t >= mHeight) {
            if (ll_tab.getParent() != ll_fixedView) {
                ll_topView.removeView(ll_tab);
                ll_fixedView.addView(ll_tab);
                ll_fixedView.setVisibility(View.VISIBLE);
                ll_fixedView.setAlpha(1.0f);
            }
        } else {
            if (ll_tab.getParent() != ll_topView) {
                ll_fixedView.removeView(ll_tab);
                ll_fixedView.setVisibility(View.INVISIBLE);
                ll_fixedView.setAlpha(0.0f);
                ll_topView.addView(ll_tab);
            }
        }
    }

}