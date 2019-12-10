package com.errorerrorerror.esplightcontroller.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.errorerrorerror.esplightcontroller.model.device_ambilight.DeviceAmbilight;
import com.errorerrorerror.esplightcontroller.model.device_ambilight.DeviceAmbilightDao;
import com.errorerrorerror.esplightcontroller.model.device_music.DeviceMusic;
import com.errorerrorerror.esplightcontroller.model.device_music.DeviceMusicDao;
import com.errorerrorerror.esplightcontroller.model.device_solid.DeviceSolid;
import com.errorerrorerror.esplightcontroller.model.device_solid.DeviceSolidDao;
import com.errorerrorerror.esplightcontroller.model.device_waves.DeviceWaves;
import com.errorerrorerror.esplightcontroller.model.device_waves.DeviceWavesDao;
import com.errorerrorerror.esplightcontroller.utils.Converters;

@Database(entities = {DeviceMusic.class, DeviceWaves.class, DeviceSolid.class, DeviceAmbilight.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class DevicesDatabase extends RoomDatabase {

    public abstract DeviceMusicDao getMusicDao();
    public abstract DeviceWavesDao getWavesDao();
    public abstract DeviceSolidDao getSolidDao();
    public abstract DeviceAmbilightDao getAmbilightDao();
    /*
    //How to setup Migration

    //P = Previous Version
    //N = New Version

    static final Migration MIGRATION_P_N = new Migration(P,N) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        // If we alter alter the table, edit like this:.
         database.execSQL("ALTER TABLE _____ "
                + " ADD COLUMN _____ ___");
    };

    //then add before build
    .addMigrations(OLD MIGRATION, NEW MIGRATION)
     */
}