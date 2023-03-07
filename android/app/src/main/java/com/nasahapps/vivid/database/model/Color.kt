package com.nasahapps.vivid.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "colors")
data class Color(@PrimaryKey(autoGenerate = true) val id: Int,
                 var brand: String,
                 var hexCode: String? = null,
                 var description: String? = null,
                 var hue: Int = 0,
                 var saturation: Int = 0,
                 var value: Int = 0,
                 var isFavorite: Boolean = false)