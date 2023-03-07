package com.nasahapps.vivid.ui.start

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nasahapps.vivid.database.Database
import com.nasahapps.vivid.database.model.Brand
import com.nasahapps.vivid.database.model.Color
import com.nasahapps.vivid.util.doInBackground
import com.nasahapps.vivid.util.logE
import com.nasahapps.vivid.util.optNullString
import kotlinx.coroutines.launch
import org.json.JSONObject

class StartViewModel(app: Application) : AndroidViewModel(app) {

    private val db = Database.getInstance(app)
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Throwable>()
    // For showing a loading indicator of the progress, current | max
    val loadingCount = MutableLiveData<Pair<Int, Int>>()
    val finished = MutableLiveData<Boolean>()

    init {
        // Check if anything important needs to be done
        viewModelScope.launch {
            try {
                var colorCount = db.dao.getColorCount()
                if (colorCount == 0) {
                    // Database empty, let's populate it
                    loading.value = true
                    doInBackground {
                        val colorsJson = getApplication<Application>().assets
                                .open("realm_colors_db_v1.json")
                                .use { String(it.readBytes()) }
                        val json = JSONObject(colorsJson)

                        // Save the brands
                        val brands = json.keys().asSequence().map { Brand(it) }.toList()
                        db.dao.insertBrands(brands)

                        // Then save the colors
                        var count = 0
                        val colorsToAdd = mutableListOf<Color>()
                        // First get the total color count
                        json.keys().forEach {
                            val colorArray = json.getJSONArray(it)
                            colorCount += colorArray.length()
                        }
                        json.keys().forEach {
                            val colorArray = json.getJSONArray(it)
                            for (i in 0 until colorArray.length()) {
                                val c = colorArray.getJSONObject(i)
                                val brand = c.getString("brand")
                                val description = c.optNullString("description")
                                val hexCode = c.optNullString("hexCode")
                                val hue = c.optInt("sortHue")
                                val saturation = c.optInt("sortSaturation")
                                val value = c.optInt("sortValue")
                                val color = Color(0, brand, hexCode, description, hue, saturation, value)
                                colorsToAdd.add(color)
                                count++
                                loadingCount.postValue(Pair(count, colorCount))
                            }
                        }
                        db.dao.insertColors(colorsToAdd)
                    }
                }

                finished.value = true
            } catch (e: Throwable) {
                logE("Error initializing database", e)
                error.value = e
            }
        }
    }

}