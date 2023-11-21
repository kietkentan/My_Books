package com.space.mycoffee.view.main;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.space.mycoffee.R;
import com.space.mycoffee.databinding.ActivityMainBinding;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Extensions;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private NavController navController;
    private CountDownTimer timer;
    private boolean doubleBackPress = false;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        init();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination().getId() == R.id.homePageFragment) {
                    if (doubleBackPress) finishAffinity();
                    doubleBackPress = true;
                    Extensions.toast(MainActivity.this, R.string.press_back_again_to_exit);
                    startTimer();
                } else navController.popBackStack();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void init() {
        AppSingleton.mode = getResources().getStringArray(R.array.mode_login);
        try {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
            navController = navHostFragment.getNavController();
            navController.addOnDestinationChangedListener((navController, navDestination, bundle) ->
                    MainActivity.this.onDestinationChanged(navDestination.getId())
            );
            binding.bottomNavigation.setOnNavigationItemSelectedListener(onBottomNavigationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void updateBottomNav(boolean isShowing) {
        binding.bottomNavigation.setVisibility(isShowing ? View.VISIBLE : View.GONE);
    }

    private boolean isNotSameDestination(int destination) {
        return destination != navController.getCurrentDestination().getId();
    }

    @SuppressLint("NonConstantResourceId")
    private void onDestinationChanged(int currentDestination) {
        try {
            switch (currentDestination) {
                case R.id.homePageFragment:
                    binding.bottomNavigation.setSelectedItemId(R.id.home_page);
                    Extensions.defaultStatusBarColor(this);
                    updateBottomNav(true);
                    break;

                case R.id.favoriteFragment:
                    binding.bottomNavigation.setSelectedItemId(R.id.favorite_page);
                    Extensions.changeStatusBarColor(this, R.color.reduced_price);
                    updateBottomNav(true);
                    break;

                case R.id.userFragment:
                    binding.bottomNavigation.setSelectedItemId(R.id.user_page);
                    Extensions.defaultStatusBarColor(this);
                    updateBottomNav(true);
                    break;

                case R.id.fragmentCart:
                    binding.bottomNavigation.setSelectedItemId(R.id.cart_page);
                    Extensions.changeStatusBarColor(this, R.color.reduced_price);
                    updateBottomNav(true);
                    break;

                case R.id.orderStatusFragment:
                case R.id.completePaymentFragment:
                case R.id.noteAddressFragment:
                case R.id.choseAddressFragment:
                case R.id.addAddressFragment:
                case R.id.addressFragment:
                    Extensions.changeStatusBarColor(this, R.color.reduced_price);
                    updateBottomNav(false);
                    break;

                default:
                    Extensions.defaultStatusBarColor(this);
                    updateBottomNav(false);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @SuppressWarnings("deprecation")
    private final BottomNavigationView.OnNavigationItemSelectedListener onBottomNavigationListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.home_page:
                        if (isNotSameDestination(R.id.homePageFragment)) {
                            navController.navigate(R.id.homePageFragment);
                        }
                        return true;

                    case R.id.favorite_page:
                        if (AppSingleton.currentUser == null) {
                            navController.navigate(R.id.signInSignUpFragment);
                        } else if (isNotSameDestination(R.id.favoriteFragment)) {
                            navController.navigate(R.id.favoriteFragment);
                        }
                        return true;

                    case R.id.user_page:
                        if (isNotSameDestination(R.id.userFragment)) {
                            navController.navigate(R.id.userFragment);
                        }
                        return true;

                    case R.id.cart_page:
                        if (AppSingleton.currentUser == null) {
                            navController.navigate(R.id.signInSignUpFragment);
                        } else if (isNotSameDestination(R.id.fragmentCart)) {
                            navController.navigate(R.id.fragmentCart);
                        }
                        return true;

                    default:
                        return true;
                }
            };
}
