package com.example.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/** Reusable low-latency UDP transport for the local Game Remote link. */
object DrivePacketTransmitter {
    private const val TAG = "DrivePacketTransmitter"
    private const val MAX_PACKET_BYTES = 4096
    private val lock = Any()
    private var socket: DatagramSocket? = null

    suspend fun sendUdpPacket(targetIp: String, targetPort: Int, payload: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                require(targetIp.isNotBlank()) { "IP address cannot be empty" }
                require(targetPort in 1..65535) { "Port must be between 1 and 65535" }
                val bytes = payload.toByteArray(Charsets.UTF_8)
                require(bytes.size <= MAX_PACKET_BYTES) { "Payload is too large" }
                val address = InetAddress.getByName(targetIp)
                synchronized(lock) {
                    if (socket == null || socket?.isClosed == true) socket = DatagramSocket()
                    socket!!.send(DatagramPacket(bytes, bytes.size, address, targetPort))
                }
                Result.success(true)
            } catch (e: Exception) {
                Log.w(TAG, "UDP send failed: ${e.message}")
                Result.failure(e)
            }
        }

    fun close() {
        synchronized(lock) {
            socket?.close()
            socket = null
        }
    }
}
