package com.nasahapps.vivid.util

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

fun Drawable.tintedDrawable(@ColorInt color: Int): Drawable {
    val newDrawable = mutate()
    newDrawable.setTint(color)
    return newDrawable
}

/**
 * Retrieves a color int from an attribute, e.g. R.attr.colorAccent
 */
@ColorInt
fun Context.getColorFromAttribute(@AttrRes res: Int): Int {
    return try {
        val tv = TypedValue()
        val ta = obtainStyledAttributes(tv.data, intArrayOf(res))
        val color = ta.getColor(0, 0)
        ta.recycle()
        color
    } catch (e: Exception) {
        logE("Error getting color from attribute", e)
        0
    }
}

fun Context.getColorStateListFromAttribute(@AttrRes res: Int): ColorStateList? {
    return try {
        val tv = TypedValue()
        val ta = obtainStyledAttributes(tv.data, intArrayOf(res))
        val list = ta.getColorStateList(0)
        ta.recycle()
        list
    } catch (e: Throwable) {
        logE("Error getting color state list from attribute", e)
        null
    }
}

/**
 * Checks if the device is at least a 7in tablet, or a smallest width of 600dp
 */
fun Fragment.isTablet() = resources.configuration.smallestScreenWidthDp >= 600

fun Context.isTablet() = resources.configuration.smallestScreenWidthDp >= 600

/**
 * Checks if the device is a large tablet (9+in), or a smallest width of 720dp
 */
fun Fragment.isLargeTablet() = resources.configuration.smallestScreenWidthDp >= 720

/**
 * Checks if the device is currently in portrait mode
 */
fun Fragment.isPortrait() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun Context.isPortrait() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun Context?.getScreenDimensions(): Point {
    this?.let {
        val display: Display
        if (this is Activity) {
            display = windowManager.defaultDisplay
        } else {
            display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        }
        val size = Point()
        display.getSize(size)
        return size
    }

    return Point()
}

fun Context?.getScreenWidth() = this?.getScreenDimensions()?.x ?: 0
fun Context?.getScreenHeight() = this?.getScreenDimensions()?.y ?: 0
fun Fragment?.getScreenWidth() = this?.context?.getScreenWidth() ?: 0
fun Fragment?.getScreenHeight() = this?.context?.getScreenHeight() ?: 0

/**
 * Returns an [ArrayList] containing all elements.
 */
fun <T> Array<out T>.toArrayList(): ArrayList<T> {
    return ArrayList(this.toList())
}

fun Context.dpToPixel(dp: Int) = (dp * resources.displayMetrics.density).toInt()
fun Fragment.dpToPixel(dp: Int) = (dp * resources.displayMetrics.density).toInt()

fun View.showKeyboard() {
    post {
        requestFocus()
        val imm = context.getSystemService(InputMethodManager::class.java)
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.hideKeyboard() {
    post {
        val imm = context.getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

fun View.showSnackbar(@StringRes res: Int,
                      config: (Snackbar.() -> Snackbar)? = null) = showSnackbar(context.getString(res), config)

fun View.showSnackbar(message: String,
                      config: (Snackbar.() -> Snackbar)? = null) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .apply { config?.let { config() } }
        .show()
}

suspend fun <T> doInBackground(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO, block)

fun JSONObject.optNullString(key: String): String? {
    val string = optString(key)
    if (string.isBlank()) {
        return null
    } else {
        return string
    }
}