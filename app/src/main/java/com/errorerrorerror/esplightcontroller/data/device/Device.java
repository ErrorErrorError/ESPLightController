package com.errorerrorerror.esplightcontroller.data.device;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.errorerrorerror.esplightcontroller.data.modes.BaseMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
@Entity(tableName = "device_table")
public class Device implements Parcelable {
    public static final String EMPTY_GROUP = "empty_group_placeholder";

    @PrimaryKey(autoGenerate = true)
    long id;
    String name;
    String ip;
    int port;
    boolean on;
    int brightness;
    String group;
    BaseMode mode;

    @Ignore
    public Device(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
        ip = parcel.readString();
        port = parcel.readInt();
        on = parcel.readByte() == 1;
        brightness = parcel.readInt();
        group = parcel.readString();
        mode = parcel.readParcelable(BaseMode.class.getClassLoader());
    }

    public Device() { }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(ip);
        dest.writeInt(port);
        dest.writeByte((byte) (on ? 1 : 0));
        dest.writeInt(brightness);
        dest.writeString(group);
        dest.writeParcelable(mode, PARCELABLE_WRITE_RETURN_VALUE );
    }


    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
}
