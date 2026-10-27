package com.example.studystreak;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

public class TasksActivity extends AppCompatActivity {

    EditText taskInput;
    Button addTaskButton;
    LinearLayout taskContainer;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        taskInput = findViewById(R.id.taskInput);
        addTaskButton = findViewById(R.id.addTaskButton);
        taskContainer = findViewById(R.id.taskContainer);

        preferences = getSharedPreferences("TaskData", MODE_PRIVATE);

        loadTasks();

        addTaskButton.setOnClickListener(v -> {

            String task = taskInput.getText().toString().trim();

            if (!task.isEmpty()) {

                addTask(task);
                saveTask(task);
                taskInput.setText("");

                Toast.makeText(TasksActivity.this,
                        "Task Saved!",
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(TasksActivity.this,
                        "Please enter a task",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTask(String task) {

        LinearLayout taskRow = new LinearLayout(this);
        taskRow.setOrientation(LinearLayout.HORIZONTAL);
        taskRow.setGravity(Gravity.CENTER_VERTICAL);
        taskRow.setPadding(0, 10, 0, 10);

        TextView taskText = new TextView(this);
        taskText.setText("• " + task);
        taskText.setTextSize(17);
        taskText.setTextColor(Color.BLACK);

        Button completeButton = new Button(this);
        completeButton.setText("✓");

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");

        taskRow.addView(taskText,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1));

        taskRow.addView(completeButton);
        taskRow.addView(deleteButton);

        taskContainer.addView(taskRow);

        completeButton.setOnClickListener(v -> {
            taskText.setText("✓ " + task);
            taskText.setTextColor(Color.GRAY);
            completeButton.setEnabled(false);
        });

        deleteButton.setOnClickListener(v -> {
            taskContainer.removeView(taskRow);
            deleteTask(task);

            Toast.makeText(TasksActivity.this,
                    "Task Deleted!",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void saveTask(String task) {

        String savedTasks = preferences.getString("tasks", "[]");

        try {
            JSONArray jsonArray = new JSONArray(savedTasks);
            jsonArray.put(task);

            preferences.edit()
                    .putString("tasks", jsonArray.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {

        String savedTasks = preferences.getString("tasks", "[]");

        try {
            JSONArray jsonArray = new JSONArray(savedTasks);

            for (int i = 0; i < jsonArray.length(); i++) {
                addTask(jsonArray.getString(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteTask(String task) {

        String savedTasks = preferences.getString("tasks", "[]");

        try {
            JSONArray oldArray = new JSONArray(savedTasks);
            JSONArray newArray = new JSONArray();

            boolean removed = false;

            for (int i = 0; i < oldArray.length(); i++) {

                String currentTask = oldArray.getString(i);

                if (!removed && currentTask.equals(task)) {
                    removed = true;
                } else {
                    newArray.put(currentTask);
                }
            }

            preferences.edit()
                    .putString("tasks", newArray.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}