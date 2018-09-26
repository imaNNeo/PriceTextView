package com.neo.pricetextview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator

/**
 * Created by iman.
 * iman.neofight@gmail.com
 */

class PriceTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  var number: String = "0"
    set(value) {
      field = if (value.isEmpty()) {
        "0"
      } else {
        if (value.length <= maxChars) {
          value
        } else {
          field
        }
      }
      numberChars = field.toCharArray()
      animateSize()
    }

  var numberChars: CharArray = charArrayOf()

  var textSpace: Float

  var maxChars : Int

  var maxTextSize = 100.sp.toFloat()
  var minTextSize = 48.sp.toFloat()

  private val textPaint: TextPaint

  private val tmpRect1 = Rect()
  private val tmpRect2 = Rect()

  private val horizontalPadding
    get() = paddingLeft + paddingRight

  private val verticalPadding
    get() = paddingTop + paddingBottom

  init {
    textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.BLACK
      textSize = maxTextSize
    }

    textSpace = 16.dp.toFloat()

    number = "0"

    maxChars = 8
  }

  private fun calculateTextSize(text : String) : Float {
    return when {
      text.length < 3 -> maxTextSize
      text.length < 4 -> ((maxTextSize - minTextSize) * 0.9 + minTextSize).toFloat()
      text.length < 5 -> ((maxTextSize - minTextSize) * 0.7 + minTextSize).toFloat()
      text.length < 6 -> ((maxTextSize - minTextSize) * 0.5 + minTextSize).toFloat()
      text.length < 7 -> ((maxTextSize - minTextSize) * 0.3 + minTextSize).toFloat()
      text.length < 8 -> ((maxTextSize - minTextSize) * 0.1 + minTextSize).toFloat()
      text.length < 9 -> minTextSize
      else -> maxTextSize
    }
  }

  private fun animateSize() {
    val newTextSize = calculateTextSize(number)

    val animatior = ValueAnimator.ofFloat(textPaint.textSize, newTextSize)
    animatior.addUpdateListener {
      textPaint.textSize = it.animatedValue as Float
      invalidate()
    }
    animatior.interpolator = LinearInterpolator()
    animatior.duration = 300
    animatior.start()
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    val resolvedWidth = resolveSize(desireWidth().toInt(), widthMeasureSpec)
    val resolvedHeight = resolveSize(desireHeight().toInt(), heightMeasureSpec)

    setMeasuredDimension(resolvedWidth, resolvedHeight)
  }

  private fun desireWidth(): Float {
    val numberDesireWidth = getRequiredTextWidth(numberChars, textPaint)
    return numberDesireWidth + horizontalPadding
  }

  private fun desireHeight(): Float {
    val height = getRequiredTextHeight(numberChars, textPaint)
    return height + verticalPadding
  }

  override fun onDraw(canvas: Canvas?) {

    drawText(canvas)
  }

  private fun getRequiredTextWidth(
    char: Char,
    textPaint: TextPaint
  ): Float {
    return textPaint.measureText(char.toString())
  }

  private fun getRequiredTextWidth(
    chars: CharArray,
    textPaint: TextPaint
  ): Float {
    var requiredWidth = 0f
    for (c in chars) {
      requiredWidth += getRequiredTextWidth(c, textPaint) + textSpace.toInt()
    }
    requiredWidth -= textSpace.toInt()
    return requiredWidth
  }

  private fun getRequiredTextHeight(
    char: Char,
    textPaint: TextPaint
  ): Float {
    textPaint.getTextBounds(char.toString(), 0, char.toString().length, tmpRect1)
    return tmpRect1.height()
        .toFloat()
  }

  private fun getRequiredTextHeight(
    chars: CharArray,
    textPaint: TextPaint
  ): Float {
    var maxHeight = 0f
    for (c in chars) {
      val h = getRequiredTextHeight(c, textPaint)
      if (h > maxHeight) {
        maxHeight = h
      }
    }
    return maxHeight
  }

  private fun drawText(canvas: Canvas?) {

    val requiredWidth = getRequiredTextWidth(numberChars, textPaint).toInt()
    val requiredHeight = getRequiredTextHeight(numberChars, textPaint).toInt()

    //Drawing Bound
    tmpRect1.set(
        (width / 2) - (requiredWidth / 2),
        (height / 2) - (requiredHeight / 2),
        (width / 2) + (requiredWidth / 2),
        (height / 2) + (requiredHeight / 2)
    )

    for (i in 0 until numberChars.size) {

      val char = numberChars[i]

      val w = getRequiredTextWidth(char, textPaint);
      //Assigned space
      tmpRect2.set(
          (tmpRect1.left + (i * w) + (i * textSpace)).toInt(),
          tmpRect1.top,
          (tmpRect1.left + (i * w) + (i * textSpace) + w).toInt(),
          tmpRect1.bottom
      )

      canvas?.drawText(char.toString(), tmpRect2.left.toFloat(), tmpRect2.bottom.toFloat(), textPaint)
    }
  }
}