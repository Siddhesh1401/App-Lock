package com.applock.secure.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intruder_photos")
data class IntruderPhoto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val photoPath: String,
    val timestamp: Long = System.currentTimeMillis()
)
