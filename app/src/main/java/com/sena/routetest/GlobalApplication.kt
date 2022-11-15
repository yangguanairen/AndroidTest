package com.sena.routetest

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter


/**
 * FileName: GlobalApplication
 * Author: JiaoCan
 * Date: 2022/9/8 17:35
 */

class GlobalApplication: Application() {


    override fun onCreate() {
        super.onCreate()
        ARouter.openLog()
        ARouter.openDebug()
        ARouter.init(this)

    }

}

