package com.example.studystreak;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TopicActivity extends AppCompatActivity {

    TextView topicTitle;
    EditText topicInput;
    Button addTopicButton;
    LinearLayout topicContainer;

    SharedPreferences preferences;

    String subjectName;

    // Same PC IP that worked for your Subject database
    String API_URL =
            "http://192.168.0.108/StudyStreakAPI/add_topic.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_topic);

        topicTitle = findViewById(R.id.topicTitle);
        topicInput = findViewById(R.id.topicInput);
        addTopicButton = findViewById(R.id.addTopicButton);
        topicContainer = findViewById(R.id.topicContainer);

        subjectName = getIntent()
                .getStringExtra("subjectName");

        if (subjectName == null) {
            subjectName = "Subject";
        }

        topicTitle.setText("📘 " + subjectName);

        preferences = getSharedPreferences(
                "TopicData_" + subjectName,
                MODE_PRIVATE
        );

        loadTopics();

        addTopicButton.setOnClickListener(v -> {

            String topic = topicInput
                    .getText()
                    .toString()
                    .trim();

            if (!topic.isEmpty()) {

                addTopic(topic, false);

                saveTopic(
                        topic,
                        false,
                        ""
                );

                // Save topic to MySQL
                saveTopicToDatabase(topic);

                topicInput.setText("");

                Toast.makeText(
                        TopicActivity.this,
                        "Topic Added & Saved!",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        TopicActivity.this,
                        "Please enter a topic",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void saveTopicToDatabase(String topic) {

        new Thread(() -> {

            try {

                URL url = new URL(API_URL);

                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");

                connection.setDoOutput(true);

                connection.setConnectTimeout(5000);

                connection.setReadTimeout(5000);

                String data =
                        "user_id=" +
                                URLEncoder.encode(
                                        "1",
                                        "UTF-8"
                                )

                                + "&subject_name=" +
                                URLEncoder.encode(
                                        subjectName,
                                        "UTF-8"
                                )

                                + "&topic_name=" +
                                URLEncoder.encode(
                                        topic,
                                        "UTF-8"
                                );

                OutputStream outputStream =
                        connection.getOutputStream();

                outputStream.write(
                        data.getBytes("UTF-8")
                );

                outputStream.flush();

                outputStream.close();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()
                                )
                        );

                StringBuilder response =
                        new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {

                    response.append(line);
                }

                reader.close();

                String result =
                        response.toString().trim();

                runOnUiThread(() -> {

                    Toast.makeText(
                            TopicActivity.this,
                            result,
                            Toast.LENGTH_SHORT
                    ).show();

                });

                connection.disconnect();

            } catch (Exception e) {

                runOnUiThread(() -> {

                    Toast.makeText(
                            TopicActivity.this,
                            "Database Error: " +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
            }

        }).start();
    }

    private void addTopic(
            String topic,
            boolean completed
    ) {

        LinearLayout topicRow =
                new LinearLayout(this);

        topicRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        topicRow.setGravity(
                android.view.Gravity.CENTER_VERTICAL
        );

        CheckBox completeBox =
                new CheckBox(this);

        completeBox.setChecked(completed);

        TextView topicText =
                new TextView(this);

        topicText.setText("📖 " + topic);

        topicText.setTextSize(16);

        topicText.setTextColor(
                Color.rgb(20, 60, 100)
        );

        Button notesButton =
                new Button(this);

        notesButton.setText("Notes");

        notesButton.setTextSize(13);

        notesButton.setAllCaps(false);

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(225, 240, 255)
        );

        background.setCornerRadius(25);

        topicRow.setBackground(background);

        topicRow.setPadding(
                5,
                5,
                5,
                5
        );

        topicRow.addView(
                completeBox,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        topicRow.addView(
                topicText,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        topicRow.addView(
                notesButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                8,
                0,
                8
        );

        topicContainer.addView(
                topicRow,
                params
        );

        completeBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {

                        topicText.setPaintFlags(
                                topicText.getPaintFlags()
                                        | Paint.STRIKE_THRU_TEXT_FLAG
                        );

                        updateTopic(
                                topic,
                                true
                        );

                        Toast.makeText(
                                TopicActivity.this,
                                "Topic Completed ✓",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        topicText.setPaintFlags(
                                topicText.getPaintFlags()
                                        & (~Paint.STRIKE_THRU_TEXT_FLAG)
                        );

                        updateTopic(
                                topic,
                                false
                        );
                    }
                }
        );

        notesButton.setOnClickListener(v ->
                showNotesDialog(topic)
        );

        topicRow.setOnLongClickListener(v -> {

            new AlertDialog.Builder(
                    TopicActivity.this
            )
                    .setTitle("Delete Topic")
                    .setMessage(
                            "Do you want to delete \"" +
                                    topic +
                                    "\"?"
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {

                                topicContainer.removeView(
                                        topicRow
                                );

                                deleteTopic(topic);

                                Toast.makeText(
                                        TopicActivity.this,
                                        "Topic Deleted!",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                    )
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .show();

            return true;
        });

        if (completed) {

            topicText.setPaintFlags(
                    topicText.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );
        }
    }

    private void saveTopic(
            String topic,
            boolean completed,
            String notes
    ) {

        String savedTopics =
                preferences.getString(
                        "topics",
                        "[]"
                );

        try {

            JSONArray jsonArray =
                    new JSONArray(savedTopics);

            JSONObject topicObject =
                    new JSONObject();

            topicObject.put(
                    "topic",
                    topic
            );

            topicObject.put(
                    "completed",
                    completed
            );

            topicObject.put(
                    "notes",
                    notes
            );

            jsonArray.put(topicObject);

            preferences.edit()
                    .putString(
                            "topics",
                            jsonArray.toString()
                    )
                    .apply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void loadTopics() {

        String savedTopics =
                preferences.getString(
                        "topics",
                        "[]"
                );

        try {

            JSONArray jsonArray =
                    new JSONArray(savedTopics);

            for (int i = 0;
                 i < jsonArray.length();
                 i++) {

                JSONObject object =
                        jsonArray.getJSONObject(i);

                String topic =
                        object.getString("topic");

                boolean completed =
                        object.getBoolean("completed");

                addTopic(
                        topic,
                        completed
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void updateTopic(
            String topic,
            boolean completed
    ) {

        String savedTopics =
                preferences.getString(
                        "topics",
                        "[]"
                );

        try {

            JSONArray array =
                    new JSONArray(savedTopics);

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.getJSONObject(i);

                if (object.getString("topic")
                        .equals(topic)) {

                    object.put(
                            "completed",
                            completed
                    );

                    break;
                }
            }

            preferences.edit()
                    .putString(
                            "topics",
                            array.toString()
                    )
                    .apply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void showNotesDialog(String topic) {

        EditText notesInput =
                new EditText(this);

        notesInput.setHint(
                "Write your notes here..."
        );

        notesInput.setMinLines(4);

        String oldNotes =
                getNotes(topic);

        notesInput.setText(oldNotes);

        new AlertDialog.Builder(
                TopicActivity.this
        )
                .setTitle("📝 Notes - " + topic)
                .setView(notesInput)
                .setPositiveButton(
                        "Save",
                        (dialog, which) -> {

                            String notes =
                                    notesInput
                                            .getText()
                                            .toString();

                            saveNotes(
                                    topic,
                                    notes
                            );

                            Toast.makeText(
                                    TopicActivity.this,
                                    "Notes Saved!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    private String getNotes(String topic) {

        String savedTopics =
                preferences.getString(
                        "topics",
                        "[]"
                );

        try {

            JSONArray array =
                    new JSONArray(savedTopics);

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.getJSONObject(i);

                if (object.getString("topic")
                        .equals(topic)) {

                    return object.optString(
                            "notes",
                            ""
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "";
    }

    private void saveNotes(
            String topic,
            String notes
    ) {

        String savedTopics =
                preferences.getString(
                        "topics",
                        "[]"
                );

        try {

            JSONArray array =
                    new JSONArray(savedTopics);

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.getJSONObject(i);

                if (object.getString("topic")
                        .equals(topic)) {

                    object.put(
                            "notes",
                            notes
                    );

                    break;
                }
            }

            preferences.edit()
                    .putString(
                            "topics",
                            array.toString()
                    )
                    .apply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void deleteTopic(String topic) {

        String savedTopics =
                preferences.getString(
                        "topics",
                        "[]"
                );

        try {

            JSONArray oldArray =
                    new JSONArray(savedTopics);

            JSONArray newArray =
                    new JSONArray();

            boolean removed = false;

            for (int i = 0;
                 i < oldArray.length();
                 i++) {

                JSONObject object =
                        oldArray.getJSONObject(i);

                String currentTopic =
                        object.getString("topic");

                if (!removed &&
                        currentTopic.equals(topic)) {

                    removed = true;

                } else {

                    newArray.put(object);
                }
            }

            preferences.edit()
                    .putString(
                            "topics",
                            newArray.toString()
                    )
                    .apply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}