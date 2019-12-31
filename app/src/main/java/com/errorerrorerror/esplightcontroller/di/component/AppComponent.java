package com.errorerrorerror.esplightcontroller.di.component;

import com.errorerrorerror.esplightcontroller.di.AppModule;
import com.errorerrorerror.esplightcontroller.di.BusModule;
import com.errorerrorerror.esplightcontroller.di.RoomModule;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.add.AddDeviceManuallyFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.add.AddDeviceScanFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.DeviceBrightnessFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.DeviceConfigActivity;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.modes.DeviceModeFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.modes.ConfigMusicFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.modes.ConfigSolidFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.configs.modes.ConfigWavesFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.main.HomeFragment;
import com.errorerrorerror.esplightcontroller.ui.modules.main.MainActivity;
import com.errorerrorerror.esplightcontroller.ui.modules.settings.SettingsActivity;
import com.errorerrorerror.esplightcontroller.ui.modules.settings.ThemesBottomSheetDialog;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RoomModule.class, BusModule.class})
public interface AppComponent {

    /// Settings
    void inject(MainActivity mainActivity);
    void inject(SettingsActivity settingsActivity);

    /// Room database
    void inject(HomeFragment baseFragment);
    void inject(AddDeviceManuallyFragment addDeviceManuallyFragment);
    void inject(AddDeviceScanFragment addDeviceScanFragment);
    void inject(ConfigSolidFragment configSolidFragment);
    void inject(ConfigWavesFragment configWavesFragment);
    void inject(ConfigMusicFragment configMusicFragment);
    void inject(DeviceBrightnessFragment deviceBrightnessFragment);
    void inject(DeviceConfigActivity deviceConfigActivity);
    void inject(DeviceModeFragment deviceModeFragment);

    void inject(ThemesBottomSheetDialog themesBottomSheetDialog);
}

