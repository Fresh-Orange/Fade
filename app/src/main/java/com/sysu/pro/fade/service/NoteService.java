package com.sysu.pro.fade.service;

import com.sysu.pro.fade.beans.DetailPage;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.SimpleResponse;

import okhttp3.RequestBody;
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

public interface NoteService {
    //发布帖子
    @POST("addNote")
    Observable<SimpleResponse> addNote(@Body RequestBody body);

    //初次加载以及向下加载10条，按照时间顺序
    @FormUrlEncoded
    @POST("getTenNoteByTime")
    Observable<NoteQuery> getTenNoteByTime(@Field("user_id") String user_id, @Field("start")String start,
                                           @Field("concern_num")String concern_num,@Field("updateList")String updateList);

    //顶部下拉刷新，更新已加载帖子的存活，顺便加载新数据
    //第二个参数实际是Set<Note>updateList, Note的每一项要包括note_id,target_id
    @FormUrlEncoded
    @POST("getMoreNote")
    Observable<NoteQuery> getMoreNote(@Field("user_id")String user_id, @Field("updateList")String updateList);

    //改变续秒数，包括增一秒和减一秒
    @FormUrlEncoded
    @POST("changeSecond")
    Observable<SimpleResponse>changeSecond(@Field("note")String note);

    //获得详情页内容, getFull为0代表获取部分note信息， 1代表将返回整个完整note
    @GET("getNotePage/{note_id}/{user_id}/{getFull}")
    Observable<DetailPage> getNotePage(@Path("note_id")String note_id,@Path("user_id")String user_id
                                       ,@Path("getFull")String getFull);

    //删除帖子
    @DELETE("deleteNote/{note_id}/{user_id}")
    Observable<SimpleResponse> deleteNote(@Path("note_id")String note_id,@Path("user_id")String user_id);

    //获取一个首页完整帖子的请求
    @GET("getFullNote/{note_id}/{user_id}")
    Observable<Note> getFullNote(@Path("note_id")String note_id, @Path("user_id")String user_id);

    //搜索帖子的请求,start一开始填0，后面就填服务器返回的start；isAlive=0表明查询死的帖子，为1表明查询活的帖子
    //查询死贴和查询活帖的start一开始都填0，user_id为用户自己的id
    //当返回的记录条数小于10的时候，判定已经到底部
    @GET("searchNote/{keyword}/{start}/{isAlive}/{user_id}")
    Observable<NoteQuery> searchNote(@Path("keyword")String keyword, @Path("start")String start,
                                @Path("isAlive")String isAlive,@Path("user_id")String user_id);

    //点开折叠列表，获取20条记录, type为1和2，分别代表增和减
    @GET("getConcernSecond/{user_id}/{target_id}/{start}/{type}")
    Observable<NoteQuery> getConcernSecond(@Path("user_id")String user_id, @Path("target_id")String target_id,
                                           @Path("start")String start,@Path("type")String type);

    //每次获取20条续秒详情
    //user_id为帖子发布者的id！
    //type为1表示获取续一秒，为2表示获取减一秒
    @GET("getAllSecond/{user_id}/{note_id}/{start}/{type}")
    Observable<NoteQuery> getAllSecond(@Path("user_id")String user_id, @Path("note_id")String note_id,
                                       @Path("start")String start, @Path("type")String type);

}
