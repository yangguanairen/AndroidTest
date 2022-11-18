package com.sena.module_two

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sena.module_two.databinding.ActivityModuleTwoBinding
import com.sena.module_two.mediaRecord.MediaRecordActivity
import java.util.AbstractMap
import java.util.HashMap

class ModuleTwoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModuleTwoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModuleTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ModuleTwoAdapter(this, dataList)
        binding.recyclerView.adapter = adapter
    }

    private companion object {
        val dataList = arrayListOf<Map.Entry<String, Class<out AppCompatActivity>>>().apply {
            add(AbstractMap.SimpleEntry("录音功能测试", MediaRecordActivity::class.java))
        }
    }
}