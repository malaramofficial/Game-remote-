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
import java.net.SocketTimeoutException

/** Receives controller packets on Phone A. UDP liveness is timeout-based. */
class GameRemoteReceiver(
    private val port: Int = GameRemoteProtocol.DEFAULT_PORT,
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
        lastSequence = -1L
        job = scope.launch(Dispatchers.IO) {
            var connected = false
            try {
                val localSocket = DatagramSocket(null).apply {
                    reuseAddress = true
                    bind(InetSocketAddress(port))
                    soTimeout = timeoutMs.toInt().coerceAtLeast(1)
                }
                socket = localSocket
                while (isActive && !localSocket.isClosed) {
                    val packet = DatagramPacket(ByteArray(4096), 4096)
                    try {
                        localSocket.receive(packet)
                        val state = GameRemoteProtocol.decode(
                            String(packet.data, packet.offset, packet.length, Charsets.UTF_8)
                        ) ?: continue
                        if (state.sequence <= lastSequence) continue
                        lastSequence = state.sequence
                        if (!connected) {
                            connected = true
                            onConnectionChanged(true)
                        }
                        onState(state)
                    } catch (_: SocketTimeoutException) {
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
                if (connected) onConnectionChanged(false)
                onSafeState()
            }
        }
    }

    fun stop() {
        socket?.close()
        socket = null
        job?.cancel()
        job = null
        lastSequence = -1L
    }
}
