package com.errorerrorerror.esplightcontroller.model.modes;

import java.util.List;
import java.util.Objects;

public abstract class BaseMode {

    /// Modes
    public static final int STEADY = 0;
    public static final int WAVES = 1;
    public static final int MUSIC = 2;

//    private long id;

    List<Integer> colorList;

    public abstract byte[] toDataByte();

    public List<Integer> getColorList() {
        return colorList;
    }

    public void setColorList(List<Integer> colorList) {
        this.colorList = colorList;
    }

/*
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseMode)) return false;
        BaseMode baseMode = (BaseMode) o;
        return colorList.equals(baseMode.getColorList());
    }

    @Override
    public String toString() {
        return "BaseMode{" +
                "colorList=" + colorList +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(colorList);
    }
}
