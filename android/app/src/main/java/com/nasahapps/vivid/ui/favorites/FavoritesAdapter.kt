package com.nasahapps.vivid.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nasahapps.vivid.R
import com.nasahapps.vivid.database.model.Color
import kotlinx.android.synthetic.main.list_favorite.view.*

/**
 * Created by Hakeem on 8/16/15.
 */
class FavoritesAdapter(val colors: List<Color>,
                       private val listener: (Color, View, Int) -> Unit) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    var transitionPosition = 0
    var transitionLoadedListener: (() -> Unit)? = null

    override fun getItemCount() = colors.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_favorite, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val color = colors[position]
        vh.itemView.color?.setBackgroundColor(android.graphics.Color.parseColor(color.hexCode))
        vh.itemView.colorTitle?.text = color.description?.trim()
        vh.itemView.colorBrand?.text = color.brand
        vh.itemView.color?.transitionName = "color$position"
        vh.itemView.setOnClickListener { listener(color, it, vh.adapterPosition) }

        if (position >= transitionPosition) {
            transitionLoadedListener?.invoke()
            transitionLoadedListener = null
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
}
