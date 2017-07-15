package com.sysu.pro.fade.tool;

import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by road on 2017/7/11.
 */
public class LoginTool {
    private LoginTool(){

    }

    public static void sendToLogin(final Handler handler,final String ip, final String nickname, final String password){
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair>list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair("nickname",nickname));
                list.add(new BasicNameValuePair("password",password));
                String param = URLEncodedUtils.format(list, "UTF-8");

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://" +ip+"/Fade/login?"+param);
                try {
                    HttpResponse response = httpClient.execute(httpGet);
                    HttpEntity httpEntity1 = response.getEntity();
                    String ans = EntityUtils.toString(httpEntity1,"utf-8");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = ans;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                super.run();
            }
        }.start();
    }

    public static void getHeadImageUrl(final  Handler handler, final String ip,final String nickname){
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair>list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair("nickname",nickname));
                String param = URLEncodedUtils.format(list, "UTF-8");

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://" +ip+"/Fade/ImageUrl?"+param);
                try {
                    HttpResponse response = httpClient.execute(httpGet);
                    HttpEntity httpEntity1 = response.getEntity();
                    String ans = EntityUtils.toString(httpEntity1,"utf-8");
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = ans;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                super.run();
            }
        }.start();
    }
}
