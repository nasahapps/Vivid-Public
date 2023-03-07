package com.nasahapps.vivid

import android.app.Application

/**
 * Created by Hakeem on 8/15/15.
 */
class VividApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {
        lateinit var appContext: VividApplication
            private set
    }
}
