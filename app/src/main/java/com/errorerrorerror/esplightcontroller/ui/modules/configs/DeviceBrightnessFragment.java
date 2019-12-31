package com.errorerrorerror.esplightcontroller.ui.modules.configs;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.DeviceBrightnessFragmentBinding;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.ui.base.BaseFragment;
import com.errorerrorerror.esplightcontroller.utils.RxBus;
import com.errorerrorerror.ioslider.IOSlider;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeviceBrightnessFragment extends BaseFragment<DeviceBrightnessFragmentBinding> {

    private Device device;

    @Override
    protected void containsArguments() {
        if (getArguments() != null) {
            device = getArguments().getParcelable("com.errorerrorerror.device");
        }
    }

    @Override
    protected void initializeFragment() {
        binding.brightness.setProgress(device.getBrightness());
        binding.brightness.setOnProgressChangeListener(new IOSlider.OnChangeListener() {
            @Override
            public void onProgressChanged(IOSlider slider, float progress) {
                device.setBrightness(Math.round(progress));
                eventBus.publish(device);
//                RxBus.publish(device);
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
