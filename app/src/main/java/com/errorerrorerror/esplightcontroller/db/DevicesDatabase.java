package com.errorerrorerror.esplightcontroller.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.errorerrorerror.esplightcontroller.data.device.Device;
import com.errorerrorerror.esplightcontroller.data.device.DeviceDao;
import com.errorerrorerror.esplightcontroller.utils.Converters;

@Database(entities = {Device.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class DevicesDatabase extends RoomDatabase {
    public abstract DeviceDao getDeviceDao();

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