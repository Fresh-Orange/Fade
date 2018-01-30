package com.sysu.pro.fade.service;

import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.PersonPage;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.beans.UserQuery;

import java.util.Map;

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
    //获得一个用户全部信息，密码除外
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
    Observable<SimpleResponse> getHeadImageUrl(@Field("telephone")String telephone,
                                               @Field("fade_name")String fade_name, @Field("wechat_id")String wechat_id);

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

    //得到他人或自己的主页信息,user_id为别人的和my_id为自己的，返回信息包括了用户信息和十条动态
    //若是自己主页，则user_id和my_id都填自己的id
    @GET("getPersonPage/{user_id}/{my_id}")
    Observable<PersonPage>getPersonPage(@Path("user_id")String  user_id, @Path("my_id")String  my_id);

    //和getPersonPage搭配使用，负责个人页动态部分的继续加载，一开始start填getPersonPage中NoteQuery返回的
    @GET("getLiveNote/{user_id}/{my_id}/{start}")
    Observable<NoteQuery>getLiveNote(@Path("user_id")String  user_id, @Path("my_id")String  my_id, @Path("start")String  start);

    //检索用户，分段加载,start一开始填0，后面就填服务器返回的start
    @GET("searchUser/{keyword}/{start}")
    Observable<UserQuery>searchUser(@Path("keyword")String  keyword, @Path("start")String  page);

    //获取我自己的原创帖子（个人页面fade部分）
    @GET("getMyNote/{user_id}/{start}")
    Observable<NoteQuery>getMyNote(@Path("user_id")String  user_id, @Path("start")String  start);

    //获取融云token,返回的string字符串即是token
    @GET("getMessageToken/{user_id}")
    Observable<Map<String,Object>>getMessageToken(@Path("user_id")String  user_id);

    //获取他人个人页的原创帖子（他人页面fade部分）
    @GET("getOtherPersonNote/{user_id}/{my_id}/{start}")
    Observable<NoteQuery>getOtherPersonNote(@Path("user_id")String  user_id, @Path("my_id")String my_id,@Path("start")String  start);

    //一次得到十条推荐用户
    @GET("getRecommendUser/{user_id}/{start}")
    Observable<UserQuery>getRecommendUser(@Path("user_id")String  user_id, @Path("start")String  start);

    //个人页，分页查询20条粉丝，user里面有个isConcern属性,返回的start小于20判定为加载到底
    @GET("getFans/{user_id}/{start}")
    Observable<UserQuery>getFans(@Path("user_id")String  user_id,@Path("start")String  start);

    //个人页，分页查询20条关注的人，返回的start小于20判定为加载到底
    @GET("getConcerns/{user_id}/{start}")
    Observable<UserQuery>getConcerns(@Path("user_id")String  user_id,@Path("start")String  start);
}
