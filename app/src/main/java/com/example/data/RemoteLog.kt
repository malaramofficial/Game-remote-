package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_logs")
data class RemoteLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val command: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS" // "SUCCESS", "PENDING", "FAILED"
)
