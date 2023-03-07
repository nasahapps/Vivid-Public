package com.nasahapps.vivid.ui.color_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.TextView
import com.nasahapps.vivid.R

/**
 * Created by Hakeem on 10/29/15.
 */
class BrandAdapter(context: Context, objects: List<String>) : ArrayAdapter<String>(context, 0, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        }

        val b = getItem(position)
        (view as? TextView)?.text = b

        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item,
                    parent, false)
        }

        val b = getItem(position)
        (view as? CheckedTextView)?.text = b

        return view
    }

}
