package com.errorerrorerror.esplightcontroller.ui.modules.configs.modes;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.data.modes.MusicMode;
import com.errorerrorerror.esplightcontroller.data.modes.SteadyMode;
import com.errorerrorerror.esplightcontroller.data.modes.WavesMode;
import com.errorerrorerror.esplightcontroller.databinding.DeviceModeFragmentBinding;
import com.errorerrorerror.esplightcontroller.ui.base.BaseFragment;
import com.jakewharton.rxbinding3.widget.RxAutoCompleteTextView;

import java.util.Objects;

public class DeviceModeFragment extends BaseFragment<DeviceModeFragmentBinding> {

    private enum ModeTypes {
        SOLID,
        MUSIC,
        WAVES;

        public static ModeTypes valueOf(int x) {
            switch(x) {
                case 0:
                    return SOLID;
                case 1:
                    return MUSIC;
                case 2:
                    return WAVES;
            }
            return null;
        }
    }

    private ModeTypes currentMode;

    private Device device;

    @Override
    public int getLayoutRes() {
        return R.layout.device_mode_fragment;
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
        String[] MODES = new String[] {"Solid", "Music", "Waves"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        Objects.requireNonNull(getContext()),
                        R.layout.dropdown_menu_item,
                        MODES);

        binding.filledExposedDropdown.setAdapter(adapter);

        disposable.add(RxAutoCompleteTextView.itemClickEvents(binding.filledExposedDropdown).subscribe(clickEvent -> {
            ModeTypes newMode = ModeTypes.valueOf(clickEvent.getPosition());
            if (newMode == null) {
                return;
            }
            setMode(newMode);

        }));

        if (device.getMode() instanceof SteadyMode) {
            binding.filledExposedDropdown.setText(adapter.getItem(0), false);
            setMode(ModeTypes.SOLID);
        } else if (device.getMode() instanceof MusicMode) {
            binding.filledExposedDropdown.setText(adapter.getItem(1), false);
            setMode(ModeTypes.MUSIC);
        } else if (device.getMode() instanceof WavesMode) {
            binding.filledExposedDropdown.setText(adapter.getItem(2), false);
            setMode(ModeTypes.WAVES);
        }

    }

    private void setMode(ModeTypes mode) {
        if (currentMode == mode) {
            return;
        }

        currentMode = mode;

        Bundle bundle = new Bundle();
        bundle.putParcelable("com.errorerrorerror.device", device);
        if (currentMode == ModeTypes.SOLID) {
            ConfigSolidFragment solidFragment = BaseFragment.newInstance(ConfigSolidFragment.class, bundle);
            getChildFragmentManager().beginTransaction().replace(R.id.mode_fragment_container, solidFragment, "mode_root_child").commit();
        } else if (currentMode == ModeTypes.WAVES) {

            ConfigWavesFragment wavesFragment = BaseFragment.newInstance(ConfigWavesFragment.class, bundle);
            getChildFragmentManager().beginTransaction().replace(R.id.mode_fragment_container, wavesFragment, "mode_root_child").commit();
        } else if (currentMode == ModeTypes.MUSIC) {
            ConfigMusicFragment musicFragment = BaseFragment.newInstance(ConfigMusicFragment.class, bundle);
            getChildFragmentManager().beginTransaction().replace(R.id.mode_fragment_container, musicFragment, "mode_root_child").commit();
        }
    }
}
