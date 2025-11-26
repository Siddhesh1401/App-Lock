package com.applock.secure.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locked_apps")
data class LockedApp(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val added: Long = System.currentTimeMillis()
)
