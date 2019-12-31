package com.errorerrorerror.esplightcontroller.ui.modules.configs;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.databinding.DeviceConfigurationBinding;
import com.errorerrorerror.esplightcontroller.ui.base.BaseActivity;
import com.errorerrorerror.esplightcontroller.ui.base.BaseFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.modes.DeviceModeFragment;
import com.errorerrorerror.esplightcontroller.viewmodel.DevicesCollectionViewModel;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeviceConfigActivity extends BaseActivity<DeviceConfigurationBinding> {

    @Inject
    protected ViewModelProvider.Factory viewModelFactory;
    protected DevicesCollectionViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.device_configuration;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    private enum LayoutState {
        MODE,
        BRIGHTNESS,
        TIMER
    }

    private LayoutState currentLayout;
    private Device device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) getApplication()).getAppComponent().inject(this);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(DevicesCollectionViewModel.class);

        device = getIntent().getParcelableExtra("com.errorerrorerror.device");

        binding.includeLayout.toolbar.setTitle(device.getName());
        binding.includeLayout.toolbar.setNavigationIcon(R.drawable.back_arrow_icon);
        binding.includeLayout.toolbar.setNavigationOnClickListener(v -> onBackPressed());

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
        binding.powerButton.setChecked(device.isOn());
        disposable.add(eventBus.observable(Device.class).subscribe(device -> viewModel.updateDevice(device).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()));

        binding.powerButton.addOnCheckedChangeListener((button, isChecked) -> {
            device.setOn(isChecked);
            disposable.add(viewModel.setSwitch(device.getId(), isChecked).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());
        });
    }

    private void setButtonSelection(LayoutState state) {
        if (currentLayout != state) {
            currentLayout = state;

            Bundle bundle = new Bundle();
            switch (currentLayout) {
                case MODE:
                    bundle.putParcelable("com.errorerrorerror.device", device);
                    DeviceModeFragment modeFragment = BaseFragment.newInstance(DeviceModeFragment.class, bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(getFragmentContainerId(), modeFragment, "root_fragment_test")
                            .commit();
                    break;
                case BRIGHTNESS:
                    bundle.putParcelable("com.errorerrorerror.device", device);
                    DeviceBrightnessFragment brightnessFragment = BaseFragment.newInstance(DeviceBrightnessFragment.class, bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(getFragmentContainerId(), brightnessFragment, "root_fragment_test")
                            .commit();
                    break;
                case TIMER:
                    break;
            }
        }
    }
}
