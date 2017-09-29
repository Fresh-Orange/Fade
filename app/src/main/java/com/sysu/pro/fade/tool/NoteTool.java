package com.sysu.pro.fade.tool;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.utils.HttpUtils;
import com.sysu.pro.fade.utils.GsonUtil;

import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by road on 2017/7/30.
 */
public class NoteTool {

    //帖子相关的请求类
    public static void getBigSectionHome(final Handler handler, final String user_id, final String start){
        //首页大请求，最多获得20条帖子内容 和 180条note_id
        // 使用okhttp
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder().url(Const.IP
                        +"/note?user_id="+user_id
                        +"&start="+start
                        +"&code=00");
                //设置参数
                Request request = builder.build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    Map<String,Object>map = null;
                    @Override
                    public void onFailure(Call call, IOException e) {
                        map = new HashMap<String, Object>();
                        map.put(Const.ERR,"请求异常，请检查你的网络");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        try{
                            map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                        }catch (Exception e){
                            map = new HashMap<String, Object>();
                            map.put(Const.ERR,"服务器返回异常" + e.getMessage().toString());
                        }
                        Message msg = new Message();
                        msg.what = 0x1;
                        msg.obj = map;
                        handler.sendMessage(msg);
                    }
                });
                super.run();
            }
        }.start();
    }

    public static void getSmallSectionHome(final Handler handler, final String user_id, final String bunch){
        //首页小请求，最多获得20条帖子内容
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder().url(Const.IP
                        +"/note?bunch="+bunch
                        +"&code=01"
                        +"&user_id="+user_id);
                //设置参数
                Request request = builder.build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    Map<String,Object>map = null;
                    @Override
                    public void onFailure(Call call, IOException e) {
                        map = new HashMap<String, Object>();
                        map.put(Const.ERR,"请求异常，请检查你的网络");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        try{
                            map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                        }catch (Exception e){
                            map = new HashMap<String, Object>();
                            map.put(Const.ERR,"服务器返回异常" + e.getMessage().toString());
                        }

                        Message msg = new Message();
                        msg.what = 0x2;
                        msg.obj = map;
                        handler.sendMessage(msg);
                    }
                });
                super.run();
            }
        }.start();
    }

    public static void getSectionDiscoverRecommond(final  Handler handler, final String user_id,final String start){
        //发现推荐版块，20条一次 加载数据的请求：
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder().url(Const.IP
                        +"/note?start="+start
                        +"&code=05"
                        +"user_id="+user_id);
                //设置参数
                Request request = builder.build();
                //发现版块以后写
                super.run();
            }
        }.start();
    }

    public static void addNote(final Handler handler, final Integer user_id, final String nickname, final String head_image_url,
                               final String note_content, final Integer isRelay, final String tag_list){
        //发帖请求 原创贴的话isRelay是0，否则是原贴note_id
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/note?user_id=").append(user_id.toString())
                        .append("&nickname=").append(nickname)
                        .append("&head_image_url=").append(head_image_url)
                        .append("&note_content=").append(note_content)
                        .append("&isRelay=").append(isRelay.toString())
                        .append("&tag_list=").append(tag_list)
                        .append("&code=");
                if (isRelay == 0) sb.append("03");
                else sb.append("04");

                Request.Builder builder = new Request.Builder().url(sb.toString());
                Request request = builder.build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    Map<String,Object>map = null;
                    @Override
                    public void onFailure(Call call, IOException e) {
                        map = new HashMap<String, Object>();
                        map.put(Const.ERR,"请求异常，请检查你的网络");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        try{
                            map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                        }catch (Exception e){
                            map = new HashMap<String, Object>();
                            map.put(Const.ERR,"服务器返回异常" + e.getMessage().toString());
                        }

                        Message msg = new Message();
                        msg.what = 0x1;
                        msg.obj = map;
                        handler.sendMessage(msg);
                    }
                });
            }
        }.start();
    }

    public static void addSecond(final Handler handler,final String user_id,final String note_id
            , final String isRelay, final int position){
        //续一秒请求
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/note?user_id=").append(user_id.toString())
                        .append("&note_id=").append(note_id.toString())
                        .append("&isRelay=").append(isRelay.toString())
                        .append("&code=07");
                Request.Builder builder = new Request.Builder().url(sb.toString());
                Request request = builder.build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    Map<String,Object>map = null;
                    @Override
                    public void onFailure(Call call, IOException e) {
                        map = new HashMap<String, Object>();
                        map.put(Const.ERR,"请求异常，请检查你的网络");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        try{
                            map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                        }catch (Exception e){
                            map = new HashMap<String, Object>();
                            map.put(Const.ERR,"服务器返回异常" + e.getMessage().toString());
                        }
                        Message message = new Message();
                        message.obj = map;
                        message.what = 0x3;
                        message.arg1 = position;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();

    }

    public static void getLatestThreeNumAndSummary(final Handler handler,final String note_id,final String user_id){
        //打开详情页，更新三大数量以及得到用户签名（不确定用不用得上）
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/note?user_id=").append(user_id.toString())
                        .append("&note_id=").append(note_id.toString())
                        .append("&code=09");
                Request.Builder builder = new Request.Builder().url(sb.toString());
                Request request = builder.build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    Map<String,Object>map = null;
                    @Override
                    public void onFailure(Call call, IOException e) {
                        map = new HashMap<String, Object>();
                        map.put(Const.ERR,"请求异常，请检查你的网络");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        try{
                            map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                        }catch (Exception e){
                            map = new HashMap<String, Object>();
                            map.put(Const.ERR,"服务器返回异常" + e.getMessage().toString());
                        }
                        Message message = new Message();
                        message.obj = map;
                        message.what = 0x4;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }

    public static void getTwentyRelay(final Handler handler, final String note_id,final String start){
        //打开详情页，在转发列表一次获取20条转发
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/note?note_id=").append(note_id.toString())
                        .append("&start=").append(start)
                        .append("&code=10");
                Request.Builder builder = new Request.Builder().url(sb.toString());
                Request request = builder.build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    Map<String,Object>map = null;
                    @Override
                    public void onFailure(Call call, IOException e) {
                        map = new HashMap<String, Object>();
                        map.put(Const.ERR,"请求异常，请检查你的网络");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        try{
                            map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                        }catch (Exception e){
                            map = new HashMap<String, Object>();
                            map.put(Const.ERR,"服务器返回异常" + e.getMessage().toString());
                        }
                        Message message = new Message();
                        message.what = 0x14;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }

    public static void getTwentyGood(final Handler handler, final String note_id,final String start){
        //打开详情页，在一次获取20条点赞信息
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/note?note_id=").append(note_id.toString())
                        .append("&start=").append(start)
                        .append("&code=11");
                Request.Builder builder = new Request.Builder().url(sb.toString());
                Request request = builder.build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    Map<String,Object>map = null;
                    @Override
                    public void onFailure(Call call, IOException e) {
                        map = new HashMap<String, Object>();
                        map.put(Const.ERR,"请求异常，请检查你的网络");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        try{
                            map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                        }catch (Exception e){
                            map = new HashMap<String, Object>();
                            map.put(Const.ERR,"服务器返回异常" + e.getMessage().toString());
                        }
                        Message message = new Message();
                        message.what = 0x11;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }

    public static void uploadNoteImage(final Handler handler, Integer note_id, List<File>image_files,
                                       String image_size_list,String coordinate_list, String cut_size_list){
        //上传帖子图片
        String upload_url = Const.IP +"/uploadImage";
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("imageType","note");
        builder.addFormDataPart("image_size_list",image_size_list);
        //加入左上角点的坐标，以及裁剪比例
        builder.addFormDataPart("image_coordinate_list",coordinate_list);
        builder.addFormDataPart("image_cut_size",cut_size_list);

        builder.addFormDataPart(Const.NOTE_ID,note_id.toString());
        for(File file : image_files){
            builder.addFormDataPart("img",file.getName(), RequestBody.create(MediaType.parse("image/png"),file));
        }
        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(upload_url).post(requestBody).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            Map<String,Object>map = null;
            @Override
            public void onFailure(Call call, IOException e) {
                map.put(Const.ERR,"上传图片失败，请检查网络！");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ans_str = response.body().string();
                map = GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.what = 0x22;
                message.obj = map;
                handler.sendMessage(message);
            }
        });
    }


    public static void topReload(final Handler handler, final Integer user_id, final String bunch){
        //顶部下拉刷新
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/note?user_id=").append(user_id.toString())
                        .append("&bunch=").append(bunch.toString())
                        .append("&code=14");
                Request.Builder builder = new Request.Builder().url(sb.toString());
                Request request = builder.build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    Map<String,Object>map = null;
                    @Override
                    public void onFailure(Call call, IOException e) {
                        map = new HashMap<String, Object>();
                        map.put(Const.ERR,"请求异常，请检查你的网络");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        try{
                            map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                        }catch (Exception e){
                            map = new HashMap<String, Object>();
                            map.put(Const.ERR,"服务器返回异常" + e.getMessage().toString());
                        }
                        Message message = new Message();
                        message.obj = map;
                        message.what = 0x5;
                        handler.sendMessage(message);
                    }
                });
            }
        }.start();
    }


}
