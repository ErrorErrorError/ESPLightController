package com.errorerrorerror.esplightcontroller.ui.modules.configs.modes;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.core.graphics.ColorUtils;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.ConfigMusicLayoutBinding;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.data.modes.MusicMode;
import com.errorerrorerror.esplightcontroller.ui.base.BaseFragment;
import com.errorerrorerror.esplightcontroller.utils.RxBus;
import com.google.android.material.chip.Chip;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConfigMusicFragment extends BaseFragment<ConfigMusicLayoutBinding> {
    private Device device;

    @Override
    public int getLayoutRes() {
        return R.layout.config_music_layout;
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
        initializeViews();

        binding.colorPickerMusic.addOnSelectorColorChangedListener((selector, color, index) -> updateViews(color,index));
    }

    private void updateViews(@ColorInt int color, int index) {
        ((MusicMode) device.getMode()).setColor(index, color);
        updateChipView(color, index);
        eventBus.publish(device);
//        RxBus.publish(device);
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
        if (!(device.getMode() instanceof MusicMode)) {
            device.setMode(MusicMode.getDefaultMode());
            eventBus.publish(eventBus);
//            RxBus.publish(device);
        }

        binding.colorPickerMusic.setMainSelectorColor(((MusicMode) device.getMode()).getLowColor());
        binding.colorPickerMusic.addSelector(((MusicMode) device.getMode()).getMidColor());
        binding.colorPickerMusic.addSelector(((MusicMode) device.getMode()).getHighColor());

        updateChipView(((MusicMode) device.getMode()).getLowColor(), 0);
        updateChipView(((MusicMode) device.getMode()).getMidColor(), 1);
        updateChipView(((MusicMode) device.getMode()).getHighColor(), 2);
    }
}
