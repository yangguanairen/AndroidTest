package com.sena.module_one.transparent_bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sena.module_one.databinding.ActivityTransparentBitmapBinding

class TransparentBitmapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransparentBitmapBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransparentBitmapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        test()
    }

    private fun test() {
        val stream = assets.open("02.png")
        val bitmap = BitmapFactory.decodeStream(stream)
        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        println(pixels.joinToString(","))

//        val transparentP = 0
//
//        val newPixels = IntArray(w * h)
//        pixels.forEachIndexed { i, p ->
//
//            val a = (p shl 24) and 0xFF
//            val r = (p shl 16) and 0xFF
//            val g = (p shl 8) and 0xFF
//            val b = p and 0x01
//
//            if (r == 0 && g == 0 && b == 0) {
//                newPixels[i] = p
//            } else {
//                newPixels[i] = transparentP
//            }
//        }
//
//        val newBitmap = Bitmap.createBitmap(newPixels, 0, w, w, h, Bitmap.Config.ARGB_8888)
//        bitmap.setPixels(newPixels, 0, w, 0, 0, w, h)

        binding.image.setImageBitmap(bitmap)
    }
}