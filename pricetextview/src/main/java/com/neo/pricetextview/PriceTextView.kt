package com.neo.pricetextview

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by iman.
 * iman.neofight@gmail.com
 */

class PriceTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  var numberChars: ArrayList<Char> = arrayListOf()

  var textSpace: Float

  var maxChars : Int

  var maxTextSize = 100.sp.toFloat()
  var minTextSize = 48.sp.toFloat()

  private val textPaint: TextPaint

  private val drawingTextBoundRect = Rect()
  private val tmpRect2 = Rect()

  var addNumberAnimDuration : Long = 600
  var addNumberTranslateXAnim : Float = 120.dp.toFloat()
  var addNumberTranslateYAnim : Float = 12.dp.toFloat()

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

    numberChars.add('0')

    maxChars = 8
  }

  private fun calculateTextSize(numbersList : ArrayList<Char>) : Float {
    return when {
      numbersList.size < 3 -> maxTextSize
      numbersList.size < 4 -> ((maxTextSize - minTextSize) * 0.9 + minTextSize).toFloat()
      numbersList.size < 5 -> ((maxTextSize - minTextSize) * 0.7 + minTextSize).toFloat()
      numbersList.size < 6 -> ((maxTextSize - minTextSize) * 0.5 + minTextSize).toFloat()
      numbersList.size < 7 -> ((maxTextSize - minTextSize) * 0.3 + minTextSize).toFloat()
      numbersList.size < 8 -> ((maxTextSize - minTextSize) * 0.1 + minTextSize).toFloat()
      numbersList.size < 9 -> minTextSize
      else -> maxTextSize
    }
  }

  fun addNumber(number : Char) {
    if (numberChars.size >= maxChars) {
      return
    }

    animateAndAddNumber(number)
    animateSize(true)
  }

  private fun animateAndAddNumber(number: Char) {
    numberChars.add(number)

    isAnimatingLastNumber = true

    val animator = ValueAnimator.ofFloat(1.0f, 0.0f)
    animator.interpolator = LinearInterpolator()
    animator.duration = addNumberAnimDuration
    animator.addUpdateListener {
      animatingLastNumberPlusX = (it.animatedValue as Float) * addNumberTranslateXAnim
      animatingLastNumberPlusY = (it.animatedValue as Float) * addNumberTranslateYAnim
      animatingLastNumberAlpha = (255 - ((it.animatedValue as Float) * 255)).toInt()

      invalidate()
    }
    animator.addListener(object : AnimatorListener {
      override fun onAnimationRepeat(p0: Animator?) {}
      override fun onAnimationEnd(p0: Animator?) {
        isAnimatingLastNumber = false
        animatingLastNumberPlusX = 0f
        animatingLastNumberPlusY = 0f
        animatingLastNumberAlpha = 255
        invalidate()
      }
      override fun onAnimationCancel(p0: Animator?) {}
      override fun onAnimationStart(p0: Animator?) {}
    })
    animator.start()
  }

  fun removeNumber() {
    if (numberChars.size <= 1) {
      return
    }

    animateAndRemoveNumber()
    animateSize(false)
  }

  private fun animateAndRemoveNumber() {
    isAnimatingLastNumber = true

    val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
    animator.interpolator = LinearInterpolator()
    animator.duration = addNumberAnimDuration
    animator.addUpdateListener {
      animatingLastNumberPlusX = (it.animatedValue as Float) * addNumberTranslateXAnim
      animatingLastNumberPlusY = (it.animatedValue as Float) * addNumberTranslateYAnim
      animatingLastNumberAlpha = (255 - ((it.animatedValue as Float) * 255)).toInt()

      invalidate()
    }
    animator.addListener(object : AnimatorListener {
      override fun onAnimationRepeat(p0: Animator?) {}
      override fun onAnimationEnd(p0: Animator?) {
        isAnimatingLastNumber = false
        animatingLastNumberPlusX = 0f
        animatingLastNumberPlusY = 0f
        animatingLastNumberAlpha = 255
        numberChars.removeAt(numberChars.lastIndex)
        invalidate()
      }
      override fun onAnimationCancel(p0: Animator?) {}
      override fun onAnimationStart(p0: Animator?) {}
    })
    animator.start()
  }

  private fun animateSize(isAdding : Boolean) {

    isAnimatingLastNumber = true

    val listBeforeAnimate : ArrayList<Char> =
      if (isAdding) {
        ArrayList(numberChars.dropLast(1))
      } else {
        numberChars
      }

    val listAfterAnimate : ArrayList<Char> =
      if (isAdding) {
        numberChars
      } else {
        ArrayList(numberChars.dropLast(1))
      }

    val newTextSize = calculateTextSize(numberChars)
    val currentTextSize = textPaint.textSize

    val currentRequiredWidth = getRequiredTextWidth(listBeforeAnimate, textPaint).toInt()
    val currentRequiredHeight = getRequiredTextHeight(listBeforeAnimate, textPaint).toInt()
    val fromLeft = (width / 2) - (currentRequiredWidth / 2)
    val fromTop = (height / 2) - (currentRequiredHeight / 2)
    val fromRight = (width / 2) + (currentRequiredWidth / 2)
    val fromBot = (height / 2) + (currentRequiredHeight / 2)

    textPaint.textSize = newTextSize
    val newRequiredWidth = getRequiredTextWidth(listAfterAnimate, textPaint).toInt()
    val newRequiredHeight = getRequiredTextHeight(listAfterAnimate, textPaint).toInt()
    val toLeft = (width / 2) - (newRequiredWidth / 2)
    val toTop = (height / 2) - (newRequiredHeight / 2)
    val toRight = (width / 2) + (newRequiredWidth / 2)
    val toBot = (height / 2) + (newRequiredHeight / 2)
    textPaint.textSize = currentTextSize


    val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
    animator.addUpdateListener {
      textPaint.textSize = currentTextSize + ((it.animatedValue as Float) * (newTextSize - currentTextSize))
      animatingDrawingBoundLeft = (fromLeft + ((it.animatedValue as Float) * (toLeft - fromLeft))).toInt()
      animatingDrawingBoundTop = (fromTop + ((it.animatedValue as Float) * (toTop - fromTop))).toInt()
      animatingDrawingBoundRight = (fromRight + ((it.animatedValue as Float) * (toRight - fromRight))).toInt()
      animatingDrawingBoundBottom = (fromBot + ((it.animatedValue as Float) * (toBot - fromBot))).toInt()

      invalidate()
    }
    animator.addListener(object : AnimatorListener {
      override fun onAnimationRepeat(p0: Animator?) {}
      override fun onAnimationEnd(p0: Animator?) {
        isAnimatingLastNumber = false

        animatingDrawingBoundLeft = 0
        animatingDrawingBoundTop = 0
        animatingDrawingBoundRight = 0
        animatingDrawingBoundBottom = 0

        invalidate()
      }
      override fun onAnimationCancel(p0: Animator?) {}
      override fun onAnimationStart(p0: Animator?) {}
    })
    animator.interpolator = LinearInterpolator()
    animator.duration = addNumberAnimDuration
    animator.start()
    Log.d("SS", "Start Animating From $currentTextSize, to $newTextSize")
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
    chars: ArrayList<Char>,
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
    textPaint.getTextBounds(char.toString(), 0, char.toString().length, drawingTextBoundRect)
    return drawingTextBoundRect.height()
        .toFloat()
  }

  private fun getRequiredTextHeight(
    chars: ArrayList<Char>,
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

  var isAnimatingLastNumber = false
  var animatingLastNumberPlusX = 0f
  var animatingLastNumberPlusY = 0f
  var animatingLastNumberAlpha = 255

  var animatingDrawingBoundLeft = 0
  var animatingDrawingBoundTop = 0
  var animatingDrawingBoundRight = 0
  var animatingDrawingBoundBottom = 0


  private fun drawText(canvas: Canvas?) {

    val requiredWidth = getRequiredTextWidth(numberChars, textPaint).toInt()
    val requiredHeight = getRequiredTextHeight(numberChars, textPaint).toInt()

    //Drawing Bound
    if (!isAnimatingLastNumber) {
      drawingTextBoundRect.set(
          (width / 2) - (requiredWidth / 2),
          (height / 2) - (requiredHeight / 2),
          (width / 2) + (requiredWidth / 2),
          (height / 2) + (requiredHeight / 2)
      )
    } else {
      drawingTextBoundRect.set(
          animatingDrawingBoundLeft,
          animatingDrawingBoundTop,
          animatingDrawingBoundRight,
          animatingDrawingBoundBottom
      )
    }

    val p = Paint()
    p.style = STROKE
    p.strokeWidth = 2f
    p.color = Color.RED
    canvas?.drawRect(drawingTextBoundRect, p)

    for (i in 0 until numberChars.size) {

      val char = numberChars[i]

      val w = getRequiredTextWidth(char, textPaint);
      //Assigned space
      tmpRect2.set(
          (drawingTextBoundRect.left + (i * w) + (i * textSpace)).toInt(),
          drawingTextBoundRect.top,
          (drawingTextBoundRect.left + (i * w) + (i * textSpace) + w).toInt(),
          drawingTextBoundRect.bottom
      )

      val pp = Paint()
      pp.style = STROKE
      pp.strokeWidth = 2f
      pp.color = Color.BLACK
      canvas?.drawRect(tmpRect2, pp)

      if (i == numberChars.size - 1 && isAnimatingLastNumber) {
        Log.d("SS", "drawWithAnim px : $animatingLastNumberPlusX, py : $animatingLastNumberPlusY, alpha = $animatingLastNumberAlpha")
        val tmpAlpha = textPaint.alpha
        textPaint.alpha = animatingLastNumberAlpha

        val drawX = tmpRect2.left.toFloat() + animatingLastNumberPlusX
        val drawY = tmpRect2.bottom.toFloat() + animatingLastNumberPlusY
        canvas?.drawText(char.toString(), drawX, drawY, textPaint)
        Log.d("SS", "drawing $char on $drawX, $drawY, with Alpha ${textPaint.alpha}")
        textPaint.alpha = tmpAlpha
      } else {
        canvas?.drawText(char.toString(), tmpRect2.left.toFloat(), tmpRect2.bottom.toFloat(), textPaint)
      }
    }
  }
}