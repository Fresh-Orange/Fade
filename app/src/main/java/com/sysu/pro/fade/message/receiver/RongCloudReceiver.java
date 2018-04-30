package com.sysu.pro.fade.message.receiver;

import android.content.Context;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

public class RongCloudReceiver extends PushMessageReceiver {

	@Override
	public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationessage) {
		return false;
	}

	@Override
	public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
		return false;
	}
}
