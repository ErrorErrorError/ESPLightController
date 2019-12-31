package com.errorerrorerror.esplightcontroller.data.modes;

import android.graphics.Color;
import android.os.Parcel;

import androidx.annotation.ColorInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MusicMode extends BaseMode {

    private int intensity;

    public static MusicMode getDefaultMode() {
        return MusicMode.builder().intensity(0).colorList(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE)).build();
    }

    public void setLowColor(@ColorInt int lowColor) {
        colorList.set(0, lowColor);
    }

    public void setMidColor(@ColorInt int midColor) {
        colorList.set(1, midColor);
    }

    public void setHighColor(@ColorInt int highColor) {
        colorList.set(2, highColor);
    }

    public void setColor(int index, @ColorInt int color) {
        if (colorList.size() > index && index >= 0) {
            colorList.set(index, color);
        }
    }

    @ColorInt
    public int getLowColor() {
        return colorList.get(0);
    }

    @ColorInt
    public int getMidColor() {
        return colorList.get(1);
    }

    @ColorInt
    public int getHighColor() {
        return colorList.get(2);
    }

    @Override
    public byte[] toDataByte() {
        byte[] data = new byte[(3 * 3) + 2];
        int index = 0;

        data[index] = MUSIC;
        data[index + 1] = (byte) intensity;

        /// Low
        data[index + 2] = (byte) Color.red(colorList.get(0));
        data[index + 3] = (byte) Color.green(colorList.get(0));
        data[index + 4] = (byte) Color.blue(colorList.get(0));

        /// Mid
        data[index + 5] = (byte) Color.red(colorList.get(1));
        data[index + 6] = (byte) Color.green(colorList.get(1));
        data[index + 7] = (byte) Color.blue(colorList.get(1));

        /// High
        data[index + 8] = (byte) Color.red(colorList.get(2));
        data[index + 9] = (byte) Color.green(colorList.get(2));
        data[index + 10] = (byte) Color.blue(colorList.get(2));

        return data;
    }

    @Override
    public void setColorList(List<Integer> colorList) {
        /// Handles his own color list
    }


    /// Parcelable Methods
    public static final Creator<MusicMode> CREATOR = new Creator<MusicMode>() {
        @Override public MusicMode createFromParcel(Parcel source) {return new MusicMode(source);}

        @Override public MusicMode[] newArray(int size) {return new MusicMode[size];}
    };

    public MusicMode(Parcel in) {
        colorList = new ArrayList<>();
        in.readList(colorList, Integer.class.getClassLoader());
        this.intensity = in.readInt();
    }

        @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(colorList);
        dest.writeInt(intensity);
    }

}
