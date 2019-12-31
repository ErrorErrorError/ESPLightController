package com.errorerrorerror.esplightcontroller.ui.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.utils.RxBus;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();

    protected CompositeDisposable disposable = new CompositeDisposable();
    protected T binding;
    protected static final String fragmentTag = "root_tag";

    @Inject
    protected RxBus eventBus;

    @Inject
    protected SharedPreferences preferences;

    @LayoutRes
    protected abstract int getLayoutId();

    @IdRes
    protected abstract int getFragmentContainerId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.MyTheme_DayNight);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    public void addFragmentToTop(BaseFragment fragment) {
        BaseFragment getPreviousFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (getPreviousFragment == null) return;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.anim_enter_fragment, R.anim.anim_exit_fragment, R.anim.anim_pop_enter_fragment, 0)
                .add(getFragmentContainerId(), fragment, fragmentTag)
                .addToBackStack(null)
                .hide(getPreviousFragment)
                .commit();
    }

    public void backToHomeScreen() {
        int backstack = getSupportFragmentManager().getBackStackEntryCount();
        while (backstack > 0) {
            getSupportFragmentManager().popBackStack();
            backstack--;
        }
    }
/*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {

            InputMethodManager imm = ContextCompat.getSystemService(getApplicationContext(), InputMethodManager.class);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
 */

    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        binding.unbind();
    }
}
