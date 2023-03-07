/*
 * Created by hhasan on 5/22/19.
 * Copyright (c) 2019 Goldstar Events, Inc. All rights reserved.
 */

package com.nasahapps.vivid.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.transition.TransitionValues
import androidx.transition.Visibility

/**
 * Created by hhasan on 6/3/19.
 */
class FragmentTransition(val reverse: Boolean = false) : Visibility() {

    override fun onAppear(sceneRoot: ViewGroup?, view: View?, startValues: TransitionValues?,
                          endValues: TransitionValues?): Animator? {
        if (endValues == null && view == null) {
            return null
        }

        val startScale = if (reverse) 0.975f else 1.125f
        val endScale = 1f
        val startAlpha = 0f
        val endAlpha = 1f

        return createAnimator(view!!, startScale, endScale, startAlpha, endAlpha)
    }

    override fun onDisappear(sceneRoot: ViewGroup?, view: View?, startValues: TransitionValues?,
                             endValues: TransitionValues?): Animator? {
        if (startValues == null && view == null) {
            return null
        }

        val startScale = 1f
        val endScale = if (reverse) 1.125f else 0.975f
        val startAlpha = 1f
        val endAlpha = 0f

        return createAnimator(view!!, startScale, endScale, startAlpha, endAlpha)
    }

    private fun createAnimator(view: View, startScale: Float, endScale: Float,
                               startAlpha: Float, endAlpha: Float): Animator {
        val scaleAnim = ObjectAnimator.ofPropertyValuesHolder(view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, startScale, endScale),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, startScale, endScale)).apply {
            interpolator = DecelerateInterpolator(2.5f)
            duration = 220
        }
        val alphaAnim = ObjectAnimator.ofPropertyValuesHolder(view,
            PropertyValuesHolder.ofFloat(View.ALPHA, startAlpha, endAlpha)).apply {
            interpolator = DecelerateInterpolator(1.5f)
            duration = 220
        }
        return AnimatorSet().apply {
            playTogether(scaleAnim, alphaAnim)
        }
    }

}