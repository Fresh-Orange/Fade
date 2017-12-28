package com.sysu.pro.fade.service;

import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by road on 2017/12/28.
 */

public interface UserService {
    //获得一个用户全部信息
    @GET("getUserById/{user_id}")
    Observable<User> getUserById(@Path("user_id")String user_id);

    //文件上传，请求体包括一个user以及file
    @POST("registerByName")
    Observable<ResponseBody>registerByName(@Body RequestBody body);

    //查询手机号是否被注册
    @GET("registerQueryTel/{telephone}")
    Observable<SimpleResponse> registerQueryTel(@Path("telephone")String telephone);

    //fade_name的登录方式
    @GET("loginUserByName/{fade_name}/{password}")
    Observable<User> loginUserByName(@Path("fade_name")String fade_name,@Path("password")String password);

    //手机号的登录方式
    @GET("loginUserByTel/{telephone}/{password}")
    Observable<User> loginUserByTel(@Path("telephone")String telephone,@Path("password")String password);

    //根据手机号或者fade_name获取头像
    @FormUrlEncoded
    @POST("getHeadImageUrl")
    Observable<SimpleResponse> getHeadImageUrl(@Field("telephone")String telephone,@Field("fade_name")String fade_name, @Field("wechat_id")String wechat_id);

    //更新用户信息
    @POST("updateUserById")
    Observable<SimpleResponse> updateUserById(@Body RequestBody body);

    //注销登录
    @DELETE("logoutByToken/{tokenModel}")
    Observable<SimpleResponse> logoutUserByToken(@Path("tokenModel")String tokenModel);






}
