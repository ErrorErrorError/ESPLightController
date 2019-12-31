package com.errorerrorerror.esplightcontroller.ui.modules.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.ActivityMainBinding;
import com.errorerrorerror.esplightcontroller.ui.base.BaseActivity;
import com.errorerrorerror.esplightcontroller.ui.base.BaseFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.settings.SettingsActivity;
import com.errorerrorerror.esplightcontroller.ui.widgets.RoundedDrawerArrowDrawable;
import com.errorerrorerror.esplightcontroller.utils.DisplayUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener {

    private int insetTop = 0;
    private int insetBottom = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.mainContainer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((App) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);

        int defaultValue = preferences.getInt(getString(R.string.theme_preferences), AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(defaultValue);

        setupMainFragment();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        disableNavigationViewScrollbars(binding.navigationView);

        binding.mainContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                insetTop = (insets.getSystemWindowInsetTop() == 0) ? insetTop : insets.getSystemWindowInsetTop();
                insetBottom = (insets.getSystemWindowInsetBottom() == 0) ? insetBottom : insets.getSystemWindowInsetBottom();
                for (int i = 0; i < binding.mainContainer.getChildCount(); i++) {
                    final View view = binding.mainContainer.getChildAt(i);
                    final ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    marginLayoutParams.setMargins(marginLayoutParams.leftMargin, insetTop, marginLayoutParams.rightMargin , insetBottom);
                    view.setLayoutParams(marginLayoutParams);
                }

                return insets.consumeSystemWindowInsets();
            }
        });
    }

    private static void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsMenuItem) {
            binding.drawerLayout.closeDrawer(GravityCompat.START, true);
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), SettingsActivity.class);
            disposable.add(Completable.timer(200, TimeUnit.MILLISECONDS).subscribe(() -> startActivity(intent)));
        }
        return false;
    }

    private void setupMainFragment() {
        HomeFragment homeFragment = BaseFragment.newInstance(HomeFragment.class, null);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(getFragmentContainerId(), homeFragment, fragmentTag)
                .commit();
//        homeFragment.setRetainInstance(true);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            final int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count > 0) {
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });
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
                binding.mainContainer.setTranslationX(slideX);
                binding.mainContainer.setScaleX(scaleXY);
                binding.mainContainer.setScaleY(scaleXY);

                binding.mainContainer.setRadius(dpRoundedCorner);
                binding.mainContainer.setElevation(elevation);
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
        roundedToggleDrawable.setColor(getColorFromAttr(R.attr.textIconColorSync, this));

        binding.drawerLayout.setScrimColor(Color.TRANSPARENT);
        binding.drawerLayout.setStatusBarBackground(null);
        binding.drawerLayout.setDrawerElevation(0);

        navigationToggle.setDrawerArrowDrawable(roundedToggleDrawable);
        navigationToggle.syncState();

        binding.drawerLayout.addDrawerListener(navigationToggle);

        binding.navigationView.setNavigationItemSelectedListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = insets.getSystemWindowInsetTop();
            return insets.consumeSystemWindowInsets();
        });
    }

    @ColorInt
    private static int getColorFromAttr(@AttrRes int attr, @NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}
