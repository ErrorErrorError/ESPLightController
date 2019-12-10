package com.errorerrorerror.esplightcontroller.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.DeviceBrightnessFragmentBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.errorerrorerror.iosstyleslider.IOSStyleSlider;
import com.google.android.material.slider.Slider;

import io.reactivex.schedulers.Schedulers;

public class DeviceBrightnessFragment extends BaseFragment<DeviceBrightnessFragmentBinding> {

    public static final String ID_BUNDLE  = "id_bundle";

    private long deviceId = -1;

    private BaseDevice baseDevice;

    @Override
    void containsArguments() {
        deviceId = getArguments().getLong(ID_BUNDLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        baseDevice = viewModel.getDeviceById(deviceId).subscribeOn(Schedulers.io()).blockingGet();

        /*
        binding.brightness.setValueFrom(0);
        binding.brightness.setValueTo(100);

        binding.brightness.setValue(baseDevice.getBrightness());

        binding.brightness.setOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value) {
                disposable.add(viewModel.updateBrightnessLevel((int) value, baseDevice).subscribeOn(Schedulers.io()).subscribe());
            }
        });

         */

        /*
        binding.brightness.setSliderProgress(baseDevice.getBrightness());

        binding.brightness.addOnProgressChanged(new IOSStyleSlider.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(IOSStyleSlider slider, int progress) {
                disposable.add(viewModel.updateBrightnessLevel(progress, baseDevice).subscribeOn(Schedulers.io()).subscribe());
            }

            @Override
            public void onStartTrackingTouch(IOSStyleSlider slider) {

            }

            @Override
            public void onStopTrackingTouch(IOSStyleSlider slider) {

            }
        });

         */
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
