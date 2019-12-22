package com.errorerrorerror.esplightcontroller.model.modes;

import android.graphics.Color;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SteadyMode extends BaseMode {

    public SteadyMode() {
        colorList = new ArrayList<>();
        colorList.add(Color.RED);
    }

    public SteadyMode(@NonNull List<Integer> colorList) {
        this.colorList = colorList;
    }

    @Override
    public byte[] toDataByte() {
        byte[] data = new byte[colorList.size() * 3 + 2];

        data[0] = STEADY;
        data[1] = (byte) colorList.size();

        for (int i = 2; i < colorList.size() + 2; i++) {
            int color = colorList.get(i - 2);
            int index = (i - 2) * 3 + 2;
            data[index] = (byte) Color.red(color);
            data[index + 1] = (byte) Color.green(color);
            data[index + 2] = (byte) Color.blue(color);
        }

        return data;
    }

    @NotNull
    @Override
    public String toString() {
        return "SteadyMode{} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SteadyMode)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
