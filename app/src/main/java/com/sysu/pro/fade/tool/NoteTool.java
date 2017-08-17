package com.sysu.pro.fade.tool;

import android.os.Handler;

import com.sysu.pro.fade.utils.Const;
import com.sysu.pro.fade.utils.HttpUtils;
import com.sysu.pro.fade.utils.JsonUtil;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by road on 2017/7/30.
 */
public class NoteTool {
    private NoteTool(){

    }

    public static void getBigSectionHome(final Handler handler, final String user_id, final String start){
        //首页大请求，最多获得20条帖子内容 和 180条note_id
        // 使用okhttp
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder().url(Const.NOTE_URL
                        +"?user_id="+user_id
                        +"&start="+start
                        +"&code=00");
                //设置参数
                Request request = builder.build();
                Call call = mokHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String ans_str = response.body().string();
                    //开始json解析
                        Map<String,Object>map = (Map<String, Object>) JsonUtil.jsonToMap(ans_str);
                        String ans = (String) map.get(Const.ANS);
                        List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                        System.out.println(result);
                        List<String>list = (List<String>) map.get(Const.LIST);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
    }

    public static void getSmallSectionHome(final Handler handler, final String bunch){
        //首页小请求，最多获得20条帖子内容
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder().url(Const.NOTE_URL
                        +"?bunch="+bunch
                        +"&code=01");
                //设置参数
                Request request = builder.build();
                Call call = mokHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String ans_str = response.body().string();
                    //开始json解析
                    Map<String,Object>map = (Map<String, Object>) JsonUtil.jsonToMap(ans_str);
                    String ans = (String) map.get(Const.ANS);
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
    }

    public static void getSectionDiscoverRecommond(final  Handler handler, final String start){
        //发现推荐版块，20条一次 加载数据的请求：
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder().url(Const.NOTE_URL
                        +"?start="+start
                        +"&code=05");
                //设置参数
                Request request = builder.build();
                Call call = mokHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String ans_str = response.body().string();
                    //开始json解析
                    Map<String,Object>map = (Map<String, Object>) JsonUtil.jsonToMap(ans_str);
                    String ans = (String) map.get(Const.ANS);
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                    System.out.println(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
    }

    public static void addNote(final Handler handler, final String user_id, final String nickname, final String head_image_url,
                               final String note_content, final String isRelay, final String tag_list){
        //发帖请求 原创贴的话isRelay是0，否则是原贴note_id
        new Thread(){
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair(Const.USER_ID, user_id));
                list.add(new BasicNameValuePair(Const.NICKNAME, nickname));
                list.add(new BasicNameValuePair(Const.HEAD_IMAGE_URL, head_image_url));
                list.add(new BasicNameValuePair(Const.NOTE_CONTENT, note_content));
                list.add(new BasicNameValuePair(Const.ISRELAY, String.valueOf(isRelay)));
                list.add(new BasicNameValuePair(Const.TAG_LIST, tag_list));
                if (isRelay.equals("0")) list.add(new BasicNameValuePair(Const.CODE, "03"));
                else list.add(new BasicNameValuePair(Const.CODE, "04"));

                String ans_str = HttpUtils.getRequest(Const.NOTE_URL, list);
                System.out.println(ans_str);
                Map<String,Object>map = (Map<String, Object>) JsonUtil.jsonToMap(ans_str);
                String ans = (String) map.get(Const.ANS);
                String note_id = (String) map.get(Const.NOTE_ID);
            }
        }.start();
    }

    public static void addSecond(final String user_id,final String note_id, final String isRelay){
        //续一秒请求
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair>list = new ArrayList<>();
                list.add(new BasicNameValuePair(Const.USER_ID,user_id));
                list.add(new BasicNameValuePair(Const.NOTE_ID,note_id));
                list.add(new BasicNameValuePair(Const.ISRELAY,isRelay));
                list.add(new BasicNameValuePair(Const.CODE,"07"));

                String ans_str = HttpUtils.getRequest(Const.NOTE_URL,list);
                Map<String,Object>map = (Map<String, Object>) JsonUtil.jsonToMap(ans_str);
                String ans = (String) map.get(Const.ANS);
                String good_num = (String) map.get(Const.GOOD_NUM);
                super.run();
            }
        }.start();

    }

    public static void getLatestThreeNumAndSummary(final Handler handler,final String note_id,final String user_id){
        //打开详情页，更新三大数量以及得到用户签名
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<BasicNameValuePair>list = new ArrayList<>();
                list.add(new BasicNameValuePair(Const.NOTE_ID,note_id));
                list.add(new BasicNameValuePair(Const.USER_ID,user_id));
                list.add(new BasicNameValuePair(Const.CODE,"09"));

                String ans_str = HttpUtils.getRequest(Const.NOTE_URL,list);
                Map<String,Object>map = (Map<String, Object>) JsonUtil.jsonToMap(ans_str);
            }
        }.start();
    }

    public static void getTwentyRelay(final Handler handler, final String note_id,final String start){
        //打开详情页，在转发列表一次获取20条转发
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<BasicNameValuePair>list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair(Const.NOTE_ID,note_id));
                list.add(new BasicNameValuePair(Const.START,start));
                list.add(new BasicNameValuePair(Const.CODE,"10"));
                String ans_str = HttpUtils.getRequest(Const.NOTE_URL,list);
                Map<String,Object>map = (Map<String, Object>) JsonUtil.jsonToMap(ans_str);
                String ans = (String) map.get(Const.ANS);
                List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                System.out.println(result);
            }
        }.start();
    }

    public static void getTwentyGood(final Handler handler, final String note_id,final String start){
        //打开详情页，在转发列表一次获取20条点赞信息
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<BasicNameValuePair>list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair(Const.NOTE_ID,note_id));
                list.add(new BasicNameValuePair(Const.START,start));
                list.add(new BasicNameValuePair(Const.CODE,"11"));
                String ans_str = HttpUtils.getRequest(Const.NOTE_URL,list);
                Map<String,Object>map = (Map<String, Object>) JsonUtil.jsonToMap(ans_str);
                String ans = (String) map.get(Const.ANS);
                List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                System.out.println(result);
            }
        }.start();
    }

}
