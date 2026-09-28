package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleDevice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val model: String,
    val deviceType: String, // "CAR", "RC_ROVER", "DRONE", "PC_MEDIA"
    val connectionType: String, // "BLE", "WIFI_UDP", "CLOUD"
    val targetAddress: String, // IP or MAC
    val port: Int = 8888,
    val isSelected: Boolean = false,
    val batteryPct: Int = 86,
    val rangeKm: Int = 412,
    val lastConnected: Long = System.currentTimeMillis()
)
