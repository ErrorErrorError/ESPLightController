package com.divyanshu.colorseekbar

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Color.colorToHSV
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.ArrayRes
import java.util.*


class ColorSeekBar(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val minThumbRadius = 16f
    /*
    private var colorSeeds = intArrayOf(Color.parseColor("#ff0000"),
            Color.parseColor("#ffff00"),
            Color.parseColor("#00ff00"),
            Color.parseColor("#00ffff"),
            Color.parseColor("#0000ff"),
            Color.parseColor("#ff0000"))
            */
    private var colorSeeds = IntArray(360)
    private var canvasHeight: Int = 60
    private var barHeight: Int = 20
    private var rectf: RectF = RectF()
    private var rectPaint: Paint = Paint()
    private var thumbBorderPaint: Paint = Paint()
    private var thumbPaint: Paint = Paint()
    private lateinit var colorGradient: LinearGradient
    private var thumbX: Float = 24f
    private var thumbY: Float = (canvasHeight / 2).toFloat()
    private var thumbBorder: Float = 4f
    private var thumbRadius: Float = 16f
    private var thumbBorderRadius: Float = thumbRadius + thumbBorder
    private var thumbBorderColor = Color.BLACK
    private var paddingStart = 30f
    private var paddingEnd = 30f
    private var barCornerRadius: Float = 8f
    private var oldThumbRadius = thumbRadius
    private var oldThumbBorderRadius = thumbBorderRadius
    private var colorChangeListener: OnColorChangeListener? = null
    private var tempBR: Float = 0f
    private var tempTR: Float = 0f
    private var initialColor: Int = 0
    private var scale: Float = 1.3f
    private var allowTouch: Boolean = true

    init {
        attributeSet.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ColorSeekBar)
            colorSeeds = buildHueColorArray()
            val colorsId = typedArray.getResourceId(R.styleable.ColorSeekBar_colorSeeds, 0)
            if (colorsId != 0) colorSeeds = getColorsById(colorsId)
            barCornerRadius = typedArray.getDimension(R.styleable.ColorSeekBar_cornerRadius, 8f)
            barHeight = typedArray.getDimension(R.styleable.ColorSeekBar_barHeight, 20f).toInt()
            thumbBorder = typedArray.getDimension(R.styleable.ColorSeekBar_thumbBorder, 4f)
            thumbBorderColor = typedArray.getColor(R.styleable.ColorSeekBar_thumbBorderColor, Color.BLACK)
            initialColor = typedArray.getColor(R.styleable.ColorSeekBar_initialColor, initialColor)
            typedArray.recycle()
        }

        rectPaint.isAntiAlias = true

        thumbBorderPaint.isAntiAlias = true
        thumbBorderPaint.color = thumbBorderColor

        thumbPaint.isAntiAlias = true

        thumbRadius = (barHeight / 2).toFloat().let {
            if (it < minThumbRadius) minThumbRadius else it
        }
        thumbBorderRadius = thumbRadius + thumbBorder
        canvasHeight = (thumbBorderRadius * 3).toInt()
        thumbY = (canvasHeight / 2).toFloat()

        oldThumbRadius = thumbRadius
        oldThumbBorderRadius = thumbBorderRadius

        tempTR = oldThumbRadius
        tempBR = oldThumbBorderRadius
    }

    private fun getColorsById(@ArrayRes id: Int): IntArray {
        if (isInEditMode) {
            val s = context.resources.getStringArray(id)
            val colors = IntArray(s.size)
            for (j in s.indices) {
                colors[j] = Color.parseColor(s[j])
            }
            return colors
        } else {
            val typedArray = context.resources.obtainTypedArray(id)
            val colors = IntArray(typedArray.length())
            for (j in 0 until typedArray.length()) {
                colors[j] = typedArray.getColor(j, Color.BLACK)
            }
            typedArray.recycle()
            return colors
        }
    }

    private fun buildHueColorArray(): IntArray {
        val hue = IntArray(361)
        var count = 0
        var i = hue.size - 1
        while (i >= 0) {
            hue[count] = Color.HSVToColor(floatArrayOf(i.toFloat(), 1f, 1f))
            i--
            count++
        }
        return hue
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //color bar position
        val barLeft = paddingStart
        val barRight: Float = width.toFloat() - paddingEnd
        val barTop: Float = ((canvasHeight / 2) - (barHeight / 2)).toFloat()
        val barBottom: Float = ((canvasHeight / 2) + (barHeight / 2)).toFloat()

        //draw color bar
        rectf.set(barLeft, barTop, barRight, barBottom)
        canvas?.drawRoundRect(rectf, barCornerRadius, barCornerRadius, rectPaint)

        if (thumbX < barLeft) {
            thumbX = barLeft
        } else if (thumbX > barRight) {
            thumbX = barRight
        }
        if (initialColor != 0) {
            thumbPaint.color = initialColor
            initialColor = 0
        } else {
            val color = pickColor(thumbX, width)
            thumbPaint.color = color
        }

        // draw color bar thumb
        canvas?.drawCircle(thumbX, thumbY, thumbBorderRadius, thumbBorderPaint)
        canvas?.drawCircle(thumbX, thumbY, thumbRadius, thumbPaint)
    }

    private fun pickColor(position: Float, canvasWidth: Int): Int {
        val value = (position) / (canvasWidth)
        when {
            value <= 0.0 -> return colorSeeds[0]
            value >= 1 -> return colorSeeds[colorSeeds.size - 1]
            else -> {
                var colorPosition = value * (colorSeeds.size - 1)
                val i = colorPosition.toInt()
                colorPosition -= i
                val c0 = colorSeeds[i]
                val c1 = colorSeeds[i + 1]

                val red = mix(Color.red(c0), Color.red(c1), colorPosition)
                val green = mix(Color.green(c0), Color.green(c1), colorPosition)
                val blue = mix(Color.blue(c0), Color.blue(c1), colorPosition)

                return Color.rgb(red, green, blue)
            }
        }
    }

    private fun mix(start: Int, end: Int, position: Float): Int {
        return start + Math.round(position * (end - start))
    }


    private fun setThumbOnSpecificColor() {
        val hsv = FloatArray(3)
        colorToHSV(initialColor, hsv)
        val unit = (360 - hsv[0]) / 360
        thumbX = this.width.toFloat() * unit
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        colorGradient = LinearGradient(0f, 0f, w.toFloat(), 0f, colorSeeds, null, Shader.TileMode.CLAMP)
        rectPaint.shader = colorGradient

        setThumbOnSpecificColor()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        if (!enabled){
            val hue = IntArray(361)
            var count = 0
            var i = hue.size - 1
            while (i >= 0) {
                hue[count] = Color.GRAY
                i--
                count++
            }
            colorSeeds = hue
            invalidate()
            onSizeChanged(width, height, width, height)
            allowTouch = false

        } else {
            colorSeeds = buildHueColorArray()
            invalidate()
            allowTouch = true
            onSizeChanged(width, height, width, height)

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, canvasHeight)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(!allowTouch){
            return allowTouch
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                animateScale((oldThumbRadius * scale), (oldThumbBorderRadius * scale))
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                event.x.let {
                    thumbX = it
                    invalidate()
                }
                colorChangeListener?.onColorChangeListener(getColor())
                return true

            }
            MotionEvent.ACTION_UP -> {
                animateScale(oldThumbRadius, oldThumbBorderRadius)
                return true
            }
        }
        return false
    }

    private fun animateScale(tr: Float, tbr: Float) {
        val thumbBorderRad: ValueAnimator = ValueAnimator.ofFloat(thumbBorderRadius, tbr)
        val thumbRad: ValueAnimator = ValueAnimator.ofFloat(thumbRadius, tr)
        thumbBorderRad.addUpdateListener {
            val value = it.animatedValue as Float
            thumbBorderRadius = value
            oldThumbBorderRadius = tempBR
            if (value == tempTR) {
                thumbBorderRadius = tempBR
                oldThumbBorderRadius = tempBR
            }
            invalidate()
        }

        thumbRad.addUpdateListener {
            val value = it.animatedValue as Float
            thumbRadius = value
            oldThumbRadius = tempTR
            if (value == tempTR) {
                thumbRadius = tempTR
                oldThumbRadius = tempTR
            }
            invalidate()
        }

        thumbBorderRad.interpolator = DecelerateInterpolator(scale)
        thumbRad.interpolator = DecelerateInterpolator(scale)

        thumbBorderRad.duration = 500
        thumbRad.duration = 500

        thumbBorderRad.start()
        thumbRad.start()
    }


    fun setInitialColor(color: Int) {
        initialColor = color
        setThumbOnSpecificColor()
        invalidate()
    }

    fun getColor() = thumbPaint.color

    fun setOnColorChangeListener(onColorChangeListener: OnColorChangeListener) {
        this.colorChangeListener = onColorChangeListener
    }

    interface OnColorChangeListener {

        fun onColorChangeListener(color: Int)
    }
}