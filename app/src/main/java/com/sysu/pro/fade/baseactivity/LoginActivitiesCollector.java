package com.sysu.pro.fade.baseactivity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 12194 on 2018/1/2.
 * 存放登录过程中产生的活动，便于一次性销毁
 */

public class LoginActivitiesCollector {

    //添加登录过程中产生的活动
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity)
    {
        activities.add(activity);
    }

    //移除活动
    public static void removeActivity(Activity activity)
    {
        activities.remove(activity);
    }

    //全部销毁活动
    public static void finishAll()
    {
        for (Activity activity : activities)
        {
            if (!activity.isFinishing())
            {
                activity.finish();
            }
        }
    }

}
