package com.nasahapps.vivid.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nasahapps.vivid.database.Database
import com.nasahapps.vivid.database.model.Color
import kotlinx.coroutines.launch

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val db = Database.getInstance(app)
    val favorites = db.dao.getFavoriteColors()

    fun setColorAsFavorite(color: Color, favorite: Boolean): Color {
        color.isFavorite = favorite
        viewModelScope.launch { db.dao.updateColor(color) }
        return color
    }

}