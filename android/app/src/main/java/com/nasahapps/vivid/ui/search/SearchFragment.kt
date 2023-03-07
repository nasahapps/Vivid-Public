package com.nasahapps.vivid.ui.search

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import androidx.appcompat.widget.SearchView
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nasahapps.vivid.R
import com.nasahapps.vivid.ui.BaseFragment
import com.nasahapps.vivid.ui.color.ColorViewPagerFragment
import com.nasahapps.vivid.util.dpToPixel
import com.nasahapps.vivid.util.getScreenWidth
import com.nasahapps.vivid.util.hideKeyboard
import com.nasahapps.vivid.util.isPortrait
import com.nasahapps.vivid.util.isTablet
import com.nasahapps.vivid.util.showKeyboard
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(R.layout.fragment_search) {

    val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar?.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar?.setNavigationOnClickListener { activity?.onBackPressed() }

        if (isTablet() && !isPortrait()) {
            recyclerView?.clipChildren = false
            recyclerView?.clipToPadding = false
            val dp64 = dpToPixel(64)
            recyclerView?.setPadding(0, dp64, 0, dp64)
        }

        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
        if (isTablet() && !isPortrait()) {
            val padding = getScreenWidth() / 6
            recyclerView?.apply {
                setPadding(padding, paddingTop, padding, paddingBottom)
            }
        }

        recyclerView?.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                // Hide the keyboard
                rv.hideKeyboard()
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }
        })

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.hideKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.search(newText)
                return true
            }
        })

        searchView?.showKeyboard()

        viewModel.results.observe(viewLifecycleOwner, Observer {
            it?.let {
                val adapter = SearchAdapter(it) { color, v, position ->
                    fragmentManager?.commit {
                        addToBackStack(null)
                        replace(Window.ID_ANDROID_CONTENT, ColorViewPagerFragment.newInstanceFromSearch(position, searchView?.query.toString()))
                    }
                }
                recyclerView?.adapter = adapter
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view?.hideKeyboard()
    }

}