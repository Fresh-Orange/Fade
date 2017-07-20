package com.sysu.pro.fade.tool;

import android.os.Handler;
import android.os.Message;

import com.sysu.pro.fade.utils.Const;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by road on 2017/7/10.
 */
public class RegisterTool {

    private RegisterTool(){

    }

    //用户名密码注册
    public static void sendToRegister(final String ip, final Handler handler, final String nickname, final String password, final String sex,final String user_id){
        new Thread(){
            @Override
            public void run() {

                List<BasicNameValuePair>list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair("nickname",nickname));
                list.add(new BasicNameValuePair("password",password));
                list.add(new BasicNameValuePair("sex",sex));
                list.add(new BasicNameValuePair("user_id",user_id));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String param = URLEncodedUtils.format(list, "UTF-8");
                    HttpGet httpGet = new HttpGet("http://" +ip+"/Fade/register?"+param);
                    HttpResponse response = httpClient.execute(httpGet);
                    if(response.getStatusLine().getStatusCode() == 200){
                        HttpEntity entity1 = response.getEntity();
                        String ans = EntityUtils.toString(entity1,"utf-8");
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = ans;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    //发送图片类
    public static void sendImage(final String ip, final Handler handler, final String imageType, final String path, final String id){
        new Thread(){
            @Override
            public void run() {
                String rsp = "";
                HttpURLConnection conn = null;
                String BOUNDARY = "|"; // request头和上传文件内容分隔符
                try {
                    URL url = new URL("http://" +ip+"/Fade/uploadImage");
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
                msg.obj = rsp;
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


}
