package com.errorerrorerror.esplightcontroller.data.base;

import android.graphics.drawable.Drawable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode @AllArgsConstructor
public abstract class BaseModel {
    private Drawable image;
    private String title;
}
