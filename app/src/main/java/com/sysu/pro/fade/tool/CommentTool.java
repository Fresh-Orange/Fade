package com.sysu.pro.fade.tool;

import android.os.Handler;
import android.os.Message;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.utils.GsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by road on 2017/8/31.
 */
public class CommentTool {
    public static void addComment(final Handler handler , final Integer note_id, final Integer user_id, final String nickname, final String head_image_url, final Integer to_comment_id, final String comment_content){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/comment?user_id=").append(user_id.toString())
                        .append("&note_id=").append(note_id)
                        .append("&nickname=").append(nickname)
                        .append("&head_image_url=").append(head_image_url)
                        .append("&to_comment_id=").append(to_comment_id)
                        .append("&comment_content=").append(comment_content)
                        .append("&code=01");
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
                        message.what = 0x4;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }

    public static void getTenHotComment(final Handler handler,final Integer user_id, final Integer note_id){
        //获取十条热评，显示在评论列表的上半部分
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/comment?user_id=").append(user_id.toString())
                        .append("&note_id=").append(note_id)
                        .append("&code=00");
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
                        message.what = 0x5;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }

    public static void addCommentGood(final Handler handler, final Integer comment_id, final Integer user_id, final Integer note_id){
        //为评论点赞
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/comment?user_id=").append(user_id.toString())
                        .append("&note_id=").append(note_id)
                        .append("&comment_id=").append(comment_id)
                        .append("&code=03");
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
                        message.what = 0x7;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                });
            }
        }.start();
    }

    public static void getFirstComments(final Handler handler, final Integer user_id, final  Integer note_id){
        new Thread(){
            public void run() {
                //得到十条热门评论+20条普通评论
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/comment?user_id=").append(user_id.toString())
                        .append("&note_id=").append(note_id)
                        .append("&code=05");
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
                        message.what = 0x12;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }

    public static void getTwentyComments(final Handler handler, final Integer user_id, final  Integer note_id, final Integer start){
        new Thread(){
            public void run() {
                //首次加载之后，使用这个函数获得20条普通评论（按时间排序）
                OkHttpClient mokHttpClient = new OkHttpClient();
                StringBuilder sb = new StringBuilder();
                sb.append(Const.IP)
                        .append("/comment?user_id=").append(user_id.toString())
                        .append("&note_id=").append(note_id)
                        .append("&start=").append(start)
                        .append("&code=02");
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
                        message.what = 0x13;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }

}
