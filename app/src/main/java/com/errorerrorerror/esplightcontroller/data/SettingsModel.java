package com.errorerrorerror.esplightcontroller.data;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;

import com.errorerrorerror.esplightcontroller.data.base.BaseModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode(callSuper = true) @Getter @Setter
public class SettingsModel extends BaseModel {

    public static final int THEME = 0;
    public static final int NOTIFICATION = 1;
    public static final int CUSTOMIZATION = 2;

    public SettingsModel(Drawable image, String title, int settingsType) {
        super(image, title);
        this.settingsType = settingsType;
    }

    @IntDef({
            THEME,
            NOTIFICATION,
            CUSTOMIZATION
    })
    @Retention(RetentionPolicy.SOURCE) public @interface SettingsType {}

    @SettingsType private int settingsType;
}
