package com.errorerrorerror.esplightcontroller.model.device_ambilight;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.errorerrorerror.esplightcontroller.model.device.BaseDevice;

import java.util.Objects;

@Entity(tableName = "device_ambilight")
public class DeviceAmbilight extends BaseDevice {

    private int ledNums;

    public DeviceAmbilight() { }

    public DeviceAmbilight(@NonNull BaseDevice baseDevice, int ledNums) {
        super(baseDevice);
        this.ledNums = ledNums;
    }

    public int getLedNums() {
        return ledNums;
    }

    public void setLedNums(int ledNums) {
        this.ledNums = ledNums;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeviceAmbilight that = (DeviceAmbilight) o;
        return getLedNums() == that.getLedNums();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLedNums());
    }

    @Override
    public String toString() {
        return "DeviceAmbilight{" +
                "ledNums=" + ledNums +
                "} " + super.toString();
    }
}
