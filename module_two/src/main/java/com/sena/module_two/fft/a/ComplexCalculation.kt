package com.sena.module_two.fft.a

import kotlin.math.hypot


/**
 * FileName: ComplexCaloulation
 * Author: JiaoCan
 * Date: 2022/12/8 14:20
 */

class ComplexCalculation {

    fun add(tmp1: ComplexNumber, tmp2: ComplexNumber): ComplexNumber {
        return ComplexNumber(
            tmp1.real + tmp2.real,
            tmp1.img + tmp2.img
        )
    }

    fun minus(tmp1: ComplexNumber, tmp2: ComplexNumber): ComplexNumber {
        return ComplexNumber(
            tmp1.real - tmp2.real,
            tmp1.img - tmp2.img
        )
    }

    fun multiply(tmp1: ComplexNumber, tmp2: ComplexNumber): ComplexNumber {
        return ComplexNumber(
            tmp1.real * tmp2.real - tmp1.img * tmp2.img,
            tmp1.real * tmp2.img + tmp1.img * tmp2.real
        )
    }

    fun magnitudeDouble(num: ComplexNumber): Double {
        return hypot(num.real, num.img)
    }

    fun magnitudeShort(num: ComplexNumber): Float {
        var result = hypot(num.real, num.img)
        if (result <= 0) result = 0.0
//        if (result >= 32767) result = 32767.0
        return (result / 40000).toFloat()
    }

    fun shortToComplex(shorts: ShortArray): List<ComplexNumber> {
        val len = shorts.size
        val result = arrayListOf<ComplexNumber>()
        shorts.forEach {
            result.add(ComplexNumber(it.toDouble(), 0.0))
        }
        return result
    }



}

data class ComplexNumber(
    val real: Double,
    val img: Double
)

