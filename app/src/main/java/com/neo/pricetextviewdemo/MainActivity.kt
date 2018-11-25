package com.neo.pricetextviewdemo

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.neo.pricetextview.spToPx
import com.neo.pricetextview.toPersianNumbers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)


    btn_1.setOnClickListener { onNumberClicked(1) }
    btn_2.setOnClickListener { onNumberClicked(2) }
    btn_3.setOnClickListener { onNumberClicked(3) }
    btn_4.setOnClickListener { onNumberClicked(4) }
    btn_5.setOnClickListener { onNumberClicked(5) }
    btn_6.setOnClickListener { onNumberClicked(6) }
    btn_7.setOnClickListener { onNumberClicked(7) }
    btn_8.setOnClickListener { onNumberClicked(8) }
    btn_9.setOnClickListener { onNumberClicked(9) }
    btn_0.setOnClickListener { onNumberClicked(0) }
    btn_back.setOnClickListener { onDeleteClicked() }

    priceTextView1.setTypeface(Typeface.createFromAsset(assets, "GoogleSans-Regular.ttf"))
    priceTextView1.preText = "$"
    priceTextView1.preTextSize = spToPx(22f)

    priceTextView2.setTypeface(Typeface.createFromAsset(assets, "iransans.ttf"))
    priceTextView2.defaultShowingChar = '۰'
    priceTextView2.preText = "تومان"
  }

  private fun onNumberClicked(i: Int) {
    priceTextView1.addNumber(i.toString().toCharArray()[0])
    priceTextView2.addNumber(i.toString().toCharArray()[0].toPersianNumbers())
  }

  private fun onDeleteClicked() {
    priceTextView1.removeNumber()
    priceTextView2.removeNumber()
  }
}