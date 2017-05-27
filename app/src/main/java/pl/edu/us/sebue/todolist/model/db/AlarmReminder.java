package pl.edu.us.sebue.todolist.model.db;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import pl.edu.us.sebue.todolist.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Sebue on 26.05.2017.
 */

public class AlarmReminder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String title = extras.getString("Title");
        String text = extras.getString("Text");
        createNotification(title, text, context);
    }

    protected void createNotification(String title, String text, Context context) {
        Notification noti = new Notification.Builder(context)
                .setContentTitle("ToDo: " + title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_remove_circle_black_48dp)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);
    }
}
