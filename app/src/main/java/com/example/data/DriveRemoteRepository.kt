package com.example.data

import kotlinx.coroutines.flow.Flow

class DriveRemoteRepository(private val dao: DriveRemoteDao) {

    val allVehicles: Flow<List<VehicleDevice>> = dao.getAllVehicles()
    val selectedVehicle: Flow<VehicleDevice?> = dao.getSelectedVehicle()
    val recentLogs: Flow<List<RemoteLog>> = dao.getRecentLogs()

    suspend fun insertVehicle(vehicle: VehicleDevice): Long = dao.insertVehicle(vehicle)

    suspend fun updateVehicle(vehicle: VehicleDevice) = dao.updateVehicle(vehicle)

    suspend fun selectVehicle(id: Long) {
        dao.clearSelection()
        dao.setSelectedVehicle(id)
    }

    suspend fun deleteVehicle(id: Long) = dao.deleteVehicle(id)

    suspend fun logCommand(command: String, details: String, isSuccess: Boolean = true) {
        dao.insertLog(
            RemoteLog(
                command = command,
                details = details,
                status = if (isSuccess) "SUCCESS" else "FAILED"
            )
        )
    }

    suspend fun clearLogs() = dao.clearAllLogs()
}
