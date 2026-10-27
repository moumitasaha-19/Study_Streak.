package com.example.studystreak;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button subjectsButton = findViewById(R.id.subjectsButton);
        Button tasksButton = findViewById(R.id.tasksButton);
        Button timerButton = findViewById(R.id.timerButton);
        Button remindersButton = findViewById(R.id.remindersButton);

        subjectsButton.setOnClickListener(v ->
                startActivity(new Intent(
                        HomeActivity.this, SubjectsActivity.class)));

        tasksButton.setOnClickListener(v ->
                startActivity(new Intent(
                        HomeActivity.this, TasksActivity.class)));

        timerButton.setOnClickListener(v ->
                startActivity(new Intent(
                        HomeActivity.this, TimerActivity.class)));

        remindersButton.setOnClickListener(v ->
                startActivity(new Intent(
                        HomeActivity.this, ReminderActivity.class)));
    }
}