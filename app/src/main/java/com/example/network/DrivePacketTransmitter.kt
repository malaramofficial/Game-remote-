package com.example.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object DrivePacketTransmitter {
    private const val TAG = "DrivePacketTransmitter"

    suspend fun sendUdpPacket(
        targetIp: String,
        targetPort: Int,
        payload: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (targetIp.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("IP address cannot be empty"))
            }
            val address = InetAddress.getByName(targetIp)
            val bytes = payload.toByteArray(Charsets.UTF_8)
            val socket = DatagramSocket()
            socket.soTimeout = 1000
            val packet = DatagramPacket(bytes, bytes.size, address, targetPort)
            socket.send(packet)
            socket.close()
            Log.d(TAG, "Sent packet to $targetIp:$targetPort -> $payload")
            Result.success(true)
        } catch (e: Exception) {
            Log.w(TAG, "Failed sending packet to $targetIp:$targetPort: ${e.message}")
            Result.failure(e)
        }
    }
}
