package com.nasahapps.vivid.ui.color_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nasahapps.vivid.database.Database
import com.nasahapps.vivid.database.model.Brand
import com.nasahapps.vivid.database.model.Color
import kotlinx.coroutines.launch

class ColorListViewModel(app: Application) : AndroidViewModel(app) {

    private val db = Database.getInstance(app)
    val brands = MutableLiveData<List<Brand>>()
    val colors = MutableLiveData<List<Color>>()

    fun getBrands() {
        if (brands.value == null) {
            viewModelScope.launch {
                brands.value = db.dao.getBrands()
            }
        }
    }

    fun getColorsForBrand(brand: String) {
        viewModelScope.launch {
            colors.value = db.dao.getColorsForBrand(brand)
        }
    }

}