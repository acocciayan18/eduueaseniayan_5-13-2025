package com.example.eduease;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isFreshLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Check if a user is logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is logged in, navigate to Home
            Intent homeIntent = new Intent(this, Home.class);
            homeIntent.putExtra("user_email", user.getEmail());
            homeIntent.putExtra("user_name", user.getDisplayName());

            // You can pass profile image if you store it somewhere
            String profileImageUrl = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "";
            homeIntent.putExtra("PROFILE_IMAGE_URL", profileImageUrl);

            // Clear back stack so back button won't return to login/signup
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);

            // Finish this activity so it doesn't show behind Home
            finish();
            return;
        }

        // User is not logged in, continue showing this screen
        boolean fromSettings = getIntent().getBooleanExtra("from_settings", false);
        if (isFreshLaunch && !fromSettings) {
            playOpeningSound();
        }

        setContentView(R.layout.activity_main);
        applyEdgeToEdgePadding();
        setupGetStartedButton();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        isFreshLaunch = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playOpeningSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.opening_music);
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            mediaPlayer = null;
        });
        mediaPlayer.start();
    }

    private void applyEdgeToEdgePadding() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupGetStartedButton() {
        Button getStartedBtn = findViewById(R.id.getStarted_btn);
        getStartedBtn.setOnClickListener(view -> {
            vibratePhone();
            navigateToCreateAccount();
        });
    }

    private void vibratePhone() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    private void navigateToCreateAccount() {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }
}
