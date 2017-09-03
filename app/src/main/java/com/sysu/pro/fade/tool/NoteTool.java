package com.sysu.pro.fade.tool;

import android.os.Handler;
import android.os.Message;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.utils.HttpUtils;
import com.sysu.pro.fade.utils.GsonUtil;

import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
                Call call = mokHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String ans_str = response.body().string();
                    //开始json解析
                    Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                    Message msg = new Message();
                    msg.what = 0x1;
                    msg.obj = map;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                Call call = mokHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String ans_str = response.body().string();
                    //开始json解析
                    Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                    Message msg = new Message();
                    msg.what = 0x2;
                    msg.obj = map;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                Call call = mokHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String ans_str = response.body().string();
                    //开始json解析
                    Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
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

    public static void addNote(final Handler handler, final Integer user_id, final String nickname, final String head_image_url,
                               final String note_content, final Integer isRelay, final String tag_list){
        //发帖请求 原创贴的话isRelay是0，否则是原贴note_id
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair(Const.USER_ID, user_id.toString()));
                list.add(new BasicNameValuePair(Const.NICKNAME, nickname));
                list.add(new BasicNameValuePair(Const.HEAD_IMAGE_URL, head_image_url));
                list.add(new BasicNameValuePair(Const.NOTE_CONTENT, note_content));
                list.add(new BasicNameValuePair(Const.ISRELAY, isRelay.toString()));
                list.add(new BasicNameValuePair(Const.TAG_LIST, tag_list));
                if (isRelay == 0) list.add(new BasicNameValuePair(Const.CODE, "03"));
                else list.add(new BasicNameValuePair(Const.CODE, "04"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/note", list);
                Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.what = 0x1;
                message.obj = map;
                handler.sendMessage(message);
            }
        }.start();
    }

    public static void addSecond(final Handler handler,final String user_id,final String note_id, final String isRelay){
        //续一秒请求
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair>list = new ArrayList<>();
                list.add(new BasicNameValuePair(Const.USER_ID,user_id));
                list.add(new BasicNameValuePair(Const.NOTE_ID,note_id));
                list.add(new BasicNameValuePair(Const.ISRELAY,isRelay));
                list.add(new BasicNameValuePair(Const.CODE,"07"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/note",list);
                Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.obj = map;
                message.what = 0x3;
                handler.sendMessage(message);
                super.run();
            }
        }.start();

    }

    public static void getLatestThreeNumAndSummary(final Handler handler,final String note_id,final String user_id){
        //打开详情页，更新三大数量以及得到用户签名（不确定用不用得上）
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<BasicNameValuePair>list = new ArrayList<>();
                list.add(new BasicNameValuePair(Const.NOTE_ID,note_id));
                list.add(new BasicNameValuePair(Const.USER_ID,user_id));
                list.add(new BasicNameValuePair(Const.CODE,"09"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/note",list);
                Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
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
                String ans_str = HttpUtils.getRequest(Const.IP+"/note",list);
                Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
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
                String ans_str = HttpUtils.getRequest(Const.IP+"/note",list);
                Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                String ans = (String) map.get(Const.ANS);
                List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                System.out.println(result);
            }
        }.start();
    }

    public static void uploadNoteImage(final Handler handler, Integer note_id, List<File>image_files, String image_size_list){
        //上传帖子图片
        String upload_url = Const.IP +"/uploadImage";
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for(int i = 0; i < image_files.size(); i++){
            File file = image_files.get(i);
            if(file != null){
                builder.addFormDataPart("img",file.getName(), RequestBody.create(MediaType.parse("image/png"),file));
            }
        }
        builder.addFormDataPart("image_size_list",image_size_list);
        builder.addFormDataPart("imageType","note");
        builder.addFormDataPart(Const.NOTE_ID,note_id.toString());
        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder()
                          .url(upload_url).post(requestBody).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ans_str = response.body().string();
                Map<String,Object>ans_map = GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.what = 0x2;
                message.obj = ans_map;
                handler.sendMessage(message);
            }
        });
    }

    public static void topReload(final Handler handler, final Integer user_id, final String bunch){
        //顶部下拉刷新
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair>list = new ArrayList<>();
                list.add(new BasicNameValuePair(Const.USER_ID,user_id.toString()));
                if(bunch != null)
                    list.add(new BasicNameValuePair("bunch",bunch));
                list.add(new BasicNameValuePair(Const.CODE,"14"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/note",list);
                Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.obj = map;
                message.what = 0x5;
                handler.sendMessage(message);
                super.run();
            }
        }.start();
    }

}
