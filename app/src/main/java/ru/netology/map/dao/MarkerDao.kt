package ru.netology.map.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.netology.map.entity.MarkerEntity

@Dao
interface MarkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marker: MarkerEntity)
    
    @Update
    suspend fun update(marker: MarkerEntity)

    @Query("SELECT * FROM marker_table")
    suspend fun getAllMarkers(): List<MarkerEntity>

    @Query("SELECT * FROM marker_table WHERE id = :id")
    suspend fun getMarkerById(id: Long): MarkerEntity?

    @Query("DELETE FROM marker_table WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM marker_table")
    suspend fun deleteAllMarkers(): Int
}