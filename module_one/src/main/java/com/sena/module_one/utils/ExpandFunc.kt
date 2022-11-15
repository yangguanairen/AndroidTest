package com.sena.module_one.utils

import android.content.Context
import android.graphics.PointF
import kotlin.math.pow
import kotlin.math.sqrt


// 判断当前点是否在b点范围内
fun PointF.contains(b: PointF, bRadius: Float = 0f): Boolean {
    val isX = this.x <= b.x + bRadius && this.x >= b.x - bRadius
    val isY = this.y <= b.y + bRadius && this.y >= b.y - bRadius
    return isX && isY
}

fun PointF.distance(b: PointF): Float = let {
    val a = this
    val dx = b.x - a.x * 1.0
    val dy = b.y - a.y * 1.0
    return@let sqrt(dx.pow(2)  + dy.pow(2)).toFloat()
}

fun Context.dpToPx(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}
