package com.errorerrorerror.esplightcontroller.views;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.DeviceModeFragmentBinding;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.errorerrorerror.esplightcontroller.model.device_music.DeviceMusic;
import com.errorerrorerror.esplightcontroller.model.device_solid.DeviceSolid;
import com.errorerrorerror.esplightcontroller.model.device_waves.DeviceWaves;
import com.jakewharton.rxbinding3.widget.AdapterViewItemClickEvent;
import com.jakewharton.rxbinding3.widget.RxAutoCompleteTextView;

import java.util.Objects;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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

    private long deviceId = -1;


    @Override
    public int getLayoutRes() {
        return R.layout.device_mode_fragment;
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

//        binding.filledExposedDropdown.setInputType(InputType.TYPE_NULL);
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


        BaseDevice baseDevice = viewModel.getDeviceById(deviceId).subscribeOn(Schedulers.io()).blockingGet();

        if (baseDevice instanceof DeviceSolid) {
            binding.filledExposedDropdown.setText(adapter.getItem(0), false);
            setMode(ModeTypes.SOLID);
        } else if (baseDevice instanceof DeviceMusic) {
            binding.filledExposedDropdown.setText(adapter.getItem(1), false);
            setMode(ModeTypes.MUSIC);
        } else if (baseDevice instanceof DeviceWaves) {
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
        bundle.putLong(ID_BUNDLE, deviceId);

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
