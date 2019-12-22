package com.errorerrorerror.esplightcontroller.di.component;

import com.errorerrorerror.esplightcontroller.MainActivity;
import com.errorerrorerror.esplightcontroller.di.AppModule;
import com.errorerrorerror.esplightcontroller.di.RoomModule;
import com.errorerrorerror.esplightcontroller.views.AddDeviceManuallyFragment;
import com.errorerrorerror.esplightcontroller.views.AddDeviceScanFragment;
import com.errorerrorerror.esplightcontroller.views.ConfigMusicFragment;
import com.errorerrorerror.esplightcontroller.views.ConfigSolidFragment;
import com.errorerrorerror.esplightcontroller.views.ConfigWavesFragment;
import com.errorerrorerror.esplightcontroller.views.DeviceBrightnessFragment;
import com.errorerrorerror.esplightcontroller.views.DeviceConfigurationFragment;
import com.errorerrorerror.esplightcontroller.views.DeviceModeFragment;
import com.errorerrorerror.esplightcontroller.views.HomeFragment;
import com.errorerrorerror.esplightcontroller.views.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RoomModule.class})
public interface AppComponent {

    void inject(MainActivity mainActivity);
    void inject(SettingsFragment settingsFragment);

    void inject(HomeFragment baseFragment);
    void inject(AddDeviceManuallyFragment addDeviceManuallyFragment);
    void inject(AddDeviceScanFragment addDeviceScanFragment);
    void inject(ConfigSolidFragment configSolidFragment);
    void inject(ConfigWavesFragment configWavesFragment);
    void inject(ConfigMusicFragment configMusicFragment);
    void inject(DeviceBrightnessFragment deviceBrightnessFragment);
    void inject(DeviceConfigurationFragment deviceConfigurationFragment);
    void inject(DeviceModeFragment deviceModeFragment);

}

