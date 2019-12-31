package com.errorerrorerror.esplightcontroller.ui.modules.configs.modes;

import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.ConfigSolidLayoutBinding;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.data.modes.SteadyMode;
import com.errorerrorerror.esplightcontroller.ui.base.BaseFragment;
import com.errorerrorerror.esplightcontroller.utils.RxBus;
import com.errorerrorerror.multicolorpicker.MultiColorPickerView;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConfigSolidFragment extends BaseFragment<ConfigSolidLayoutBinding> {

    private Device device;

    @Override
    public int getLayoutRes() {
        return R.layout.config_solid_layout;
    }

    @Override
    public void injectFragment() {
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void containsArguments() {
        device = getArguments().getParcelable("com.errorerrorerror.device");
    }

    @Override
    protected void initializeFragment() {

        if (!(device.getMode() instanceof SteadyMode)) {
            device.setMode(SteadyMode.getDefaultMode());
            eventBus.publish(device);
//            RxBus.publish(device);
        }

        binding.solidColorPicker.setColorPickerMode(MultiColorPickerView.HUE_SATURATION);
        binding.solidColorPicker.setMainSelectorColor(device.getMode().getColorList().get(0));

        binding.solidColorPicker.addOnSelectorColorChangedListener((selector, color, index) -> {
            device.getMode().getColorList().set(0, color);
            eventBus.publish(device);
//            RxBus.publish(device);
            Log.v("ConfigSolidFragment", "Selector: " + selector.getId() + " Color: " + Color.valueOf(color) + " Index: " + index);
        });
    }
}
