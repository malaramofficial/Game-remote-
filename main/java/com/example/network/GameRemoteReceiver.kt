package com.example.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.SocketException

/**
 * Phone A receiver. Safe-state callback is invoked after the receive timeout.
 */
class GameRemoteReceiver(
    private val port: Int = 8888,
    private val timeoutMs: Long = 400L,
    private val scope: CoroutineScope
) {
    private var socket: DatagramSocket? = null
    private var job: Job? = null
    private var lastSequence = -1L

    fun start(
        onState: (ReceivedControllerState) -> Unit,
        onConnectionChanged: (Boolean) -> Unit,
        onSafeState: () -> Unit
    ) {
        stop()
        job = scope.launch(Dispatchers.IO) {
            try {
                val localSocket = DatagramSocket(null).apply {
                    reuseAddress = true
                    bind(InetSocketAddress(port))
                    soTimeout = timeoutMs.toInt().coerceAtLeast(1)
                }
                socket = localSocket
                var connected = false

                while (isActive && !localSocket.isClosed) {
                    val buffer = ByteArray(4096)
                    val packet = DatagramPacket(buffer, buffer.size)
                    try {
                        localSocket.receive(packet)
                        val payload = String(packet.data, packet.offset, packet.length, Charsets.UTF_8)
                        val state = GameRemoteProtocol.decode(payload) ?: continue
                        if (state.sequence <= lastSequence) continue

                        lastSequence = state.sequence
                        if (!connected) {
                            connected = true
                            onConnectionChanged(true)
                        }
                        onState(state)
                    } catch (_: java.net.SocketTimeoutException) {
                        if (connected) {
                            connected = false
                            onConnectionChanged(false)
                            onSafeState()
                        }
                    }
                }
            } catch (_: SocketException) {
                // Expected when stop() closes the socket.
            } finally {
                socket = null
                onSafeState()
                onConnectionChanged(false)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        socket?.close()
        socket = null
        lastSequence = -1L
    }
}
