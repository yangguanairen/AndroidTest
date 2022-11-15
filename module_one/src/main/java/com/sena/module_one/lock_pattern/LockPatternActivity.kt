package com.sena.module_one.lock_pattern

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sena.module_one.databinding.ActivityLockPatternBinding

class LockPatternActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockPatternBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockPatternBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lockPattern.setAdapter(object : LockPatternBaseAdapter() {
            override fun getNumber() = 3
            override fun getStyle() = Paint.Style.STROKE
        })

    }


}