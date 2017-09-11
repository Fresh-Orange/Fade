package com.sysu.pro.fade.tool;

import android.os.Handler;
import android.os.Message;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.utils.GsonUtil;
import com.sysu.pro.fade.utils.HttpUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
 * Created by road on 2017/8/29.
 */
public class UserTool {
    public static void sendToLogin(final Handler handler, final String ip, final String password, final String account, final String accountType){
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();

                if(accountType.equals(Const.TELEPHONE))
                    list.add(new BasicNameValuePair(Const.TELEPHONE,account));
                else if(accountType.equals(Const.FADE_NAME))
                    list.add(new BasicNameValuePair(Const.FADE_NAME,account));

                list.add(new BasicNameValuePair(Const.PASSWORD,password));
                list.add(new BasicNameValuePair(Const.CODE,"05"));
                String param = URLEncodedUtils.format(list, "UTF-8");

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(ip+"/user?"+param);
                try {
                    HttpResponse response = httpClient.execute(httpGet);
                    HttpEntity httpEntity1 = response.getEntity();
                    String ans = EntityUtils.toString(httpEntity1,"utf-8");
                    Message msg = new Message();
                    Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans);
                    msg.what = 1;
                    msg.obj = map;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                super.run();
            }
        }.start();
    }

    public static void getHeadImageUrl(final  Handler handler, final String ip,final String account,final String accountType){
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair>list = new ArrayList<BasicNameValuePair>();
                if(accountType.equals(Const.TELEPHONE))
                    list.add(new BasicNameValuePair(Const.TELEPHONE,account));
                else if(accountType.equals(Const.FADE_NAME))
                    list.add(new BasicNameValuePair(Const.FADE_NAME,account));

                list.add(new BasicNameValuePair("imageType","head"));
                list.add(new BasicNameValuePair(Const.CODE,"06"));

                String param = URLEncodedUtils.format(list, "UTF-8");

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(ip+"/user?"+param);
                try {
                    HttpResponse response = httpClient.execute(httpGet);
                    HttpEntity httpEntity1 = response.getEntity();
                    String ans = EntityUtils.toString(httpEntity1,"utf-8");
                    Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans);
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = map;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                super.run();
            }
        }.start();
    }


    //用户名密码注册
    public static void sendToRegister(final String ip, final Handler handler, final String nickname, final String password, final String sex,final String telephone){
        new Thread(){
            @Override
            public void run() {

                List<BasicNameValuePair>list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair(Const.NICKNAME,nickname));
                list.add(new BasicNameValuePair(Const.PASSWORD,password));
                list.add(new BasicNameValuePair(Const.SEX,sex));
                list.add(new BasicNameValuePair(Const.TELEPHONE,telephone));
                list.add(new BasicNameValuePair("code","04"));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String param = URLEncodedUtils.format(list, "UTF-8");
                    HttpGet httpGet = new HttpGet(ip+"/user?"+param);
                    HttpResponse response = httpClient.execute(httpGet);
                    if(response.getStatusLine().getStatusCode() == 200){
                        HttpEntity entity1 = response.getEntity();
                        String ans_str = EntityUtils.toString(entity1,"utf-8");
                        Map<String,Object> map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = map;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    //发送图片类
    public static void sendImage(final String ip, final Handler handler, final String imageType, final String path, final Integer id){
        new Thread(){
            @Override
            public void run() {
                String rsp = "";
                HttpURLConnection conn = null;
                String BOUNDARY = "|"; // request头和上传文件内容分隔符
                try {
                    URL url = new URL("http://" +ip+"/fade/uploadImage");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(30000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data; boundary=" + BOUNDARY);

                    OutputStream out = new DataOutputStream(conn.getOutputStream());
                    File file = new File(path);
                    String filename = file.getName();
                    String contentType = "";
                    if (filename.endsWith(".png")) {
                        contentType = "image/png";
                    }
                    if (filename.endsWith(".jpg")) {
                        contentType = "image/jpg";
                    }
                    if (filename.endsWith(".gif")) {
                        contentType = "image/gif";
                    }
                    if (filename.endsWith(".bmp")) {
                        contentType = "image/bmp";
                    }
                    if (contentType == null || contentType.equals("")) {
                        contentType = "application/octet-stream";
                    }
                    StringBuffer strBuf = new StringBuffer();

                    //加入imageType
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=" + "imageType"
                            +"\r\n\r\n");
                    strBuf.append(imageType + "\r\n");

                    //加入user_id
                    strBuf.append("--").append(BOUNDARY).append("\r\n");
                    if(imageType == "head"){
                        strBuf.append("Content-Disposition: form-data; name=\"" + "user_id"+"\""
                                +"\r\n\r\n");
                    }else{
                        strBuf.append("Content-Disposition: form-data; name=\"" + "note_id"+"\""
                                +"\r\n\r\n");
                    }
                    strBuf.append(id + "\r\n");

                    //加入图片内容
                    strBuf.append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + path
                            + "\"; filename=\"" + filename + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    out.write(strBuf.toString().getBytes());
                    System.out.println(strBuf.toString());
                    System.out.println(strBuf.toString().getBytes());
                    DataInputStream in = new DataInputStream(new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                    byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
                    out.write(endData);
                    out.flush();
                    out.close();

                    // 读取返回数据
                    StringBuffer buffer = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    rsp = buffer.toString();
                    reader.close();
                    reader = null;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                        conn = null;
                    }
                }
                Message msg = new Message();
                msg.what = 2;
                Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(rsp);
                msg.obj = map;
                handler.sendMessage(msg);
                super.run();
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
    public static void checkTel(final String ip, final Handler handler, final String telephone){
        new Thread(){
            @Override
            public void run() {

                List<BasicNameValuePair>list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair(Const.TELEPHONE,telephone));
                list.add(new BasicNameValuePair("code","03"));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String param = URLEncodedUtils.format(list, "UTF-8");
                    HttpGet httpGet = new HttpGet(ip+"/user?"+param);
                    HttpResponse response = httpClient.execute(httpGet);
                    if(response.getStatusLine().getStatusCode() == 200){
                        HttpEntity entity1 = response.getEntity();
                        String ans = EntityUtils.toString(entity1,"utf-8");
                        Message msg = new Message();
                        Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans);
                        msg.what = 2;
                        msg.obj = map;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                List<BasicNameValuePair>list = new ArrayList<>();
                list.add(new BasicNameValuePair(Const.USER_ID,user_id.toString()));
                if(nickname != null)
                    list.add(new BasicNameValuePair(Const.NICKNAME,nickname));
                list.add(new BasicNameValuePair(Const.CODE,"07"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/user",list);
                //Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.obj = ans_str;
                message.what = 1;
                handler.sendMessage(message);
                super.run();
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
                List<BasicNameValuePair>list = new ArrayList<>();
                list.add(new BasicNameValuePair(Const.USER_ID,user_id.toString()));
                if(summary != null)
                    list.add(new BasicNameValuePair(Const.SUMMARY,summary));
                list.add(new BasicNameValuePair(Const.CODE,"08"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/user",list);
                //Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.obj = ans_str;
                message.what = 3;
                handler.sendMessage(message);
                super.run();
            }
        }.start();
    }
    //修改用户性别的请求
    public static void  editSex(final Handler handler, final Integer user_id, final String sex){
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair>list = new ArrayList<>();
                list.add(new BasicNameValuePair(Const.USER_ID,user_id.toString()));
                if(sex != null)
                    list.add(new BasicNameValuePair(Const.SEX,sex));
                list.add(new BasicNameValuePair(Const.CODE,"09"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/user",list);
                //Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.obj = ans_str;
                message.what = 4;
                handler.sendMessage(message);
                super.run();
            }
        }.start();
    }
    //修改用户地区的请求
    public static void  editArea(final Handler handler, final Integer user_id, final String area){
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair>list = new ArrayList<>();
                list.add(new BasicNameValuePair(Const.USER_ID,user_id.toString()));
                if(area != null)
                    list.add(new BasicNameValuePair(Const.AREA,area));
                list.add(new BasicNameValuePair(Const.CODE,"10"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/user",list);
               // Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.obj = ans_str;
                message.what = 5;
                handler.sendMessage(message);
                super.run();
            }
        }.start();
    }
    //修改用户学校的请求
    public static void  editSchool(final Handler handler, final Integer user_id, final String school){
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair>list = new ArrayList<>();
                list.add(new BasicNameValuePair(Const.USER_ID,user_id.toString()));
                if(school != null)
                    list.add(new BasicNameValuePair(Const.AREA,school));
                list.add(new BasicNameValuePair(Const.CODE,"11"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/user",list);
               // Map<String,Object>map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.obj = ans_str;
                message.what = 6;
                handler.sendMessage(message);
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
