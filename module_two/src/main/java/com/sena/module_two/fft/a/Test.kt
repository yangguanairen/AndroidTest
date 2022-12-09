package com.sena.module_two.fft.a

import com.google.gson.Gson
import kotlin.math.*


/**
 * FileName: Test
 * Author: JiaoCan
 * Date: 2022/12/8 14:14
 */

class Test {

    fun test(buffer: ByteArray): FloatArray {
        val shortArray = ShortArray(buffer.size / 2)
        for (i in 0 until shortArray.size) {
            val l = buffer[i * 2].toInt()
            val h = buffer[i * 2 + 1].toInt() shl 8
            val s = l or h
            shortArray[i] = s.toShort()
        }

        return algorithmFFT(algorithmRador(shortArray))
    }



    fun algorithmRador(shorts: ShortArray): ShortArray {
        val len = shorts.size
        var k: Int
        var j: Int = 0
        var tmp: Short
        for (i in 0 until len - 1) {
            if (i < j) {
                tmp = shorts[i]
                shorts[i] = shorts[j]
                shorts[j] = tmp
            }
            k = len shr 1
            while (k <= j) {
                j -= k
                k = k shr 1
            }
            j += k
        }
        return shorts
    }

    fun algorithmFFT(shorts: ShortArray): FloatArray {
        val len = shorts.size

        val power = ln(len.toFloat()) / ln(2f)
        var l: Int = 0

        val calculation = ComplexCalculation()
        var tmpRotate = complexRotate(len)
        val complexNumbers = calculation.shortToComplex(shorts).toMutableList()
        var tmpProduct: ComplexNumber
        var tmpUp: ComplexNumber
        var tmpDown: ComplexNumber

        for (i in 0 until power.toInt()) {
            l = 1 shl i
            for (j in 0 until len step 2 * l) {
                for (k in 0 until l) {
                    tmpProduct = calculation.multiply(complexNumbers[j + k + l], tmpRotate[len * k / 2 / l])
                    tmpUp = calculation.add(complexNumbers[j + k], tmpProduct)
                    tmpDown = calculation.minus(complexNumbers[j + k], tmpProduct)
                    complexNumbers[j + k] = tmpUp
                    complexNumbers[j + k + l] = tmpDown

                }
            }
        }

        val out = FloatArray(len / 2)
        for (i in 0 until len / 2) {
            out[i] = calculation.magnitudeShort(complexNumbers[i])
        }
        println(Gson().toJson(out))

        return out
    }

    fun complexRotate(n: Int): List<ComplexNumber> {
        val pi = 3.14159
        val result = arrayListOf<ComplexNumber>()
        for (i in 0 until n) {
            result.add(ComplexNumber(
                cos(2 * pi * i / n),
                -sin(2 * pi * i / n)
            ))
        }
        return result
    }

}

