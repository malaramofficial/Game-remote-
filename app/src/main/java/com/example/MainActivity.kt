package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DriveRemoteViewModel
import com.example.ui.NavTab
import com.example.ui.model.AppLanguage
import com.example.ui.model.AppStrings
import com.example.ui.screens.CarRemoteScreen
import com.example.ui.screens.CloudMediaRemoteScreen
import com.example.ui.screens.DevicesSettingsScreen
import com.example.ui.screens.VirtualCockpitScreen
import com.example.ui.theme.CockpitBorder
import com.example.ui.theme.CockpitCardBg
import com.example.ui.theme.CockpitDarkBg
import com.example.ui.theme.DriveRemoteTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriveRemoteTheme {
                DriveRemoteApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriveRemoteApp(
    viewModel: DriveRemoteViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val vehicleState by viewModel.vehicleState.collectAsStateWithLifecycle()
    val controllerState by viewModel.controllerState.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsStateWithLifecycle()
    val allVehicles by viewModel.allVehicles.collectAsStateWithLifecycle()
    val selectedVehicle by viewModel.selectedVehicle.collectAsStateWithLifecycle()
    val recentLogs by viewModel.recentLogs.collectAsStateWithLifecycle()

    val activeVehicle = selectedVehicle ?: allVehicles.firstOrNull()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(1.dp, NeonCyan, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "Logo",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = AppStrings.get("app_title", language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(NeonGreen)
                                )
                                Text(
                                    text = "${activeVehicle?.name ?: "HyperDrive"} • ONLINE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Language Switch Pill Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CockpitCardBg)
                            .border(1.dp, CockpitBorder, RoundedCornerShape(20.dp))
                            .clickable { viewModel.toggleLanguage() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("appbar_language_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (language == AppLanguage.HINDI) "हिंदी" else "ENG",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CockpitDarkBg
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav"),
                containerColor = CockpitCardBg,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == NavTab.VEHICLE_KEY,
                    onClick = { viewModel.setTab(NavTab.VEHICLE_KEY) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = AppStrings.get("tab_car", language)
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.get("tab_car", language),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == NavTab.VEHICLE_KEY) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_vehicle_key")
                )

                NavigationBarItem(
                    selected = currentTab == NavTab.COCKPIT_DRIVE,
                    onClick = { viewModel.setTab(NavTab.COCKPIT_DRIVE) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = AppStrings.get("tab_controller", language)
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.get("tab_controller", language),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == NavTab.COCKPIT_DRIVE) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_cockpit_drive")
                )

                NavigationBarItem(
                    selected = currentTab == NavTab.REMOTE_DECK,
                    onClick = { viewModel.setTab(NavTab.REMOTE_DECK) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CastConnected,
                            contentDescription = AppStrings.get("tab_media", language)
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.get("tab_media", language),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == NavTab.REMOTE_DECK) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_remote_deck")
                )

                NavigationBarItem(
                    selected = currentTab == NavTab.SETTINGS,
                    onClick = { viewModel.setTab(NavTab.SETTINGS) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = AppStrings.get("tab_settings", language)
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.get("tab_settings", language),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == NavTab.SETTINGS) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            label = "tab_transition"
        ) { tab ->
            when (tab) {
                NavTab.VEHICLE_KEY -> {
                    CarRemoteScreen(
                        vehicle = activeVehicle,
                        vehicleState = vehicleState,
                        language = language,
                        onToggleEngine = viewModel::toggleEngine,
                        onToggleLock = viewModel::toggleLock,
                        onToggleTrunk = viewModel::toggleTrunk,
                        onToggleFrunk = viewModel::toggleFrunk,
                        onToggleHorn = viewModel::toggleHorn,
                        onToggleHazards = viewModel::toggleHazards,
                        onToggleWindows = viewModel::toggleWindows,
                        onToggleSentry = viewModel::toggleSentry,
                        onStartSummon = viewModel::startSummon,
                        onStopSummon = viewModel::stopSummon,
                        onAdjustTemp = viewModel::adjustTemp,
                        onToggleAc = viewModel::toggleAc,
                        onToggleDefrost = viewModel::toggleDefrost,
                        onCycleSeatWarmer = viewModel::cycleSeatWarmer
                    )
                }

                NavTab.COCKPIT_DRIVE -> {
                    VirtualCockpitScreen(
                        controllerState = controllerState,
                        language = language,
                        onSteeringChange = viewModel::setSteeringAngle,
                        onThrottleChange = viewModel::setThrottle,
                        onBrakeChange = viewModel::setBrake,
                        onGearChange = viewModel::setGear,
                        onToggleHighBeam = viewModel::toggleHighBeam,
                        onToggleNitro = viewModel::toggleNitro,
                        onToggleCruise = viewModel::toggleCruise,
                        onUpdateTargetAddress = viewModel::updateTargetAddress
                    )
                }

                NavTab.REMOTE_DECK -> {
                    CloudMediaRemoteScreen(
                        language = language,
                        onCommandLogged = viewModel::logCommand
                    )
                }

                NavTab.SETTINGS -> {
                    DevicesSettingsScreen(
                        vehicles = allVehicles,
                        selectedVehicleId = activeVehicle?.id,
                        logs = recentLogs,
                        language = language,
                        hapticsEnabled = hapticsEnabled,
                        onSelectVehicle = viewModel::selectVehicle,
                        onAddVehicle = viewModel::addVehicle,
                        onDeleteVehicle = viewModel::deleteVehicle,
                        onToggleLanguage = viewModel::toggleLanguage,
                        onToggleHaptics = viewModel::toggleHaptics,
                        onClearLogs = viewModel::clearLogs
                    )
                }
            }
        }
    }
}
