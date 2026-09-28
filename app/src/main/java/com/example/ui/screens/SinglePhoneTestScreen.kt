package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.model.ControllerState

@Composable
fun SinglePhoneTestScreen(
    running: Boolean,
    receiverConnected: Boolean,
    packetsReceived: Long,
    lastSequence: Long,
    lastLatencyMs: Long?,
    safeStateCount: Long,
    controllerState: ControllerState,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Single-Phone Test", style = MaterialTheme.typography.headlineSmall)
        Text("Loopback: Controller → 127.0.0.1:8888 → Receiver")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (!running) Button(onClick = onStart) { Text("START TEST") }
            else OutlinedButton(onClick = onStop) { Text("STOP TEST") }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Receiver: ${if (receiverConnected) "CONNECTED" else if (running) "WAITING" else "STOPPED"}")
                Text("Packets received: $packetsReceived")
                Text("Last sequence: $lastSequence")
                Text("Last latency: ${lastLatencyMs?.let { "$it ms" } ?: "--"}")
                Text("Safe-state events: $safeStateCount")
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Live controller state", style = MaterialTheme.typography.titleMedium)
                Text("Steering: ${"%.2f".format(controllerState.steeringAngle)}°")
                Text("Throttle: ${"%.1f".format(controllerState.throttlePercent)}%")
                Text("Brake: ${"%.1f".format(controllerState.brakePercent)}%")
                Text("Gear: ${controllerState.gear.label}")
                Text("UDP send: ${if (controllerState.isTransmittingUdp) "OK" else "OFF/FAILED"}")
            }
        }
        Text("PASS: packets/sequence लगातार बढ़ें, latency दिखे और UDP send OK रहे.")
        Text("यह केवल same-phone network/protocol test है; game touch injection अभी अलग चरण है.")
    }
}
