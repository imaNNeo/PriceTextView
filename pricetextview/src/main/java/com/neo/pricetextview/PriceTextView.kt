package com.neo.pricetextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

    var text : String = ""
    set(value) {
        value.toIntOrNull() ?: return
        field = value
        invalidate()
    }

    private val textPaint : TextPaint

    init {
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawText(text, 0f, 50f, textPaint)
    }
}