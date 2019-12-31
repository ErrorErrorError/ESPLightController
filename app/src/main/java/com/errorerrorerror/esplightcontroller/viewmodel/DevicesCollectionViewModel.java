package com.errorerrorerror.esplightcontroller.viewmodel;

import androidx.lifecycle.ViewModel;

import com.errorerrorerror.esplightcontroller.db.MainRepository;
import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.data.modes.BaseMode;

import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class DevicesCollectionViewModel extends ViewModel {

    private final MainRepository devicesDataSource;

    DevicesCollectionViewModel(MainRepository devicesDataSource) {
        this.devicesDataSource = devicesDataSource;
    }

    public Observable<List<Device>> getAllDevices() {
        return devicesDataSource.getDeviceRepository().getAllDevices()
                .map(baseDevices -> {
                    Collections.sort(baseDevices, (o1, o2) -> (int) (o2.getId() - o1.getId()));
                    return baseDevices;
                }).distinctUntilChanged();
    }

    public Completable setSwitch(long id, Boolean bool) {
        return devicesDataSource.getDeviceRepository().setSwitch(id, bool);
    }

    public Completable updateBrightnessLevel(long id, int progress) {
        return devicesDataSource.getDeviceRepository().setBrightnessLevel(id, progress);
    }

    public Completable deleteDevice(Device device) {
        return devicesDataSource.getDeviceRepository().deleteDevice(device);
    }

    public Completable insertDevice(Device device) {
        return devicesDataSource.getDeviceRepository().insertDevice(device);
    }

    public Completable updateDevice(Device device) {
        return devicesDataSource.getDeviceRepository().updateDevice(device);
    }

    public Completable updateMode(long id, BaseMode mode) {
        return devicesDataSource.getDeviceRepository().updateMode(id, mode);
    }

    public Single<Device> getDeviceById(long id) {
        return getAllDevices().flatMapIterable(deviceList -> deviceList).filter(device -> device.getId() == id).firstOrError();
    }
}
