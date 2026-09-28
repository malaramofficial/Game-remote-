package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.AppLanguage
import com.example.ui.model.AppStrings
import com.example.ui.model.ControllerState
import com.example.ui.model.Gear
import com.example.ui.theme.CockpitBorder
import com.example.ui.theme.CockpitCardBg
import com.example.ui.theme.CockpitCardElevated
import com.example.ui.theme.CockpitDarkBg
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun VirtualCockpitControls(
    state: ControllerState,
    language: AppLanguage,
    onSteeringChange: (Float) -> Unit,
    onThrottleChange: (Float) -> Unit,
    onBrakeChange: (Float) -> Unit,
    onGearChange: (Gear) -> Unit,
    onToggleHighBeam: () -> Unit,
    onToggleNitro: () -> Unit,
    onToggleCruise: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Digital Cluster HUD: Speedometer & RPM Gauges
        DigitalClusterHUD(
            state = state,
            language = language
        )

        // Transmission Gear Shifter Row (P - R - N - D - S)
        GearSelectorRow(
            currentGear = state.gear,
            onSelectGear = onGearChange
        )

        // Interactive Steering Wheel & Controls Center
        InteractiveSteeringWheel(
            angle = state.steeringAngle,
            language = language,
            onAngleChanged = onSteeringChange
        )

        // Pedals and Boost Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Brake Pedal (Red)
            PedalControl(
                label = AppStrings.get("brake", language),
                activeColor = NeonRed,
                currentPercent = state.brakePercent,
                testTag = "brake_pedal",
                modifier = Modifier.weight(1f),
                onPressureChange = onBrakeChange
            )

            // Nitro Boost & Utility Center
            Column(
                modifier = Modifier.weight(0.9f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Boost / Nitro Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (state.isNitroActive) NeonAmber else CockpitCardElevated)
                        .border(1.dp, if (state.isNitroActive) NeonAmber else CockpitBorder, RoundedCornerShape(16.dp))
                        .clickable(onClick = onToggleNitro)
                        .testTag("nitro_boost_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Nitro",
                            tint = if (state.isNitroActive) Color.Black else NeonAmber,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = AppStrings.get("nitro", language),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = if (state.isNitroActive) Color.Black else NeonAmber
                        )
                    }
                }

                // Headlights & Cruise Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                1.dp,
                                if (state.isHighBeamOn) NeonCyan else CockpitBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable(onClick = onToggleHighBeam)
                            .testTag("lights_toggle_button"),
                        color = if (state.isHighBeamOn) NeonCyan.copy(alpha = 0.2f) else CockpitCardBg
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Highlight,
                                contentDescription = "Lights",
                                tint = if (state.isHighBeamOn) NeonCyan else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                1.dp,
                                if (state.isCruiseControlOn) NeonGreen else CockpitBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable(onClick = onToggleCruise)
                            .testTag("cruise_toggle_button"),
                        color = if (state.isCruiseControlOn) NeonGreen.copy(alpha = 0.2f) else CockpitCardBg
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Cruise",
                                tint = if (state.isCruiseControlOn) NeonGreen else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Throttle / Gas Pedal (Green / Cyan)
            PedalControl(
                label = AppStrings.get("throttle", language),
                activeColor = NeonGreen,
                currentPercent = state.throttlePercent,
                testTag = "throttle_pedal",
                modifier = Modifier.weight(1f),
                onPressureChange = onThrottleChange
            )
        }

        // UDP Packet Transmission Status
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CockpitCardElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CockpitBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "UDP",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "UDP Telemetry Target: ${state.targetIp}:${state.targetPort}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = if (state.lastPacketSent.isNotEmpty()) "TX OK" else "STANDBY",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun DigitalClusterHUD(
    state: ControllerState,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, CockpitBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = CockpitCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RPM gauge left
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "${state.rpm}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (state.rpm > 5500) NeonRed else NeonAmber
                    )
                    Text(
                        text = AppStrings.get("rpm", language),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                // Speedometer Center
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.speedKmh.roundToInt()}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = NeonCyan
                    )
                    Text(
                        text = "KM/H",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Active Gear right
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = state.gear.label,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = NeonGreen
                    )
                    Text(
                        text = AppStrings.get("gear", language),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Speed gauge sweep arc
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
            ) {
                val strokeW = 8.dp.toPx()
                val sweep = 180f
                val start = 180f
                val progressFraction = (state.speedKmh / 180f).coerceIn(0f, 1f)

                // Track arc
                drawArc(
                    color = CockpitCardElevated,
                    startAngle = start,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.1f, -size.height),
                    size = Size(size.width * 0.8f, size.height * 2.5f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )

                // Active progress arc
                drawArc(
                    brush = Brush.horizontalGradient(
                        colors = listOf(NeonCyan, NeonGreen, NeonAmber, NeonRed)
                    ),
                    startAngle = start,
                    sweepAngle = sweep * progressFraction,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.1f, -size.height),
                    size = Size(size.width * 0.8f, size.height * 2.5f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun GearSelectorRow(
    currentGear: Gear,
    onSelectGear: (Gear) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CockpitCardBg)
            .border(1.dp, CockpitBorder, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Gear.values().forEach { gear ->
            val isSelected = currentGear == gear
            val activeColor = when (gear) {
                Gear.PARK -> NeonAmber
                Gear.REVERSE -> NeonRed
                Gear.NEUTRAL -> TextSecondary
                Gear.DRIVE -> NeonGreen
                Gear.SPORT -> NeonCyan
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) activeColor.copy(alpha = 0.2f) else Color.Transparent)
                    .border(
                        1.dp,
                        if (isSelected) activeColor else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectGear(gear) }
                    .testTag("gear_${gear.label.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gear.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    color = if (isSelected) activeColor else TextMuted
                )
            }
        }
    }
}

