package com.sena.module_one.lock_pattern

import android.graphics.Color
import android.graphics.Paint


/**
 * FileName: LockPatternAdapter
 * Author: JiaoCan
 * Date: 2022/11/15 14:01
 */

abstract class LockPatternBaseAdapter {

    abstract fun getNumber(): Int

    abstract fun getStyle(): Paint.Style


    open fun getOriginColor(): Int = let {
        return Color.parseColor("#D8D9D8")
    }

    open fun getDownColor(): Int = let {
        return Color.parseColor("#3AD94E")
    }

    open fun getUpColor(): Int = let {
        return Color.parseColor("#57D900")
    }

    open fun getErrorColor(): Int = let {
        return Color.parseColor("#D9251E")
    }

}

