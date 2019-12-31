package com.errorerrorerror.esplightcontroller.ui.modules.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.errorerrorerror.esplightcontroller.App;
import com.errorerrorerror.esplightcontroller.R;
import com.errorerrorerror.esplightcontroller.data.ThemesSettingsModel;
import com.errorerrorerror.esplightcontroller.databinding.ThemesLayoutBinding;
import com.errorerrorerror.esplightcontroller.ui.adapters.ThemesAdapter;
import com.errorerrorerror.esplightcontroller.ui.base.BaseBottomSheetDialog;
import com.errorerrorerror.esplightcontroller.ui.widgets.list.BaseItemListAdapter;

import java.util.ArrayList;

public class ThemesBottomSheetDialog extends BaseBottomSheetDialog<ThemesLayoutBinding> {

    private ArrayList<ThemesSettingsModel> themesSettings = new ArrayList<>();
    private static final int LIGHT_THEME = 0;
    private static final int DARK_THEME = 1;
    private static final int FOLLOW_SYSTEM = 2;


    @Override
    protected int layoutRes() {
        return R.layout.themes_layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int defaultValue = preferences.getInt(getString(R.string.theme_preferences), AppCompatDelegate.MODE_NIGHT_NO);

        themesSettings.add(new ThemesSettingsModel(null, "Light", defaultValue == AppCompatDelegate.MODE_NIGHT_NO));
        themesSettings.add(new ThemesSettingsModel(null, "Dark", defaultValue == AppCompatDelegate.MODE_NIGHT_YES));
        themesSettings.add(new ThemesSettingsModel(null, "Follow System", defaultValue == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));

        BaseItemListAdapter adapter = new ThemesAdapter(themesSettings);
        binding.themesListView.setAdapter(adapter);
        binding.themesListView.setOnItemClickListener((parent, view1, position, id) -> {
            boolean needsUpdate = updateListToPosition(position);
            if (needsUpdate) {
                themesSettings.get(position).setActive(true);
                adapter.setList(themesSettings);
                updateToNewTheme(position);
            }
        });
    }

    private boolean updateListToPosition(int newPosition) {
        boolean updatedPosition = false;

        for (int i = 0; i < themesSettings.size(); i++) {
            final ThemesSettingsModel model = themesSettings.get(i);

            if (!model.isActive() && newPosition == i) {
                model.setActive(true);
                updatedPosition = true;
            } else if (model.isActive() && newPosition != i) {
                model.setActive(false);
            }
        }

        return updatedPosition;
    }

    private void updateToNewTheme(int newPosition) {
        SharedPreferences.Editor editor = preferences.edit();
        int newMode = AppCompatDelegate.MODE_NIGHT_NO;

        if (newPosition == DARK_THEME) {
            newMode = AppCompatDelegate.MODE_NIGHT_YES;
        } else if (newPosition == FOLLOW_SYSTEM) {
            newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }

        editor.putInt(getString(R.string.theme_preferences), newMode);
        editor.commit();

        AppCompatDelegate.setDefaultNightMode(newMode);
    }
}
