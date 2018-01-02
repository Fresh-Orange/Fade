package com.sysu.pro.fade.tool;

import android.os.Handler;
import android.os.Message;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.utils.GsonUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by road on 2017/8/29.
 */
public class UserTool {
    public static void sendToLogin(final Handler handler, final String password, final String account, final String accountType){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                //涉及到用户关键信息，用post传更安全
                FormBody.Builder builder = new FormBody.Builder();
                builder.add(Const.PASSWORD,password)
                        .add(Const.CODE,"05");
                if(accountType.equals(Const.TELEPHONE))
                    builder.add(Const.TELEPHONE,account);
                else if(accountType.equals(Const.FADE_NAME))
                    builder.add(Const.FADE_NAME,account);
                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "/user").build();
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
                        message.what = 1;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }

    public static void getHeadImageUrl(final  Handler handler,final String account,final String accountType){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                //涉及到用户关键信息，用post传更安全
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("imageType","head")
                        .add(Const.CODE,"06");
                if(accountType.equals(Const.TELEPHONE))
                    builder.add(Const.TELEPHONE,account);
                else if(accountType.equals(Const.FADE_NAME))
                    builder.add(Const.FADE_NAME,account);
                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "/user").build();
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
                        message.what = 2;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }


    //用户名密码注册
    public static void sendToRegister(final Handler handler, final String nickname, final String password, final String sex,final String telephone){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                //涉及到用户关键信息，用post传更安全
                FormBody.Builder builder = new FormBody.Builder();
                builder.add(Const.CODE,"04")
                       .add(Const.NICKNAME,nickname)
                       .add(Const.PASSWORD,password)
                       .add(Const.SEX,sex)
                       .add(Const.TELEPHONE,telephone);

                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "/user").build();
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
                        message.what = 1;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                });
            }
        }.start();
    }

    //短信发送验证码
    public static void sendIdentifyCode(final Handler handler, final String tel){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Const.SEND_SMS_URL);
                //添加请求头
                httpPost.addHeader("X-LC-Id",Const.X_LC_ID);
                httpPost.addHeader("X-LC-Key",Const.X_LC_KEY);
                httpPost.setHeader("Content-Type","application/json");

                //实体内容加入参数 注意json格式
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("mobilePhoneNumber",tel);
                    jsonObject.put("op","注册");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    httpPost.setEntity(new StringEntity(jsonObject.toString(),"utf-8"));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity entity1 = httpResponse.getEntity();
                    String ans = EntityUtils.toString(entity1,"utf-8");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = ans;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //短信核验验证码
    public static void toCheck(final Handler handler, final String mobilePhoneNumber, final String checkNum){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Const.CHECK_SMS_URL+checkNum+"?mobilePhoneNumber="+mobilePhoneNumber);
                //添加请求头
                httpPost.addHeader("X-LC-Id",Const.X_LC_ID);
                httpPost.addHeader("X-LC-Key",Const.X_LC_KEY);
                httpPost.setHeader("Content-Type","application/json");
                try {
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity entity1 = httpResponse.getEntity();
                    String ans = EntityUtils.toString(entity1,"utf-8");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = ans;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
    //校验手机号是否已被注册
    public static void checkTel( final Handler handler, final String telephone){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                //涉及到用户关键信息，用post传更安全
                FormBody.Builder builder = new FormBody.Builder();
                builder.add(Const.CODE,"03")
                        .add(Const.TELEPHONE,telephone);

                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "/user").build();
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
                        message.what = 2;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                });
            }
        }.start();
    }

    /**
     * 2017/9/3  hl
     * 修改个人信息相关的请求
     */
    //修改用户昵称的请求
    public static void  editNickname(final Handler handler, final Integer user_id, final String nickname){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                //涉及到用户关键信息，用post传更安全
                FormBody.Builder builder = new FormBody.Builder();
                builder.add(Const.CODE,"07")
                        .add(Const.USER_ID,user_id.toString())
                        .add(Const.NICKNAME,nickname);

                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "/user").build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        Message message = new Message();
                        message.what = 1;
                        message.obj = ans_str;
                        handler.sendMessage(message);
                    }
                });
            }
        }.start();
    }
    //修改或上传头像的请求
    public static void uploadHeadImage(final Handler handler, Integer user_id, String head_path){
        String upload_url = Const.IP +"/uploadImage";
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("imageType","head");
        builder.addFormDataPart(Const.USER_ID,user_id.toString());
        File file = new File(head_path);
        builder.addFormDataPart("img",file.getName(), RequestBody.create(MediaType.parse("image/png"),file));
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
                message.what = 2;
                message.obj = ans_map;
                handler.sendMessage(message);
            }
        });
    }
    //修改用户签名的请求
    public static void  editSummary(final Handler handler, final Integer user_id, final String summary){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                //涉及到用户关键信息，用post传更安全
                FormBody.Builder builder = new FormBody.Builder();
                builder.add(Const.CODE,"08")
                        .add(Const.USER_ID,user_id.toString())
                        .add(Const.SUMMARY,summary);

                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "/user").build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        Message message = new Message();
                        message.what = 3;
                        message.obj = ans_str;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }
    //修改用户性别的请求
    public static void  editSex(final Handler handler, final Integer user_id, final String sex){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                //涉及到用户关键信息，用post传更安全
                FormBody.Builder builder = new FormBody.Builder();
                builder.add(Const.CODE,"09")
                        .add(Const.USER_ID,user_id.toString())
                        .add(Const.SEX,sex);

                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "/user").build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        Message message = new Message();
                        message.what = 4;
                        message.obj = ans_str;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }
    //修改用户地区的请求
    public static void  editArea(final Handler handler, final Integer user_id, final String area){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                //涉及到用户关键信息，用post传更安全
                FormBody.Builder builder = new FormBody.Builder();
                builder.add(Const.CODE,"10")
                        .add(Const.USER_ID,user_id.toString())
                        .add(Const.AREA,area);

                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "/user").build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        Message message = new Message();
                        message.what = 5;
                        message.obj = ans_str;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }
    //修改用户学校的请求
    public static void  editSchool(final Handler handler, final Integer user_id, final String school){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient mokHttpClient = new OkHttpClient();
                //涉及到用户关键信息，用post传更安全
                FormBody.Builder builder = new FormBody.Builder();
                builder.add(Const.CODE,"11")
                        .add(Const.USER_ID,user_id.toString())
                        .add(Const.SCHOOL,school);

                Request request = new Request.Builder().post(builder.build()).url(Const.IP + "/user").build();
                mokHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String ans_str = response.body().string();
                        //开始json解析
                        Message message = new Message();
                        message.what = 6;
                        message.obj = ans_str;
                        handler.sendMessage(message);
                    }
                });
                super.run();
            }
        }.start();
    }
    //上传或修改用户壁纸的请求
    public static void editWallpaperUrl(final Handler handler, Integer user_id, String wallpaper_path){
        String upload_url = Const.IP +"/uploadImage";
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("imageType","wallpaper");
        builder.addFormDataPart(Const.USER_ID,user_id.toString());
        File file = new File(wallpaper_path);
        builder.addFormDataPart("img",file.getName(), RequestBody.create(MediaType.parse("image/png"),file));
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
                message.what = 7;
                message.obj = ans_map;
                handler.sendMessage(message);
            }
        });
    };



}
