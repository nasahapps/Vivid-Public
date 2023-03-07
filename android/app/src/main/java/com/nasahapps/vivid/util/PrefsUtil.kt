package com.nasahapps.vivid.util

import androidx.core.content.edit
import com.nasahapps.vivid.VividApplication

/**
 * Created by Hakeem on 7/26/15.
 */
object PrefsUtil {

    val PREFS_FILE = "PrefsFile"
    val KEY_LAST_BRAND = "lastBrand"
    val KEY_COLOR_DB_VERSION = "dbVersion"
    val KEY_HSV_MIGRATION = "hsvMigration"

    private val prefs = VividApplication.appContext.getSharedPreferences(PREFS_FILE, 0)

    var lastBrand: String?
        get() = prefs.getString(KEY_LAST_BRAND, null)
        set(value) = prefs.edit { putString(KEY_LAST_BRAND, value) }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String, defValue: String?): String? {
        return prefs.getString(key, defValue)
    }

    fun putString(key: String, value: String?) {
        prefs.edit().putString(key, value).apply()
    }

    fun getInt(key: String, defValue: Int): Int {
        return prefs.getInt(key, defValue)
    }

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

}
