package com.errorerrorerror.esplightcontroller.model.device_music;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;

import java.util.Objects;


@Entity(tableName = "device_music")
public class DeviceMusic extends BaseDevice {

    public static final String MUSIC_LOW = "music_low";
    public static final String MUSIC_MID = "music_mid";
    public static final String MUSIC_HIGH = "music_high";
    public static final String MUSIC_INTENSITY = "music_intensity";

    @ColumnInfo(name = "low")
    private int lowColor;

    @ColumnInfo(name = "mid")
    private int midColor;

    @ColumnInfo(name = "high")
    private int highColor;

    @ColumnInfo(name = "intensity")
    private int intensity;

    public DeviceMusic() { }

    public DeviceMusic(@NonNull BaseDevice baseDevice,
                       int lowColor,
                       int midColor,
                       int highColor,
                       int intensity) {
        super(baseDevice);
        this.lowColor = lowColor;
        this.midColor = midColor;
        this.highColor = highColor;
        this.intensity = intensity;
    }

    public int getLowColor() {
        return lowColor;
    }

    public void setLowColor(int lowColor) {
        this.lowColor = lowColor;
    }

    public int getMidColor() {
        return midColor;
    }

    public void setMidColor(int midColor) {
        this.midColor = midColor;
    }

    public int getHighColor() {
        return highColor;
    }

    public void setHighColor(int highColor) {
        this.highColor = highColor;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceMusic{" +
                "lowColor=" + lowColor +
                ", midColor=" + midColor +
                ", highColor=" + highColor +
                ", intensity=" + intensity +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceMusic)) return false;
        if (!super.equals(o)) return false;
        DeviceMusic that = (DeviceMusic) o;
        return getLowColor() == that.getLowColor() &&
                getMidColor() == that.getMidColor() &&
                getHighColor() == that.getHighColor() &&
                getIntensity() == that.getIntensity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLowColor(), getMidColor(), getHighColor(), getIntensity());
    }
}
