package com.errorerrorerror.esplightcontroller.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.errorerrorerror.esplightcontroller.model.MainRepository;
import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;
import com.errorerrorerror.esplightcontroller.model.device_ambilight.DeviceAmbilight;
import com.errorerrorerror.esplightcontroller.model.device_music.DeviceMusic;
import com.errorerrorerror.esplightcontroller.model.device_solid.DeviceSolid;
import com.errorerrorerror.esplightcontroller.model.device_waves.DeviceWaves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;

public class DevicesCollectionViewModel extends ViewModel {

    private final MainRepository devicesDataSource;

    DevicesCollectionViewModel(MainRepository devicesDataSource) {
        this.devicesDataSource = devicesDataSource;
    }

    public Observable<List<BaseDevice>> getAllDevices() {
        return Observable.combineLatest(getAllDeviceSolid()
                        .map(i -> (List<BaseDevice>) new ArrayList<BaseDevice>(i))
                , getAllDeviceWaves()
                        .map(i -> (List<BaseDevice>) new ArrayList<BaseDevice>(i))
                , getAllDeviceMusic()
                        .map(i -> (List<BaseDevice>) new ArrayList<BaseDevice>(i)),
                getAllDeviceAmbilight()
                        .map(i -> (List<BaseDevice>) new ArrayList<BaseDevice>(i)),
                (music, waves, solid, ambilight) -> {
                    List<BaseDevice> t = new ArrayList<>(music);
                    t.addAll(waves);
                    t.addAll(solid);
                    t.addAll(ambilight);
                    Collections.sort(t, (o1, o2) -> (int) (o1.getId() - o2.getId()));
                    return t;
                })
                .distinctUntilChanged();
    }

    public Completable setSwitch(Boolean bool, BaseDevice device) {
        return (device instanceof DeviceMusic) ?
                devicesDataSource.getMusicRepository().setSwitch(bool, device.getId()) :
                (device instanceof DeviceSolid) ?
                        devicesDataSource.getSolidRepository().setSwitch(bool, device.getId()) :
                        (device instanceof DeviceWaves) ?
                                devicesDataSource.getWavesRepository().setSwitch(bool, device.getId()) :
                                (device instanceof DeviceAmbilight) ?
                                        devicesDataSource.getAmbilightRepository().setSwitch(bool, device.getId()) : Completable.error(() -> new Throwable("Cannot Update Switch With Unknown Device"));
    }

    public Completable updateBrightnessLevel(int progress, BaseDevice device) {
        return (device instanceof DeviceMusic) ?
                devicesDataSource.getMusicRepository().setBrightnessLevel(progress, device.getId()) :
                (device instanceof DeviceSolid) ?
                        devicesDataSource.getSolidRepository().setBrightnessLevel(progress, device.getId()) :
                        (device instanceof DeviceWaves) ?
                                devicesDataSource.getWavesRepository().setBrightnessLevel(progress, device.getId()) :
                                (device instanceof DeviceAmbilight) ?
                                        devicesDataSource.getAmbilightRepository().setBrightnessLevel(progress, device.getId()) : Completable.error(() -> new Throwable("Cannot Update Brightness With Unknown Device"));
    }

    public Observable<List<DeviceMusic>> getAllDeviceMusic() {
        return devicesDataSource.getMusicRepository().getAllDevices()
                .map(deviceMusics -> {
                    Collections.sort(deviceMusics, (o1, o2) -> (int) (o2.getId() - o1.getId()));
                    return deviceMusics;
                }).distinctUntilChanged();
    }

    public Observable<List<DeviceSolid>> getAllDeviceSolid() {
        return devicesDataSource.getSolidRepository().getAllDevices()
                .map(deviceSolid -> {
                    Collections.sort(deviceSolid, (o1, o2) -> (int) (o2.getId() - o1.getId()));
                    return deviceSolid;
                }).distinctUntilChanged();

    }

    public Observable<List<DeviceWaves>> getAllDeviceWaves() {
        return devicesDataSource.getWavesRepository().getAllDevices()
                .map(deviceWaves -> {
                    Collections.sort(deviceWaves, (o1, o2) -> (int) (o2.getId() - o1.getId()));
                    return deviceWaves;
                }).distinctUntilChanged();

    }

    public Observable<List<DeviceAmbilight>> getAllDeviceAmbilight() {
        return devicesDataSource.getAmbilightRepository().getAllDevices()
                .map(deviceAmbilight -> {
                    Collections.sort(deviceAmbilight, (o1, o2) -> (int) (o2.getId() - o1.getId()));
                    return deviceAmbilight;
                }).distinctUntilChanged();

    }

    public Completable deleteDevice(BaseDevice device) {
        return (device instanceof DeviceMusic) ?
                devicesDataSource.getMusicRepository().deleteDevice((DeviceMusic) device) :
                (device instanceof DeviceSolid) ?
                        devicesDataSource.getSolidRepository().deleteDevice((DeviceSolid) device) :
                        (device instanceof DeviceWaves) ?
                                devicesDataSource.getWavesRepository().deleteDevice((DeviceWaves) device) :
                                (device instanceof DeviceAmbilight) ?
                                        devicesDataSource.getAmbilightRepository().deleteDevice((DeviceAmbilight) device) : Completable.error(() -> new Throwable("Cannot Delete With Unknown Device"));
    }

    public Completable insertDevice(BaseDevice device) {
        return (device instanceof DeviceMusic) ?
                devicesDataSource.getMusicRepository().insertDevice((DeviceMusic) device) :
                (device instanceof DeviceSolid) ?
                        devicesDataSource.getSolidRepository().insertDevice((DeviceSolid) device) :
                        (device instanceof DeviceWaves) ?
                                devicesDataSource.getWavesRepository().insertDevice((DeviceWaves) device) :
                                (device instanceof DeviceAmbilight) ?
                                        devicesDataSource.getAmbilightRepository().insertDevice((DeviceAmbilight) device) : Completable.error(() -> new Throwable("Cannot Insert With Unknown Device"));
    }

    public Completable updateDevice(BaseDevice device) {
        if (device instanceof DeviceMusic) {
            return devicesDataSource.getMusicRepository().updateDevice((DeviceMusic) device);
        } else if (device instanceof DeviceSolid)
            return devicesDataSource.getSolidRepository().updateDevice((DeviceSolid) device);
        else if (device instanceof DeviceWaves)
            return devicesDataSource.getWavesRepository().updateDevice((DeviceWaves) device);
        else if (device instanceof DeviceAmbilight)
            return devicesDataSource.getAmbilightRepository().updateDevice((DeviceAmbilight) device);
        else
            return Completable.error(() -> new Throwable("Cannot Update With Unknown Device"));
    }

    public Single<BaseDevice> getDeviceById(long id) {
        return getAllDevices().flatMapIterable(deviceList -> deviceList).filter(device -> device.getId() == id).firstOrError();
    }
}
