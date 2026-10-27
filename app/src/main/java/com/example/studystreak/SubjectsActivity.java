package com.example.studystreak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SubjectsActivity extends AppCompatActivity {

    Button algorithmButton, dbmsButton, mobileButton;
    EditText subjectInput;
    Button addSubjectButton;
    LinearLayout customSubjectContainer;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subjects);

        // Find Views
        algorithmButton = findViewById(R.id.algorithmButton);
        dbmsButton = findViewById(R.id.dbmsButton);
        mobileButton = findViewById(R.id.mobileButton);

        subjectInput = findViewById(R.id.subjectInput);
        addSubjectButton = findViewById(R.id.addSubjectButton);

        customSubjectContainer =
                findViewById(R.id.customSubjectContainer);

        // SharedPreferences
        preferences =
                getSharedPreferences(
                        "SubjectData",
                        MODE_PRIVATE
                );

        // Design fixed buttons
        designButton(algorithmButton);
        designButton(dbmsButton);
        designButton(mobileButton);

        // Load saved custom subjects
        loadSubjects();

        // Fixed subject buttons
        algorithmButton.setOnClickListener(v -> {
            openTopicPage("Algorithm Design and Analysis");
        });

        dbmsButton.setOnClickListener(v -> {
            openTopicPage("Database Management System");
        });

        mobileButton.setOnClickListener(v -> {
            openTopicPage("Mobile App Development");
        });

        // Add Subject button
        addSubjectButton.setOnClickListener(v -> {

            String subject =
                    subjectInput.getText()
                            .toString()
                            .trim();

            if (subject.isEmpty()) {

                Toast.makeText(
                        SubjectsActivity.this,
                        "Please enter a subject",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Save locally
            addSubject(subject);
            saveSubject(subject);

            // Save to MySQL
            saveSubjectToDatabase(subject);

            subjectInput.setText("");

            Toast.makeText(
                    SubjectsActivity.this,
                    "Subject Added & Saved!",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // =========================================
    // OPEN TOPIC PAGE
    // =========================================

    private void openTopicPage(String subjectName) {

        Intent intent =
                new Intent(
                        SubjectsActivity.this,
                        TopicActivity.class
                );

        intent.putExtra(
                "subjectName",
                subjectName
        );

        startActivity(intent);
    }

    // =========================================
    // BUTTON DESIGN
    // =========================================

    private void designButton(Button button) {

        button.setTextSize(17);
        button.setAllCaps(false);
        button.setBackgroundTintList(null);

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(225, 240, 255)
        );

        background.setCornerRadius(25);

        button.setBackground(background);

        button.setTextColor(
                Color.rgb(20, 60, 100)
        );

        button.setPadding(
                20,
                20,
                20,
                20
        );
    }

    // =========================================
    // ADD SUBJECT BUTTON
    // =========================================

    private void addSubject(String subject) {

        Button subjectButton =
                new Button(this);

        subjectButton.setText(subject);

        designButton(subjectButton);

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

        customSubjectContainer.addView(
                subjectButton,
                params
        );

        // Click subject
        subjectButton.setOnClickListener(v -> {

            openTopicPage(subject);

        });

        // Long press = Delete
        subjectButton.setOnLongClickListener(v -> {

            new AlertDialog.Builder(
                    SubjectsActivity.this
            )
                    .setTitle("Delete Subject")
                    .setMessage(
                            "Do you want to delete \"" +
                                    subject +
                                    "\"?"
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {

                                customSubjectContainer
                                        .removeView(
                                                subjectButton
                                        );

                                deleteSubject(subject);

                                Toast.makeText(
                                        SubjectsActivity.this,
                                        "Subject Deleted!",
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
    }

    // =========================================
    // SAVE SUBJECT LOCALLY
    // =========================================

    private void saveSubject(String subject) {

        String savedSubjects =
                preferences.getString(
                        "subjects",
                        "[]"
                );

        try {

            JSONArray jsonArray =
                    new JSONArray(savedSubjects);

            jsonArray.put(subject);

            preferences.edit()
                    .putString(
                            "subjects",
                            jsonArray.toString()
                    )
                    .apply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================================
    // LOAD SUBJECTS
    // =========================================

    private void loadSubjects() {

        String savedSubjects =
                preferences.getString(
                        "subjects",
                        "[]"
                );

        try {

            JSONArray jsonArray =
                    new JSONArray(savedSubjects);

            for (int i = 0;
                 i < jsonArray.length();
                 i++) {

                addSubject(
                        jsonArray.getString(i)
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================================
    // DELETE SUBJECT
    // =========================================

    private void deleteSubject(String subject) {

        String savedSubjects =
                preferences.getString(
                        "subjects",
                        "[]"
                );

        try {

            JSONArray oldArray =
                    new JSONArray(savedSubjects);

            JSONArray newArray =
                    new JSONArray();

            boolean removed = false;

            for (int i = 0;
                 i < oldArray.length();
                 i++) {

                String currentSubject =
                        oldArray.getString(i);

                if (!removed &&
                        currentSubject.equals(subject)) {

                    removed = true;

                } else {

                    newArray.put(currentSubject);
                }
            }

            preferences.edit()
                    .putString(
                            "subjects",
                            newArray.toString()
                    )
                    .apply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================================
    // SAVE SUBJECT TO MYSQL
    // =========================================

    private void saveSubjectToDatabase(String subject) {

        new Thread(() -> {

            HttpURLConnection connection = null;

            try {

                String encodedSubject =
                        URLEncoder.encode(
                                subject,
                                "UTF-8"
                        );

                String urlString =
                        "http://192.168.0.108/StudyStreakAPI/add_subject.php";

                URL url =
                        new URL(urlString);

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod("POST");

                connection.setDoOutput(true);

                connection.setConnectTimeout(10000);

                connection.setReadTimeout(10000);

                connection.setRequestProperty(
                        "Content-Type",
                        "application/x-www-form-urlencoded"
                );

                // POST data
                String postData =
                        "user_id=1"
                                + "&subject_name="
                                + encodedSubject;

                connection.getOutputStream()
                        .write(
                                postData.getBytes("UTF-8")
                        );

                // Get response
                int responseCode =
                        connection.getResponseCode();

                BufferedReader reader;

                if (responseCode >= 200 &&
                        responseCode < 400) {

                    reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            connection.getInputStream()
                                    )
                            );

                } else {

                    reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            connection.getErrorStream()
                                    )
                            );
                }

                StringBuilder response =
                        new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {

                    response.append(line);
                }

                reader.close();

                String result =
                        response.toString();

                // Show result on phone
                runOnUiThread(() -> {

                    if (responseCode == 200) {

                        Toast.makeText(
                                SubjectsActivity.this,
                                result,
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        Toast.makeText(
                                SubjectsActivity.this,
                                "Server Error: "
                                        + responseCode
                                        + "\n"
                                        + result,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    Toast.makeText(
                            SubjectsActivity.this,
                            "Database Error:\n"
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });

            } finally {

                if (connection != null) {

                    connection.disconnect();
                }
            }

        }).start();
    }
}