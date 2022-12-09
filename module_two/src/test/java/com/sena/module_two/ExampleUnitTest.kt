package com.sena.module_two

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test01() {
        val array = byteArrayOf(54, 41, 47)
        val s = 0x00.toByte().toInt() == 0x00
        println("结果: " + s)
    }

    @Test
    fun test02() {
        val n = 157


        val h = (n shl 8)
        val l = (n - h)

        val r = (h.toInt() shr 8) or l.toInt()
        println("h: $h, l: $l, r: $r")
    }
}