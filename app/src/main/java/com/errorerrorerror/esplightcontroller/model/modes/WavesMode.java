package com.errorerrorerror.esplightcontroller.model.modes;

import android.graphics.Color;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WavesMode extends BaseMode{

    private int speed;

    public WavesMode() {
        colorList = new ArrayList<>(6);
    }

    public WavesMode(int speed, List<Integer> colorList) {
        this.speed = speed;
        this.colorList = colorList;
    }

    @Override
    public byte[] toDataByte() {
        byte[] data = new byte[(colorList.size() * 3) + 3];
        data[0] = WAVES;
        data[1] = (byte) speed;
        data[2] = (byte) colorList.size();

        for (int i = 3; i < colorList.size() + 3; i++) {
            int color = colorList.get(i - 3);
            int index = (i - 3) * 3 + 3;
            data[index] = (byte) Color.red(color);
            data[index + 1] = (byte) Color.green(color);
            data[index + 2] = (byte) Color.blue(color);
        }

        return data;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @NotNull
    @Override
    public String toString() {
        return "WavesMode{" +
                "speed=" + speed +
                "} " + super.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(colorList);
    }

}
