package com.example.studystreak;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    Button loginButton, registerButton;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();
    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        1001
                );
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        NotificationHelper.createChannel(this);
        requestNotificationPermission();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Enter email and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            loginButton.setEnabled(false);

            executor.execute(() -> {
                boolean success = false;
                String message;
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(
                            "http://10.68.227.215/StudyStreakAPI/login.php");

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setDoOutput(true);
                    connection.setRequestProperty(
                            "Content-Type",
                            "application/x-www-form-urlencoded; charset=UTF-8");

                    String data = "email="
                            + URLEncoder.encode(userEmail, "UTF-8")
                            + "&password="
                            + URLEncoder.encode(userPassword, "UTF-8");

                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(data.getBytes(StandardCharsets.UTF_8));
                    }

                    int status = connection.getResponseCode();
                    InputStream stream = status >= 200 && status < 400
                            ? connection.getInputStream()
                            : connection.getErrorStream();

                    StringBuilder response = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    stream, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    Log.d("LOGIN_RESPONSE", response.toString());

                    JSONObject json = new JSONObject(response.toString());
                    success = json.optBoolean("success", false);
                    message = json.optString(
                            "message", "Unexpected server response");

                } catch (Exception e) {
                    Log.e("LOGIN_ERROR", "Login request failed", e);
                    message = "Connection failed. Check server and IP.";
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

                boolean result = success;
                String resultMessage = message;

                handler.post(() -> {
                    loginButton.setEnabled(true);

                    Toast.makeText(MainActivity.this,
                            resultMessage, Toast.LENGTH_LONG).show();

                    if (result) {
                        Intent intent = new Intent(
                                MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
            });
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}