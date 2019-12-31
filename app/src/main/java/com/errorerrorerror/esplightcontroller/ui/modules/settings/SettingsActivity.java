package com.errorerrorerror.esplightcontroller.ui.modules.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.databinding.IconRowItemBinding;
import com.errorerrorerror.esplightcontroller.databinding.SettingsLayoutBinding;
import com.errorerrorerror.esplightcontroller.data.SettingsModel;
import com.errorerrorerror.esplightcontroller.ui.adapters.SettingsAdapter;
import com.errorerrorerror.esplightcontroller.ui.widgets.list.BaseItemListAdapter;
import com.errorerrorerror.esplightcontroller.ui.base.BaseActivity;

import java.util.ArrayList;

import javax.inject.Inject;

public class SettingsActivity extends BaseActivity<SettingsLayoutBinding> {

    @Inject
    SharedPreferences preferences;

    private ArrayList<SettingsModel> settings= new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.settings_layout;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.settingsContainer;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((App) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);
        binding.includeLayout.toolbar.setTitle("Settings");
        binding.includeLayout.toolbar.setNavigationIcon(R.drawable.back_arrow_icon);
        binding.includeLayout.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        settings.add(new SettingsModel(getDrawable(R.drawable.color_lens_icon), getString(R.string.themes), SettingsModel.THEME));

        binding.settingsList.setAdapter(new SettingsAdapter(settings));
        binding.settingsList.setOnItemClickListener((parent, view, position, id) -> {
            SettingsModel settingsModel = (SettingsModel) parent.getItemAtPosition(position);

            if (settingsModel.getSettingsType() == SettingsModel.THEME) {
                showThemesDialog();
            }
        });
    }

    private void showThemesDialog() {
        ThemesBottomSheetDialog dialog = new ThemesBottomSheetDialog();
        dialog.show(getSupportFragmentManager(), "root_settings");
    }
}