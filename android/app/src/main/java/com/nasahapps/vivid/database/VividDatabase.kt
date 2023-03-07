package com.nasahapps.vivid.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nasahapps.vivid.database.model.Brand
import com.nasahapps.vivid.database.model.Color

@Database(
        entities = [Brand::class, Color::class],
    version = 1
)
abstract class VividDatabase : RoomDatabase() {
    abstract fun dao(): VividDao
}