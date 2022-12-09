package com.sena.module_two.fft

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.gson.Gson


/**
 * FileName: VisualizeView
 * Author: JiaoCan
 * Date: 2022/12/6 11:03
 */

class VisualizeView @JvmOverloads constructor(
    context: Context,
    atts: AttributeSet? = null,
    defStyle: Int = 1
) : View(context, atts, defStyle) {

    val linePaint = Paint()

    init {
        linePaint.color = Color.parseColor("#26c6da")
    }

    private var data: FloatArray? = null

    var lineWidth = 0f
    var offset = 0f
    var unitHeight = 0f


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val viewWidth = measuredWidth
        offset = viewWidth / 60 / 3f
        lineWidth = offset * 2


        unitHeight = (measuredHeight - 50) / 22.05f
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        data?.let {
//            (lineWidth + offset) * i + lineWidth
            for (i in 0 until it.size) {
                canvas?.drawRect((lineWidth + offset) * i, height.toFloat(), (lineWidth + offset) * i + 2, height - it[i] * unitHeight - 20, linePaint)
            }
        }
    }

    fun onWrite(model: FloatArray) {
        data = model
        invalidate()
    }

}

