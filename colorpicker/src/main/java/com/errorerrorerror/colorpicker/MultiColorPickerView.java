package com.errorerrorerror.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedHashSet;

/**
 * HSV color wheel
 */
public class MultiColorPickerView extends FrameLayout {

    /** @hide */
    @IntDef({HUE_SATURATION, HUE_SATURATION_VALUE})
    @Retention(RetentionPolicy.SOURCE)
    private  @interface ColorPickerMode {}
    public static final int HUE_SATURATION = 0;
    public static final int HUE_SATURATION_VALUE = 1;

    private int colorPickerMode;

    private ColorWheelSelector mainSelector;

    private @ColorInt int mainSelectorColor;

    public interface OnSelectorColorChangedListener {
        /**
         * This interface listens for change in color of selectors.
         * @param selector The selector that changed color
         * @param color the new color
         * @param index the index of the selector.
         */
        void onSelectorColorChanged(@NonNull ColorWheelSelector selector, @ColorInt int color, int index);
    }

    private static final String TAG = "MultiColorPickerView";

    private SelectorOnColorChangedListener selectorColorTracker = new SelectorOnColorChangedListener();

    private LinkedHashSet<OnSelectorColorChangedListener> selectorColorChangedListener = new LinkedHashSet<>();

    public MultiColorPickerView(Context context) {
        this(context, null);
    }

