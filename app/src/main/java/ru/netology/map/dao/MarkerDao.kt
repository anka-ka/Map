package ru.netology.map.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.netology.map.dto.Marker
import ru.netology.map.entity.MarkerEntity

@Dao
interface MarkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marker: MarkerEntity)
    
    @Update
    suspend fun update(marker: MarkerEntity)

//    @Query("SELECT * FROM marker_table")
//    suspend fun getAllMarkers(): List<MarkerEntity>

    @Query("SELECT * FROM marker_table WHERE id = :id")
    suspend fun getMarkerById(id: Long): MarkerEntity?

    @Query("DELETE FROM marker_table WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM marker_table")
    suspend fun deleteAllMarkers(): Int

    @Query("SELECT * FROM marker_table ORDER BY `order` ASC")
    fun getAllMarkers(): Flow<List<MarkerEntity>>

    @Query("UPDATE marker_table SET `order` = :newOrder WHERE id = :markerId")
    suspend fun updateOrder(markerId: Long, newOrder: Int)
}