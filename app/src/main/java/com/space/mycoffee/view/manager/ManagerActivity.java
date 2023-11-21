package com.space.mycoffee.view.manager;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.ActivityManagerBinding;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Extensions;

public class ManagerActivity extends AppCompatActivity {
    private ActivityManagerBinding binding;

    private NavController navController;
    private CountDownTimer timer;
    private boolean doubleBackPress = false;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        Extensions.changeStatusBarColor(this, R.color.reduced_price);
        init();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination().getId() == R.id.homeManagerFragment) {
                    if (doubleBackPress) finishAffinity();
                    doubleBackPress = true;
                    Extensions.toast(ManagerActivity.this, R.string.press_back_again_to_exit);
                    startTimer();
                } else navController.popBackStack();
            }
        });
    }

    private void startTimer() {
        try {
            timer = new CountDownTimer(2000L, 1000L) {
                @Override
                public void onTick(long l) {}

                @Override
                public void onFinish() {
                    doubleBackPress = false;
                }
            };
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        AppSingleton.mode = getResources().getStringArray(R.array.mode_login);
        try {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_manager);
            navController = navHostFragment.getNavController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}