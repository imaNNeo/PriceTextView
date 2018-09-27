package com.neo.pricetextview

import android.content.Context
import android.util.DisplayMetrics

/**
 * Created by iman.
 * iman.neofight@gmail.com
 */
fun Context.dpToPx(dp: Float): Float {
  val metrics = resources.displayMetrics
  return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.pxToDp(px: Float): Float {
  val metrics = resources.displayMetrics
  return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.spToPx(sp : Float) : Float{
  val fontScale = resources.displayMetrics.scaledDensity
  return sp * fontScale + 0.5f
}

fun Context.pxToSp(px : Float) : Float{
  val fontScale = resources.displayMetrics.scaledDensity
  return px / fontScale + 0.5f
}

fun Char.toPersianNumbers() : Char{
  return when(this) {
    '1' -> '۱'
    '2' -> '۲'
    '3' -> '۳'
    '4' -> '۴'
    '5' -> '۵'
    '6' -> '۶'
    '7' -> '۷'
    '8' -> '۸'
    '9' -> '۹'
    '0' -> '۰'
    else -> '۰'
  }
}