package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.AppLanguage
import com.example.ui.model.AppStrings
import com.example.ui.model.VehicleState
import com.example.ui.theme.CockpitCardBg
import com.example.ui.theme.CockpitCardElevated
import com.example.ui.theme.CockpitDarkBg
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun KeyFobControls(
    state: VehicleState,
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Center Engine Start / Stop Button with Push-To-Start Hold Mechanism
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            EngineStartStopButton(
                isEngineRunning = state.isEngineRunning,
                language = language,
                onTrigger = onToggleEngine
            )
        }

        // Primary 4-Button Remote Grid: Lock, Unlock, Trunk, Frunk
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FobActionButton(
                title = if (state.isLocked) AppStrings.get("unlock", language) else AppStrings.get("lock", language),
                icon = if (state.isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                isActive = state.isLocked,
                activeColor = NeonCyan,
                testTag = "fob_lock_unlock_button",
                modifier = Modifier.weight(1f),
                onClick = onToggleLock
            )

            FobActionButton(
                title = AppStrings.get("trunk", language),
                icon = Icons.Default.DirectionsCar,
                isActive = state.isTrunkOpen,
                activeColor = NeonAmber,
                testTag = "fob_trunk_button",
                modifier = Modifier.weight(1f),
                onClick = onToggleTrunk
            )

            FobActionButton(
                title = AppStrings.get("frunk", language),
                icon = Icons.Default.DirectionsCar,
                isActive = state.isFrunkOpen,
                activeColor = NeonAmber,
                testTag = "fob_frunk_button",
                modifier = Modifier.weight(1f),
                onClick = onToggleFrunk
            )
        }

        // Secondary Utility Row: Horn, Hazards, Windows, Sentry Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FobIconButton(
                title = AppStrings.get("horn", language),
                icon = Icons.Default.Campaign,
                isActive = state.isHornSounding,
                activeColor = NeonRed,
                testTag = "fob_horn_button",
                modifier = Modifier.weight(1f),
                onClick = onToggleHorn
            )

            FobIconButton(
                title = AppStrings.get("hazard", language),
                icon = Icons.Default.Warning,
                isActive = state.areHazardsFlashing,
                activeColor = NeonAmber,
                testTag = "fob_hazard_button",
                modifier = Modifier.weight(1f),
                onClick = onToggleHazards
            )

            FobIconButton(
                title = AppStrings.get("windows", language),
                icon = Icons.Default.Window,
                isActive = state.areWindowsVented,
                activeColor = NeonCyan,
                testTag = "fob_windows_button",
                modifier = Modifier.weight(1f),
                onClick = onToggleWindows
            )

            FobIconButton(
                title = AppStrings.get("sentry", language),
                icon = Icons.Default.Security,
                isActive = state.isSentryModeOn,
                activeColor = NeonGreen,
                testTag = "fob_sentry_button",
                modifier = Modifier.weight(1f),
                onClick = onToggleSentry
            )
        }

        // Remote Smart Summon Controls (Move in / out of tight parking)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CockpitCardElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Smart Summon / Remote Move",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (state.summonActive) "ACTIVE" else "READY",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.summonActive) NeonCyan else TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummonHoldButton(
                        label = AppStrings.get("summon_fwd", language),
                        icon = Icons.Default.ArrowUpward,
                        direction = "FWD",
                        isActive = state.summonActive && state.summonDirection == "FWD",
                        testTag = "summon_fwd_button",
                        modifier = Modifier.weight(1f),
                        onStart = { onStartSummon("FWD") },
                        onStop = onStopSummon
                    )

                    SummonHoldButton(
                        label = AppStrings.get("summon_rev", language),
                        icon = Icons.Default.ArrowDownward,
                        direction = "REV",
                        isActive = state.summonActive && state.summonDirection == "REV",
                        testTag = "summon_rev_button",
                        modifier = Modifier.weight(1f),
                        onStart = { onStartSummon("REV") },
                        onStop = onStopSummon
                    )
                }
            }
        }
    }
}

@Composable
fun EngineStartStopButton(
    isEngineRunning: Boolean,
    language: AppLanguage,
    onTrigger: () -> Unit
) {
    var isHolding by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isHolding) {
        if (isHolding) {
            val step = 0.05f
            while (holdProgress < 1f && isHolding) {
                delay(40)
                holdProgress = (holdProgress + step).coerceAtMost(1f)
            }
            if (holdProgress >= 1f) {
                onTrigger()
                isHolding = false
                holdProgress = 0f
            }
        } else {
            holdProgress = 0f
        }
    }

    val glowColor by animateColorAsState(
        targetValue = if (isEngineRunning) NeonGreen else NeonCyan,
        label = "glow"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.25f),
                            CockpitDarkBg
                        )
                    )
                )
                .border(2.dp, glowColor.copy(alpha = 0.6f), CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isHolding = true
                            tryAwaitRelease()
                            isHolding = false
                        }
                    )
                }
                .testTag("engine_start_stop_button"),
            contentAlignment = Alignment.Center
        ) {
            if (isHolding) {
                CircularProgressIndicator(
                    progress = { holdProgress },
                    modifier = Modifier.size(102.dp),
                    color = if (isEngineRunning) NeonRed else NeonGreen,
                    strokeWidth = 4.dp,
                    trackColor = Color.Transparent
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Engine Start Stop",
                    tint = if (isEngineRunning) NeonGreen else NeonCyan,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEngineRunning) {
                        AppStrings.get("btn_stop", language)
                    } else {
                        AppStrings.get("btn_start", language)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "ENGINE",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = TextMuted
                )
            }
        }

        Text(
            text = if (isEngineRunning) AppStrings.get("hold_to_stop", language) else AppStrings.get("hold_to_start", language),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
fun FobActionButton(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isActive) activeColor else MaterialTheme.colorScheme.outline,
        label = "border"
    )

    Surface(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = if (isActive) activeColor.copy(alpha = 0.12f) else CockpitCardElevated,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) activeColor else TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) activeColor else TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun FobIconButton(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (isActive) activeColor else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = if (isActive) activeColor.copy(alpha = 0.15f) else CockpitCardElevated,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) activeColor else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (isActive) activeColor else TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SummonHoldButton(
    label: String,
    icon: ImageVector,
    direction: String,
    isActive: Boolean,
    testTag: String,
    modifier: Modifier = Modifier,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    val activeColor = NeonCyan
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) activeColor.copy(alpha = 0.25f) else CockpitCardBg)
            .border(
                1.dp,
                if (isActive) activeColor else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(12.dp)
            )
            .pointerInput(direction) {
                detectTapGestures(
                    onPress = {
                        onStart()
                        tryAwaitRelease()
                        onStop()
                    }
                )
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else TextPrimary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isActive) activeColor else TextPrimary
            )
        }
    }
}
