package com.errorerrorerror.esplightcontroller.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.MainActivity;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.DeviceConfigurationBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.jakewharton.rxbinding3.appcompat.RxToolbar;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxCompoundButton;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class DeviceConfigurationFragment extends BaseFragment<DeviceConfigurationBinding> {

    private String tabName;
    private long id = -1;

    private enum LayoutState {
        MODE,
        BRIGHTNESS,
        TIMER
    }

    private LayoutState currentLayout;

    @Override
    void containsArguments() {
        tabName = getArguments().getString(TITLE_BUNDLE);
        id = getArguments().getLong(ID_BUNDLE);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.device_configuration;
    }

    @Override
    public void injectFragment() {
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void initToolbar() {
        binding.includeLayout.toolbar.setTitle(tabName);
        binding.includeLayout.toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_arrow_icon);
        disposable.add(RxToolbar.navigationClicks(binding.includeLayout.toolbar).subscribe(unit -> getActivity().onBackPressed()));

        binding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                LayoutState setState;
                View checkedView = binding.toggleGroup.findViewById(checkedId);

                if (checkedView == binding.modeButton) {
                    setState = LayoutState.MODE;
                } else if (checkedView == binding.brightnessButton) {
                    setState = LayoutState.BRIGHTNESS;
                } else {
                    setState = LayoutState.TIMER;
                }

                setButtonSelection(setState);
            }
        });

        binding.toggleGroup.check(binding.brightnessButton.getId());
    }


    private void setChildFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "root_child").commit();
    }

    private void setButtonSelection(LayoutState state) {
        if (currentLayout != state) {
            currentLayout = state;

            Bundle bundle = new Bundle();
            switch (currentLayout) {
                case MODE:
                    bundle.putLong(ID_BUNDLE, id);
                    DeviceModeFragment modeFragment = BaseFragment.newInstance(DeviceModeFragment.class, bundle);
                    setChildFragment(modeFragment);
                    break;
                case BRIGHTNESS:
                    bundle.putLong(ID_BUNDLE, id);
                    DeviceBrightnessFragment brightnessFragment = BaseFragment.newInstance(DeviceBrightnessFragment.class, bundle);
                    setChildFragment(brightnessFragment);
                    break;
                case TIMER:
                    break;
            }
        }
    }
}
