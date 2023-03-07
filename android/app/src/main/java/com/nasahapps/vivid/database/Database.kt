package com.nasahapps.vivid.database

import android.content.Context
import androidx.room.Room

class Database private constructor(context: Context) {

    companion object {
        private lateinit var instance: Database

        fun getInstance(context: Context): Database {
            if (!this::instance.isInitialized) {
                instance = Database(context.applicationContext)
            }
            return instance
        }
    }

    private val db = Room.databaseBuilder(context, VividDatabase::class.java, "app.db").build()
    val dao = db.dao()

}