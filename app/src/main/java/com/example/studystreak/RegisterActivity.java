package com.example.studystreak;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText registerEmail, registerPassword;
    Button submitRegisterButton;

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        submitRegisterButton = findViewById(R.id.submitRegisterButton);

        submitRegisterButton.setOnClickListener(v -> {
            String email = registerEmail.getText().toString().trim();
            String password = registerPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            submitRegisterButton.setEnabled(false);

            executor.execute(() -> {
                String message;
                boolean success = false;

                HttpURLConnection connection = null;

                try {
                    URL url = new URL(
                            "http://192.168.0.105/StudyStreakAPI/register.php"
                    );

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setDoOutput(true);
                    connection.setRequestProperty(
                            "Content-Type",
                            "application/x-www-form-urlencoded; charset=UTF-8"
                    );

                    String data =
                            "email=" + URLEncoder.encode(email, "UTF-8")
                                    + "&password=" +
                                    URLEncoder.encode(password, "UTF-8");

                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(data.getBytes(StandardCharsets.UTF_8));
                    }

                    int status = connection.getResponseCode();

                    InputStream stream = status >= 200 && status < 400
                            ? connection.getInputStream()
                            : connection.getErrorStream();

                    StringBuilder response = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }
                    android.util.Log.d("REGISTER_RESPONSE", response.toString());
                    JSONObject json = new JSONObject(response.toString());
                    success = json.optBoolean("success", false);
                    message = json.optString("message", "Unknown response");

                } catch (Exception e) {
                    message = "Connection failed: " + e.getMessage();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

                final boolean result = success;
                final String resultMessage = message;

                handler.post(() -> {
                    submitRegisterButton.setEnabled(true);

                    Toast.makeText(RegisterActivity.this,
                            resultMessage, Toast.LENGTH_LONG).show();

                    if (result) {
                        finish();
                    }
                });
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}