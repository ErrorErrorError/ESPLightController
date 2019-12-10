package com.errorerrorerror.esplightcontroller.views;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.errorerrorerror.colorpicker.MultiColorPickerView;
import com.errorerrorerror.colorpicker.ColorWheelSelector;
import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.ConfigSolidLayoutBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.errorerrorerror.esplightcontroller.model.device_solid.DeviceSolid;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConfigSolidFragment extends BaseFragment<ConfigSolidLayoutBinding> {

    private long deviceId = -1;

    private DeviceSolid deviceSolid;

    @Override
    public int getLayoutRes() {
        return R.layout.config_solid_layout;
    }

    @Override
    public void injectFragment() {
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    void containsArguments() {
        deviceId = getArguments().getLong(ID_BUNDLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BaseDevice baseDevice = viewModel.getDeviceById(deviceId).subscribeOn(Schedulers.io()).blockingGet();

        if (!(baseDevice instanceof DeviceSolid)) {
            deviceSolid = new DeviceSolid(baseDevice, Color.RED);
            disposable.add(viewModel.deleteDevice(baseDevice)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .andThen(Observable.just(deviceSolid)).flatMapCompletable(newDevice -> viewModel.insertDevice(newDevice)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                    ).subscribe());

        } else {
            deviceSolid = (DeviceSolid) baseDevice;
        }

        binding.solidColorPicker.setColorPickerMode(MultiColorPickerView.HUE_SATURATION);
        binding.solidColorPicker.setMainSelectorColor(deviceSolid.getColor());

        binding.solidColorPicker.addOnSelectorColorChangedListener((selector, color, index) -> {
            deviceSolid.setColor(color);
            disposable.add(viewModel.updateDevice(deviceSolid).subscribeOn(Schedulers.io()).subscribe());
            Log.v("ConfigSolidFragment", "Selector: " + selector.getId() + " Color: " + Color.valueOf(color) + " Index: " + index);
        });
    }
}
