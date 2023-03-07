package com.nasahapps.vivid.ui.color

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nasahapps.vivid.database.Database
import com.nasahapps.vivid.database.model.Color
import kotlinx.coroutines.launch

class ColorDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val db = Database.getInstance(app)
    val colors = MutableLiveData<List<Color>>()

    fun getColorsForBrand(brand: String) {
        if (colors.value == null) {
            viewModelScope.launch {
                colors.value = db.dao.getColorsForBrand(brand)
            }
        }
    }

    fun setColorAsFavorite(color: Color, favorite: Boolean) {
        color.isFavorite = favorite
        viewModelScope.launch { db.dao.updateColor(color) }
    }

    fun getFavoriteColors() {
        viewModelScope.launch {
            colors.value = db.dao.getFavoriteColorsAsync()
        }
    }

    fun searchForColors(query: String) {
        if (query.isBlank()) {
            colors.value = emptyList()
        } else {
            viewModelScope.launch {
                val actualQuery = "%$query%"
                if (query.startsWith("#")) {
                    colors.value = db.dao.searchColorsByHexCode(actualQuery)
                } else {
                    colors.value = db.dao.searchColorsByDescription(actualQuery)
                }
            }
        }
    }

}