@Composable
fun InteractiveSteeringWheel(
    angle: Float,
    language: AppLanguage,
    onAngleChanged: (Float) -> Unit
) {
    var currentAngle by remember { mutableFloatStateOf(angle) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(210.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            // Smooth auto-centering spring
                            currentAngle = 0f
                            onAngleChanged(0f)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val delta = dragAmount.x * 0.8f
                            currentAngle = (currentAngle + delta).coerceIn(-90f, 90f)
                            onAngleChanged(currentAngle)
                        }
                    )
                }
                .testTag("steering_wheel_canvas")
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val outerRadius = size.width / 2 - 16.dp.toPx()

            rotate(degrees = currentAngle, pivot = center) {
                // Outer Rim
                drawCircle(
                    color = CockpitCardElevated,
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                )

                // Neon Rim Accent
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(NeonCyan, NeonBlue, NeonCyan)
                    ),
                    radius = outerRadius + 10.dp.toPx(),
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Top center alignment marker
                drawCircle(
                    color = NeonAmber,
                    radius = 5.dp.toPx(),
                    center = Offset(center.x, center.y - outerRadius)
                )

                // Spokes
                // Left Spoke
                drawLine(
                    color = CockpitBorder,
                    start = center,
                    end = Offset(center.x - outerRadius + 8.dp.toPx(), center.y),
                    strokeWidth = 14.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Right Spoke
                drawLine(
                    color = CockpitBorder,
                    start = center,
                    end = Offset(center.x + outerRadius - 8.dp.toPx(), center.y),
                    strokeWidth = 14.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Bottom Spoke
                drawLine(
                    color = CockpitBorder,
                    start = center,
                    end = Offset(center.x, center.y + outerRadius - 8.dp.toPx()),
                    strokeWidth = 14.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Center Boss Hub
                drawCircle(
                    color = CockpitDarkBg,
                    radius = 34.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = NeonCyan,
                    radius = 34.dp.toPx(),
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // Center Angle & Help Indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 80.dp)
        ) {
            Text(
                text = "${currentAngle.roundToInt()}°",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = if (kotlin.math.abs(currentAngle) > 40f) NeonAmber else NeonCyan
            )
            Text(
                text = "DRAG TO STEER",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PedalControl(
    label: String,
    activeColor: Color,
    currentPercent: Float,
    testTag: String,
    modifier: Modifier = Modifier,
    onPressureChange: (Float) -> Unit
) {
    var isPressed by remember { mutableFloatStateOf(currentPercent) }

    val animatedHeight by animateFloatAsState(
        targetValue = if (isPressed > 0) 115.dp.value else 130.dp.value,
        animationSpec = tween(80),
        label = "pedalHeight"
    )

    Box(
        modifier = modifier
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CockpitCardBg)
            .border(
                1.dp,
                if (isPressed > 0) activeColor else CockpitBorder,
                RoundedCornerShape(16.dp)
            )
            .pointerInput(label) {
                detectTapGestures(
                    onPress = {
                        isPressed = 100f
                        onPressureChange(100f)
                        tryAwaitRelease()
                        isPressed = 0f
                        onPressureChange(0f)
                    }
                )
            }
            .testTag(testTag),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Active pressure fill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((130 * (isPressed / 100f)).dp)
                .background(activeColor.copy(alpha = 0.25f))
        )

        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = if (isPressed > 0) activeColor else TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${isPressed.roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = if (isPressed > 0) activeColor else TextMuted,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "HOLD",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = TextMuted
            )
        }
    }
}
