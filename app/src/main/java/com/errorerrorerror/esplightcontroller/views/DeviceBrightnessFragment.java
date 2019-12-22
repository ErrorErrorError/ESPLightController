package com.errorerrorerror.esplightcontroller.views;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.DeviceBrightnessFragmentBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.errorerrorerror.ioslider.IOSlider;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DeviceBrightnessFragment extends BaseFragment<DeviceBrightnessFragmentBinding> {

    public static final String ID_BUNDLE  = "id_bundle";
    private long deviceId = -1;


    @Override
    void containsArguments() {
        deviceId = getArguments().getLong(ID_BUNDLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BaseDevice baseDevice = viewModel.getDeviceById(deviceId).subscribeOn(Schedulers.io()).doOnError(throwable -> {
            Log.e(TAG, "accept: ", throwable);
            Toast.makeText(getContext(), "An Error Occurred", Toast.LENGTH_LONG).show();
            getActivity().onBackPressed();
        }).blockingGet();

        binding.brightness.setProgress(baseDevice.getBrightness());

        binding.brightness.setOnProgressChangeListener(new IOSlider.OnChangeListener() {
            @Override
            public void onProgressChanged(IOSlider slider, float progress) {
                disposable.add(viewModel.updateBrightnessLevel(Math.round(progress), baseDevice).subscribeOn(Schedulers.io()).subscribe());
            }

            @Override
            public void onStartTrackingTouch(IOSlider slider) {

            }

            @Override
            public void onStopTrackingTouch(IOSlider slider) {

            }
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.device_brightness_fragment;
    }

    @Override
    public void injectFragment() {
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }
}
