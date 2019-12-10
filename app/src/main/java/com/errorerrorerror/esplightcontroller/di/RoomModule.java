package com.errorerrorerror.esplightcontroller.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.errorerrorerror.esplightcontroller.model.DevicesDatabase;
import com.errorerrorerror.esplightcontroller.model.MainRepository;
import com.errorerrorerror.esplightcontroller.model.device_ambilight.AmbilightRepository;
import com.errorerrorerror.esplightcontroller.model.device_ambilight.DeviceAmbilightDao;
import com.errorerrorerror.esplightcontroller.model.device_music.DeviceMusicDao;
import com.errorerrorerror.esplightcontroller.model.device_music.MusicRepository;
import com.errorerrorerror.esplightcontroller.model.device_solid.DeviceSolidDao;
import com.errorerrorerror.esplightcontroller.model.device_solid.SolidRepository;
import com.errorerrorerror.esplightcontroller.model.device_waves.DeviceWavesDao;
import com.errorerrorerror.esplightcontroller.model.device_waves.WavesRepository;
import com.errorerrorerror.esplightcontroller.viewmodel.DevicesViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {

    private static final String DATABASE_NAME = "devices_database";
    private final DevicesDatabase devicesDatabase;

    public RoomModule(Application application) {
        devicesDatabase = Room.databaseBuilder(application.getApplicationContext(), DevicesDatabase.class, DATABASE_NAME).build();
    }

    @Singleton
    @Provides
    DevicesDatabase providesDevicesDatabase() {
        return devicesDatabase;
    }

    @Singleton
    @Provides
    DeviceMusicDao providesDevicesMusicDao(@NonNull DevicesDatabase devicesDatabase) {
        return devicesDatabase.getMusicDao();
    }

    @Singleton
    @Provides
    DeviceSolidDao providesDevicesSolidDao(@NonNull DevicesDatabase devicesDatabase) {
        return devicesDatabase.getSolidDao();
    }

    @Singleton
    @Provides
    DeviceWavesDao providesDevicesWavesDao(@NonNull DevicesDatabase devicesDatabase) {
        return devicesDatabase.getWavesDao();
    }

    @Singleton
    @Provides
    DeviceAmbilightDao providesDevicesAmbilightDao(@NonNull DevicesDatabase devicesDatabase) {
        return devicesDatabase.getAmbilightDao();
    }

    @NonNull
    @Singleton
    @Provides
    WavesRepository providesWaveRepository(DeviceWavesDao deviceWavesDao) {
        return new WavesRepository(deviceWavesDao);
    }

    @NonNull
    @Singleton
    @Provides
    SolidRepository providesSolidRepository(DeviceSolidDao deviceSolidDao) {
        return new SolidRepository(deviceSolidDao);
    }

    @NonNull
    @Singleton
    @Provides
    MusicRepository providesMusicRepository(DeviceMusicDao deviceMusicDao) {
        return new MusicRepository(deviceMusicDao);
    }

    @NonNull
    @Singleton
    @Provides
    AmbilightRepository providesAmbilightRepository(DeviceAmbilightDao deviceAmbilightDao) {
        return new AmbilightRepository(deviceAmbilightDao);
    }

    @NonNull
    @Singleton
    @Provides
    MainRepository providesMainRepository(WavesRepository waves, SolidRepository solid, MusicRepository music, AmbilightRepository ambilight) {
        return new MainRepository(music, solid, waves, ambilight);
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideViewModelFactory(MainRepository mainRepository) {
        return new DevicesViewModelFactory(mainRepository);
    }
}
