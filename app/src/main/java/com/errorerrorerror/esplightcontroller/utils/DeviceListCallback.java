package com.errorerrorerror.esplightcontroller.utils;

import androidx.recyclerview.widget.DiffUtil;

public abstract class DeviceListCallback extends DiffUtil.Callback {

    /*
    @NotNull
    private final List<Device> oldList;
    @NonNull
    private final List<Device> newList;

    public DeviceListCallback(@NotNull List<Device> oldList, @NonNull List<Device> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        final Device oldDevice = oldList.get(oldItemPosition);
        final Device newDevice = newList.get(oldItemPosition);

        Bundle diff = new Bundle();
        checkIfBaseDeviceIsEqual(oldDevice, newDevice, diff);
        checkIfDeviceChangedMode(oldDevice, newDevice, diff);
        return diff.size() == 0 ? null : diff;
    }

    private static void checkIfBaseDeviceIsEqual(final Device oldDevice,final Device newDevice,final Bundle diff) {
        if (oldDevice.getBrightness() != newDevice.getBrightness()) {
            diff.putInt(Device.DEVICE_BRIGHTNESS, newDevice.getBrightness());
        }

        if (!(oldDevice.getGroup().equals(newDevice.getGroup()))) {
            diff.putString(Device.DEVICE_GROUP, newDevice.getGroup());
        }

        if (!(oldDevice.getName().equals(newDevice.getName()))) {
            diff.putString(Device.DEVICE_NAME, newDevice.getName());
        }

        if (oldDevice.isOn() == newDevice.isOn()) {
            diff.putBoolean(Device.DEVICE_ON, newDevice.isOn());
        }

        if (!oldDevice.getIp().equals(newDevice.getIp())) {
            diff.putString(Device.DEVICE_IP, newDevice.getPort());
        }

        if (!(oldDevice.getPort().equals(newDevice.getPort()))) {
            diff.putString(Device.DEVICE_PORT, newDevice.getPort());
        }
    }

    private static void checkIfDeviceChangedMode(final Device oldDevice, final Device newDevice, final Bundle diff) {

        if (oldDevice instanceof DeviceSolid && newDevice instanceof DeviceSolid) {
            final DeviceSolid oldSolid = (DeviceSolid) oldDevice;
            final DeviceSolid newSolid = (DeviceSolid) newDevice;
            if (oldSolid.getColor() != newSolid.getColor()) {
                diff.putInt(DeviceSolid.SOLID_COLOR, newSolid.getColor());
            }

        } else if (oldDevice instanceof DeviceWaves && newDevice instanceof DeviceWaves) {
            final DeviceWaves oldWaves = (DeviceWaves) oldDevice;
            final DeviceWaves newWaves = (DeviceWaves) newDevice;

            if (oldWaves.getSpeed() != newWaves.getSpeed()) {
                diff.putInt(DeviceWaves.WAVES_SPEED, newWaves.getSpeed());
            }

            if (!(oldWaves.getColors().equals(newWaves.getColors()))) {
                diff.putIntegerArrayList(DeviceWaves.WAVES_COLORS, (ArrayList<Integer>) newWaves.getColors());
            }

        } else if (oldDevice instanceof DeviceMusic && newDevice instanceof DeviceMusic) {
            final DeviceMusic oldMusic = (DeviceMusic) oldDevice;
            final DeviceMusic newMusic = (DeviceMusic) newDevice;

            if (oldMusic.getLowColor() != newMusic.getLowColor()) {
                diff.putInt(DeviceMusic.MUSIC_LOW, newMusic.getLowColor());
            }

            if (oldMusic.getMidColor() != newMusic.getMidColor()) {
                diff.putInt(DeviceMusic.MUSIC_MID, newMusic.getMidColor());
            }

            if (oldMusic.getHighColor() != newMusic.getHighColor()) {
                diff.putInt(DeviceMusic.MUSIC_HIGH, newMusic.getHighColor());
            }

            if (oldMusic.getIntensity() != newMusic.getIntensity()) {
                diff.putInt(DeviceMusic.MUSIC_INTENSITY, newMusic.getIntensity());
            }

        } else if (newDevice instanceof DeviceSolid) {
            convertToDeviceSolid((DeviceSolid) newDevice, diff);

        } else if (newDevice instanceof DeviceWaves) {
            convertToDeviceWaves((DeviceWaves) newDevice, diff);

        } else if (newDevice instanceof DeviceMusic) {
            convertToDeviceMusic((DeviceMusic) newDevice, diff);
        }
    }

    private static void convertToDeviceSolid(final DeviceSolid newSolid, final Bundle diff) {
        diff.putInt(DeviceSolid.SOLID_COLOR, newSolid.getColor());
    }

    private static void convertToDeviceMusic(final DeviceMusic newMusic, final Bundle diff) {
        diff.putInt(DeviceMusic.MUSIC_LOW, newMusic.getLowColor());
        diff.putInt(DeviceMusic.MUSIC_MID, newMusic.getMidColor());
        diff.putInt(DeviceMusic.MUSIC_HIGH, newMusic.getHighColor());
        diff.putInt(DeviceMusic.MUSIC_INTENSITY, newMusic.getIntensity());

    }

    private static void convertToDeviceWaves(final DeviceWaves newWaves, final Bundle diff) {
        diff.putInt(DeviceWaves.WAVES_SPEED, newWaves.getSpeed());
        diff.putIntegerArrayList(DeviceWaves.WAVES_COLORS, (ArrayList<Integer>) newWaves.getColors());
    }

     */
}
