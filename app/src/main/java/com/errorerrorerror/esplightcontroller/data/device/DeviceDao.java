package com.errorerrorerror.esplightcontroller.data.device;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.errorerrorerror.esplightcontroller.data.modes.BaseMode;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM device_table")
    Observable<List<Device>> getAllDevices();

    @Insert
    Completable insert(Device device);

    @Update
    Completable update(Device device);

    @Delete
    Completable delete(Device device);

    @Query("UPDATE device_table SET `on` = :bool WHERE id = :id")
    Completable updateSwitch(long id, Boolean bool);

    @Query("UPDATE device_table SET brightness = :progress WHERE id = :id")
    Completable updateBrightnessLevel(long id, int progress);

    @Query("SELECT * FROM  device_table WHERE id =:id")
    Single<Device> getDevice(long id);

    @Query("UPDATE device_table SET mode = :mode WHERE id = :id")
    Completable updateMode(long id, BaseMode mode);
}
