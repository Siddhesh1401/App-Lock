package com.applock.secure.data.dao

import androidx.room.*
import com.applock.secure.data.entity.LockedApp
import kotlinx.coroutines.flow.Flow

@Dao
interface LockedAppDao {
    @Query("SELECT * FROM locked_apps ORDER BY appName ASC")
    fun getAllLockedApps(): Flow<List<LockedApp>>
    
    @Query("SELECT * FROM locked_apps WHERE packageName = :packageName")
    suspend fun getLockedApp(packageName: String): LockedApp?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLockedApp(app: LockedApp)
    
    @Delete
    suspend fun deleteLockedApp(app: LockedApp)
    
    @Query("DELETE FROM locked_apps WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)
    
    @Query("SELECT COUNT(*) FROM locked_apps")
    fun getLockedAppsCount(): Flow<Int>
}
