package com.sena.wk_a

import android.graphics.BitmapFactory
import android.graphics.NinePatch
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.system.Os
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.sena.wk_a.databinding.ActivityMainABinding
import java.io.File

@Route(path = "/group_a/main")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainABinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainABinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.layout.background = Drawable.createFromStream(assets.open("left_bubble.png"), null)
//        test()
//
//        test(binding.message1, "left_bubble.png")
//        test(binding.message2, "right_bubble.png")

        test3()
            test2()

    }

    private fun test3() {
        try {
            val dir = File(Environment.getExternalStorageDirectory(), "AAA")
            val file = File(dir, "app-release.apk")
            val stat = Os.stat(file.path)
            println("stat:" + Gson().toJson(stat))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun test2() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val dataList = arrayListOf<String>().apply {
            for (i in 0 until 20) {
                val sb = StringBuilder()
                for (i in 0 until (0..100).random()) {
                    sb.append((0..9).random().toString())
                }
                this.add(sb.toString())
            }
        }
        val adapter = TestAdapter(this, dataList)
        binding.recyclerView.adapter = adapter
    }

    private fun test(view: View, fileName: String) {
        try {
            val bitmap = BitmapFactory.decodeStream(assets.open(fileName))
            val chunk = bitmap.ninePatchChunk
            if (NinePatch.isNinePatchChunk(chunk)) {
                val patchy = NinePatchDrawable(view.resources, bitmap, chunk, Rect(), null)
                view.background = patchy
            } else {
                view.background = Drawable.createFromStream(assets.open(fileName), null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 加载默认气泡
        }
    }
}