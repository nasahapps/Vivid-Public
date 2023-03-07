package com.nasahapps.vivid.ui.color

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.ChangeClipBounds
import androidx.transition.ChangeTransform
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionSet
import androidx.viewpager2.widget.ViewPager2
import com.nasahapps.vivid.R
import com.nasahapps.vivid.database.model.Color
import com.nasahapps.vivid.ui.BaseFragment
import com.nasahapps.vivid.ui.MainActivity
import com.nasahapps.vivid.util.logD
import com.nasahapps.vivid.util.showSnackbar
import com.nasahapps.vivid.util.tintedDrawable
import kotlinx.android.synthetic.main.fragment_color_view_pager.*

/**
 * Created by Hakeem on 12/7/13.
 */
class ColorViewPagerFragment : BaseFragment(R.layout.fragment_color_view_pager) {

    companion object {
        val EXTRA_POSITION = "position"
        val EXTRA_BRAND = "brand"
        val EXTRA_FROM_FAVORITES = "fromFavorites"
        val EXTRA_QUERY = "query"

        fun newInstance(position: Int, brand: String): ColorViewPagerFragment {
            val args = Bundle()
            args.putInt(EXTRA_POSITION, position)
            args.putString(EXTRA_BRAND, brand)

            return newInstance(args)
        }

        fun newInstanceFromFavorites(position: Int): ColorViewPagerFragment {
            val args = Bundle()
            args.putInt(EXTRA_POSITION, position)
            args.putBoolean(EXTRA_FROM_FAVORITES, true)

            return newInstance(args)
        }

        fun newInstanceFromSearch(position: Int, query: String): ColorViewPagerFragment {
            val args = Bundle()
            args.putInt(EXTRA_POSITION, position)
            args.putString(EXTRA_QUERY, query)

            return newInstance(args)
        }

        private fun newInstance(args: Bundle) = ColorViewPagerFragment().apply {
            arguments = args
        }
    }

    internal var position = -1
    var isFullScreen: Boolean = false
    val viewModel: ColorDetailViewModel by viewModels()
    override val useDefaultTransitions = false

    init {
        sharedElementEnterTransition = TransitionSet()
                .addTransition(TransitionSet()
                        .addTransition(ChangeClipBounds())
                        .addTransition(ChangeBounds())
                        .addTransition(ChangeTransform().apply { reparentWithOverlay = false })
                        .excludeTarget("appBar", true))
                .addTransition(AutoTransition()
                        .addTarget("appBar"))
                .setInterpolator(FastOutSlowInInterpolator())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar?.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar?.setNavigationOnClickListener { activity?.onBackPressed() }
        toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem?.itemId) {
                R.id.menu_item_favorite -> {
                    viewPager?.let {
                        if (it.currentItem >= 0) {
                            viewModel.colors.value?.getOrNull(it.currentItem)?.let { color ->
                                if (color.isFavorite) {
                                    // Unfavorite
                                    viewModel.setColorAsFavorite(color, false)
                                    menuItem.setTitle(R.string.add_favorite)
                                    menuItem.icon = context?.getDrawable(R.drawable.ic_not_favorite)
                                            ?.tintedDrawable(requireContext().getColor(R.color.dark_icon))
                                    view.showSnackbar(R.string.remove_favorites_toast)
                                } else {
                                    // Make favorite
                                    viewModel.setColorAsFavorite(color, true)
                                    menuItem.setTitle(R.string.remove_favorite)
                                    menuItem.icon = context?.getDrawable(R.drawable.ic_favorite)
                                            ?.tintedDrawable(requireContext().getColor(R.color.dark_icon))
                                    view.showSnackbar(R.string.added_to_favorites)
                                }
                            }
                        }
                    }
                }
                R.id.menu_item_web_search -> {
                    viewPager?.let {
                        if (it.currentItem >= 0) {
                            viewModel.colors.value?.getOrNull(it.currentItem)?.let { color ->
                                val query = "${color.brand} ${color.description}"
                                val i = Intent(Intent.ACTION_WEB_SEARCH)
                                i.putExtra(SearchManager.QUERY, query)
                                if (i.resolveActivity(requireContext().packageManager) != null)
                                    startActivity(i)
                                else
                                    view.showSnackbar(R.string.no_search_app_installed)
                            }
                        }
                    }
                }
            }
            true
        }

        if (position == -1) {
            position = requireArguments().getInt(EXTRA_POSITION)
        }
        viewPager?.transitionName = "color$position"

        viewModel.colors.observe(viewLifecycleOwner, Observer {
            it?.let { colors ->
                viewPager?.adapter = ColorPagerAdapter(colors)
                viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        this@ColorViewPagerFragment.position = position
                        toolbar?.title = colors[position].description
                        invalidateMenu()
                        mainActivity?.positionClicked = position
                    }
                })

                // Go to the page that the user selected
                viewPager?.setCurrentItem(position, false)
                toolbar?.title = colors[position].description
                invalidateMenu()

                viewPager?.doOnPreDraw { startPostponedEnterTransition() }
            }
        })

        val brand = requireArguments().getString(EXTRA_BRAND)
        val query = requireArguments().getString(EXTRA_QUERY)
        if (brand != null) {
            viewModel.getColorsForBrand(brand)
        } else if (requireArguments().getBoolean(EXTRA_FROM_FAVORITES)) {
            viewModel.getFavoriteColors()
        } else if (query != null) {
            viewModel.searchForColors(query)
        }
    }

    private fun invalidateMenu() {
        viewModel.colors.value?.let {
            if (viewPager != null) {
                val favorite = toolbar?.menu?.findItem(R.id.menu_item_favorite)
                if (it.getOrNull(viewPager.currentItem)?.isFavorite == true) {
                    favorite?.setTitle(R.string.remove_favorite)
                    favorite?.setIcon(R.drawable.ic_favorite)
                } else {
                    favorite?.setTitle(R.string.add_favorite)
                    favorite?.setIcon(R.drawable.ic_not_favorite)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        setSystemBarsVisible(true)
    }

    fun setSystemBarsVisible(visible: Boolean) {
        if (visible) {
            activity?.window?.decorView?.systemUiVisibility = MainActivity.DEFAULT_WINDOW_FLAGS

            isFullScreen = false
            appBarLayout?.animate()?.translationY(0f)?.interpolator = LinearOutSlowInInterpolator()
        } else {
            activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE)

            isFullScreen = true
            appBarLayout?.animate()?.translationY((-appBarLayout.height).toFloat())
                    ?.interpolator = FastOutSlowInInterpolator()
        }
    }

    inner class ColorPagerAdapter(val colors: List<Color>) : RecyclerView.Adapter<ColorPagerAdapter.ViewHolder>() {

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(View(parent.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
            })
        }

        override fun getItemCount() = colors.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val color = colors[position]
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor(color.hexCode))

            // Sets it so when user clicks on screen, it'll fade the text back in if the text was not
            // visible, and it'll fade the text out if the text was visible
            // Currently does not correctly fade text in, it just pops up
            holder.itemView.setOnClickListener {
                // Toggle system bars
                setSystemBarsVisible(isFullScreen)
            }
        }
    }
}
