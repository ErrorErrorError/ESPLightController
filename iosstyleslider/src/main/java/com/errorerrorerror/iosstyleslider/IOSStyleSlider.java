package com.errorerrorerror.iosstyleslider;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.LottieValueCallback;
import com.airbnb.lottie.value.SimpleLottieValueCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import icepick.Icepick;
import icepick.State;


public class IOSStyleSlider extends LinearLayout {

    /* Default Values */

    private final static int DEFAULT_SLIDER_COLOR = Color.parseColor("#7673E7");
    private final static int DEFAULT_BACKGROUND_SLIDER_COLOR = Color.WHITE;
    private final static int DEFAULT_MIN_VALUE = 0;
    private final static int DEFAULT_MAX_VALUE = 100;
    private final static int DEFAULT_PROGRESS = 80; //Default progress on a 0 - 100 scale
    private static final String TAG = "iosstyleslider";
    private static final float scale = 1.04f;
    @State
    float mSliderProgress = DEFAULT_PROGRESS;
    //Do not change this
    private Paint mPaint;
    private int mSliderRadius;
    private boolean mSliderEnabled;
    private LottieAnimationView iconView;
    @Nullable
    private PorterDuff.Mode mIconTintMode = null;
    private boolean hasIconTintMode = false;
    private TextView textView;
    private int iconResource;
    private int iconTint = 0;
    private boolean hasIconTint = false;
    @Nullable
    private List<OnProgressChangedListener> onProgressChangedListener;
    @Nullable
    private List<OnSliderEnabledChangeListener> onSliderEnabledChangeListeners;
    private int textColor = 0;
    private int iconSize = 0;
    private int textSize = 0;
    private GestureDetector gestureDetector;
    private int showIconText;
    //Users Can Change these values
    private int mSliderColor = DEFAULT_SLIDER_COLOR;
    private int mSliderBackgroundColor = DEFAULT_BACKGROUND_SLIDER_COLOR;
    private int mSliderBackgroundColorDisabled = 0;
    private int mSliderColorDisabled = 0;
    private int mSliderMin = DEFAULT_MIN_VALUE;
    private int mSliderMax = DEFAULT_MAX_VALUE;
    private String mText;
    private int mProgressInitialValue = -1;
    private int animatedIconResId = 0;

    public IOSStyleSlider(Context context) {
        this(context, null);
    }

    public IOSStyleSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IOSStyleSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void removeProgressChangedListener(OnProgressChangedListener listener) {
        this.onProgressChangedListener.remove(listener);
    }

    public void removeTextChangedListener(TextWatcher listener) {
        if (textView != null) {
            this.textView.removeTextChangedListener(listener);
        }
    }

    private void init(Context context, @Nullable AttributeSet set) {

        setSaveEnabled(true);
        setWillNotDraw(false);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        onProgressChangedListener = new ArrayList<>();
        onSliderEnabledChangeListeners = new ArrayList<>();
        setWeightSum(.5f);

        mPaint = new Paint();

        //mState = IOSStyleStates.IDLE;

        //Attributes

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.IOSStyleSlider);
        mSliderRadius = ta.getDimensionPixelSize(R.styleable.IOSStyleSlider_cornerRadius, getResources().getDimensionPixelSize(R.dimen.radius));
        mSliderColor = ta.getColor(R.styleable.IOSStyleSlider_sliderColor, mSliderColor);

        setSliderMin(ta.getInteger(R.styleable.IOSStyleSlider_minValue, mSliderMin));
        setSlidertMax(ta.getInteger(R.styleable.IOSStyleSlider_maxValue, mSliderMax));

        animatedIconResId = ta.getResourceId(R.styleable.IOSStyleSlider_animatedIcon, R.raw.brightness_animation);

        if (ta.hasValue(R.styleable.IOSStyleSlider_progressInitialValue)) {
            mProgressInitialValue = ta.getInt(R.styleable.IOSStyleSlider_progressInitialValue, (int) mSliderProgress);
        } else {
            setSliderProgress(ta.getInt(R.styleable.IOSStyleSlider_progress, (int) mSliderProgress));
        }

        if (ta.hasValue(R.styleable.IOSStyleSlider_android_iconTint)) {
            iconTint = ta.getColor(R.styleable.IOSStyleSlider_android_iconTint, ContextCompat.getColor(context, R.color.iconColor));
            hasIconTint = true;

            mIconTintMode = PorterDuff.Mode.SRC_ATOP;
            hasIconTintMode = true;
        }

