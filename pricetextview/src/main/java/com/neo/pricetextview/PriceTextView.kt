package com.neo.pricetextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.provider.CalendarContract
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

/**
 * Created by iman.
 * iman.neofight@gmail.com
 */

class PriceTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var number: Long = 0
        set(value) {
            field = value
            numberChars = value.toString().toCharArray()
            requestLayout()
        }

    var numberChars: CharArray = charArrayOf()


    var textSpace: Float

    private val textPaint: TextPaint

    private val tmpRect1 = Rect()
    private val tmpRect2 = Rect()


    private val horizontalPadding
        get() = paddingLeft + paddingRight

    private val verticalPadding
        get() = paddingTop + paddingBottom

    init {
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            textSize = 28.sp.toFloat()
        }

        textSpace = 16.dp.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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


    private fun getRequiredTextWidth(char: Char, textPaint: TextPaint): Float {
        return textPaint.measureText(char.toString())
    }

    private fun getRequiredTextWidth(chars: CharArray, textPaint: TextPaint): Float {
        var requiredWidth = 0f
        for (c in chars) {
            requiredWidth += getRequiredTextWidth(c, textPaint) + textSpace.toInt()
        }
        requiredWidth -= textSpace.toInt()
        return requiredWidth
    }


    private fun getRequiredTextHeight(char: Char, textPaint: TextPaint): Float {
        textPaint.getTextBounds(char.toString(), 0, char.toString().length, tmpRect1)
        return tmpRect1.height().toFloat()
    }

    private fun getRequiredTextHeight(chars: CharArray, textPaint: TextPaint): Float {
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
                (height / 2) + (requiredHeight / 2))

        val p = Paint()
        p.color = Color.GREEN
        canvas?.drawRect(tmpRect1, p)

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