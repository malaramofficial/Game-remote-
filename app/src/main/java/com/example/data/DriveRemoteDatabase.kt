package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [VehicleDevice::class, RemoteLog::class],
    version = 1,
    exportSchema = false
)
abstract class DriveRemoteDatabase : RoomDatabase() {

    abstract fun driveRemoteDao(): DriveRemoteDao

    companion object {
        @Volatile
        private var INSTANCE: DriveRemoteDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DriveRemoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DriveRemoteDatabase::class.java,
                    "driveremote_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.driveRemoteDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: DriveRemoteDao) {
                val defaultVehicles = listOf(
                    VehicleDevice(
                        name = "Malaram HyperDrive GT",
                        model = "Cyber Edition Sport",
                        deviceType = "CAR",
                        connectionType = "BLE",
                        targetAddress = "AA:BB:CC:11:22:33",
                        port = 8888,
                        isSelected = true,
                        batteryPct = 88,
                        rangeKm = 425
                    ),
                    VehicleDevice(
                        name = "CyberRover 4x4 ESP32",
                        model = "Robotic Chassis V2",
                        deviceType = "RC_ROVER",
                        connectionType = "WIFI_UDP",
                        targetAddress = "192.168.4.1",
                        port = 8888,
                        isSelected = false,
                        batteryPct = 95,
                        rangeKm = 12
                    ),
                    VehicleDevice(
                        name = "Smart Workstation PC",
                        model = "Drive & Media Deck",
                        deviceType = "PC_MEDIA",
                        connectionType = "WIFI_UDP",
                        targetAddress = "192.168.1.105",
                        port = 9000,
                        isSelected = false,
                        batteryPct = 100,
                        rangeKm = 0
                    )
                )
                dao.insertVehicles(defaultVehicles)
                dao.insertLog(
                    RemoteLog(
                        command = "System Initialized",
                        details = "DriveRemote online. Paired with default devices."
                    )
                )
            }
        }
    }
}
