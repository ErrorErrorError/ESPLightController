package com.errorerrorerror.esplightcontroller.data.device;

import com.errorerrorerror.esplightcontroller.data.modes.BaseMode;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class DeviceRepository {

    private DeviceDao dao;

    @Inject
    public DeviceRepository(DeviceDao dao) {
        this.dao = dao;
    }

    public Completable deleteDevice(Device device) {
        return dao.delete(device);
    }

    public Observable<List<Device>> getAllDevices() {
        return dao.getAllDevices();
    }

    public Completable insertDevice(Device device) {
        return dao.insert(device);
    }

    public Completable updateDevice(Device device) {
        return dao.update(device);
    }

    public Completable updateMode(long id,BaseMode mode) {
        return dao.updateMode(id, mode);
    }

    public Completable setSwitch(long id, Boolean on) {
        return dao.updateSwitch(id, on);
    }

    public Completable setBrightnessLevel(long id, int progress) {
        return dao.updateBrightnessLevel(id, progress);
    }

    public Single<Device> getDevice(long id) {
        return dao.getDevice(id);
    }
}
