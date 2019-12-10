package com.errorerrorerror.esplightcontroller.model.device_ambilight;

import com.errorerrorerror.esplightcontroller.model.DeviceRepo;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class AmbilightRepository implements DeviceRepo<DeviceAmbilight> {
    private DeviceAmbilightDao dao;

    @Inject
    public AmbilightRepository(DeviceAmbilightDao dao) {
        this.dao = dao;
    }

    @Override
    public Completable deleteDevice(DeviceAmbilight device) {
        return dao.delete(device);
    }

    @Override
    public Observable<List<DeviceAmbilight>> getAllDevices() {
        return dao.getAllAmbilightDevices();
    }

    @Override
    public Completable insertDevice(DeviceAmbilight device) {
        return dao.insert(device);
    }

    @Override
    public Completable updateDevice(DeviceAmbilight device) {
        return dao.update(device);
    }

    @Override
    public Completable setSwitch(Boolean bool, long id) {
        return dao.updateSwitch(bool, id);
    }

    @Override
    public Completable setBrightnessLevel(int progress, long id) {
        return dao.updateBrightnessLevel(progress, id);
    }

    @Override
    public Single<DeviceAmbilight> getDevice(long id) {
        return dao.getDevice(id);
    }

}
