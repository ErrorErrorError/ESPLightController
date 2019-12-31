package com.errorerrorerror.esplightcontroller.db;

import com.errorerrorerror.esplightcontroller.data.device.DeviceRepository;

import javax.inject.Inject;

public class MainRepository {
    private DeviceRepository deviceRepository;

    @Inject
    public MainRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceRepository getDeviceRepository() {
        return deviceRepository;
    }
}
