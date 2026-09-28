package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.VehicleDevice
import com.example.ui.components.ClimateControls
import com.example.ui.components.DiagnosticsDialog
import com.example.ui.components.KeyFobControls
import com.example.ui.components.TelemetryPanel
import com.example.ui.components.VehicleHeroCard
import com.example.ui.model.AppLanguage
import com.example.ui.model.AppStrings
import com.example.ui.model.VehicleState
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary

@Composable
fun CarRemoteScreen(
    vehicle: VehicleDevice?,
    vehicleState: VehicleState,
    language: AppLanguage,
    onToggleEngine: () -> Unit,
    onToggleLock: () -> Unit,
    onToggleTrunk: () -> Unit,
    onToggleFrunk: () -> Unit,
    onToggleHorn: () -> Unit,
    onToggleHazards: () -> Unit,
    onToggleWindows: () -> Unit,
    onToggleSentry: () -> Unit,
    onStartSummon: (String) -> Unit,
    onStopSummon: () -> Unit,
    onAdjustTemp: (Int) -> Unit,
    onToggleAc: () -> Unit,
    onToggleDefrost: () -> Unit,
    onCycleSeatWarmer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDiagnostics by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("car_remote_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vehicle Hero Banner & Status HUD
        item {
            VehicleHeroCard(
                vehicleName = vehicle?.name ?: "Malaram HyperDrive GT",
                vehicleModel = vehicle?.model ?: "Cyber Edition Sport",
                state = vehicleState,
                language = language
            )
        }

        // Diagnostics Quick Trigger Bar
        item {
            OutlinedButton(
                onClick = { showDiagnostics = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("run_diagnostics_button"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Scan",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("run_diagnostics", language),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }

        // Digital Key Fob Actions
        item {
            KeyFobControls(
                state = vehicleState,
                language = language,
                onToggleEngine = onToggleEngine,
                onToggleLock = onToggleLock,
                onToggleTrunk = onToggleTrunk,
                onToggleFrunk = onToggleFrunk,
                onToggleHorn = onToggleHorn,
                onToggleHazards = onToggleHazards,
                onToggleWindows = onToggleWindows,
                onToggleSentry = onToggleSentry,
                onStartSummon = onStartSummon,
                onStopSummon = onStopSummon
            )
        }

        // Telemetry & 4-Wheel TPMS
        item {
            TelemetryPanel(
                state = vehicleState,
                language = language
            )
        }

        // Climate & Cabin Environment
        item {
            ClimateControls(
                state = vehicleState,
                language = language,
                onAdjustTemp = onAdjustTemp,
                onToggleAc = onToggleAc,
                onToggleDefrost = onToggleDefrost,
                onCycleSeatWarmer = onCycleSeatWarmer
            )
        }
    }

    if (showDiagnostics) {
        DiagnosticsDialog(
            vehicleName = vehicle?.name ?: "Malaram HyperDrive GT",
            language = language,
            onDismiss = { showDiagnostics = false }
        )
    }
}
