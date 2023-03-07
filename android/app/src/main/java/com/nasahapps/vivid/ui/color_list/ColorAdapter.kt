package com.nasahapps.vivid.ui.color_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nasahapps.vivid.R
import com.nasahapps.vivid.database.model.Color

/**
 * Created by Hakeem on 8/15/15.
 */
class ColorAdapter(val colors: List<Color>,
                   private var listener: (Color, View, Int) -> Unit) : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    override fun getItemCount() = colors.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_color, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = colors[position]
        val hexCode = color.hexCode
        holder.itemView.setBackgroundColor(android.graphics.Color.parseColor(hexCode))
        holder.itemView.transitionName = "color$position"
        holder.itemView.setOnClickListener { listener(color, it, holder.adapterPosition) }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
}
