package com.nasahapps.vivid.ui.color_list

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.nasahapps.vivid.R
import com.nasahapps.vivid.ui.BaseFragment
import com.nasahapps.vivid.ui.color.ColorViewPagerFragment
import com.nasahapps.vivid.ui.favorites.FavoritesFragment
import com.nasahapps.vivid.ui.search.SearchFragment
import com.nasahapps.vivid.util.PrefsUtil
import com.nasahapps.vivid.util.dpToPixel
import com.nasahapps.vivid.util.getScreenWidth
import kotlinx.android.synthetic.main.fragment_color_picker.*

class ColorPickerFragment : BaseFragment(R.layout.fragment_color_picker) {

    private val prefs = PrefsUtil
    var lastBrandIndex = -1
    val viewModel: ColorListViewModel by viewModels()

    init {
        enterTransition = TransitionSet()
                .addTransition(Slide(Gravity.TOP)
                        .addTarget(R.id.appBarLayout)
                        .addTarget(R.id.statusBar))
                .addTransition(Slide(Gravity.BOTTOM)
                        .excludeTarget(R.id.appBarLayout, true)
                        .excludeTarget(R.id.statusBar, true))
        exitTransition = Fade()
        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                super.onMapSharedElements(names, sharedElements)
                mainActivity?.positionClicked?.let { position ->
                    if (position > -1) {
                        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
                        val transitionName = "color$position"
                        recyclerView?.findViewHolderForAdapterPosition(position)?.itemView?.let {
                            sharedElements?.put(transitionName, it)
                            if (names?.contains(transitionName) != true) {
                                names?.add(transitionName)
                            }
                            // Remove any transition names that weren't "appBar" or the value
                            // of the variable 'transitionName' above
                            names?.removeAll { it != "appBar" && it != transitionName }
                        }
                    }
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()

        statusBar?.setOnApplyWindowInsetsListener { v, insets ->
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                height = insets.systemWindowInsetTop
            }
            insets
        }
        recyclerView?.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }

        toolbar?.setOnMenuItemClickListener {
            when (it?.itemId) {
                R.id.favorites -> {
                    fragmentManager?.commit {
                        addToBackStack(null)
                        replace(Window.ID_ANDROID_CONTENT, FavoritesFragment())
                    }
                }
                R.id.menu_item_search_colors -> {
                    fragmentManager?.commit {
                        addToBackStack(null)
                        replace(Window.ID_ANDROID_CONTENT, SearchFragment())
                    }
                }
            }
            true
        }

        // Set number of columns
        val spanSize = getScreenWidth() / dpToPixel(100)
        recyclerView?.layoutManager = GridLayoutManager(activity, spanSize)

        viewModel.brands.observe(viewLifecycleOwner, Observer {
            it?.let { brands ->
                val lastPickedBrandIndex = it.indexOfFirst { it.name == prefs.lastBrand }
                activity?.let {
                    val spinnerAdapter = BrandAdapter(it, brands.map { it.name })
                    spinner?.adapter = spinnerAdapter
                    spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            if (position != lastBrandIndex) {
                                lastBrandIndex = position
                                val brand = parent?.adapter?.getItem(position) as? String
                                prefs.lastBrand = brand

                                brand?.let {
                                    viewModel.getColorsForBrand(it)
                                }
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
                    if (lastPickedBrandIndex > -1) {
                        spinner?.setSelection(lastPickedBrandIndex)
                    }
                }
            }
        })
        viewModel.colors.observe(viewLifecycleOwner, Observer {
            it?.let {
                val adapter = ColorAdapter(it) { color, v, position ->
                    mainActivity?.positionClicked = position
                    val fragment = ColorViewPagerFragment.newInstance(position, color.brand)
                    fragment.postponeEnterTransition()
                    fragmentManager?.commit {
                        addToBackStack(null)
                        setReorderingAllowed(true)
                        addSharedElement(v, v.transitionName)
                        addSharedElement(appBarLayout, appBarLayout.transitionName)
                        replace(Window.ID_ANDROID_CONTENT, fragment)
                    }
                }
                recyclerView?.adapter = adapter
                recyclerView?.doOnPreDraw { startPostponedEnterTransition() }
            }
        })

        viewModel.getBrands()
    }

}

