package com.nasahapps.vivid.ui.favorites

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nasahapps.vivid.R
import com.nasahapps.vivid.ui.BaseFragment
import com.nasahapps.vivid.ui.color.ColorViewPagerFragment
import com.nasahapps.vivid.util.getScreenWidth
import com.nasahapps.vivid.util.isPortrait
import com.nasahapps.vivid.util.isTablet
import com.nasahapps.vivid.util.showSnackbar
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : BaseFragment(R.layout.fragment_favorites) {

    val viewModel: FavoritesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar?.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar?.setNavigationOnClickListener { activity?.onBackPressed() }

        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.setOnApplyWindowInsetsListener { v, insets ->
            if (!isTablet()) {
                v.updatePadding(bottom = insets.systemWindowInsetBottom)
            }
            insets
        }
        ItemTouchHelper(SwipeCallback()).attachToRecyclerView(recyclerView)
        if (isTablet() && !isPortrait()) {
            val padding = getScreenWidth() / 6
            recyclerView?.apply {
                setPadding(padding, paddingTop, padding, paddingBottom)
            }
        }

        viewModel.favorites.observe(viewLifecycleOwner, Observer {
            it?.let {
                val adapter = FavoritesAdapter(it) { color, v, position ->
                    fragmentManager?.commit {
                        addToBackStack(null)
                        replace(Window.ID_ANDROID_CONTENT, ColorViewPagerFragment.newInstanceFromFavorites(position))
                    }
                }
                recyclerView?.adapter = adapter
                emptyView?.isVisible = it.isEmpty()
            }
        })
    }

    inner class SwipeCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val index = viewHolder.adapterPosition
            viewModel.favorites.value?.getOrNull(index)?.let { color ->
                val removedColor = viewModel.setColorAsFavorite(color, false)
                view?.showSnackbar(R.string.remove_favorites_toast) {
                    setAction(R.string.undo) {
                        viewModel.setColorAsFavorite(removedColor, true)
                    }
                }
            }
        }
    }
}
