package com.sysu.pro.fade.message.GeTui.Service;

/**
 * Created by yellow on 2018/4/26.
 */

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.PushMessage;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.activity.DetailActivity;
import com.sysu.pro.fade.home.activity.OtherActivity;
import com.sysu.pro.fade.message.Activity.ContributionActivity;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;

import org.greenrobot.eventbus.EventBus;

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

    private PushMessage pushMessage;

    private Context mContext;
    public DemoIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.e("getui", "------------onReceiveServicePid------------");

    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        Log.e("getui", "------------onReceiveMessageData------------");
//        String noteID = new String(msg.getPayload());
////        Log.e("getui", "data: " + data);
//        Intent intent = new Intent(context, DetailActivity.class);
//        intent.putExtra(Const.NOTE_ID, noteID);
//        intent.putExtra(Const.IS_COMMENT, false);
////        intent.putExtra(Const.COMMENT_NUM, temp.getComment_num());
////        intent.putExtra(Const.COMMENT_ENTITY, temp);
//        intent.putExtra("getFull",true);
//        startActivity(intent);

        mContext = context;
        String str = new String(msg.getPayload());
        Log.d("YellowGetui","str = : " + str);
        pushMessage = JSON.parseObject(str, PushMessage.class);
        Log.d("YellowGetui","这里进不去了吗");
        if(pushMessage == null) {
            Log.d("YellowGetui","PushMessage is null!");
            return;
        }
        Log.d("YellowGetui","NotNull: ");
        Log.d("YellowGetui","PushMessage: " + pushMessage);
        new MyServerThread().start();
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e("getui", "------------onReceiveClientId------------");
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
        Log.e("getui", "------------onReceiveOnlineState------------");

    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.e("getui", "------------onReceiveCommandResult------------");
    }



    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
        String content = msg.getContent();
        String messageId = msg.getMessageId();
        String taskId = msg.getTaskId();
        String title = msg.getTitle();
        String pkgName = msg.getPkgName();
        Log.e("getui", "------------onNotificationMessageArrived------------");
        Log.e("getui", "content: " + content);
        Log.e("getui", "messageId: " + messageId);
        Log.e("getui", "taskId: " + taskId);
        Log.e("getui", "title: " + title);
        Log.e("getui", "pkgName: " + pkgName);
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
        String content = msg.getContent();
        String messageId = msg.getMessageId();
        String taskId = msg.getTaskId();
        String title = msg.getTitle();
        String pkgName = msg.getPkgName();
        Log.e("getui", "------------onNotificationMessageClicked------------");
        Log.e("getui", "content: " + content);
        Log.e("getui", "messageId: " + messageId);
        Log.e("getui", "taskId: " + taskId);
        Log.e("getui", "title: " + title);
        Log.e("getui", "pkgName: " + pkgName);
        Log.e("getui", "------------通知被点击------------");


    }

    class MyServerThread extends Thread {
        @Override
        public void run() {
//            Intent launchIntent = mContext.getPackageManager().
//                    getLaunchIntentForPackage("com.liangzili.notificationlaunch");
//            launchIntent.setFlags(
//                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            Bundle args = new Bundle();
//            args.putString("name", "电饭锅");
//            args.putString("price", "58元");
//            args.putString("detail", "这是一个好锅, 这是app进程不存在，先启动应用再启动Activity的");
//            launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
//            context.startActivity(launchIntent);

//            EventBus.getDefault().post(pushMessage);
            //设定传递对象，动态广播只需传送message，在MainActivity返回onNewIntent
            switch (pushMessage.getMsgId()) {
                case 1:
                    Log.e("YellowMain", "Case 1");
                    Note contributionNote = (Note) pushMessage.getObj();
                    Intent intent = new Intent(DemoIntentService.this, DetailActivity.class);
                    intent.putExtra(Const.NOTE_ID,contributionNote.getTarget_id());
                    intent.putExtra(Const.IS_COMMENT,false);
                    intent.putExtra(Const.COMMENT_NUM, contributionNote.getComment_num());
                    intent.putExtra(Const.COMMENT_ENTITY, contributionNote);
                    intent.putExtra("getFull",true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case 2:
                    Log.e("YellowMain", "Case 2");
                    Comment commentNote = (Comment) pushMessage.getObj();
                    Intent intent3 = new Intent(DemoIntentService.this, DetailActivity.class);
                    intent3.putExtra(Const.NOTE_ID,commentNote.getComment_id());
                    intent3.putExtra(Const.IS_COMMENT,true);
//                intent3.putExtra(Const.COMMENT_NUM, commentNote.g());
                    intent3.putExtra(Const.COMMENT_ENTITY, commentNote);
                    intent3.putExtra("getFull",true);
                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent3);
                    break;
                case 3:
                    Log.e("YellowMain", "Case 3");
                    User user = (User) pushMessage.getObj();
                    if(user != null){
                        Intent intent2 = new Intent(DemoIntentService.this, OtherActivity.class);
                        intent2.putExtra(Const.USER_ID , user.getUser_id());
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
                    }
                    break;
            }

        }
    }
}
