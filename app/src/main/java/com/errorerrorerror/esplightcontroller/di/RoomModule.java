package com.errorerrorerror.esplightcontroller.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.errorerrorerror.esplightcontroller.db.DevicesDatabase;
import com.errorerrorerror.esplightcontroller.db.MainRepository;
import com.errorerrorerror.esplightcontroller.data.device.DeviceDao;
import com.errorerrorerror.esplightcontroller.data.device.DeviceRepository;
import com.errorerrorerror.esplightcontroller.viewmodel.DevicesViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {

    private static final String DATABASE_NAME = "devices_database";
    private final DevicesDatabase devicesDatabase;

    public RoomModule(Application application) {
        devicesDatabase = Room.databaseBuilder(application.getApplicationContext(), DevicesDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
    }

    @Singleton
    @Provides
    DevicesDatabase providesDevicesDatabase() {
        return devicesDatabase;
    }

    @Singleton
    @Provides
    DeviceDao providesDeviceDao(@NonNull DevicesDatabase devicesDatabase) {
        return devicesDatabase.getDeviceDao();
    }

    @Singleton
    @Provides
    DeviceRepository providesDeviceRepository(DeviceDao baseDeviceDao) {
        return new DeviceRepository(baseDeviceDao);
    }

    @NonNull
    @Singleton
    @Provides
    MainRepository providesMainRepository(DeviceRepository deviceRepository) {
        return new MainRepository(deviceRepository);
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideViewModelFactory(MainRepository mainRepository) {
        return new DevicesViewModelFactory(mainRepository);
    }
}