        iconResource = ta.getResourceId(R.styleable.IOSStyleSlider_icon, R.drawable.ic_brightness_icon);

        mSliderBackgroundColor = (getBackground() != null) ? ((ColorDrawable) getBackground()).getColor() : DEFAULT_BACKGROUND_SLIDER_COLOR;

        iconSize = ta.getDimensionPixelSize(R.styleable.IOSStyleSlider_iconSize, getResources().getDimensionPixelSize(R.dimen.iconSize));

        mSliderColorDisabled = ta.getColor(R.styleable.IOSStyleSlider_sliderDisabledColor, 0);
        mSliderBackgroundColorDisabled = ta.getColor(R.styleable.IOSStyleSlider_backgroundColorDisabled, 0);
        showIconText = ta.getInteger(R.styleable.IOSStyleSlider_visibilityIconText, 3);


        if (ta.hasValue(R.styleable.IOSStyleSlider_android_iconTintMode)) {
            mIconTintMode = (PorterDuff.Mode.values()[ta.getInt(R.styleable.IOSStyleSlider_android_iconTintMode, 0)]);
            hasIconTintMode = true;
        }
        mSliderEnabled = ta.getBoolean(R.styleable.IOSStyleSlider_android_enabled, true);
        mText = (ta.getText(R.styleable.IOSStyleSlider_android_text) != null) ? ta.getText(R.styleable.IOSStyleSlider_android_text).toString() : null;
        textColor = ta.getColor(R.styleable.IOSStyleSlider_android_textColor, ContextCompat.getColor(context, R.color.textColor));
        textSize = ta.getDimensionPixelSize(R.styleable.IOSStyleSlider_android_textSize, getResources().getDimensionPixelSize(R.dimen.textSize));

        setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.minWidth));
        setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.minHeight));
        ta.recycle();

        addViews(context);
    }

    public void addOnProgressChanged(OnProgressChangedListener progressChangedListener) {
        this.onProgressChangedListener.add(progressChangedListener);
    }

    public void addTextChangedListener(TextWatcher textChangedListener) {
        this.textView.addTextChangedListener(textChangedListener);
    }

    public boolean hasIconTintMode() {
        return hasIconTintMode;
    }

    public boolean hasIconTint() {
        return hasIconTint;
    }

    public void setIconTintMode(PorterDuff.Mode mIconTintMode) {
        this.mIconTintMode = mIconTintMode;
        hasIconTintMode = true;
        iconView.setImageTintMode(mIconTintMode);
    }

    private void addViews(Context context) {
        if (showIconText == IOSStyleView.icon.ordinal()) {
            addIconView(context);
        } else if (showIconText == IOSStyleView.text.ordinal()) {
            addTextView(context);
        } else if (showIconText == IOSStyleView.textIcon.ordinal()) {
            addTextView(context);
            addIconView(context);
        } else if (showIconText == IOSStyleView.iconText.ordinal()) {
            addIconView(context);
            addTextView(context);
        }
    }

    @Nullable
    public List<OnSliderEnabledChangeListener> getOnSliderEnabledChangeListeners() {
        return onSliderEnabledChangeListeners;
    }

    public void setOnSliderEnabledChangeListeners(OnSliderEnabledChangeListener onSliderEnabledChangeListeners) {
        this.onSliderEnabledChangeListeners.add(onSliderEnabledChangeListeners);
    }

    public void setAnimatedIcon(@RawRes int res) {
        iconView.setAnimation(res);
        if (res != animatedIconResId) {
            animatedIconResId = res;
        }
    }

    private void setAnimatedIconProgress(float progress) {
        if (iconView == null) {
            return;
        }

        iconView.setProgress(progress);
    }

    public int getAnimatedIconResId() {
        return animatedIconResId;
    }

    public void setAnimatedIconResId(int animatedIconResId) {
        this.animatedIconResId = animatedIconResId;
    }

    private void dispatchSliderState() {
        for (OnSliderEnabledChangeListener listener : onSliderEnabledChangeListeners) {
            listener.onSliderEnabledChanged(this, mSliderEnabled);
        }
    }

    public void setDisabledSliderColor(@ColorInt int color) {
        if (mSliderColorDisabled != color) {
            mSliderColorDisabled = color;
            invalidate();
        }
    }

    public void setDisabledSliderBackgroundColor(@ColorInt int color) {
        if (mSliderBackgroundColorDisabled != color) {
            mSliderBackgroundColorDisabled = color;
            invalidate();
        }
    }

    private void addIconView(Context context) {
        iconView = new LottieAnimationView(context);

        setAnimatedIconProgress(mSliderProgress / mSliderMax);

        iconView.setImageResource(iconResource);

        if (hasIconTintMode()) {
            iconView.setImageTintMode(mIconTintMode);
        }

        if (hasIconTint()) {
            iconView.setImageTintList(ColorStateList.valueOf(iconTint));
        }

        if (animatedIconResId != 0) {
            iconView.setAnimation(animatedIconResId);
            SimpleColorFilter filter = new SimpleColorFilter(iconTint);
            KeyPath keyPath = new KeyPath("**");
            LottieValueCallback<ColorFilter> callback = new LottieValueCallback<ColorFilter>(filter);
            iconView.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback);

        }

        setAnimatedIconProgress(mSliderProgress / mSliderMax);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        addView(iconView, params);
    }

    private void addTextView(Context context) {
        textView = new TextView(context);

        textView.setGravity(Gravity.CENTER);
        if (mText != null) {
            textView.setText(mText);
        } else {
            mText = (int) mSliderProgress + "%";
            textView.setText(mText);
        }
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setId(generateViewId());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        addView(textView, params);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (mSliderEnabled) {
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                onStopTrackingTouch();
                getParent().requestDisallowInterceptTouchEvent(false);
                animateView(1.0f, 200);
            }

            if (this.gestureDetector.onTouchEvent(event)) {
                performClick();
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        } else {
            return false;
        }
    }

    public void animateView(float scale, int duration) {
        animate().scaleX(scale).setDuration(duration).start();
        animate().scaleY(scale).setDuration(duration).start();
    }

    void onStartTrackingTouch() {
        if (onProgressChangedListener != null) {
            for (int i = 0; i < onProgressChangedListener.size(); i++) {
                onProgressChangedListener.get(i).onStartTrackingTouch(this);
            }
        }
    }

    void onStopTrackingTouch() {
        if (onProgressChangedListener != null) {
            for (int i = 0; i < onProgressChangedListener.size(); i++) {
                onProgressChangedListener.get(i).onStopTrackingTouch(this);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setupGestureDetectors();
    }

    private void setupGestureDetectors() {
        //This allows for other gestures to occur
        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            //This allows for other gestures to occur
            @Override
            public boolean onDown(MotionEvent e) {
                animateView(scale, 200);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                onStartTrackingTouch();
                float calcDistance = calculateDistance(distanceY);
                setSliderProgress(calcDistance);
                getParent().requestDisallowInterceptTouchEvent(true);
                updateTextViewColor();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                //onStartTrackingTouch();
                //calculateDistanceFling(velocityY);
                //getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        };

        gestureDetector = new GestureDetector(getContext(), gestureListener);

        //Disable this to have a long click and be able to scroll
        gestureDetector.setIsLongpressEnabled(false);
    }

    private void updateTextViewColor() {
        /// Get Background bitmap
        Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmBackground = Bitmap.createBitmap(getWidth(), getHeight(), bitmapConfig);
        int progressHeight = (int) ((mSliderProgress / mSliderMax) * getHeight());
        progressHeight = getHeight() - progressHeight;

        int[] pixels = new int[getWidth() * getHeight()];
        Arrays.fill(pixels, 0, progressHeight * getWidth(), mSliderColor);
        Arrays.fill(pixels, progressHeight * getWidth(), getHeight() * getWidth(), mSliderBackgroundColor);

        bmBackground.setPixels(pixels, 0, getWidth(), 0, 0, getWidth(), getHeight());

//        Log.v(TAG, "Textview: " + textView.getX() + " y: " + textView.getY() + " width: " + textView.getWidth() + " getHeifht: " + textView.getHeight());

        Bitmap croppedBmp = Bitmap.createBitmap(bmBackground, (int) textView.getX(), (int) textView.getY(), textView.getWidth(), textView.getHeight());
        BitmapShader shader = new BitmapShader(croppedBmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader);
        textView.invalidate();

        Bitmap croppedBmpImage = Bitmap.createBitmap(bmBackground, (int) iconView.getX(), (int) iconView.getY(), iconView.getWidth(), iconView.getHeight());
        final int color = croppedBmpImage.getPixel(iconView.getWidth() / 2, iconView.getHeight() / 2);

        iconView.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR_FILTER,
                new SimpleLottieValueCallback<ColorFilter>() {
                    @Override
                    public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                        return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    }
                });

        iconView.invalidate();
    }

    private float calculateDistance(float distance) {
        float v = mSliderProgress + ((distance / getMeasuredHeight()) * 100);
        return v;
    }

    @Override
    protected synchronized void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        //Slider Background
        drawBackgroundSlider(canvas);
        //Slider
        drawSlider(canvas);
    }

    private void drawBackgroundSlider(@NonNull Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mSliderBackgroundColor);
        if (!mSliderEnabled && mSliderBackgroundColorDisabled != 0) {
            mPaint.setColor(mSliderBackgroundColorDisabled);
        }
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), mSliderRadius, mSliderRadius, mPaint);
    }

    public boolean isSliderEnabled() {
        return this.mSliderEnabled;
    }
    /*
    private void calculateDistanceFling(float velocity) {
        float pps = -velocity / 100;
        pps = pps + mSliderProgress;
        if (pps > mSliderMax) {
            pps = mSliderMax;
        } else if (pps < mSliderMin) {
            pps = mSliderMin;
        }


        final ValueAnimator valueAnimator = new ValueAnimator().setDuration(1000);
        valueAnimator.setFloatValues(mSliderProgress, pps);
        valueAnimator.setInterpolator(new DecelerateInterpolator(1.2f));
        final float finalPps = pps;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                setSliderProgress((Float) animation.getAnimatedValue());
                if (((float) animation.getAnimatedValue()) == finalPps) {
                    valueAnimator.removeUpdateListener(this);
                }

            }
        });

        valueAnimator.start();
    }
     */

    public void setSliderEnabled(boolean enable) {
        if (mSliderEnabled != enable) {
            mSliderEnabled = enable;
            dispatchSliderState();
            invalidate();
        }
    }

    private void drawSlider(@NonNull Canvas canvas) {
        mPaint.setColor(mSliderColor);
        if (!mSliderEnabled && mSliderColorDisabled != 0) {
            mPaint.setColor(mSliderColorDisabled);
        }

        float actualHeight = getHeight() - ((mSliderProgress / mSliderMax) * getHeight());

        canvas.drawRect(0, actualHeight, getWidth(), getHeight(), mPaint);
    }

    public int getSliderMin() {
        return this.mSliderMin;
    }

    public void setSliderMin(int min) {
        if (min >= mSliderMax) {
            mSliderMax = min;
        } else {
            mSliderMin = min;
        }
    }

    public int getSliderMax() {
        return this.mSliderMax;
    }

    public int getSliderRadius() {
        return this.mSliderRadius;
    }

    public String getText() {
        return this.mText;
    }

    public void setText(String text) {
        if (textView == null) {
            return;
        }

        this.mText = text;
        textView.setText(text);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //Elevation
        setOutlineProvider(new CustomOutline(w, h, mSliderRadius));
        setClipToOutline(true);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec));


        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child instanceof LottieAnimationView) {
                child.getLayoutParams().width = iconSize;
            } else if (child instanceof TextView) {
                child.getLayoutParams().width = getMeasuredWidth();
            }
            child.getLayoutParams().height = getMeasuredHeight() / getChildCount();
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private int measureDimension(int desiredSize, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            desiredSize = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                desiredSize = Math.min(desiredSize, specSize);
            }
        }

        return desiredSize;
    }

    public void setSlidertMax(int max) {
        if (max <= mSliderMin) {
            mSliderMin = max;
        } else {
            mSliderMax = max;
        }
    }

    public int getSliderProgress() {
        return Math.round(this.mSliderProgress);
    }

    public void setSliderProgress(float value) {
        if (value > mSliderMax) {
            value = mSliderMax;
        } else if (value < mSliderMin) {
            value = mSliderMin;
        }

        if (value != mSliderProgress) {
            this.mSliderProgress = value;
        }

        dispatchProgressChanged();
        setAnimatedIconProgress(mSliderProgress / mSliderMax);

        setText(((int) (mSliderProgress)) + "%");

        invalidate();
    }

    private void dispatchProgressChanged() {
        if (onProgressChangedListener != null) {
            for (OnProgressChangedListener listener : onProgressChangedListener) {
                listener.onProgressChanged(this, Math.round(mSliderProgress));
            }
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    public enum IOSStyleView {
        icon,
        text,
        textIcon,
        iconText
    }

    public interface OnSliderEnabledChangeListener {
        void onSliderEnabledChanged(IOSStyleSlider iosStyleSlider, boolean isOn);
    }

    public interface OnProgressChangedListener {
        /*
         * Detects changes on progress
         */
        void onProgressChanged(IOSStyleSlider slider, int progress);

        void onStartTrackingTouch(IOSStyleSlider slider);

        void onStopTrackingTouch(IOSStyleSlider slider);
    }

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
