package com.errorerrorerror.esplightcontroller.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

public class RoundedDrawerArrowDrawable extends DrawerArrowDrawable {

    private float mMaxCutForBarSize;
    private static final float ARROW_HEAD_ANGLE = (float) Math.toRadians(45);
    private final Path mPath = new Path();
    private boolean mVerticalMirror = false;
    private boolean mRound = false;
    private float shortBarWidth = 0;

    public enum Bar {
        TOP_BAR,
        MIDDLE_BAR,
        BOTTOM_BAR
    }

    /**
     * @param context used to get the configuration for the drawable from
     */
    public RoundedDrawerArrowDrawable(Context context) {
        super(context);
    }


    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        final boolean flipToPointRight;
        switch (getDirection()) {
            case ARROW_DIRECTION_LEFT:
                flipToPointRight = false;
                break;
            case ARROW_DIRECTION_RIGHT:
                flipToPointRight = true;
                break;
            case ARROW_DIRECTION_END:
                flipToPointRight = DrawableCompat.getLayoutDirection(this)
                        == ViewCompat.LAYOUT_DIRECTION_LTR;
                break;
            case ARROW_DIRECTION_START:
            default:
                flipToPointRight = DrawableCompat.getLayoutDirection(this)
                        == ViewCompat.LAYOUT_DIRECTION_RTL;
                break;
        }

        // Interpolated widths of arrow bars

        float arrowHeadBarLength = (float) Math.sqrt(getArrowHeadLength() * getArrowHeadLength() * 2);
        arrowHeadBarLength = lerp(getBarLength(), arrowHeadBarLength, getProgress());
        final float arrowShaftLength = lerp(getBarLength(), getArrowShaftLength(), getProgress());
        // Interpolated size of middle bar
        final float arrowShaftCut = Math.round(lerp(0, mMaxCutForBarSize, getProgress()));
        // The rotation of the top and bottom bars (that make the arrow head)
        final float rotation = lerp(0, ARROW_HEAD_ANGLE, getProgress());

        // The whole canvas rotates as the transition happens
        final float canvasRotate = lerp(flipToPointRight ? 0 : -180,
                flipToPointRight ? 180 : 0, getProgress());

        final float arrowWidth = Math.round(arrowHeadBarLength * Math.cos(rotation));
        final float arrowHeight = Math.round(arrowHeadBarLength * Math.sin(rotation));

        mPath.rewind();

        final int shift = mRound ? 4 : 0;

        final float topBottomBarOffset = lerp(getGapSize() + getPaint().getStrokeWidth(), -mMaxCutForBarSize,
                getProgress()) + shift;

        final float arrowEdge = -arrowShaftLength / 2;


        // draw middle bar
        mPath.moveTo(arrowEdge + arrowShaftCut, 0);
        mPath.rLineTo(arrowShaftLength - arrowShaftCut * 2, 0);

        // bottom bar
        mPath.moveTo(arrowEdge , topBottomBarOffset);
        mPath.rLineTo(arrowWidth , arrowHeight);

        // top bar
        mPath.moveTo(arrowEdge + (shortBarWidth * (1 - getProgress())), -topBottomBarOffset);
        mPath.rLineTo(arrowWidth - (shortBarWidth * (1 - getProgress())) , -arrowHeight);

        mPath.close();

        canvas.save();

        // Rotate the whole canvas if spinning, if not, rotate it 180 to get
        // the arrow pointing the other way for RTL.
        final float barThickness = getPaint().getStrokeWidth();
        final int remainingSpace = (int) (bounds.height() - barThickness * 3 - getGapSize() * 2);
        float yOffset = (remainingSpace / 4) * 2; // making sure it is a multiple of 2.
        yOffset += barThickness * 1.5f + getGapSize();

        canvas.translate(bounds.centerX(), yOffset);
        if (isSpinEnabled()) {
            canvas.rotate(canvasRotate * ((mVerticalMirror ^ flipToPointRight) ? -1 : 1));
        } else if (flipToPointRight) {
            canvas.rotate(180);
        }
        canvas.drawPath(mPath, getPaint());

        canvas.restore();
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    @Override
    public void setBarThickness(float width) {
        if (getPaint().getStrokeWidth() != width) {
            mMaxCutForBarSize = (float) (width / 2 * Math.cos(ARROW_HEAD_ANGLE));
        }
        super.setBarThickness(width);
    }

    @Override
    public void setVerticalMirror(boolean verticalMirror) {
        if (mVerticalMirror != verticalMirror) {
            mVerticalMirror = verticalMirror;
        }

        super.setVerticalMirror(verticalMirror);
    }

    public void setShortBarWidth(float width) {
        this.shortBarWidth = getBarLength() - width;
        invalidateSelf();
    }

    public void  rounded(boolean shouldRound) {
        if (shouldRound) {
            mRound = true;
            getPaint().setStrokeJoin(Paint.Join.ROUND);
            getPaint().setStrokeCap(Paint.Cap.ROUND);
        } else {
            mRound = false;
            getPaint().setStrokeJoin(Paint.Join.BEVEL);
            getPaint().setStrokeCap(Paint.Cap.SQUARE);
        }
        invalidateSelf();
    }
}
