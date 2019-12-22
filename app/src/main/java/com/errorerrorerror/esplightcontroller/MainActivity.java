package com.errorerrorerror.esplightcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.errorerrorerror.esplightcontroller.databinding.ActivityMainBinding;
import com.errorerrorerror.esplightcontroller.utils.DisplayUtils;
import com.errorerrorerror.esplightcontroller.views.BaseFragment;
import com.errorerrorerror.esplightcontroller.views.HomeFragment;
import com.errorerrorerror.esplightcontroller.views.RoundedDrawerArrowDrawable;
import com.errorerrorerror.esplightcontroller.views.SettingsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private static final String fragmentTag = "root_tag";

    @Inject SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((App) getApplication()).getAppComponent().inject(this);

        int defaultValue = preferences.getInt("mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(defaultValue);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupMainFragment();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackCount > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsMenuItem) {
            binding.drawerLayout.closeDrawer(GravityCompat.START, false);
            SettingsFragment settingsFragment = BaseFragment.newInstance(SettingsFragment.class, null);
            addFragmentToTop(settingsFragment);
        }
        return false;
    }

    public void addFragmentToTop(BaseFragment fragment) {
        BaseFragment getPreviousFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
        getSupportFragmentManager()
                .beginTransaction()
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.anim_enter_fragment, R.anim.anim_exit_fragment, R.anim.anim_pop_enter_fragment, R.anim.anim_pop_exit_fragment)
                .add(R.id.frame_container, fragment, fragmentTag)
                .addToBackStack(null)
                .hide(getPreviousFragment)
                .commit();
    }

    private void setupMainFragment() {
        HomeFragment homeFragment = BaseFragment.newInstance(HomeFragment.class, null);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, homeFragment, fragmentTag)
                .commit();
        homeFragment.setRetainInstance(true);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            final int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count > 0) {
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });
    }

    public void backToHomeScreen() {
        int backstack = getSupportFragmentManager().getBackStackEntryCount();
        while (backstack > 0) {
            getSupportFragmentManager().popBackStack();
            backstack--;
        }
    }

    public void setToolbarToDrawer(MaterialToolbar toolbar) {
        ActionBarDrawerToggle navigationToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                float slideX = drawerView.getWidth() * slideOffset;
                float dpRoundedCorner = DisplayUtils.convertDpToPixel(getApplicationContext(), 40) * slideOffset;
                float elevation = DisplayUtils.convertDpToPixel(getApplicationContext(), 20) * slideOffset;
                float scaleXY = 1 - (slideOffset / 6f);
                binding.frameContainer.setTranslationX(slideX);
                binding.frameContainer.setScaleX(scaleXY);
                binding.frameContainer.setScaleY(scaleXY);

                binding.frameContainer.setRadius(dpRoundedCorner);
                binding.frameContainer.setElevation(elevation);
            }

        };

        /// Set up custom menu toggle
        RoundedDrawerArrowDrawable roundedToggleDrawable = new RoundedDrawerArrowDrawable(getApplicationContext());
        float barThickness = DisplayUtils.convertDpToPixel(getApplicationContext(), 3);
        float barLength = DisplayUtils.convertDpToPixel(getApplicationContext(), 18);
        float shortBarWidth = DisplayUtils.convertDpToPixel(getApplicationContext(), 12);
        roundedToggleDrawable.setBarThickness(barThickness);
        roundedToggleDrawable.setBarLength(barLength);
        roundedToggleDrawable.setArrowShaftLength(barLength);
        roundedToggleDrawable.rounded(true);
        roundedToggleDrawable.setShortBarWidth(shortBarWidth);
        roundedToggleDrawable.setColor(Color.rgb(0x4a, 0x4a, 0x4a));

        binding.drawerLayout.setScrimColor(Color.TRANSPARENT);
        binding.drawerLayout.setStatusBarBackground(null);
        binding.drawerLayout.setDrawerElevation(0);

        navigationToggle.setDrawerArrowDrawable(roundedToggleDrawable);
        navigationToggle.syncState();

        binding.drawerLayout.addDrawerListener(navigationToggle);

        binding.navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {

            InputMethodManager imm = ContextCompat.getSystemService(getApplicationContext(), InputMethodManager.class);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
