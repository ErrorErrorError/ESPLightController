package com.errorerrorerror.esplightcontroller.ui.modules.configs.modes;

import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.ConfigWavesLayoutBinding;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.data.modes.WavesMode;
import com.errorerrorerror.esplightcontroller.ui.base.BaseFragment;
import com.errorerrorerror.esplightcontroller.utils.ObservableList;
import com.errorerrorerror.esplightcontroller.utils.RxBus;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConfigWavesFragment extends BaseFragment<ConfigWavesLayoutBinding> {

    private Device device;

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
    protected void containsArguments() {
        device = getArguments().getParcelable("com.errorerrorerror.device");
    }

    @Override
    protected void initializeFragment() {
        if (!(device.getMode() instanceof WavesMode)) {
            device.setMode(WavesMode.getDefaultMode());
        }

        colorList.addAll(device.getMode().getColorList());

        initializeColorPicker();

        disposable.add(colorList.getObservableList().subscribe(integers -> {
            device.getMode().setColorList(integers);
            eventBus.publish(device);
//            RxBus.publish(device);
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
}
