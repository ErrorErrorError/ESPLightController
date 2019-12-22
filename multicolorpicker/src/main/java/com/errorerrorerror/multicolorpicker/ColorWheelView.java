package com.errorerrorerror.multicolorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ColorWheelView extends View {

    private float radius;
    private float centerX;
    private float centerY;

    private Paint huePaint;
    private Paint saturationPaint;
    private Paint valuePaint;

    private boolean showValue = false;

    public ColorWheelView(Context context) {
        this(context, null);
    }

    public ColorWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        this.setVisibility(View.VISIBLE);
    }

    private void init(@Nullable AttributeSet attrs) {
        huePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        saturationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        width = height = Math.min(maxWidth, maxHeight);
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int netWidth = w - getPaddingLeft() - getPaddingRight();
        int netHeight = h - getPaddingTop() - getPaddingBottom();

        radius = Math.min(netWidth, netHeight) / 2;

        if (radius <= 0) return;
        centerX = w / 2;
        centerY = h / 2;

        dispatchColorWheelMode();
    }

    void showValue(boolean show) {
        if (showValue != show) {
            showValue = show;
            dispatchColorWheelMode();
        }
    }

    private void dispatchColorWheelMode() {
        if (radius <= 0) return;

        Shader hueShader = new SweepGradient(centerX, centerY,
                new int[]{Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED},
                null);
        huePaint.setShader(hueShader);


        Shader saturationShader;
        Shader blackShader;

        if (!showValue) {
            saturationShader = new RadialGradient(centerX, centerY, radius,Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP);
            saturationPaint.setShader(saturationShader);

            /// Removes the black shader
            valuePaint.setShader(null);

        } else {
            saturationShader = new RadialGradient(centerX, centerY, radius, new int[]{Color.WHITE, Color.TRANSPARENT}, new float[]{0, 0.5f}, Shader.TileMode.CLAMP);
            saturationPaint.setShader(saturationShader);

            blackShader = new RadialGradient(centerX, centerY, radius, new int[]{Color.TRANSPARENT, Color.BLACK}, new float[]{0.5f, 1}, Shader.TileMode.CLAMP);
            valuePaint.setShader(blackShader);
        }

        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
         canvas.drawCircle(centerX, centerY, radius, huePaint);
         canvas.drawCircle(centerX, centerY, radius, saturationPaint);

        if (showValue) {
             canvas.drawCircle(centerX, centerY, radius, valuePaint);
        }
    }
}
