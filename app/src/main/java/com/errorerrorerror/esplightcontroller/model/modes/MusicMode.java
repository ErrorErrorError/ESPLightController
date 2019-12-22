package com.errorerrorerror.esplightcontroller.model.modes;

import android.graphics.Color;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MusicMode extends BaseMode {

    private int intensity;

    public MusicMode() {
        intensity = 0;
        colorList = new ArrayList<>(3);
    }

    public MusicMode(int intensity, @NonNull List<Integer> colorList) {
        this.colorList = colorList;
        this.intensity = intensity;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    @Override
    public byte[] toDataByte() {
        byte[] data = new byte[(3 * 3) + 2];
        int index = 0;

        data[index] = MUSIC;
        data[index + 1] = (byte) intensity;

        /// Low
        data[index + 2] = (byte) Color.red(getColorList().get(0));
        data[index + 3] = (byte) Color.green(getColorList().get(0));
        data[index + 4] = (byte) Color.blue(getColorList().get(0));

        /// Mid
        data[index + 5] = (byte) Color.red(getColorList().get(1));
        data[index + 6] = (byte) Color.green(getColorList().get(1));
        data[index + 7] = (byte) Color.blue(getColorList().get(1));

        /// High
        data[index + 8] = (byte) Color.red(getColorList().get(2));
        data[index + 9] = (byte) Color.green(getColorList().get(2));
        data[index + 10] = (byte) Color.blue(getColorList().get(2));

        return data;
    }

    @NotNull
    @Override
    public String toString() {
        return "MusicMode{" +
                "intensity=" + intensity +
                "} " + super.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(intensity, colorList);
    }
}
