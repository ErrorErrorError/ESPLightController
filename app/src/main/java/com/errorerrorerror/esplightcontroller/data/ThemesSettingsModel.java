package com.errorerrorerror.esplightcontroller.data;

import android.graphics.drawable.Drawable;

import com.errorerrorerror.esplightcontroller.data.base.BaseModel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(callSuper = true)
public class ThemesSettingsModel extends BaseModel {
    private boolean active;

    public ThemesSettingsModel(Drawable image, String title, boolean active) {
        super(image, title);
        this.active = active;
    }
}
