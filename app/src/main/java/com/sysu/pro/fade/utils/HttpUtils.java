package com.sysu.pro.fade.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by road on 2017/7/30.
 */
public class HttpUtils {
    private HttpUtils(){

    }
    public  static String getRequest(String url, List<BasicNameValuePair>list){
        HttpClient httpClient = new DefaultHttpClient();
        String param = URLEncodedUtils.format(list, "UTF-8");
        HttpGet httpGet = new HttpGet(url+"?"+ param);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            return EntityUtils.toString(httpEntity,"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  static String postRequest(String url, List<BasicNameValuePair>list){
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list,"utf-8"));
            try {
                HttpResponse response = httpClient.execute(httpPost);
                return EntityUtils.toString(response.getEntity(),"utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
