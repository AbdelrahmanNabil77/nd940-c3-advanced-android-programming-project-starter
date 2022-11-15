package com.udacity

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.udacity.utils.disableViewDuringAnimation
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {
    private var widthSize = 0
    private var heightSize = 0

    private var loadingDefaultBackgroundColor = -1
    private var loadingBackgroundColor = -1
    private var loadingDefaultText = ""
    private var loadingText = ""
    private var loadingTextColor = -1
    private var progressCircleBackgroundColor = -1

    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var buttonText = ""
    private val buttonTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55f
        typeface = Typeface.DEFAULT
    }

    private lateinit var buttonTextBounds: Rect

    private val progressCircleRect = RectF()
    private var progressCircleSize = 0f

    private val animatorSet: AnimatorSet = AnimatorSet().apply {
        duration = 3000
        disableViewDuringAnimation(this@LoadingButton)
    }
    private var currentProgressCircleAnimationValue = 0f
    private val progressCircleAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            currentProgressCircleAnimationValue = it.animatedValue as Float
            invalidate()
        }
    }
    private var currentButtonBackgroundAnimationValue = 0f
    private lateinit var buttonBackgroundAnimator: ValueAnimator

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = loadingText
                if (!::buttonTextBounds.isInitialized) {
                    retrieveButtonTextBounds()
                    computeProgressCircleRect()
                }
                animatorSet.start()
            }
            else -> {
                buttonText = loadingDefaultText
                new.takeIf { it == ButtonState.Completed }?.run { animatorSet.cancel() }
            }
        }
    }

    private fun retrieveButtonTextBounds() {
        buttonTextBounds = Rect()
        buttonTextPaint.getTextBounds(buttonText, 0, buttonText.length, buttonTextBounds)
    }

    private fun computeProgressCircleRect() {
        val horizontalCenter =
            (buttonTextBounds.right + buttonTextBounds.width() + 30f)
        val verticalCenter = (heightSize / 2f)

        progressCircleRect.set(
            horizontalCenter - progressCircleSize,
            verticalCenter - progressCircleSize,
            horizontalCenter + progressCircleSize,
            verticalCenter + progressCircleSize
        )
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            loadingDefaultBackgroundColor =
                getColor(R.styleable.LoadingButton_loadingDefaultBackgroundColor, 0)
            loadingBackgroundColor =
                getColor(R.styleable.LoadingButton_loadingBackgroundColor, 0)
            loadingDefaultText =
                getText(R.styleable.LoadingButton_loadingDefaultText).toString()
            loadingTextColor =
                getColor(R.styleable.LoadingButton_loadingTextColor, 0)
            loadingText =
                getText(R.styleable.LoadingButton_loadingText).toString()
        }.also {
            buttonText = loadingDefaultText
            progressCircleBackgroundColor = ContextCompat.getColor(context, R.color.colorAccent)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = paddingLeft + paddingRight + suggestedMinimumWidth
        val w = resolveSizeAndState(
            minWidth,
            widthMeasureSpec,
            1
        )
        val h = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        progressCircleSize = (min(w, h) / 2f) * 0.5f
        createButtonBackgroundAnimator()
    }

    private fun createButtonBackgroundAnimator() {
        ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                currentButtonBackgroundAnimationValue = it.animatedValue as Float
                invalidate()
            }
        }.also {
            buttonBackgroundAnimator = it
            animatorSet.playTogetherLoadingAndCircle()
        }.start()
    }

    private fun AnimatorSet.playTogetherLoadingAndCircle() =
        apply { playTogether(progressCircleAnimator, buttonBackgroundAnimator) }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Completed) {
            buttonState = ButtonState.Clicked
            invalidate()
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { buttonCanvas ->
            buttonCanvas.apply {
                drawBackgroundColor()
                drawButtonText()
                drawProgressCircleIfLoading()
            }
        }
    }

    private fun Canvas.drawButtonText() {
        buttonTextPaint.color = loadingTextColor
        drawText(
            buttonText,
            (widthSize / 2f),
            (heightSize / 2f) + buttonTextPaint.computeTextOffset(),
            buttonTextPaint
        )
    }

    private fun TextPaint.computeTextOffset() = ((descent() - ascent()) / 2) - descent()

    private fun Canvas.drawBackgroundColor() {
        when (buttonState) {
            ButtonState.Loading -> {
                drawLoadingBackgroundColor()
                drawDefaultBackgroundColor()
            }
            else -> drawColor(loadingDefaultBackgroundColor)
        }
    }

    private fun Canvas.drawLoadingBackgroundColor() = buttonPaint.apply {
        color = loadingBackgroundColor
    }.run {
        drawRect(
            0f,
            0f,
            currentButtonBackgroundAnimationValue,
            heightSize.toFloat(),
            buttonPaint
        )
    }

    private fun Canvas.drawDefaultBackgroundColor() = buttonPaint.apply {
        color = loadingDefaultBackgroundColor
    }.run {
        drawRect(
            currentButtonBackgroundAnimationValue,
            0f,
            widthSize.toFloat(),
            heightSize.toFloat(),
            buttonPaint
        )
    }

    private fun Canvas.drawProgressCircleIfLoading() =
        buttonState.takeIf { it == ButtonState.Loading }?.let { drawProgressCircle(this) }

    private fun drawProgressCircle(buttonCanvas: Canvas) {
        buttonPaint.color = progressCircleBackgroundColor
        buttonCanvas.drawArc(
            progressCircleRect,
            0f,
            currentProgressCircleAnimationValue,
            true,
            buttonPaint
        )
    }

    fun changeButtonState(state: ButtonState) {
        if (state != buttonState) {
            buttonState = state
            invalidate()
        }
    }
}