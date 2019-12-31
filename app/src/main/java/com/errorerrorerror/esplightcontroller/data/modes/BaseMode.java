package com.errorerrorerror.esplightcontroller.data.modes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class BaseMode implements Parcelable{

    /// Modes
    public static final int STEADY = 0;
    public static final int WAVES = 1;
    public static final int MUSIC = 2;

    protected List<Integer> colorList;

    protected BaseMode() {
    }

    public abstract byte[] toDataByte();
}
