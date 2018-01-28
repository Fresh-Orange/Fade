package com.sysu.pro.fade.publish.map.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.sysu.pro.fade.R;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.publish.Event.CropToClickEvent;
import com.sysu.pro.fade.publish.Event.MapToPublish;
import com.sysu.pro.fade.publish.PublishActivity;
import com.sysu.pro.fade.publish.map.Adapter.LocNearAddressAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity {

    private RecyclerView lvLocMain;
    private List nearList = new ArrayList<PoiItem>();
    private PoiSearch poiSearch;
    private static String city;
    private String address;
    private static double latitude;
    private static double longitude;

    private int currentPageNum = 1;
    private int currentPageSize = 20;
    private EditText editSearchKeyEt;

    private LocNearAddressAdapter adapter;

    private RefreshLayout refreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);
        initView();
        searchNearBy();
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
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, SearchPlaceActivity.class);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("city", city);
                startActivityForResult(intent, 500);
            }
        });
        adapter.setOnItemClickListener(new LocNearAddressAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //获得实例
                PoiItem retail = (PoiItem) nearList.get(position);
                address = retail.getTitle();
                EventBus.getDefault().post(new MapToPublish(address));
                Log.d("yellow", "mapAddress: " + address);
                finish();
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public void finish() {
        super.finish();
    }

    private void initView() {
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude",0);
        longitude = intent.getDoubleExtra("longitude",0);
        city = intent.getStringExtra("city");
        lvLocMain = (RecyclerView) findViewById(R.id.lv_location_nearby);
        lvLocMain.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocNearAddressAdapter(nearList, getApplicationContext());
//        adapter = new MainAddressAdapter(poiList, getApplicationContext());
        lvLocMain.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 500 && resultCode == RESULT_OK) {
            if (data.getBooleanExtra("finish", true))
                finish();
        }
    }


    /**
     * 搜索周边地理位置
     * by hankkin at:2015-11-01 22:54:49
     */
    private void searchNearBy() {
        String deepType = "餐饮服务|商务住宅|生活服务|风景名胜|购物服务|科教文化服务|公司企业|" +
                "政府机构及社会团体|体育休闲服务|汽车销售|汽车维修|汽车服务";
        PoiSearch.Query query = new PoiSearch.Query(""
                , deepType, city);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(currentPageSize);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPageNum);
        Log.d("pageNum", "currentPageSize: " +currentPageSize);
        Log.d("pageNum", "currentPageNum: " +currentPageNum);
//        currentPageSize += 20;
        currentPageNum += 1;
        poiSearch = new PoiSearch(MapActivity.this, query);
        poiSearch.setOnPoiSearchListener(poiSearchListener);
        poiSearch.searchPOIAsyn();
        //设置周边搜索的中心点以及半径
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude,longitude), 500));
    }



    PoiSearch.OnPoiSearchListener poiSearchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult result, int rCode) {
            if (rCode == 1000) {
                if (result.getPois() != null) {
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
                    List<String> suggestion = result.getSearchSuggestionKeywords();
                    Log.d("suggestion", suggestion.toString());
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

    //    /**
//     * 接受周边地理位置结果
//     * by hankkin at:2015-11-02 17:14:54
//     *
//     * @param poiResult
//     */
//    @Override
//    public void onGetPoiResult(PoiResult poiResult) {
//
//    }

//    private class MyLocationListener implements BDLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation bdLocation) {
//
//        }
//    }
}

