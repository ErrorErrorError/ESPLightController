package com.errorerrorerror.esplightcontroller.model.device_waves;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;

import java.util.List;
import java.util.Objects;

@Entity(tableName = "device_waves")
public class DeviceWaves extends BaseDevice {

    public static final String WAVES_SPEED = "waves_speed";
    public static final String WAVES_COLORS = "waves_colors";

    @ColumnInfo(name = "speed")
    private int speed;

    @ColumnInfo(name = "colors")
    private List<Integer> colors;

    public DeviceWaves() { }

    public DeviceWaves(@NonNull BaseDevice baseDevice,
                       int speed,
                       List<Integer> colors) {
        super(baseDevice);
        this.colors = colors;
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setColors(List<Integer> color) {
        this.colors = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceWaves)) return false;
        if (!super.equals(o)) return false;
        DeviceWaves mDevice = (DeviceWaves) o;
        return this.getSpeed() == mDevice.getSpeed() &&
                this.getColors().equals(mDevice.getColors());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSpeed(), getColors());
    }

    @Override
    public String toString() {
        return "DeviceWaves{" +
                "speed=" + speed +
                ", colors=" + colors +
                "} " + super.toString();
    }
}
