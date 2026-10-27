package com.example.studystreak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderType = intent.getStringExtra("reminderType");

        if (reminderType == null) {
            reminderType = "Study";
        }

        if (reminderType.equals("Task")) {
            NotificationHelper.showNotification(
                    context,
                    "StudyStreak Task Reminder",
                    "Remember to complete your task!",
                    2002
            );
        } else {
            NotificationHelper.showNotification(
                    context,
                    "StudyStreak Study Reminder",
                    "It's time to study!",
                    2001
            );
        }
    }
}