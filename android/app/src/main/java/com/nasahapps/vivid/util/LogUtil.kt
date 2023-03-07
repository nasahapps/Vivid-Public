package com.nasahapps.vivid.util

import android.util.Log
import com.nasahapps.vivid.BuildConfig

/**
 * Created by Hakeem on 7/17/16.
 */

fun Any.logD(message: String) {
    if (BuildConfig.DEBUG)
        Log.d(javaClass.simpleName, message)
}

fun Any.logE(message: String, e: Throwable? = null) {
    if (e != null)
        Log.e(javaClass.simpleName, message, e)
    else
        Log.e(javaClass.simpleName, message)
}