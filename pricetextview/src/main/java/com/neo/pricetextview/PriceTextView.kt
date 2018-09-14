package com.neo.pricetextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
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
            requestLayout()
        }

    val textToDraw: String
        get() = number.toString()

    private val textPaint: TextPaint

    private val tmpRect1 = Rect()

    init {
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            textSize = 28.sp.toFloat()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val resolvedWidth = resolveSize(desireWidth(), widthMeasureSpec)
        val resolvedHeight = resolveSize(desireHeight(), heightMeasureSpec)

        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    private fun desireWidth(): Int {
        val numberDesireWidth = Math.ceil(textPaint.measureText(textToDraw).toDouble()).toInt()
        val horizontalPadding = paddingLeft + paddingRight
        return numberDesireWidth + horizontalPadding
    }

    private fun desireHeight(): Int {
        textPaint.getTextBounds(textToDraw, 0, textToDraw.length, tmpRect1)
        val numberDesireHeight = tmpRect1.height()
        val verticalPadding = paddingTop + paddingBottom
        return numberDesireHeight + verticalPadding
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawText(canvas)
    }

    private fun drawText(canvas: Canvas?) {

        textPaint.getTextBounds(textToDraw, 0, textToDraw.length, tmpRect1)
        val textWidth = textPaint.measureText(textToDraw)
        val textHeight = tmpRect1.height().toFloat()

        canvas?.drawText(
                textToDraw,
                (width / 2) - (textWidth / 2),
                (height / 2) + (textHeight / 2),
                textPaint)
    }
}