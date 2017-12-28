package com.sysu.pro.fade.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sysu.pro.fade.beans.TokenModel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by road on 2017/7/30.
 */
public class RetrofitUtils {

    public static OkHttpClient createOkHttp(final TokenModel tokenModel){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder builder= chain.request().newBuilder();
                        if(tokenModel != null){
                            builder.addHeader("tokenModel", JSON.toJSONString(tokenModel));
                        }
                        Request request = builder.build();
                        Response response =  chain.proceed(request);
                        String data = response.body().string();
                        Log.i("返回信息",data);
                        if(!response.isSuccessful()){
                           // Log.e("错误响应",data);
                            //得到错误信息，通知主界面
                        }
                        return response.newBuilder()
                                .body(ResponseBody.create(MediaType.parse("UTF-8"), data))
                                .build();
                    }
                })
                .build();
        return client;
    }

    public static Retrofit createRetrofit(String baseUrl,TokenModel tokenModel){
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(createOkHttp(tokenModel))
                .build();
    }
}
