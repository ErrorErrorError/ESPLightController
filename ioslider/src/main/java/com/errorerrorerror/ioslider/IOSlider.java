package com.errorerrorerror.ioslider;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.AnyRes;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.LottieTask;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

public class IOSlider extends View {

    public interface OnChangeListener {
        void onProgressChanged(IOSlider slider, float progress);
        void onStartTrackingTouch(IOSlider slider);
        void onStopTrackingTouch(IOSlider slider);
    }

    private static final String TAG = IOSlider.class.getName();
    private static final int DEF_STYLE_RES = R.style.Widget_IOSlider;
    private static final String TYPE_RAW  = "raw";
    private static final String TYPE_DRAWABLE  = "drawable";

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    @interface OrientationMode { }
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    @IntDef({DRAG, TOUCH})
    @Retention(RetentionPolicy.SOURCE)
    @interface TouchMode {}
    public static final int DRAG = 0;
    public static final int TOUCH = 1;

    @IntDef({TEXT, ICON, TEXTICON, ICONTEXT})
    @Retention(RetentionPolicy.SOURCE)
    @interface IconTextVisibility {}
    public static final int ICON = 0;
    public static final int TEXT = 1;
    public static final int ICONTEXT = 2;
    public static final int TEXTICON = 3;

    @NonNull
    private final Paint inactiveTrackPaint;
    @NonNull
    private final Paint activeTrackPaint;
    @NonNull
    private final Paint textPaint;

    private float mRadius;
    private float textSize;
    private float mProgress; // Range from 0.0f - 1.0f
    private float minValue;
    private float maxValue;

    private ColorStateList textColor;
    private ColorStateList inactiveTrackColor;
    private ColorStateList activeTrackColor;
    private ColorStateList iconColor;

    private final Rect activeTrackRect;
    private final Rect inactiveTrackRect;
    private final Rect drawableRect;
    private final Rect textBounds;

    private String labelText = "";

    private float lastTouchX;
    private float lastTouchY;

    private @OrientationMode int mOrientation;
    private @TouchMode int touchMode;
    private @IconTextVisibility
    int iconTextView;

    @Nullable
    private Drawable iconDrawable;
    @AnyRes
    private int iconResource;
    @Dimension
    private int iconSize;

    @Nullable
    private OnChangeListener onChangeListener;

    private boolean blendWithBackground;

    ///////////////// Lottie Animation Listeners /////////////////
    private LottieTask<LottieComposition> lottieCompositionTask;

    public IOSlider(Context context) {
        this(context, null);
    }

