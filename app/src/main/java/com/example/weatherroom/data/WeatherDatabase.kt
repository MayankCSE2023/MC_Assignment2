package com.example.weatherroom.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherroom.data.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}
