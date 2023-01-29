package com.sena.module_one.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sena.module_one.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
    }

    private var count = 1

    private val fragmentManager = supportFragmentManager
    private var lastTag = 1

    private fun initView() {

        'a'.code

        val baseFragment = TestFragment()
        fragmentManager.beginTransaction().add(baseFragment, lastTag.toString()).show(baseFragment).commit()

        binding.distribution.setOnClickListener {
            val byteArray = ByteArray(1024 * 1024 * 10 * count++)
            val runtime = Runtime.getRuntime()
            val maxMemory = runtime.maxMemory()
            val usedMemory = runtime.totalMemory() - runtime.freeMemory()
            Log.e("test", "maxMemory: $maxMemory, usedMemory: $usedMemory")
        }

        binding.jump.setOnClickListener {
//            startActivity(Intent(this, TestActivity::class.java))
            val lastFragment = fragmentManager.findFragmentByTag(lastTag.toString())
            lastTag++
            val newFragment = TestFragment()
            fragmentManager.beginTransaction().hide(lastFragment!!).add(newFragment, lastTag.toString()).show(newFragment).commit()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy: ${this.hashCode()}")
    }
}