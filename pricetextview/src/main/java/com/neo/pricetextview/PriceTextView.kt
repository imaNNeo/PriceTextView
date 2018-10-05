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
import android.graphics.Typeface
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

  companion object {
    const val DEBUG = false
  }

  var numberChars: ArrayList<Char> = arrayListOf()
  var defaultShowingChar = '0'
  var thousandsSeparatorChar = ','

  var enabledShowingThousandsSeparator = true

  var textSpace: Float

  var maxChars: Int

  var preText: String
  var preTextSize : Float
  var preTextColor : Int
  var preTextMargin : Float

  var maxTextSize = context.spToPx(100f)
  var minTextSize = context.spToPx(48f)

  private val textPaint: TextPaint
  private val preTextPaint: TextPaint

  private val drawingNumberTextBoundRect = Rect()
  private val drawingDefaultTextBoundRect = Rect()
  private val tmpRect1 = Rect()
  private val tmpRect2 = Rect()

  var addNumberAnimDuration: Long = 250
  var addNumberAnimTranslateX: Float = context.pxToDp(180f)
  var addNumberAnimTranslateY: Float = context.pxToDp(-180f)

  private val horizontalPadding
    get() = paddingLeft + paddingRight

  private val verticalPadding
    get() = paddingTop + paddingBottom

  init {
    textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.BLACK
      textSize = maxTextSize
    }

    preText = "pre"
    preTextColor = Color.BLACK
    preTextSize = context.spToPx(20f)
    preTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
      color = preTextColor
      textSize = preTextSize
    }
    preTextMargin = context.dpToPx(8f)

    textSpace = context.pxToDp(0f)

    maxChars = 8
  }

  private fun calculateTextSize(numbersList: ArrayList<Char>): Float {
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

  fun addNumber(number: Char) {
    if (isAnimatingLastNumber) {
      return
    }

    if (numberChars.size >= maxChars) {
      return
    }

    animateAndAddNumber(number)
    animateSize(true)
  }

  private fun animateAndAddNumber(number: Char) {
    numberChars.add(number)

    isAnimatingLastNumber = true

    if (numberChars.size == 1) {
      //should remove default number
      isAnimatingDefaultNumber = true
    }

    val animator = ValueAnimator.ofFloat(1.0f, 0.0f)
    animator.interpolator = LinearInterpolator()
    animator.duration = addNumberAnimDuration
    animator.addUpdateListener {
      animatingLastNumberPlusX = (it.animatedValue as Float) * addNumberAnimTranslateX
      animatingLastNumberPlusY = (it.animatedValue as Float) * addNumberAnimTranslateY
      animatingLastNumberAlpha = (255 - ((it.animatedValue as Float) * 255)).toInt()

      if (isAnimatingDefaultNumber) {
        animatingDefaultNumberPlusX = -(addNumberAnimTranslateX - animatingLastNumberPlusX)
        animatingDefaultNumberPlusY = -(addNumberAnimTranslateY - animatingLastNumberPlusY)
        animatingDefaultNumberAlpha = 255 - animatingLastNumberAlpha
      }

      invalidate()
    }
    animator.addListener(object : AnimatorListener {
      override fun onAnimationRepeat(p0: Animator?) {}
      override fun onAnimationEnd(p0: Animator?) {
        isAnimatingLastNumber = false
        animatingLastNumberPlusX = 0f
        animatingLastNumberPlusY = 0f
        animatingLastNumberAlpha = 255

        if (isAnimatingDefaultNumber) {
          isAnimatingDefaultNumber = false
          animatingDefaultNumberPlusX = 0f
          animatingDefaultNumberPlusY = 0f
          animatingDefaultNumberAlpha = 255
        }

        invalidate()
      }

      override fun onAnimationCancel(p0: Animator?) {}
      override fun onAnimationStart(p0: Animator?) {}
    })
    animator.start()
  }

  fun removeNumber() {
    if (isAnimatingLastNumber) {
      return
    }

    if (numberChars.size <= 0) {
      return
    }

    animateAndRemoveNumber()
    animateSize(false)
  }

  private fun animateAndRemoveNumber() {
    isAnimatingLastNumber = true

    if (numberChars.size == 1) {
      //should show default
      isAnimatingDefaultNumber = true
    }

    val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
    animator.interpolator = LinearInterpolator()
    animator.duration = addNumberAnimDuration
    animator.addUpdateListener {
      animatingLastNumberPlusX = (it.animatedValue as Float) * addNumberAnimTranslateX
      animatingLastNumberPlusY = (it.animatedValue as Float) * addNumberAnimTranslateY
      animatingLastNumberAlpha = (255 - ((it.animatedValue as Float) * 255)).toInt()

      if (isAnimatingDefaultNumber) {
        animatingDefaultNumberPlusX = 0f
        animatingDefaultNumberPlusY = 0f
        animatingDefaultNumberAlpha = 255 - animatingLastNumberAlpha
      }

      invalidate()
    }
    animator.addListener(object : AnimatorListener {
      override fun onAnimationRepeat(p0: Animator?) {}
      override fun onAnimationEnd(p0: Animator?) {
        isAnimatingLastNumber = false
        animatingLastNumberPlusX = 0f
        animatingLastNumberPlusY = 0f
        animatingLastNumberAlpha = 255

        if (isAnimatingDefaultNumber) {
          isAnimatingDefaultNumber = false
          animatingDefaultNumberPlusX = 0f
          animatingDefaultNumberPlusY = 0f
          animatingDefaultNumberAlpha = 255
        }

        numberChars.removeAt(numberChars.lastIndex)
        invalidate()
      }

      override fun onAnimationCancel(p0: Animator?) {}
      override fun onAnimationStart(p0: Animator?) {}
    })
    animator.start()
  }

  private fun animateSize(isAdding: Boolean) {

    isAnimatingLastNumber = true

    val listBeforeAnimate: ArrayList<Char> =
      if (isAdding) {
        ArrayList(numberChars.dropLast(1))
      } else {
        numberChars
      }

    val listAfterAnimate: ArrayList<Char> =
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

  fun setTypeface(tf : Typeface?) {
    if (tf == null) {
      return
    }

    textPaint.typeface = tf
    requestLayout()
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
    val numberDesireWidth = if (numberChars.size > 0) {
      val numbersSize = getRequiredTextWidth(numberChars, textPaint)
      val preTextSize = getRequiredTextWidth(preText, preTextPaint)

      numbersSize + preTextSize
    } else {
      getRequiredTextWidth(defaultShowingChar, textPaint)
    }

    return numberDesireWidth + horizontalPadding
  }

  private fun desireHeight(): Float {
    val height = if (numberChars.size > 0) {
      getRequiredTextHeight(numberChars, textPaint)
    } else {
      getRequiredTextHeight(defaultShowingChar, textPaint)
    }

    val animationNeeded = addNumberAnimTranslateY * 4
    return height + Math.abs(animationNeeded) + verticalPadding
  }

  override fun onDraw(canvas: Canvas?) {

    if (DEBUG) {
      //Draw View Rect
      val p = Paint()
      p.style = STROKE
      p.strokeWidth = 2f
      p.color = Color.BLUE
      canvas?.drawRect(0F, 0F, width.toFloat(), height.toFloat(), p)
    }

    drawText(canvas)
  }

  var isAnimatingLastNumber = false
  var animatingLastNumberPlusX = 0f
  var animatingLastNumberPlusY = 0f
  var animatingLastNumberAlpha = 255

  var animatingDrawingBoundLeft = 0
  var animatingDrawingBoundTop = 0
  var animatingDrawingBoundRight = 0
  var animatingDrawingBoundBottom = 0

  var isAnimatingDefaultNumber = false
  var animatingDefaultNumberPlusX = 0f
  var animatingDefaultNumberPlusY = 0f
  var animatingDefaultNumberAlpha = 255

  private fun drawText(canvas: Canvas?) {

    val defaultCharWidth = getRequiredTextHeight(defaultShowingChar, textPaint).toInt()
    val defaultCharHeight = getRequiredTextWidth(defaultShowingChar, textPaint).toInt()

    val requiredWidth: Int =
      if (numberChars.size > 0) {
        getRequiredTextWidth(numberChars, textPaint).toInt()
      } else {
        defaultCharHeight
      }

    val requiredHeight: Int =
      if (numberChars.size > 0) {
        getRequiredTextHeight(numberChars, textPaint).toInt()
      } else {
        defaultCharWidth
      }

    //Drawing Bound
    if (!isAnimatingLastNumber) {
      drawingNumberTextBoundRect.set(
          (width / 2) - (requiredWidth / 2),
          (height / 2) - (requiredHeight / 2),
          (width / 2) + (requiredWidth / 2),
          (height / 2) + (requiredHeight / 2)
      )
    } else {
      drawingNumberTextBoundRect.set(
          animatingDrawingBoundLeft,
          animatingDrawingBoundTop,
          animatingDrawingBoundRight,
          animatingDrawingBoundBottom
      )
    }

    drawingDefaultTextBoundRect.set(
        (width / 2) - (defaultCharWidth / 2),
        (height / 2) - (defaultCharHeight / 2),
        (width / 2) + (defaultCharWidth / 2),
        (height / 2) + (defaultCharHeight / 2)
    )

    if (DEBUG) {
      //Draw DrawingBound Rect
      val p = Paint()
      p.style = STROKE
      p.strokeWidth = 2f
      p.color = Color.RED
      canvas?.drawRect(drawingNumberTextBoundRect, p)
    }

    //Draw Default Character
    if (isAnimatingDefaultNumber || numberChars.size == 0) {

      val char = defaultShowingChar

      //Assigned space
      tmpRect2.set(
          (drawingDefaultTextBoundRect.left),
          drawingDefaultTextBoundRect.top,
          (drawingDefaultTextBoundRect.left),
          drawingDefaultTextBoundRect.bottom
      )

      if (DEBUG) {
        //Draw characters Assigned area rect
        val pp = Paint()
        pp.style = STROKE
        pp.strokeWidth = 2f
        pp.color = Color.BLACK
        canvas?.drawRect(tmpRect2, pp)
      }

      val tmpAlpha = textPaint.alpha
      textPaint.alpha = animatingDefaultNumberAlpha

      canvas?.drawText(
          char.toString(),
          tmpRect2.left.toFloat() + animatingDefaultNumberPlusX,
          tmpRect2.bottom.toFloat() + animatingDefaultNumberPlusY, textPaint
      )

      textPaint.alpha = tmpAlpha

      if (DEBUG) {
        Log.d(
            "SS",
            "drawingDefaultWidthAnim x : ${tmpRect2.left},  px : $animatingDefaultNumberPlusX, y : ${tmpRect2.bottom}, py : $animatingDefaultNumberPlusY, alpha = $animatingDefaultNumberAlpha"
        )
      }
    }

    //Draw Number Characters
    var widthSum = 0f
    for (i in 0 until numberChars.size) {

      val char = numberChars[i]

      widthSum += getRequiredTextWidth(char, textPaint)

      val currentCharNumberWidth = getRequiredTextWidth(char, textPaint)
      val thousandsSeparatorWidth = getRequiredTextWidth(thousandsSeparatorChar, textPaint)
      val previousSumWidth = widthSum - currentCharNumberWidth

      //about separator
      var separatorsCountToDraw = 0
      var separatorsCountToDrawOnThisPos = 0
      var shouldDrawSeparatorAtThisPos = false
      if (enabledShowingThousandsSeparator) {
        separatorsCountToDraw = (numberChars.size - 1) / 3
        separatorsCountToDrawOnThisPos = ((numberChars.size - 1) / 3) - ((numberChars.size - (i + 1)) / 3)
        shouldDrawSeparatorAtThisPos =
            (numberChars.size > 3) &&
            (i != numberChars.size - 1) &&
            (i == numberChars.size - (((numberChars.size - (i + 1)) / 3) * 3) - 1)
      }

      Log.d("FF", "i : $i, separatorsCountToDraw : $separatorsCountToDraw, separatorsCountToDrawOnThisPos : $separatorsCountToDrawOnThisPos, shouldDrawSeparatorAtThisPos : $shouldDrawSeparatorAtThisPos")

      //Assigned number char bounds
      var left =
        (drawingNumberTextBoundRect.left +
            previousSumWidth + (i * textSpace) +
            separatorsCountToDrawOnThisPos * thousandsSeparatorWidth).toInt()

      val top = drawingNumberTextBoundRect.top
      val right = (left + currentCharNumberWidth).toInt()
      val bot = drawingNumberTextBoundRect.bottom
      tmpRect2.set(left, top, right, bot)

      //Assigned separator char bounds
      if (enabledShowingThousandsSeparator) {
        if (shouldDrawSeparatorAtThisPos) {
          //Should Draw at this position
          val left = tmpRect2.right
          val top = tmpRect2.top
          val right = (tmpRect2.left + thousandsSeparatorWidth).toInt()
          val bot = tmpRect2.bottom
          tmpRect1.set(left, top, right, bot)
        }

        if (DEBUG) {
          //Draw characters Assigned area rect
          val pp = Paint()
          pp.style = STROKE
          pp.strokeWidth = 2f
          pp.color = Color.BLACK
          canvas?.drawRect(tmpRect2, pp)
        }
      }

      if (DEBUG) {
        //Draw characters Assigned area rect
        val pp = Paint()
        pp.style = STROKE
        pp.strokeWidth = 2f
        pp.color = Color.BLACK
        canvas?.drawRect(tmpRect2, pp)
      }

      if (i == numberChars.size - 1 && isAnimatingLastNumber) {
        if (DEBUG) {
          Log.d("SS", "drawWithAnim px : $animatingLastNumberPlusX, py : $animatingLastNumberPlusY, alpha = $animatingLastNumberAlpha")
        }

        val tmpAlpha = textPaint.alpha
        textPaint.alpha = animatingLastNumberAlpha

        val drawX = tmpRect2.left.toFloat() + animatingLastNumberPlusX
        val drawY = tmpRect2.bottom.toFloat() + animatingLastNumberPlusY
        canvas?.drawText(char.toString(), drawX, drawY, textPaint)

        if (DEBUG) {
          Log.d("SS", "drawing $char on $drawX, $drawY, with Alpha ${textPaint.alpha}")
        }

        textPaint.alpha = tmpAlpha
      } else {
        canvas?.drawText(char.toString(), tmpRect2.left.toFloat(), tmpRect2.bottom.toFloat(), textPaint)

        if (separatorsCountToDraw > 0) {
          if ((i + 1) % 3 == 0) {
            canvas?.drawText(thousandsSeparatorChar.toString(), tmpRect1.left.toFloat(), tmpRect1.bottom.toFloat(), textPaint)
          }
        }
      }
    }

    //Draw Pre Text
    var x : Float= if (numberChars.size > 0 && !isAnimatingDefaultNumber) {
      (drawingNumberTextBoundRect.left).toFloat()
    } else {
      (drawingNumberTextBoundRect.centerX() - (defaultCharWidth / 2)).toFloat()
    }
    x -= getRequiredTextWidth(preText, preTextPaint)
    x -= preTextMargin

    var y = drawingDefaultTextBoundRect.top + getRequiredTextHeight(preText, preTextPaint)
    canvas?.drawText(preText, x, y, preTextPaint)

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

    //add space for separators
    if (enabledShowingThousandsSeparator) {
      val separatorsCount = (chars.size - 1) / 3
      if (separatorsCount > 0) {
        val w = getRequiredTextWidth(thousandsSeparatorChar, textPaint)
        requiredWidth += (w * separatorsCount)
      }
    }

    return requiredWidth
  }

  private fun getRequiredTextWidth(
    str: String,
    textPaint: TextPaint
  ) = getRequiredTextWidth(ArrayList(str.toCharArray().toList()), textPaint)

  private fun getRequiredTextHeight(
    char: Char,
    textPaint: TextPaint
  ): Float {
    var checkingChar = char
    if (checkingChar == '۰') {
      checkingChar = '۱'
    }
    textPaint.getTextBounds(checkingChar.toString(), 0, checkingChar.toString().length, drawingNumberTextBoundRect)
    return drawingNumberTextBoundRect.height()
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

  private fun getRequiredTextHeight(
    str: String,
    textPaint: TextPaint
  ) = getRequiredTextHeight(ArrayList(str.toCharArray().toList()), textPaint)

}