    public MultiColorPickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(attrs);
        init();
    }

    private void init() {
        ColorWheelView colorWheelView = new ColorWheelView(getContext());
        setGeneratedIdIfNeeded(colorWheelView);
        addView(colorWheelView);

        mainSelector = new ColorWheelSelector(getContext());
        setGeneratedIdIfNeeded(mainSelector);
        mainSelector.setColor(mainSelectorColor);
        addView(mainSelector);
    }

    private void getAttributes(@Nullable AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MultiColorPickerView);
        colorPickerMode = ta.getInteger(R.styleable.MultiColorPickerView_pickerMode, HUE_SATURATION);
        mainSelectorColor = ta.getColor(R.styleable.MultiColorPickerView_mainSelectorColor, Color.WHITE);

        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        width = height = Math.min(maxWidth, maxHeight);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

        int defaultSelectorRadius = width / 12;

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child instanceof ColorWheelView) {
                child.getLayoutParams().width = (width - defaultSelectorRadius);
                child.getLayoutParams().height = (height - defaultSelectorRadius);
                child.setTranslationX(defaultSelectorRadius / 2);
                child.setTranslationY(defaultSelectorRadius / 2);
            } else if (child instanceof ColorWheelSelector) {
                child.getLayoutParams().width = Math.round(defaultSelectorRadius);
                child.getLayoutParams().height = Math.round(defaultSelectorRadius);
            }

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setMainSelectorColor(@ColorInt int newColor) {
        mainSelector.setColor(newColor);
    }

    /**
     * Adds a new ColorWheelSelector with the specified color.
     * @param color The color of the selector.
     */
    public void addSelector(@ColorInt int color) {
        final ColorWheelSelector selector = new ColorWheelSelector(getContext());
        setGeneratedIdIfNeeded(selector);
        selector.setColor(color);
        addView(selector);
    }

    private void setGeneratedIdIfNeeded(@NonNull View child) {
        if (child.getId() == View.NO_ID) {
            child.setId(ViewCompat.generateViewId());
        }
    }

    private int colorWheelViewCount() {
        int count = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child instanceof ColorWheelView) {
                count++;
            }
        }

        return count;
    }

    /**
     * Counts the number of selectors in the ViewGroup.
     * @return the number of selectors.
     */
    public int getSelectorCount() {
        int count = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child instanceof ColorWheelSelector) {
                count++;
            }
        }

        return count;
    }

    /**
     * This gets the selector at specified index.
     * <br/>
     * <br/>
     * Note: This is basically calling {@link #getChildAt(int)} but since this layout has a ColorWheelView,
     * this method increments the index by one to avoid getting the ColorWheelView.
     * @param index the index of the selector. Range from 0 to {@link #getSelectorCount()} - 1
     * @return Returns the selector at the specified index. If selector not found or is not a ColorWheelSelector at a given index, it returns null.
     */
    public ColorWheelSelector getSelectorAt(int index) {
        View child;

        try {
            child = getChildAt(index + 1);
        } catch (IndexOutOfBoundsException e) {
            child = null;
        }

        return (child instanceof ColorWheelSelector) ? (ColorWheelSelector) child : null;
    }

    /**
     * Removes the selector at a given index.
     * <br/>
     * <br/>
     * Note: This method increments the index by one since ColorWheelView is in the ViewGroup.
     * For more info read {@link #getSelectorAt(int index)}
     * @param index The index of the selector.
     */
    public void removeSelectorAt(int index) {
        super.removeViewAt(index + 1);
    }

    @ColorPickerMode
    public int getColorPickerMode() {
        return colorPickerMode;
    }

    public void setColorPickerMode(@ColorPickerMode int mode) {
        if (colorPickerMode != mode) {
            this.colorPickerMode = mode;
            dispatchColorPickerMode();
        }
    }

    private void dispatchColorPickerMode() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ColorWheelView) {
                ((ColorWheelView) child).showValue(colorPickerMode == HUE_SATURATION_VALUE);
                break;
            }
        }

        for (int i = 0; i < getSelectorCount(); i++) {
            ColorWheelSelector selector = getSelectorAt(i);
            selector.shouldCalculateValue(colorPickerMode == HUE_SATURATION_VALUE);
        }
    }

    private void setupSelectors(ColorWheelSelector selector) {
        selector.setOnColorChangedListener(selectorColorTracker);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        dispatchColorPickerMode();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
        if (!result && event.getAction() == MotionEvent.ACTION_DOWN) {
            ColorWheelSelector selector = new ColorWheelSelector(getContext());
            addView(selector);
            selector.onTouchEvent(event);
            result = true;
        }

        Log.d(TAG, "onTouchEvent: " + result + " event: X: "  + event.getRawX() + " y: "  + event.getRawY());
        */

        return super.onTouchEvent(event);
    }

    @Override
    public void addView(View child) {
        if ((child instanceof ColorWheelSelector) || ((child instanceof ColorWheelView) && colorWheelViewCount() < 1)) {
            super.addView(child);

            if (child instanceof ColorWheelSelector) {
                setupSelectors((ColorWheelSelector) child);
            }

        } else {
            Log.v(TAG, "This only accepts ColorWheelSelector and only one ColorWheelView");
        }
    }

    @Override
    public void addView(View child, int index) {
        if ((child instanceof ColorWheelSelector) || ((child instanceof ColorWheelView) && colorWheelViewCount() < 1)) {
            super.addView(child, index);
            setGeneratedIdIfNeeded(child);
            if (child instanceof ColorWheelSelector) {
                setupSelectors((ColorWheelSelector) child);
            }

        } else {
            Log.v(TAG, "This only accepts ColorWheelSelector and only one ColorWheelView");
        }
    }

    @Override
    public void addView(View child, int width, int height) {
        if ((child instanceof ColorWheelSelector) || ((child instanceof ColorWheelView) && colorWheelViewCount() < 1)) {
            super.addView(child, width, height);
            setGeneratedIdIfNeeded(child);

            if (child instanceof ColorWheelSelector) {
                setupSelectors((ColorWheelSelector) child);
            }

        } else {
            Log.v(TAG, "This only accepts ColorWheelSelector and only one ColorWheelView");
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if ((child instanceof ColorWheelSelector) || ((child instanceof ColorWheelView) && colorWheelViewCount() < 1)) {
            super.addView(child, params);
            setGeneratedIdIfNeeded(child);

            if (child instanceof ColorWheelSelector) {
                setupSelectors((ColorWheelSelector) child);
            }

        } else {
            Log.v(TAG, "This only accepts ColorWheelSelector and only one ColorWheelView");
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if ((child instanceof ColorWheelSelector) || ((child instanceof ColorWheelView) && colorWheelViewCount() < 1)) {
            super.addView(child, index, params);
            setGeneratedIdIfNeeded(child);

            if (child instanceof ColorWheelSelector) {
                setupSelectors((ColorWheelSelector) child);
            }

        } else {
            Log.v(TAG, "This only accepts ColorWheelSelector and only one ColorWheelView");
        }
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);

        if (child instanceof ColorWheelSelector) {
            ((ColorWheelSelector) child).setOnColorChangedListener(null);
        }
    }

    public void removeOnSelectorColorChangedListener(OnSelectorColorChangedListener colorChangeListener) {
        this.selectorColorChangedListener.remove(colorChangeListener);
    }

    public void addOnSelectorColorChangedListener(OnSelectorColorChangedListener colorChangeListener) {
        this.selectorColorChangedListener.add(colorChangeListener);
    }

    private void dispatchOnSelectorColorChanged(@NonNull ColorWheelSelector selector, @ColorInt int color) {
        for (OnSelectorColorChangedListener listener : selectorColorChangedListener) {
            listener.onSelectorColorChanged(selector, color, indexOfChild(selector) - 1);
        }
    }

    private class SelectorOnColorChangedListener implements ColorWheelSelector.OnColorChangedListener {
        @Override
        public void onColorSelected(ColorWheelSelector selector, int color) {
            dispatchOnSelectorColorChanged(selector, color);
        }
    }
}
