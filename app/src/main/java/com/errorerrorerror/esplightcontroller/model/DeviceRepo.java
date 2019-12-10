package com.errorerrorerror.esplightcontroller.model;


import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Singleton pattern
 */

public interface DeviceRepo<T> {

    Completable deleteDevice(T device);
    Observable<List<T>> getAllDevices();
    Completable insertDevice(T device);
    Completable updateDevice(T device);
    Completable setSwitch(Boolean bool, long id);
    Completable setBrightnessLevel(int progress, long id);
    Single<T> getDevice(long id);
}