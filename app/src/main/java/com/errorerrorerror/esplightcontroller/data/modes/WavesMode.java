package com.errorerrorerror.esplightcontroller.data.modes;

import android.graphics.Color;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WavesMode extends BaseMode {

    private int speed;

    public static WavesMode getDefaultMode() {
        return WavesMode.builder().speed(0).colorList(Arrays.asList(Color.RED, Color.BLUE, Color.WHITE)).build();
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

    /// Parcelable Methods
    public static final Creator<WavesMode> CREATOR = new Creator<WavesMode>() {
        @Override public WavesMode createFromParcel(Parcel source) {return new WavesMode(source);}

        @Override public WavesMode[] newArray(int size) {return new WavesMode[size];}
    };

    public WavesMode(Parcel in) {
        colorList = new ArrayList<>();
        in.readList(colorList, Integer.class.getClassLoader());
        this.speed = in.readInt();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(colorList);
        dest.writeInt(speed);
    }
}
