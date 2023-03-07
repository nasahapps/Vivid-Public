package com.nasahapps.vivid.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nasahapps.vivid.database.Database
import com.nasahapps.vivid.database.model.Color
import kotlinx.coroutines.launch

class SearchViewModel(app: Application) : AndroidViewModel(app) {

    private val db = Database.getInstance(app)
    val results = MutableLiveData<List<Color>>()

    fun search(query: String) {
        if (query.isBlank()) {
            results.value = emptyList()
        } else {
            viewModelScope.launch {
                val actualQuery = "%$query%"
                if (query.startsWith("#")) {
                    results.value = db.dao.searchColorsByHexCode(actualQuery)
                } else {
                    results.value = db.dao.searchColorsByDescription(actualQuery)
                }
            }
        }
    }

}