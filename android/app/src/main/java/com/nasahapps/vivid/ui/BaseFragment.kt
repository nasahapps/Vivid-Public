package com.nasahapps.vivid.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes) {

    protected val mainActivity: MainActivity?
        get() = activity as? MainActivity
    protected open val useDefaultTransitions = true

    init {
        retainInstance = true
        if (useDefaultTransitions) {
            enterTransition = FragmentTransition()
            exitTransition = FragmentTransition()
            returnTransition = FragmentTransition(reverse = true)
            reenterTransition = FragmentTransition(reverse = true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.requestApplyInsets()
    }

}