package com.errorerrorerror.multicolorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.errorerrorerror.colorpicker.R;

public class ColorWheelSelector extends View {

    interface OnColorChangedListener {
        /**
         * This is a listener when color has changed.
         *
         * @param selector The selector that changed color.
         * @param color    The new color
         */
        void onColorSelected(ColorWheelSelector selector, @ColorInt int color);
    }

    @ColorInt
    private int currentColor;

    private OnColorChangedListener onColorChangedListener;

    private static final String TAG = ColorWheelSelector.class.getSimpleName();

    private final Paint paint;

    private float dX;

    private float dY;

    private boolean calculateValue = false;

    private float strokeWidth;

    private boolean floatingLabel = false;

    private Drawable floatingDrawable;

    private boolean isBeingPressed = false;

    public ColorWheelSelector(Context context) {
        this(context, null);
    }

    public ColorWheelSelector(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorWheelSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorWheelSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        getAttrs(attrs, defStyleAttr, defStyleRes);

        paint = new Paint();

        floatingDrawable = context.getDrawable(R.drawable.selectable_label_drawable);
        floatingDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
    }

    private void getAttrs(@Nullable AttributeSet attrs, int styleAttr, int styleRes) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ColorWheelSelector);

        currentColor = ta.getColor(R.styleable.ColorWheelSelector_selectorColor, Color.WHITE);
        setElevation(ta.getDimension(R.styleable.ColorWheelSelector_android_elevation, getResources().getDimension(R.dimen.elevation)));
        strokeWidth = ta.getDimension(R.styleable.ColorWheelSelector_strokeWidth, getResources().getDimension(R.dimen.strokeWidth));

        ta.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float calcX;
        float calcY;
        boolean handledTouch = false;

        boolean needsInvalidate = false;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);

                dX = getX() - event.getRawX() + Math.round(getWidth() / 2);
                dY = getY() - event.getRawY() + Math.round(getHeight() / 2);

                needsInvalidate = convertEventToColor(dX + event.getRawX(), dY + event.getRawY());
                handledTouch = true;
                isBeingPressed = true;
                break;

            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);

                calcX = event.getRawX() + dX;
                calcY = event.getRawY() + dY;

                convertEventToColor(calcX, calcY);

                needsInvalidate = true;
                handledTouch = true;
                isBeingPressed = false;
                break;

            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);

                calcX = event.getRawX() + dX;
                calcY = event.getRawY() + dY;

                needsInvalidate = convertEventToColor(calcX, calcY);
                handledTouch = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                isBeingPressed = false;
                Log.v(TAG, "Selector Canceled");
                break;
        }

        if (needsInvalidate) {
            updateFloatColor();
            invalidate();
        }

        setPressed(isBeingPressed);

        return handledTouch;
    }

    private int getCenterX() {
        return (getParentWidth() / 2);
    }

    private int getCenterY() {
        return (getParentHeight() / 2);
    }

    private int getParentRadius() {
        return (Math.min(getParentWidth(), getParentHeight()) - getWidth()) / 2;
    }

    private int getParentWidth() {
        return getParent() != null ? ((View) getParent()).getMeasuredWidth() : 0;
    }

    private int getParentHeight() {
        return getParent() != null ? ((View) getParent()).getMeasuredHeight() : 0;
    }

    private void updateFloatColor() {
        floatingDrawable.setColorFilter(new PorterDuffColorFilter(currentColor, PorterDuff.Mode.SRC_IN));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int radius = getWidth() / 2;
        int yAxis = getHeight() / 2;


        /// Draws Color Selected
        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(currentColor);
        canvas.drawCircle(radius, yAxis, radius, paint);

        /// Draws the stroke
        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(radius, yAxis, radius, paint);


        /// Draws the floating label

        if (floatingLabel && isBeingPressed) {
            // drawFloatingLabel(canvas, radius);
        }
    }

    private void drawFloatingLabel(Canvas canvas, int radius) {
        int offset = radius;
        int height = getHeight();
        floatingDrawable.setBounds(0, -height - offset, radius + radius, -offset);
        floatingDrawable.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        width = height = Math.min(maxWidth, maxHeight);

        int floatingLabelHeight = floatingLabel ? 0 : 0;

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height + floatingLabelHeight, MeasureSpec.EXACTLY));
    }

    private void resetPositionToColor() {
        setSelectorToColorAndPosition(currentColor);
    }

    @ColorInt
    public final int getColor() {
        return currentColor;
    }

    public void setColor(@ColorInt int color) {
        if (currentColor != color) {
            currentColor = color;
            setSelectorToColorAndPosition(color);
            updateFloatColor();
            dispatchColorChanged();
            invalidate();
        }
    }

    public boolean isFloatingLabel() {
        return floatingLabel;
    }

    public void showFloadingLabel(boolean floadingLabel) {
        if (this.floatingLabel != floadingLabel) {
            this.floatingLabel = floadingLabel;
            requestLayout();
        }
    }

    void shouldCalculateValue(boolean shouldCalcVal) {
        if (calculateValue != shouldCalcVal) {
            calculateValue = shouldCalcVal;
            resetPositionToColor();
        }
    }

    public OnColorChangedListener getOnColorChangedListener() {
        return onColorChangedListener;
    }

    public void setOnColorChangedListener(@Nullable OnColorChangedListener onColorChangedListener) {
        this.onColorChangedListener = onColorChangedListener;
    }

    private void dispatchColorChanged() {
        if (onColorChangedListener != null) {
            onColorChangedListener.onColorSelected(this, currentColor);
        }
    }

    private void setSelectorToColorAndPosition(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        float saturationOffset = hsv[1] * getParentRadius();
        float radian = (float) (hsv[0] / 180f * Math.PI);

        if (calculateValue) {
            // Adds the Value from the HSV.
            saturationOffset += (1 - hsv[2]) * getParentRadius();
            if (color == Color.BLACK) {
                // Since the color Black has no saturation, we need to add the radius so it can be in the far
                // right.
                saturationOffset += getParentRadius();
            }

            // Divide the saturation by two since we are also including the Value of HSV.
            saturationOffset = saturationOffset/2;
        }

        float xAxis = (float) (saturationOffset * Math.cos(radian) + getCenterX());
        float yAxis = (float) (-saturationOffset * Math.sin(radian) + getCenterY());

        updateSelectorPosition(xAxis, yAxis);
    }

    private void updateSelectorPosition(float eventX, float eventY) {
        float x = eventX - getCenterX();
        float y = eventY - getCenterY();
        float radius = getParentRadius();
        double r = Math.sqrt(x * x + y * y);
        if (r > radius) {
            x *= radius / r;
            y *= radius / r;
        }

        float newPointX = x + getCenterX() - (float) getWidth() / 2;
        float newPointY = y + getCenterY() - (float) getHeight() / 2;

        this.setX(newPointX);
        this.setY(newPointY);
    }

    private int getColorAtPoint(float eventX, float eventY) {
        float x = eventX - getCenterX();
        float y = eventY - getCenterY();
        float radius = getParentRadius();
        double r = Math.sqrt(x * x + y * y);

        float hue = (float) (Math.atan2(y, -x) / Math.PI * 180f) + 180;
        float saturation;
        float value;
        if (calculateValue) {
            saturation = Math.max(0f, Math.min(1f, (float) (r * 2 / radius)));
            value = Math.min(1f, 2 - ((float) r * 2/ radius));
        } else {
            saturation = Math.max(0f, Math.min(1f, (float) (r / radius)));
            value = 1;
        }

        return Color.HSVToColor(new float[]{hue, saturation, value});
    }

    private boolean convertEventToColor(float eventX, float eventY) {
        boolean needsInvalidate = false;
        updateSelectorPosition(eventX, eventY);
        int newColor = getColorAtPoint(eventX, eventY);
        if (newColor != currentColor) {
            currentColor = newColor;
            dispatchColorChanged();
            needsInvalidate = true;
        }

        return needsInvalidate;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

//        Elevation
        setOutlineProvider(new SelectorOutlineProvider(w, h));
        resetPositionToColor();
    }

    private class SelectorOutlineProvider extends ViewOutlineProvider {
        int width;
        int height;

        SelectorOutlineProvider(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void getOutline(View view, @NonNull Outline outline) {
            outline.setOval(0, 0, width, height);
        }
    }
}
