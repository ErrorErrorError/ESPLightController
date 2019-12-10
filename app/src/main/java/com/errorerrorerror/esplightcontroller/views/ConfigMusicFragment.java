package com.errorerrorerror.esplightcontroller.views;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.errorerrorerror.colorpicker.ColorWheelSelector;
import com.errorerrorerror.colorpicker.MultiColorPickerView;
import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.ConfigMusicLayoutBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.errorerrorerror.esplightcontroller.model.device_music.DeviceMusic;
import com.google.android.material.chip.Chip;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxCompoundButton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ConfigMusicFragment extends BaseFragment<ConfigMusicLayoutBinding> {

    private long deviceId = -1;

    private DeviceMusic deviceMusic;

    @Override
    public int getLayoutRes() {
        return R.layout.config_music_layout;
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

        initializeViews();

        binding.colorPickerMusic.addOnSelectorColorChangedListener((selector, color, index) -> updateViews(color,index));
    }

    private void updateViews(@ColorInt int color, int index) {
        if (index == 0) {
            deviceMusic.setLowColor(color);
        } else if (index == 1) {
            deviceMusic.setMidColor(color);
        } else if (index == 2) {
            deviceMusic.setHighColor(color);
        }

        updateChipView(color, index);
        updateDeviceDatabase();
    }

    private void updateChipView(@ColorInt int color, int index) {
        final Chip chip = ((Chip)binding.chipGroup.getChildAt(index));
        chip.setChipBackgroundColor(ColorStateList.valueOf(color));

        float[] hsv = {0,0,0};

        ColorUtils.colorToHSL(color, hsv);
        if (hsv[2] > 0.5) {
            chip.setTextColor(Color.BLACK);

        } else {
            chip.setTextColor(Color.WHITE);
        }
    }

    private void initializeViews() {
        BaseDevice baseDevice = viewModel.getDeviceById(deviceId).subscribeOn(Schedulers.io()).blockingGet();

        if (!(baseDevice instanceof DeviceMusic)) {
            deviceMusic = new DeviceMusic(baseDevice, Color.RED, Color.GREEN, Color.BLUE, 0);
            disposable.add(viewModel.deleteDevice(baseDevice)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .andThen(Observable.just(deviceMusic)).flatMapCompletable(newDevice -> viewModel.insertDevice(newDevice)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                    ).subscribe());
        } else {
            deviceMusic = (DeviceMusic) baseDevice;
        }

        binding.colorPickerMusic.setMainSelectorColor(deviceMusic.getLowColor());
        binding.colorPickerMusic.addSelector(deviceMusic.getMidColor());
        binding.colorPickerMusic.addSelector(deviceMusic.getHighColor());

        updateChipView(deviceMusic.getLowColor(), 0);
        updateChipView(deviceMusic.getMidColor(), 1);
        updateChipView(deviceMusic.getHighColor(), 2);
    }

    private void updateDeviceDatabase() {
        disposable.add(viewModel.updateDevice(deviceMusic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

}
