package com.errorerrorerror.esplightcontroller.views;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.errorerrorerror.colorpicker.ColorWheelSelector;
import com.errorerrorerror.colorpicker.MultiColorPickerView;
import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.ConfigWavesLayoutBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.errorerrorerror.esplightcontroller.model.device_solid.DeviceSolid;
import com.errorerrorerror.esplightcontroller.model.device_waves.DeviceWaves;
import com.errorerrorerror.esplightcontroller.utils.ObservableList;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ConfigWavesFragment extends BaseFragment<ConfigWavesLayoutBinding> {

    private long deviceId = -1;

    private DeviceWaves deviceWaves;

    private ObservableList<Integer> colorList = new ObservableList<>(6);

    @Override
    public int getLayoutRes() {
        return R.layout.config_waves_layout;
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

        if (!(baseDevice instanceof DeviceWaves)) {
            colorList.add(Color.RED);
            colorList.add(Color.BLUE);
            colorList.add(Color.WHITE);

            deviceWaves = new DeviceWaves(baseDevice, 0, colorList.getList());
            disposable.add(viewModel.deleteDevice(baseDevice)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .andThen(Observable.just(deviceWaves)).flatMapCompletable(newDevice -> viewModel.insertDevice(newDevice)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                    ).subscribe());
        } else {
            deviceWaves = (DeviceWaves) baseDevice;
            colorList.addAll(deviceWaves.getColors());
        }

        initializeColorPicker();

        disposable.add(colorList.getObservableList().subscribe(integers -> {
            deviceWaves.setColors(integers);
            updateDeviceDatabase();
        }));

        binding.wavesColorPicker.addOnSelectorColorChangedListener((selector, color, index) -> updateColorList(color, index));
    }

    private void updateColorList(@ColorInt int color, int index) {
        colorList.set(index, color);
        ((MaterialShapeDrawable )binding.colorPalette.getChildAt(index).getBackground()).setFillColor(ColorStateList.valueOf(color));
    }

    private void initializeColorPicker() {
        for (int i = 0; i < colorList.getList().size(); i++) {
            int color = colorList.getList().get(i);
            if (i == 0) {
                binding.wavesColorPicker.setMainSelectorColor(color);
            } else {
                binding.wavesColorPicker.addSelector(color);
            }

            View colorPalette = createColorPaletteView();
            ((MaterialShapeDrawable) colorPalette.getBackground()).setFillColor(ColorStateList.valueOf(color));

            binding.colorPalette.addView(colorPalette);
        }
    }

    private View createColorPaletteView() {
        View view = new View(getContext());
        MaterialShapeDrawable drawable = new MaterialShapeDrawable();
        ShapeAppearanceModel shapeAppearanceModel = ShapeAppearanceModel.builder().setAllCorners(CornerFamily.ROUNDED, 10 * getResources().getDisplayMetrics().density).build();
        drawable.setShapeAppearanceModel(shapeAppearanceModel);
        view.setBackground(drawable);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        int margins = Math.round(5 * getResources().getDisplayMetrics().density);
        layoutParams.setMargins(margins,margins,margins,margins);
        view.setLayoutParams(layoutParams);

        return view;
    }

    private void updateDeviceDatabase() {
        disposable.add(viewModel.updateDevice(deviceWaves)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }
}
