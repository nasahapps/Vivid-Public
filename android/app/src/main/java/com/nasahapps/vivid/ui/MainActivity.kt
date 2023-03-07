package com.nasahapps.vivid.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.nasahapps.vivid.ui.start.StartFragment

/**
 * Created by Hakeem on 5/7/16.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        val DEFAULT_WINDOW_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION else 0
    }

    private val BUNDLE_POSITION_CLICKED = "positionClicked"
    var positionClicked = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            positionClicked = it.getInt(BUNDLE_POSITION_CLICKED, -1)
        }

        window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        window.decorView.systemUiVisibility = DEFAULT_WINDOW_FLAGS

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(Window.ID_ANDROID_CONTENT, StartFragment())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_POSITION_CLICKED, positionClicked)
    }
}
