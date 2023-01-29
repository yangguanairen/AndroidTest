package com.sena.module_one

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sena.module_one.databinding.ActivityModuleOneBinding
import com.sena.module_one.lock_pattern.LockPatternActivity
import com.sena.module_one.test.TestActivity
import com.sena.module_one.transparent_bitmap.TransparentBitmapActivity
import java.util.AbstractMap

class ModuleOneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModuleOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModuleOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ModuleOneAdapter(this, list)
        binding.recyclerView.adapter = adapter
    }

    private companion object {
        val list = arrayListOf<Map.Entry<String, Class<out AppCompatActivity>>>().apply {
            add(AbstractMap.SimpleEntry("01_九宫格解锁", LockPatternActivity::class.java))
            add(AbstractMap.SimpleEntry("01_透明底图片", TransparentBitmapActivity::class.java))
            add(AbstractMap.SimpleEntry("03_应用内内存不足测试", TestActivity::class.java))
        }
    }

}