package com.example.studystreak;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "studystreak_reminders";

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "StudyStreak Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("Study and task reminders");

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showNotification(
            Context context, String title, String message, int notificationId) {

        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        createChannel(context);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(context.getApplicationInfo().icon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(context)
                .notify(notificationId, builder.build());
    }
}