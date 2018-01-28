package com.sysu.pro.fade.service;

import com.sysu.pro.fade.beans.AddMessage;
import com.sysu.pro.fade.beans.CommentMessageQuery;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.UserQuery;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by road on 2017/12/31.
 */

public interface MessageService {
    //给通知页的请求
    //获得AddMessage对象，里面包含有三个通知（贡献、粉丝、评论）的数量，ContentMessage初始化时调用
    @GET("getAddMessage/{user_id}")
    Observable<AddMessage> getAddMessage(@Path(("user_id"))String user_id);

    //“进度贡献”页，请求一次获取10条续减秒信息，第一次请求start填0，后面start填服务器返回的
    //返回的一个Note包含的信息有user_id,nickname,head_image_url,post_time, type
    @GET("getAddContribute/{user_id}/{start}/{point}")
    Observable<NoteQuery>getAddContribute(@Path("user_id")String user_id, @Path("start")String start,@Path("point")String point);

    //"新的粉丝"页,请求一次获取10条粉丝，第一次请求start填0，后面start填服务器返回的
    @GET("getAddFans/{user_id}/{start}/{point}")
    Observable<UserQuery>getAddFans(@Path("user_id")String user_id, @Path("start")String start,@Path("point")String point);

    //"评论"页,请求一次获取10条评论，第一次请求start填0，后面start填服务器返回的
    @GET("getAddComment/{user_id}/{start}/{point}")
    Observable<CommentMessageQuery>getAddComment(@Path("user_id")String user_id, @Path("start")String start,@Path("point")String point);

    //查看更多系列,查看以前旧的消息
    //返回的一个Note包含的信息有user_id,nickname,head_image_url,post_time, type
    @GET("getOldContribute/{user_id}/{start}")
    Observable<NoteQuery>getOldContribute(@Path("user_id")String user_id, @Path("start")String start);

    //"新的粉丝"页,请求一次获取10条粉丝，第一次请求start填0，后面start填服务器返回的
    @GET("getOldFans/{user_id}/{start}")
    Observable<UserQuery>getOldFans(@Path("user_id")String user_id, @Path("start")String start);

    //"评论"页,请求一次获取10条评论，第一次请求start填0，后面start填服务器返回的
    @GET("getOldComment/{user_id}/{start}")
    Observable<CommentMessageQuery>getOldComment(@Path("user_id")String user_id, @Path("start")String start);

}
