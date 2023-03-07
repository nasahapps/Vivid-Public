package com.nasahapps.vivid.ui.start

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.nasahapps.vivid.R
import com.nasahapps.vivid.ui.BaseFragment
import com.nasahapps.vivid.ui.color_list.ColorPickerFragment
import com.nasahapps.vivid.util.logD
import kotlinx.android.synthetic.main.fragment_start.*

/**
 * Created by Hakeem on 12/6/15.
 *
 * Check if all brands in DB. If not, save them.
 * Check if all colors in DB. If not, save them.
 * Migrate already-existing data (mainly which colors are favorited)
 */
class StartFragment : BaseFragment(R.layout.fragment_start) {

    val viewModel: StartViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setOnApplyWindowInsetsListener { v, insets ->
            insets
        }

        viewModel.loading.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (viewModel.loadingCount.value == null) {
                    progressBar?.isVisible = true
                    progressBar?.isIndeterminate = true
                    loadingText?.isVisible = true
                }
            }
        })
        viewModel.loadingCount.observe(viewLifecycleOwner, Observer {
            it?.let {
                logD("Loading color ${it.first} of ${it.second}")
                progressBar?.isVisible = true
                progressBar?.isIndeterminate = false
                progressBar?.max = it.second
                progressBar?.progress = it.first
                loadingText?.isVisible = true
                animateProgressBarColor(it.first.toFloat() / it.second.toFloat())
            }
        })
        viewModel.finished.observe(viewLifecycleOwner, Observer {
            it?.let {
                fragmentManager?.commit {
                    setReorderingAllowed(true)
                    replace(Window.ID_ANDROID_CONTENT, ColorPickerFragment())
                }
            }
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {
            it?.let {
                AlertDialog.Builder(requireActivity())
                        .setTitle("Error starting app")
                        .setMessage("There was an error in setting up the colors for the app, try again later.")
                        .setPositiveButton("Ok", null)
                        .setOnDismissListener { activity?.finish() }
                        .show()
            }
        })
    }

    fun animateProgressBarColor(percentage: Float) {
        val hex = when (percentage) {
            in 0f..0.14f -> "#ef4339"
            in 0.14f..0.28f -> "#f8981e"
            in 0.28f..0.43f -> "#f6e838"
            in 0.43f..0.57f -> "#49af4f"
            in 0.57f..0.71f -> "#458fcd"
            in 0.71f..0.86f -> "#4155a4"
            else -> "#913d97"
        }
        val newColor = Color.parseColor(hex)
        val oldColor = progressBar?.progressTintList?.defaultColor ?: 0
        val anim = ValueAnimator.ofObject(ArgbEvaluator(), oldColor, newColor)
        anim.addUpdateListener {
            progressBar?.progressTintList = ColorStateList.valueOf(it.animatedValue as Int)
        }
        anim.start()
    }

}
