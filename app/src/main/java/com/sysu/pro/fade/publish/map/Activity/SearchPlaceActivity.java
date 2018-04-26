package com.sysu.pro.fade.publish.map.Activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.publish.Event.MapToPublish;
import com.sysu.pro.fade.publish.Event.SearchToPublish;
import com.sysu.pro.fade.publish.PublishActivity;
import com.sysu.pro.fade.publish.map.Adapter.LocNearAddressAdapter;
import com.sysu.pro.fade.publish.map.MyEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class SearchPlaceActivity extends AppCompatActivity {

    private RecyclerView rvNearBy;
    private RecyclerView.LayoutManager mLayoutManager;
    private List nearList = new ArrayList<PoiItem>();
    private PoiSearch poiSearch;
    private String city;
    private double latitude;
    private double longitude;

    private int currentPageNum = 1;
    private int currentPageSize = 20;
    private MyEditText editSearchKeyEt;

    private LocNearAddressAdapter adapter;
    //声明AMapLocationClientOption对象
    private RefreshLayout refreshLayout;

    private String keyword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        initView();
        final Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        city = intent.getStringExtra("city");
        editSearchKeyEt = findViewById(R.id.edit);
        rvNearBy = findViewById(R.id.rv_nearby);
        refreshLayout = findViewById(R.id.refreshLayout);

//        suggestion_address = ((TextView)findViewById(R.id.suggestion_address));
//        suggestion = findViewById(R.id.suggestion);
//        editSearchKeyEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                return false;
//            }
//        });
        editSearchKeyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                keyword = editable.toString();
                Log.d("keyword", "keyword: " + keyword);
                currentPageNum = 1;
                nearList.clear();
                adapter.setKeyword(keyword);
                adapter.notifyDataSetChanged();
                if (!keyword.isEmpty()) {
                    Log.d("keyword", "notEmpty");
                    searchNearBy();
                }
            }
        });
        //设置底部加载刷新
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);    //取消下拉刷新功能
        refreshLayout.setEnableAutoLoadmore(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore();
                searchNearBy();
//                findViewById(R.id.suggestion).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent();
                intent2.putExtra("finish", false);
                setResult(RESULT_OK, intent2);
                finish();
            }
        });
//        suggestion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("yellow", "suggestion clicked!");
//                EventBus.getDefault().post(new SearchToPublish(keyword));
//                Intent intent4 = new Intent();
//                intent4.putExtra("finish", true);
//                setResult(RESULT_OK, intent4);
//                finish();
//            }
//        });
        rvNearBy.getFocusedChild();
        adapter.setOnItemClickListener(new LocNearAddressAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("adapter", "position: " + position);
                if (position < nearList.size()) {
                    //获得实例
                    PoiItem retail = (PoiItem) nearList.get(position);
                    EventBus.getDefault().post(new SearchToPublish(retail.getTitle()));
                    Intent intent3 = new Intent();
                    intent3.putExtra("finish", true);
                    setResult(RESULT_OK, intent3);
                    finish();
                }
                else {
                    EventBus.getDefault().post(new SearchToPublish(keyword));
                    Intent intent4 = new Intent();
                    intent4.putExtra("finish", true);
                    setResult(RESULT_OK, intent4);
                    finish();
                }
            }
        });
//        rvNearBy.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (!recyclerView.canScrollVertically(-1)) {
//                    onScrolledToTop();
//                } else if (!recyclerView.canScrollVertically(1)) {
//                    onScrolledToBottom();
//                } else if (dy < 0) {
//                    onScrolledUp();
//                } else if (dy > 0) {
//                    onScrolledDown();
//                }
//            }
//
//            public void onScrolledUp() {
//                if (isShowSuggestion) {
//                    hideSuggestion();
//                    isShowSuggestion = false;
//                }
//            }
//
//            public void onScrolledDown() {
//                if (currentPageNum > 2) {
//                    if (!isShowSuggestion) {
//                        showSuggestion();
//                        isShowSuggestion = true;
//                    }
//
//                }
//            }
//
//            public void onScrolledToTop() {
//
//            }
//
//            public void onScrolledToBottom() {
////                Log.d("Bottom", "size: " + nearList.size());
////                if (!isShowSuggestion && nearList.size() > 0) {
////                    TextView bottom = findViewById(R.id.suggestion_address_bottom);
////                    bottom.setText(keyword);
////                    findViewById(R.id.suggestion_bottom).setVisibility(View.VISIBLE);
////                }
//            }
//        });
    }



//    /**
//     * 显示时间条
//     */
//    private void showSuggestion() {
//        suggestion_address.setText(keyword);
//        ObjectAnimator.ofFloat(suggestion, "alpha", 0, 1).setDuration(50).start();
//        isShowSuggestion = true;
//        suggestion.setVisibility(View.VISIBLE);
//    }
//
//    /**
//     * 隐藏时间条
//     */
//    private void hideSuggestion() {
//        ObjectAnimator.ofFloat(suggestion, "alpha", 1, 0).setDuration(50).start();
//        suggestion.setVisibility(View.GONE);
//    }

    private void initView() {
        rvNearBy = (RecyclerView) findViewById(R.id.rv_nearby);
        mLayoutManager = new LinearLayoutManager(this);
        rvNearBy.setLayoutManager(mLayoutManager);
        adapter = new LocNearAddressAdapter(nearList, getApplicationContext(), keyword);
//        adapter = new MainAddressAdapter(poiList, getApplicationContext());
        rvNearBy.setAdapter(adapter);
    }
    /**
     * 搜索周边地理位置
     * by hankkin at:2015-11-01 22:54:49
     */
    private void searchNearBy() {
        String deepType = "餐饮服务|商务住宅|生活服务|风景名胜|购物服务|科教文化服务|公司企业|" +
                "政府机构及社会团体|体育休闲服务|汽车销售|汽车维修|汽车服务";
        PoiSearch.Query query = new PoiSearch.Query(keyword
                , "", city);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(currentPageSize);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPageNum);
        Log.d("pageNum", "currentPageSize: " +currentPageSize);
        Log.d("pageNum", "currentPageNum: " +currentPageNum);
//        currentPageSize += 20;
        currentPageNum += 1;
        poiSearch = new PoiSearch(SearchPlaceActivity.this, query);
        poiSearch.setOnPoiSearchListener(poiSearchListener);
        poiSearch.searchPOIAsyn();
        //设置周边搜索的中心点以及半径
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude,longitude), 10000));
    }

    PoiSearch.OnPoiSearchListener poiSearchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult result, int rCode) {
            if (rCode == 1000) {
                if (!result.getPois().isEmpty()) {
                    List<PoiItem> itemList = result.getPois();
//                        nearList.clear();
//                        nearList.addAll(itemList);
//                        adapter.notifyDataSetChanged();
                    for (PoiItem p : itemList) {
                        nearList.add(p);
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
//                    if (!keyword.isEmpty())
//                        showSuggestion();
//                    List<String> suggestion = result.getSearchSuggestionKeywords();
//                    Log.d("suggestion", suggestion.toString());
                }
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

