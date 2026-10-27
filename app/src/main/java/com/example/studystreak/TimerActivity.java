package com.example.studystreak;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TimerActivity extends AppCompatActivity {

    TextView timerText;
    Button startButton, resetButton;

    CountDownTimer countDownTimer;

    long timeLeft = 25 * 60 * 1000; // 25 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timerText = findViewById(R.id.timerText);
        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);

        startButton.setOnClickListener(v -> {

            countDownTimer = new CountDownTimer(timeLeft, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeft = millisUntilFinished;

                    int minutes = (int) (timeLeft / 1000) / 60;
                    int seconds = (int) (timeLeft / 1000) % 60;

                    timerText.setText(
                            String.format("%02d:%02d", minutes, seconds)
                    );
                }

                @Override
                public void onFinish() {
                    timerText.setText("00:00");
                }

            }.start();

            startButton.setEnabled(false);
        });

        resetButton.setOnClickListener(v -> {

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            timeLeft = 25 * 60 * 1000;
            timerText.setText("25:00");
            startButton.setEnabled(true);
        });
    }
}