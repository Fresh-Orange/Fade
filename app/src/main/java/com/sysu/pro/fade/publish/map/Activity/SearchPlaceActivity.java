package com.sysu.pro.fade.publish.map.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
    private List nearList = new ArrayList<PoiItem>();
    private PoiSearch poiSearch;
    private String city;
    public AMapLocationClientOption mLocationOption = null;
    private double latitude;
    private double longitude;

    private int currentPageNum = 1;
    private int currentPageSize = 20;
    private MyEditText editSearchKeyEt;

    private LocNearAddressAdapter adapter;
    //声明AMapLocationClientOption对象
    public AMapLocationClient mLocationClient = null;
    private RefreshLayout refreshLayout;

    private String keyword = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        initView();
        final Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude",0);
        longitude = intent.getDoubleExtra("longitude",0);
        city = intent.getStringExtra("city");
        editSearchKeyEt = findViewById(R.id.edit);
        rvNearBy = findViewById(R.id.rv_nearby);
        refreshLayout = findViewById(R.id.refreshLayout);

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
                findViewById(R.id.suggestion).setVisibility(View.GONE);
                currentPageNum = 1;
                nearList.clear();
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
                searchNearBy();
                refreshlayout.finishLoadmore();
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent();
                intent2.putExtra("finish", false);
                setResult(RESULT_OK,intent2);
                finish();
            }
        });
        findViewById(R.id.suggestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SearchToPublish(keyword));
                Intent intent4 = new Intent();
                intent4.putExtra("finish", true);
                setResult(RESULT_OK,intent4);
                finish();
            }
        });
        adapter.setOnItemClickListener(new LocNearAddressAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //获得实例
                PoiItem retail = (PoiItem) nearList.get(position);
                EventBus.getDefault().post(new SearchToPublish(retail.getTitle()));
                Intent intent3 = new Intent();
                intent3.putExtra("finish", true);
                setResult(RESULT_OK,intent3);
                finish();
            }
        });
    }

    private void initView() {
        rvNearBy = (RecyclerView) findViewById(R.id.rv_nearby);
        rvNearBy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocNearAddressAdapter(nearList, getApplicationContext());
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
                        Log.d("haa", "here" + p.getPoiExtension());
                        adapter.notifyDataSetChanged();
                    }
                }
                else {
                    ((TextView)findViewById(R.id.address)).setText(keyword);
                    findViewById(R.id.suggestion).setVisibility(View.VISIBLE);
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

