package com.sysu.pro.fade.message.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.PushMessage;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.activity.DetailActivity;
import com.sysu.pro.fade.message.Activity.CommentActivity;
import com.sysu.pro.fade.message.Activity.ContributionActivity;
import com.sysu.pro.fade.message.Activity.FansActivity;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by yellow on 2018/4/29.
 */

public class StaticReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent receivedIntent) {
        if (receivedIntent.getAction().equals(Const.STATICACTION)) {
            String title = "";
            String text = "";
            //获取数据
            Bundle bundle = receivedIntent.getBundleExtra("data");
            PushMessage pushMessage = (PushMessage) bundle.getSerializable("pushMessage");

            //设定传递对象
            Intent intent = null;

            switch (pushMessage.getMsgId()) {
                case 1:
                    //点赞
                    Log.e("YellowMain", "Case 1");
                    intent = new Intent(context, ContributionActivity.class);
//                    Note contributionNote = (Note) pushMessage.getObj();
                    Note contributionNote = ((JSONObject)pushMessage.getObj()).toJavaObject(Note.class);
//                    intent = new Intent(context, DetailActivity.class);
//                    intent.putExtra(Const.NOTE_ID,contributionNote.getOrigin().getNote_id());
//                    intent.putExtra(Const.IS_COMMENT,false);
//                    intent.putExtra(Const.COMMENT_NUM, contributionNote.getComment_num());
//                    intent.putExtra(Const.COMMENT_ENTITY, contributionNote);
//                    intent.putExtra("getFull",true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    title = "Fade";
                    text = contributionNote.getNickname() + "续了你一秒";
                    break;
                case 2:
                    //新评论
                    Log.e("YellowMain", "Case 2");
//                    intent = new Intent(context, Com.class);
                    Note commentNote = ((JSONObject)pushMessage.getObj()).toJavaObject(Note.class);
                    intent = new Intent(context, CommentActivity.class);
                    intent.putExtra(Const.NOTE_ID,commentNote.getNote_id());
                    intent.putExtra(Const.IS_COMMENT,false);
                    intent.putExtra(Const.COMMENT_NUM, commentNote.getComment_num());
                    intent.putExtra(Const.COMMENT_ENTITY, commentNote);
                    intent.putExtra("getFull",false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    title = "Fade";
                    text = "你有一条新评论";
                    break;
                case 3:
                    //新粉丝
                    Log.e("YellowMain", "Case 3");
//                    User user = (User) pushMessage.getObj();
                    User user = ((JSONObject)pushMessage.getObj()).toJavaObject(User.class);
                    if(user != null){
                        intent = new Intent(context, FansActivity.class);
//                        intent = new Intent(context, OtherActivity.class);
//                        intent.putExtra(Const.USER_ID , user.getUser_id());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        title = "Fade";
                        text = "你有一位新粉丝";
                    }
                    break;
            }

            //将Intent封装，异步处理
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, FLAG_UPDATE_CURRENT);

            Resources resources = context.getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.push);
            //设定通知内容
            Notification notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.push_small)     //设置小ICON
                    .setLargeIcon(bitmap)       //设置大ICON
                    .setContentTitle(title)   //标题
                    .setContentText(text)    //内容
                    .setAutoCancel(true)        //可自动取消
                    .setContentIntent(pendingIntent)    //根据pendingIntent来设置活动跳转
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build();

//            /**
//             * vibrate属性是一个长整型的数组，用于设置手机静止和振动的时长，以毫秒为单位。
//             * 参数中下标为0的值表示手机静止的时长，下标为1的值表示手机振动的时长， 下标为2的值又表示手机静止的时长，以此类推。
//             */
//            long[] vibrates = { 0, 1000, 1000, 1000 };
//            notification.vibrate = vibrates;
//
//            /**
//             * 手机处于锁屏状态时， LED灯就会不停地闪烁， 提醒用户去查看手机,下面是绿色的灯光一 闪一闪的效果
//             */
//            notification.ledARGB = Color.GREEN;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
//            notification.ledOnMS = 1000;// 指定 LED 灯亮起的时长，以毫秒为单位
//            notification.ledOffMS = 1000;// 指定 LED 灯暗去的时长，也是以毫秒为单位
//            notification.flags = Notification.DEFAULT_ALL;// 指定通知的一些行为，其中就包括显示

            //获取状态通知栏管理
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            //绑定notification,发送请求
            notificationManager.notify(0, notification);

        }
    }
}
