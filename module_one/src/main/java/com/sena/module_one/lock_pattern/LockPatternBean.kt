package com.sena.module_one.lock_pattern

import android.graphics.Color


/**
 * FileName: LockPatternBean
 * Author: JiaoCan
 * Date: 2022/11/14 15:21
 */

data class LockPatternBean(
    val x: Float,
    val y: Float,
    val index: Int,
    var type: Type = Type.ORIGIN,
    val smallCircleAlpha: Int = 255,
    val bigCircleAlpha: Int = (255 * 0.6).toInt()
)

enum class Type {
    ORIGIN,
    DOWN,
    UP,
    ERROR
}


