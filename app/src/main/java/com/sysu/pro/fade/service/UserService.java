package com.sysu.pro.fade.service;

import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.PersonPage;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.beans.UserQuery;

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

    //上线请求
    @FormUrlEncoded
    @POST("online")
    Observable<SimpleResponse>online(@Field("user_id")String user_id);

    //下线请求
    @DELETE("offline/{user_id}")
    Observable<SimpleResponse> offline(@Path("user_id")String  user_id);

    //关注某人,fans_id为关注者，fans_id被关注者
    @FormUrlEncoded
    @POST("concern")
    Observable<SimpleResponse> concern(@Field("fans_id")String  fans_id,@Field("star_id")String  star_id);

    //取消关注某人
    @DELETE("cancelConcern/{fans_id}/{star_id}")
    Observable<SimpleResponse> cancelConcern(@Path("fans_id")String  fans_id, @Path("star_id")String  star_id);

    //得到某人的主页信息,user_id为别人的和my_id为自己的
    @GET("getPersonPage/{user_id}/{my_id}")
    Observable<PersonPage>getPersonPage(@Path("user_id")String  user_id, @Path("my_id")String  my_id);

    //检索用户，分段加载
    @GET("searchUser/{keyword}/{page}")
    Observable<UserQuery>searchUser(@Path("keyword")String  keyword, @Path("page")String  page);

    //分页加载用户个人主页数据
    @GET("getMyNote/{user_id}/{start}")
    Observable<NoteQuery>getMyNote(@Path("user_id")String  user_id, @Path("start")String  start);




}
