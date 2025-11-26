package com.applock.secure.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.applock.secure.data.dao.IntruderPhotoDao
import com.applock.secure.data.dao.LockedAppDao
import com.applock.secure.data.entity.IntruderPhoto
import com.applock.secure.data.entity.LockedApp

@Database(
    entities = [LockedApp::class, IntruderPhoto::class],
    version = 1,
    exportSchema = false
)
abstract class AppLockDatabase : RoomDatabase() {
    abstract fun lockedAppDao(): LockedAppDao
    abstract fun intruderPhotoDao(): IntruderPhotoDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppLockDatabase? = null
        
        fun getDatabase(context: Context): AppLockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppLockDatabase::class.java,
                    "applock_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
