package com.example.yuuuuu.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Random;


/**
 * Created by YUNA on 2017-06-25.
 *         by YUUUUU on 2017-08-25.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;
    private Notification.Builder notification;

    public void onReceive(Context context, Intent intent){
        Random random = new Random();

        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            String mHour="";
            String mMin="";
            String month;
            String date;
            String ampm = "  ";
            String sports="";

            mHour = bundle.getString("hour");
            mMin = bundle.getString("min");
            sports = bundle.getString("sport");
            date = bundle.getString("date");

            //System.out.println("date mHour mMin sports " +date +" "+ mHour + " " + mMin+ " " + sports);

            int Hour = 0;

            while(mHour.length() > 0) {
                if (Integer.parseInt(mHour) > 13) {
                    ampm = "오후 ";
                    Hour = Integer.parseInt(mHour) % 12;
                    break;
                } else {
                    ampm = "오전 ";
                    Hour = Integer.parseInt(mHour);
                    break;
                }
            }

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
            notification = new Notification.Builder(context);
            notification.setSmallIcon(R.mipmap.icon);
            notification.setContentTitle("경기 알람");
            notification.setContentText("2월 " + date + "일 "+ ampm + Integer.toString(Hour) + "시" + mMin + "분 " + sports + " 경기 시작합니다");
            notificationManager.notify(random.nextInt(), notification.build());

        }
    }

}
