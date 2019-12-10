package com.errorerrorerror.esplightcontroller.model.device_ambilight;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface DeviceAmbilightDao {

    @Query("SELECT * FROM device_ambilight")
    Observable<List<DeviceAmbilight>> getAllAmbilightDevices();

    @Insert
    Completable insert(DeviceAmbilight DeviceAmbilight);

    @Update
    Completable update(DeviceAmbilight DeviceAmbilight);

    @Delete
    Completable delete(DeviceAmbilight DeviceAmbilight);

    @Query("UPDATE device_ambilight SET device_on = :bool WHERE id = :id")
    Completable updateSwitch(Boolean bool, long id);

    @Query("UPDATE device_ambilight SET brightness_level = :progress WHERE id = :id")
    Completable updateBrightnessLevel(int progress, long id);

    @Query("SELECT * FROM  device_ambilight WHERE id =:id")
    Single<DeviceAmbilight> getDevice(long id);
}
