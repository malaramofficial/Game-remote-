package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DriveRemoteDao {

    @Query("SELECT * FROM vehicles ORDER BY id ASC")
    fun getAllVehicles(): Flow<List<VehicleDevice>>

    @Query("SELECT * FROM vehicles WHERE isSelected = 1 LIMIT 1")
    fun getSelectedVehicle(): Flow<VehicleDevice?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleDevice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<VehicleDevice>)

    @Update
    suspend fun updateVehicle(vehicle: VehicleDevice)

    @Query("UPDATE vehicles SET isSelected = 0")
    suspend fun clearSelection()

    @Query("UPDATE vehicles SET isSelected = 1 WHERE id = :id")
    suspend fun setSelectedVehicle(id: Long)

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteVehicle(id: Long)

    @Query("SELECT * FROM remote_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<RemoteLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RemoteLog): Long

    @Query("DELETE FROM remote_logs")
    suspend fun clearAllLogs()
}
