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
public class SteadyMode extends BaseMode {

    private boolean blend;

    public static SteadyMode getDefaultMode() {
        return SteadyMode.builder().blend(true).colorList(Arrays.asList(Color.RED)).build();
    }

    @Override
    public byte[] toDataByte() {
        byte[] data = new byte[colorList.size() * 3 + 3];

        data[0] = STEADY;
        data[1] = (byte) (blend ? 1 : 0);
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
    public static final Creator<SteadyMode> CREATOR = new Creator<SteadyMode>() {
        @Override public SteadyMode createFromParcel(Parcel source) {return new SteadyMode(source);}

        @Override public SteadyMode[] newArray(int size) {return new SteadyMode[size];}
    };

    public SteadyMode(Parcel in) {
        colorList = new ArrayList<>();
        in.readList(colorList, Integer.class.getClassLoader());
        this.blend = in.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(colorList);
        dest.writeByte((byte) (blend ? 1 : 0));
    }
}
