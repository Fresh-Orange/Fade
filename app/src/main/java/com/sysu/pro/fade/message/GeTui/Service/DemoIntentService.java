package com.sysu.pro.fade.message.GeTui.Service;

/**
 * Created by yellow on 2018/4/26.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class DemoIntentService extends GTIntentService {

    private User myself;

    public DemoIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {

    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        if(myself == null){
            SharedPreferences sharedPreferences = getSharedPreferences(Const.USER_SHARE, Context.MODE_PRIVATE);
            myself = JSON.parseObject(sharedPreferences.getString("user","{}"),User.class);
        }
        Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,myself.getTokenModel());
        UserService userService = retrofit.create(UserService.class);
        userService.addClientId(myself.getUser_id().toString(),clientid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SimpleResponse>(){
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {

                    }
                    @Override
                    public void onNext(SimpleResponse simpleResponse) {
                        Log.i("上传clientid","成功");
                    }
                });
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }



    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
    }
}