    public IOSlider(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.IOSliderStyle);
    }

    public IOSlider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, DEF_STYLE_RES);
    }

    public IOSlider(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        getResources(context, attrs, defStyleAttr, defStyleRes);

        inactiveTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        inactiveTrackPaint.setStyle(Paint.Style.FILL);

        activeTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        activeTrackPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextSize(textSize);

        activeTrackRect = new Rect();
        inactiveTrackRect = new Rect();
        textBounds = new Rect();
        drawableRect = new Rect();

        final String typeResource = context.getResources().getResourceTypeName(iconResource);
        if (typeResource.equals(TYPE_RAW)) {
            setAnimation(iconResource);
        } else if (typeResource.equals(TYPE_DRAWABLE)) {
            setIconDrawable(context.getDrawable(iconResource));
        } else {
            throw new IllegalArgumentException("Icon must be a drawable or a raw res animation.");
        }
    }

    private void getResources(Context context, @Nullable AttributeSet attrs, int style, int res) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IOSlider, style, res);

        mRadius = ta.getDimensionPixelSize(R.styleable.IOSlider_cornerRadius, context.getResources().getDimensionPixelSize(R.dimen.corner_radius));

        iconResource = ta.getResourceId(R.styleable.IOSlider_iconAnimation, R.raw.brightness_animation);
        iconColor = ta.getColorStateList(R.styleable.IOSlider_iconColor);
        iconSize = ta.getDimensionPixelSize(R.styleable.IOSlider_iconSize, getContext().getResources().getDimensionPixelSize(R.dimen.icon_size));

        textColor = ta.getColorStateList(R.styleable.IOSlider_labelColor);
        textSize = ta.getDimension(R.styleable.IOSlider_labelSize, context.getResources().getDimension(R.dimen.text_size));

        activeTrackColor = ta.getColorStateList(R.styleable.IOSlider_activeTrackColor);
        inactiveTrackColor = ta.getColorStateList(R.styleable.IOSlider_inactiveTrackColor);

        minValue = ta.getFloat(R.styleable.IOSlider_android_min, 0);
        maxValue = ta.getFloat(R.styleable.IOSlider_android_max, 100);

        blendWithBackground = ta.getBoolean(R.styleable.IOSlider_blendLabelIcon, true);

        mOrientation = ta.getInt(R.styleable.IOSlider_orientation, VERTICAL);

        touchMode = ta.getInt(R.styleable.IOSlider_touchMode, DRAG);

        setProgress(ta.getFloat(R.styleable.IOSlider_android_progress, 50));

        iconTextView = ta.getInt(R.styleable.IOSlider_iconTextVisibility, TEXTICON);

        ta.recycle();

        validateMinValue();
        validateMaxValue();
    }

    public void setProgress(float progress) {
        if(!isValueValid(progress)) return;

        float calc = progress / (maxValue - minValue);
        if (calc == mProgress) return;

        mProgress = calc;
        dispatchProgressChanged();
        updateText();
        updateDrawableAnimation();

        invalidate();
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
        validateMinValue();
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        validateMaxValue();
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getProgress() {
        return (maxValue - minValue) * mProgress;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(@OrientationMode int orientation) {
        if (orientation != VERTICAL && orientation != HORIZONTAL) return;

        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            invalidate();
        }

    }

    public int getTouchMode() {
        return touchMode;
    }

    public void setTouchMode(@TouchMode int touchMode) {
        if (touchMode != TOUCH && touchMode != DRAG) return;

        if (this.touchMode != touchMode) {
            this.touchMode = touchMode;
            invalidate();
        }
    }

    public void setIconSize(int iconSize) {
        if (this.iconSize == iconSize) return;

        this.iconSize = iconSize;
        updateIconSize();
        invalidate();
    }

    @IconTextVisibility
    public int getIconTextVisibility() {
        return iconTextView;
    }

    public void setIconTextVisibility(@IconTextVisibility int iconTextVisibility) {
        if (iconTextView != iconTextVisibility) {
            this.iconTextView = iconTextVisibility;
            invalidate();
        }
    }

    public void setOnProgressChangeListener(@Nullable OnChangeListener listener) {
        this.onChangeListener = listener;
    }

    private void dispatchProgressChanged() {
        if (onChangeListener == null) return;

        onChangeListener.onProgressChanged(this, getProgress());
    }

    private void dispatchOnStartTrackingTouch() {
        if (onChangeListener == null) return;

        onChangeListener.onStartTrackingTouch(this);

    }

    private void dispatchOnStopTrackingTouch() {
        if (onChangeListener == null) return;

        onChangeListener.onStopTrackingTouch(this);
    }

    private void updateText() {
        float value = getProgress();
        labelText = String.format(Locale.ENGLISH,"%.0f", value);
    }

    private void updateDrawableAnimation() {
        if (!(iconDrawable instanceof LottieDrawable)) return;

        final LottieDrawable lottieDrawable = (LottieDrawable) iconDrawable;

        lottieDrawable.setProgress(mProgress);
    }

    private void updateIconDrawableColor(@Nullable ColorStateList colorStateList) {
        if (iconDrawable == null) return;

        PorterDuffColorFilter colorFilter = null;
        if (colorStateList != null) {
            colorFilter = new PorterDuffColorFilter(getColorForState(colorStateList), PorterDuff.Mode.SRC_IN);
        }

        if (iconDrawable instanceof LottieDrawable) {
            KeyPath keyPath = new KeyPath("**");
            LottieValueCallback<ColorFilter> callback = new LottieValueCallback<ColorFilter>(colorFilter);
            ((LottieDrawable) iconDrawable).addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback);

        } else {
            if (iconDrawable.getColorFilter() != colorFilter) {
                iconDrawable.setColorFilter(colorFilter);
            }
        }
    }

    private void updateIconSize() {
        if (iconDrawable == null) return;

        if (iconDrawable instanceof LottieDrawable) {

            /// Workaround since LottieDrawable handles width/height on setScale
            int previousWidth = Math.max(1, iconDrawable.getBounds().width());
            float scale = (float) iconSize/previousWidth;
            ((LottieDrawable) iconDrawable).setScale(scale);
        } else {
            iconDrawable.setBounds(0,0, iconSize, iconSize);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;
        float dX;
        float dY;
        float distance;
        boolean handledTouch = false;
        boolean needsUpdate = false;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);

                lastTouchX = event.getX();
                lastTouchY = event.getY();

                if (touchMode == TOUCH) {
                    if (mOrientation == VERTICAL) {
                        mProgress = Math.max(0.0f, Math.min(1.0f, 1.0f - (lastTouchY / getHeight())));
                    } else {
                        mProgress = Math.max(0.0f, Math.min(1.0f, (lastTouchX / getWidth())));
                    }

                    needsUpdate = true;

                    dispatchOnStartTrackingTouch();
                }

                setPressed(true);

                handledTouch = true;
                break;

            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);

                dX = lastTouchX - event.getX();
                dY = lastTouchY - event.getY();

                distance = (mOrientation == VERTICAL) ? dY : dX;

                needsUpdate = calculateValueFromEvent(distance);

                lastTouchX = event.getX();
                lastTouchY = event.getY();

                dispatchOnStartTrackingTouch();

                setPressed(true);

                handledTouch = true;
                break;

            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);

                dX = lastTouchX - event.getX();
                dY = lastTouchY - event.getY();

                distance = (mOrientation == VERTICAL) ? dY : dX;

                needsUpdate = calculateValueFromEvent(distance);

                lastTouchX = event.getX();
                lastTouchY = event.getY();

                dispatchOnStopTrackingTouch();
                setPressed(false);

                handledTouch = true;
                break;


            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                dispatchOnStopTrackingTouch();
                setPressed(false);
                handledTouch = true;
                needsUpdate = true;
                break;
        }

        if (needsUpdate) {
            dispatchProgressChanged();
            updateText();
            updateDrawableAnimation();
            invalidate();
        }

        return handledTouch;
    }

    private boolean calculateValueFromEvent(float distanceXY) {
        boolean updatedValue = false;

        float distance = (mOrientation == VERTICAL) ? (distanceXY / getHeight()) : (distanceXY / getWidth());
        float newProgress = (mOrientation == VERTICAL) ? mProgress + distance : mProgress - distance;

        newProgress = Math.max(0.0f, Math.min(1.0f, newProgress));

        if (mProgress != newProgress) {
            mProgress = newProgress;
            updatedValue = true;
        }

        return updatedValue;
    }

    public void setIconDrawable(@Nullable Drawable iconDrawable) {
        if (this.iconDrawable == iconDrawable) return;

        cancelLoaderTask();
        clearComposition();

        this.iconDrawable = iconDrawable;

        if (this.iconDrawable != null) {
            this.iconDrawable.setCallback(this);
            updateIconDrawableColor(iconColor);
            updateDrawableAnimation();
            updateIconSize();
            invalidate();
        }
    }

    @Nullable
    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconColor(@Nullable ColorStateList iconColor) {
        if (this.iconColor == iconColor) return;

        this.iconColor = iconColor;
        updateIconDrawableColor(iconColor);

        invalidate();
    }

    public ColorStateList getIconColor() {
        return iconColor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        setOutlineProvider(new CustomOutline(w, h, mRadius));
        setClipToOutline(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        textPaint.setColor(getColorForState(textColor));
        activeTrackPaint.setColor(getColorForState(activeTrackColor));
        inactiveTrackPaint.setColor(getColorForState(inactiveTrackColor));
        updateIconDrawableColor(iconColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawInactiveTrack(canvas);

        drawActiveTrack(canvas);

        if (iconTextView == ICONTEXT || iconTextView == TEXT || iconTextView == TEXTICON) {
            drawLabel(canvas);
        }

        if (iconTextView == ICONTEXT || iconTextView == ICON || iconTextView == TEXTICON) {
            drawIcon(canvas);
        }
    }

    private void drawInactiveTrack(Canvas canvas) {
        float inactiveTrackRange = maxValue - getProgress();

        int left = 0;
        int bottom;

        if (mOrientation == VERTICAL) {
            bottom = Math.round((inactiveTrackRange / maxValue) * getHeight());
        } else {
            left =  Math.round( getWidth() - ((inactiveTrackRange / maxValue) * getWidth()));
            bottom = getHeight();
        }

        inactiveTrackRect.set(left, 0, getWidth(), bottom);

        canvas.drawRect(inactiveTrackRect, inactiveTrackPaint);
    }

    private void drawActiveTrack(Canvas canvas) {
        float activeTrackRange = getProgress();

        int top = 0;
        int right;

        if (mOrientation == VERTICAL) {
            top = Math.round(getHeight() - ((activeTrackRange / maxValue) * getHeight()));
            right = getWidth();
        } else {
            right = Math.round((activeTrackRange / maxValue) * getWidth());
        }

        activeTrackRect.set(0, top, right, getHeight());

         canvas.drawRect(activeTrackRect, activeTrackPaint);
    }

    private void drawLabel(Canvas canvas) {
        textPaint.getTextBounds(labelText, 0, labelText.length(), textBounds);
        textBounds.right = (int) textPaint.measureText(labelText);

        int left;
        int top;

        if (mOrientation == VERTICAL) {
            left = (getWidth() - textBounds.width())/2;
            top = (3 * getHeight() - 2 * textBounds.height()) / 4;
        } else {
            left = (getWidth() - 2 * textBounds.width()) / 4;
            top = (getHeight() - textBounds.height()) / 2;
        }

        if (blendWithBackground) {

            boolean isVertical = mOrientation == VERTICAL;

            boolean isInBounds = isVertical ? activeTrackRect.contains(left, top + textBounds.height()/2, left + textBounds.width(), top + textBounds.height()) :
                    activeTrackRect.contains(left, top, left + textBounds.width()/2, top + textBounds.height());

            if (isInBounds) {

                if (textPaint.getColor() != getColorForState(inactiveTrackColor)) {
                    textPaint.setColor(getColorForState(inactiveTrackColor));
                }

            } else {
                if (textPaint.getColor() != getColorForState(textColor)) {
                    textPaint.setColor(getColorForState(textColor));
                }
            }

        } else {
            if (textPaint.getColor() != getColorForState(textColor)) {
                textPaint.setColor(getColorForState(textColor));
            }
        }

         canvas.drawText(labelText,0, labelText.length(), left, top + textBounds.height(), textPaint);
    }

    private void drawIcon(Canvas canvas) {
        if (iconDrawable == null) return;

        int iconWidth = iconDrawable.getBounds().width();
        int iconHeight = iconDrawable.getBounds().height();
        int left;
        int top;

        if (mOrientation == VERTICAL) {
            left = (getWidth() - iconWidth)/2;
            top = (getHeight() - 2 * iconHeight) / 4;
        } else {
            left = (3 * getWidth() - 2 * iconWidth) / 4;
            top = (getHeight() - iconHeight) / 2;
        }

        drawableRect.set(left, top, left + iconWidth, + top);

        if (blendWithBackground) {

            boolean isVertical = mOrientation == VERTICAL;

            boolean isInBounds = isVertical ? activeTrackRect.contains(left, top + iconHeight/2, left + iconWidth, top + iconHeight) :
                    activeTrackRect.contains(left, top, left + iconWidth/2, top + iconHeight);

            if (isInBounds) {
                updateIconDrawableColor(inactiveTrackColor);
            } else {
                updateIconDrawableColor(iconColor);
            }
        } else {
            updateIconDrawableColor(iconColor);
        }

        canvas.save();
        canvas.translate(left, top);

        iconDrawable.draw(canvas);

        canvas.restore();
    }

    @ColorInt
    private int getColorForState(@NonNull ColorStateList colorStateList) {
        return colorStateList.getColorForState(getDrawableState(), colorStateList.getDefaultColor());
    }

    private boolean isValueValid(float value) {
        boolean isValid = false;
        if (value < minValue || value > maxValue) {
            Log.e(TAG, "Value must be in between min value and max value");
        } else {
            isValid = true;
        }

        return isValid;
    }

    private void validateMinValue() {
        if (minValue >= maxValue) {
            Log.e(TAG, "Minimum value must be less than max value.");
            throw new IllegalArgumentException("Minimum value must be less than max value.");
        }
    }

    private void validateMaxValue() {
        if (maxValue <= minValue) {
            Log.e(TAG, "Max value must be greater than min value.");
            throw new IllegalArgumentException("Max value must be greater than min value.");
        }
    }

    /////////////// LottieDrawable Settings /////////////////
    public void setAnimation(@RawRes int res) {
        if (iconDrawable == null) {
            iconDrawable = new LottieDrawable();
        } else if (!(iconDrawable instanceof LottieDrawable)) {
            iconDrawable.setCallback(null);
            iconDrawable = null;
            iconDrawable = new LottieDrawable();
        }

        LottieTask<LottieComposition> task = LottieCompositionFactory.fromRawRes(getContext(), res, null);
        setCompositionTask(task);
    }

    private void setComposition(LottieComposition composition) {
        if (!(iconDrawable instanceof LottieDrawable)) return;

        if (iconDrawable.getCallback() != this) {
            iconDrawable.setCallback(this);
        }

        boolean isNewComposition = ((LottieDrawable) iconDrawable).setComposition(composition);

        if (isNewComposition) {
            updateDrawableAnimation();
            updateIconSize();
            updateIconDrawableColor(iconColor);
            invalidate();
        }
    }

    private void setCompositionTask(LottieTask<LottieComposition> compositionTask) {
        cancelLoaderTask();
        clearComposition();
        compositionTask
                .addListener(new LottieListener<LottieComposition>() {
                    @Override
                    public void onResult(LottieComposition result) {
                        setComposition(result);
                    }
                })
                .addFailureListener(new LottieListener<Throwable>() {
                    @Override
                    public void onResult(Throwable result) {
                        throw new IllegalStateException("Unable to parse composition", result);
                    }
                });
    }

    private void cancelLoaderTask() {
        if (lottieCompositionTask == null) return;
        lottieCompositionTask
                .removeListener(new LottieListener<LottieComposition>() {
                    @Override
                    public void onResult(LottieComposition result) {
                        setComposition(result);
                    }
                })
                .removeFailureListener(new LottieListener<Throwable>() {
                    @Override
                    public void onResult(Throwable result) {
                        throw new IllegalStateException("Unable to parse composition", result);
                    }
                });
    }

    private void clearComposition() {
        lottieCompositionTask = null;
        if (iconDrawable instanceof LottieDrawable) {
            ((LottieDrawable) iconDrawable).clearComposition();
        }
    }

    /*
    /////// IGNORE METHODS - IN DEVELOPMENT ///////////
    private BitmapShader createBlendShader(final ColorStateList colorStateList) {
        Bitmap bitmapReverse;
        BitmapShader shader;

        int[] colorBitmap = new int[getWidth() * getHeight()];

        Arrays.fill(colorBitmap, 0, inactiveTrackRect.width() * inactiveTrackRect.height(), getColorForState(colorStateList));
        Arrays.fill(colorBitmap,  inactiveTrackRect.width() * inactiveTrackRect.height(), getWidth() * getHeight(), getColorForState(inactiveTrackColor));

        bitmapReverse = Bitmap.createBitmap(colorBitmap, 0, getWidth(), getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        shader = new BitmapShader(bitmapReverse, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        return shader;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
     */

    //////// Elevation //////////
    private class CustomOutline extends ViewOutlineProvider {
        int width;
        int height;
        float cornerRadius;

        CustomOutline(int width, int height, float cornerRadius) {
            this.width = width;
            this.height = height;
            this.cornerRadius = cornerRadius;
        }

        @Override
        public void getOutline(View view, @NonNull Outline outline) {
            outline.setRoundRect(0, 0, width, height, cornerRadius);
        }
    }
}
