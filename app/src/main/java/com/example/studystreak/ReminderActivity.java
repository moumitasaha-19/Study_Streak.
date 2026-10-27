package com.example.studystreak;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReminderActivity extends AppCompatActivity {

    private TimePicker studyTimePicker;
    private TimePicker taskTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        studyTimePicker = findViewById(R.id.studyTimePicker);
        taskTimePicker = findViewById(R.id.taskTimePicker);

        Button setStudyButton = findViewById(R.id.setStudyButton);
        Button setTaskButton = findViewById(R.id.setTaskButton);

        studyTimePicker.setIs24HourView(false);
        taskTimePicker.setIs24HourView(false);

        setStudyButton.setOnClickListener(v -> {
            int hour = studyTimePicker.getHour();
            int minute = studyTimePicker.getMinute();

            ReminderScheduler.scheduleDaily(
                    ReminderActivity.this,
                    hour,
                    minute,
                    "Study",
                    2001
            );

            Toast.makeText(
                    ReminderActivity.this,
                    "Study reminder set!",
                    Toast.LENGTH_SHORT
            ).show();
        });

        setTaskButton.setOnClickListener(v -> {
            int hour = taskTimePicker.getHour();
            int minute = taskTimePicker.getMinute();

            ReminderScheduler.scheduleDaily(
                    ReminderActivity.this,
                    hour,
                    minute,
                    "Task",
                    2002
            );

            Toast.makeText(
                    ReminderActivity.this,
                    "Task reminder set!",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}