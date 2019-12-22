package com.errorerrorerror.esplightcontroller.model.device;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Objects;

public class BaseDevice {
    public static final String EMPTY_GROUP = "empty_group_placeholder";

    /// Bundle Names
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_IP = "device_ip";
    public static final String DEVICE_PORT = "device_port";
    public static final String DEVICE_ON = "device_on";
    public static final String DEVICE_BRIGHTNESS = "device_brightness";
    public static final String DEVICE_GROUP = "device_group";

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "device_name")
    private String deviceName;

    @ColumnInfo(name = "device_ip")
    private String ip;

    @ColumnInfo(name = "device_port")
    private String port;

    @ColumnInfo(name = "device_on")
    private boolean on;

    @ColumnInfo(name = "brightness_level")
    private int brightness;

    @ColumnInfo(name = "group_name")
    private String groupName;

    public BaseDevice() { }

    public BaseDevice(BaseDevice device) {
        this.id = device.getId();
        this.deviceName = device.getDeviceName();
        this.ip = device.getIp();
        this.port = device.getPort();
        this.on = device.isOn();
        this.brightness = device.getBrightness();
        this.groupName = device.getGroupName();
    }

    public BaseDevice(String deviceName, String ip, String port, boolean on, int brightness, String groupName) {
        this.deviceName = deviceName;
        this.ip = ip;
        this.port = port;
        this.on = on;
        this.brightness = brightness;
        this.groupName = groupName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //Setters and getters
    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public String toString() {
        return "BaseDevice{" +
                "id=" + id + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", on=" + on + '\'' +
                ", brightness=" + brightness + '\'' +
                ", groupName=" + groupName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseDevice)) return false;
        BaseDevice baseDevice = (BaseDevice) o;
        return id == baseDevice.getId() &&
                deviceName.equals(baseDevice.getDeviceName()) &&
                ip.equals(baseDevice.getIp()) &&
                port.equals(baseDevice.getPort()) &&
                on == baseDevice.isOn() &&
                brightness == baseDevice.getBrightness() &&
                groupName.equals(baseDevice.getGroupName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deviceName, ip, port, on, brightness, groupName);
    }
}
