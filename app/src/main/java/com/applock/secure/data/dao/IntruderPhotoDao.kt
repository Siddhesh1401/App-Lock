package com.applock.secure.data.dao

import androidx.room.*
import com.applock.secure.data.entity.IntruderPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface IntruderPhotoDao {
    @Query("SELECT * FROM intruder_photos ORDER BY timestamp DESC")
    fun getAllIntruderPhotos(): Flow<List<IntruderPhoto>>
    
    @Insert
    suspend fun insertIntruderPhoto(photo: IntruderPhoto)
    
    @Delete
    suspend fun deleteIntruderPhoto(photo: IntruderPhoto)
    
    @Query("DELETE FROM intruder_photos")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM intruder_photos")
    fun getIntruderCount(): Flow<Int>
}
