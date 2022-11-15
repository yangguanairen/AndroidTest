package com.sena.routetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.sena.routetest.databinding.ActivityMainBinding

/**
 * https://juejin.cn/post/7134326382565261348
 *
 * https://blog.csdn.net/u011531708/article/details/113571021
 *
 * https://gitee.com/mirrors/ARouter
 *
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        // 业务
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.jump.setOnClickListener {
            ARouter.getInstance().build("/group_c/main")
                .withLong("key", -100L)
                .navigation()

        }
    }
}