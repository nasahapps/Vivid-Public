package com.nasahapps.vivid.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brands")
data class Brand(@PrimaryKey var name: String)