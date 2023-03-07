package com.nasahapps.vivid.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nasahapps.vivid.database.model.Brand
import com.nasahapps.vivid.database.model.Color

@Dao
interface VividDao {

    // For brands

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrands(brands: List<Brand>)

    @Query("SELECT * FROM brands ORDER BY name ASC")
    suspend fun getBrands(): List<Brand>

    // For colors

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColors(colors: List<Color>)

    @Update
    suspend fun updateColor(color: Color)

    @Query("SELECT * FROM colors WHERE brand = :brand ORDER BY hue, saturation, value ASC")
    suspend fun getColorsForBrand(brand: String): List<Color>

    @Query("SELECT * FROM colors WHERE id = :id")
    suspend fun getColorForId(id: Int): List<Color>

    @Query("SELECT * FROM colors WHERE isFavorite == 1")
    fun getFavoriteColors(): LiveData<List<Color>>

    @Query("SELECT * FROM colors WHERE isFavorite == 1")
    suspend fun getFavoriteColorsAsync(): List<Color>

    @Query("SELECT count(*) FROM colors")
    suspend fun getColorCount(): Int

    @Query("SELECT * FROM colors WHERE hexCode LIKE :hexCode COLLATE NOCASE ORDER BY hue, saturation, value ASC")
    suspend fun searchColorsByHexCode(hexCode: String): List<Color>

    @Query("SELECT * FROM colors WHERE description LIKE :description COLLATE NOCASE ORDER BY hue, saturation, value ASC")
    suspend fun searchColorsByDescription(description: String): List<Color>

}