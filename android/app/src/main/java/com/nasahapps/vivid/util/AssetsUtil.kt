package com.nasahapps.vivid.util

import android.content.Context
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by hhasan on 6/21/16.
 */

object AssetsUtil {

    @Throws(IOException::class)
    fun getJsonFromFile(c: Context, filename: String): String {
        c.assets.open(filename).use {
            val size = it.available()
            val buffer = ByteArray(size)
            it.read(buffer)
            return String(buffer, Charset.forName("UTF-8"))
        }
    }

}
