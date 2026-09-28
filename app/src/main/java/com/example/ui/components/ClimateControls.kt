package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirlineSeatReclineExtra
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.AppLanguage
import com.example.ui.model.AppStrings
import com.example.ui.model.VehicleState
import com.example.ui.theme.CockpitCardBg
import com.example.ui.theme.CockpitCardElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ClimateControls(
    state: VehicleState,
    language: AppLanguage,
    onAdjustTemp: (Int) -> Unit,
    onToggleAc: () -> Unit,
    onToggleDefrost: () -> Unit,
    onCycleSeatWarmer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .testTag("climate_controls_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = "Climate",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = AppStrings.get("climate_control", language),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = if (state.isAcOn) "A/C ACTIVE" else "STANDBY",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.isAcOn) NeonCyan else TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }

            // Temperature adjustment row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CockpitCardElevated)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onAdjustTemp(-1) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CockpitCardBg)
                        .testTag("temp_decrement_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease Temp",
                        tint = NeonCyan
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.targetTempC}°C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Target Cabin Temperature",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = { onAdjustTemp(1) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CockpitCardBg)
                        .testTag("temp_increment_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase Temp",
                        tint = NeonCyan
                    )
                }
            }

            // Action toggles: A/C, Defrost, Seat Heater
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ClimateToggleButton(
                    label = AppStrings.get("ac", language),
                    icon = Icons.Default.AcUnit,
                    isActive = state.isAcOn,
                    activeColor = NeonCyan,
                    modifier = Modifier.weight(1f),
                    testTag = "climate_ac_toggle",
                    onClick = onToggleAc
                )

                ClimateToggleButton(
                    label = AppStrings.get("defrost", language),
                    icon = Icons.Default.Air,
                    isActive = state.isDefrostOn,
                    activeColor = NeonAmber,
                    modifier = Modifier.weight(1f),
                    testTag = "climate_defrost_toggle",
                    onClick = onToggleDefrost
                )

                ClimateToggleButton(
                    label = "${AppStrings.get("seat_heater", language)}: ${if (state.seatWarmerLevel == 0) "OFF" else "L${state.seatWarmerLevel}"}",
                    icon = Icons.Default.Whatshot,
                    isActive = state.seatWarmerLevel > 0,
                    activeColor = NeonRed,
                    modifier = Modifier.weight(1f),
                    testTag = "climate_seat_heater_toggle",
                    onClick = onCycleSeatWarmer
                )
            }
        }
    }
}

@Composable
fun ClimateToggleButton(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isActive) activeColor else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = if (isActive) activeColor.copy(alpha = 0.15f) else CockpitCardBg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) activeColor else TextPrimary,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
