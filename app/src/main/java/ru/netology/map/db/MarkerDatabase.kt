package ru.netology.map.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.map.dao.MarkerDao
import ru.netology.map.entity.MarkerEntity

@Database(entities = [MarkerEntity::class], version = 1)
abstract class MarkerDatabase : RoomDatabase() {
    abstract fun markerDao(): MarkerDao
}