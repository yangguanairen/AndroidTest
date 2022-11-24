package com.sena.module_two

import android.util.Base64
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.nio.charset.Charset

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.sena.module_two", appContext.packageName)
    }


    @Test
    fun test01() {
        val array = byteArrayOf(0x00, 0x4C, 0x61, 0x76, 0x66, 0x35, 0x37, 0x2E, 0x37, 0x31, 0x2E, 0x31, 0x30, 0x30, 0x43)
        val s = String(array)
        println(s)
        val format = "%6s %6s"
        println(String.format(format, "名称", "值"))
    }
}