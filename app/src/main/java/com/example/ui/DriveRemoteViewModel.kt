package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DriveRemoteDatabase
import com.example.data.DriveRemoteRepository
import com.example.data.RemoteLog
import com.example.data.VehicleDevice
import com.example.network.DrivePacketTransmitter
import com.example.ui.model.AppLanguage
import com.example.ui.model.ControllerState
import com.example.ui.model.Gear
import com.example.ui.model.VehicleState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavTab {
    VEHICLE_KEY,
    COCKPIT_DRIVE,
    REMOTE_DECK,
    SETTINGS
}

class DriveRemoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DriveRemoteRepository
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        val db = DriveRemoteDatabase.getDatabase(application, viewModelScope)
        repository = DriveRemoteRepository(db.driveRemoteDao())
    }

    val allVehicles: StateFlow<List<VehicleDevice>> = repository.allVehicles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedVehicle: StateFlow<VehicleDevice?> = repository.selectedVehicle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentLogs: StateFlow<List<RemoteLog>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(NavTab.VEHICLE_KEY)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _controllerState = MutableStateFlow(ControllerState())
    val controllerState: StateFlow<ControllerState> = _controllerState.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.HINDI)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private var hornJob: Job? = null
    private var telemetryJob: Job? = null
    private var transmitJob: Job? = null

    init {
        startTelemetryLoop()
        startTransmitLoop()
    }

    fun setTab(tab: NavTab) {
        _currentTab.value = tab
        triggerHaptic(30)
    }

    fun toggleLanguage() {
        _appLanguage.value = if (_appLanguage.value == AppLanguage.HINDI) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.HINDI
        }
        triggerHaptic(40)
    }

    fun toggleHaptics() {
        _hapticsEnabled.value = !_hapticsEnabled.value
    }

    fun toggleEngine() {
        val current = _vehicleState.value
        val newState = !current.isEngineRunning
        _vehicleState.value = current.copy(isEngineRunning = newState)
        triggerHaptic(if (newState) 100 else 60)
        logCommand(
            if (newState) "Engine Started" else "Engine Stopped",
            "Remote engine power toggled via digital key"
        )
    }

    fun toggleLock() {
        val current = _vehicleState.value
        val newLock = !current.isLocked
        _vehicleState.value = current.copy(isLocked = newLock)
        triggerHaptic(50)
        logCommand(
            if (newLock) "Doors Locked" else "Doors Unlocked",
            "Vehicle security lock state updated"
        )
    }

    fun toggleTrunk() {
        val current = _vehicleState.value
        val newTrunk = !current.isTrunkOpen
        _vehicleState.value = current.copy(isTrunkOpen = newTrunk)
        triggerHaptic(40)
        logCommand("Trunk ${if (newTrunk) "Opened" else "Closed"}", "Rear cargo tailgate")
    }

    fun toggleFrunk() {
        val current = _vehicleState.value
        val newFrunk = !current.isFrunkOpen
        _vehicleState.value = current.copy(isFrunkOpen = newFrunk)
        triggerHaptic(40)
        logCommand("Frunk ${if (newFrunk) "Opened" else "Closed"}", "Front trunk latch")
    }

    fun toggleHorn() {
        val current = _vehicleState.value
        val newHorn = !current.isHornSounding
        _vehicleState.value = current.copy(isHornSounding = newHorn)
        triggerHaptic(80)
        logCommand("Vehicle Horn", if (newHorn) "Sounding panic alert" else "Silenced")

        hornJob?.cancel()
        if (newHorn) {
            hornJob = viewModelScope.launch {
                delay(2500)
                _vehicleState.value = _vehicleState.value.copy(isHornSounding = false)
            }
        }
    }

    fun toggleHazards() {
        val current = _vehicleState.value
        val newHazards = !current.areHazardsFlashing
        _vehicleState.value = current.copy(areHazardsFlashing = newHazards)
        triggerHaptic(40)
        logCommand("Hazard Lights", if (newHazards) "Flashing active" else "Turned off")
    }

    fun toggleWindows() {
        val current = _vehicleState.value
        val newWindows = !current.areWindowsVented
        _vehicleState.value = current.copy(areWindowsVented = newWindows)
        triggerHaptic(40)
        logCommand("Windows", if (newWindows) "Vented for cooling" else "Closed")
    }

    fun toggleSentry() {
        val current = _vehicleState.value
        val newSentry = !current.isSentryModeOn
        _vehicleState.value = current.copy(isSentryModeOn = newSentry)
        triggerHaptic(40)
        logCommand("Sentry Mode", if (newSentry) "Armed & monitoring" else "Disarmed")
    }

    fun startSummon(direction: String) {
        _vehicleState.value = _vehicleState.value.copy(
            summonActive = true,
            summonDirection = direction
        )
        triggerHaptic(60)
        logCommand("Summon Started", "Moving vehicle $direction")
    }

    fun stopSummon() {
        _vehicleState.value = _vehicleState.value.copy(
            summonActive = false,
            summonDirection = "NONE"
        )
        triggerHaptic(30)
        logCommand("Summon Stopped", "Vehicle stationary")
    }

    fun adjustTemp(delta: Int) {
        val current = _vehicleState.value
        val newTemp = (current.targetTempC + delta).coerceIn(16, 30)
        _vehicleState.value = current.copy(targetTempC = newTemp)
        triggerHaptic(25)
    }

    fun toggleAc() {
        val current = _vehicleState.value
        val newAc = !current.isAcOn
        _vehicleState.value = current.copy(isAcOn = newAc)
        triggerHaptic(40)
        logCommand("A/C Climate", if (newAc) "Turned on" else "Turned off")
    }

    fun toggleDefrost() {
        val current = _vehicleState.value
        val newDefrost = !current.isDefrostOn
        _vehicleState.value = current.copy(isDefrostOn = newDefrost)
        triggerHaptic(40)
        logCommand("Defrost", if (newDefrost) "Max defrost active" else "Turned off")
    }

    fun cycleSeatWarmer() {
        val current = _vehicleState.value
        val newLevel = (current.seatWarmerLevel + 1) % 4
        _vehicleState.value = current.copy(seatWarmerLevel = newLevel)
        triggerHaptic(30)
        logCommand("Seat Heater", "Set to Level $newLevel")
    }

    // Virtual Cockpit Driving Controls
    fun setSteeringAngle(angle: Float) {
        _controllerState.value = _controllerState.value.copy(steeringAngle = angle)
    }

    fun setThrottle(percent: Float) {
        _controllerState.value = _controllerState.value.copy(throttlePercent = percent)
        if (percent > 0) {
            triggerHaptic(20)
        }
    }

    fun setBrake(percent: Float) {
        _controllerState.value = _controllerState.value.copy(brakePercent = percent)
        if (percent > 0) {
            triggerHaptic(40)
        }
    }

    fun setGear(gear: Gear) {
        _controllerState.value = _controllerState.value.copy(gear = gear)
        triggerHaptic(50)
        logCommand("Gear Shifter", "Selected gear ${gear.label}")
    }

    fun toggleHighBeam() {
        val current = _controllerState.value
        _controllerState.value = current.copy(isHighBeamOn = !current.isHighBeamOn)
        triggerHaptic(30)
    }

    fun toggleNitro() {
        val current = _controllerState.value
        val newNitro = !current.isNitroActive
        _controllerState.value = current.copy(isNitroActive = newNitro)
        triggerHaptic(80)
        logCommand("Nitro Boost", if (newNitro) "Turbo engaged!" else "Disengaged")
    }

    fun toggleCruise() {
        val current = _controllerState.value
        _controllerState.value = current.copy(isCruiseControlOn = !current.isCruiseControlOn)
        triggerHaptic(30)
        logCommand("Cruise Control", if (!_controllerState.value.isCruiseControlOn) "Activated" else "Deactivated")
    }

    fun updateTargetAddress(ip: String, port: Int) {
        _controllerState.value = _controllerState.value.copy(
            targetIp = ip,
            targetPort = port
        )
        triggerHaptic(40)
        logCommand("Network Target", "Updated receiver address to $ip:$port")
    }

    private fun transmitDrivePacket() {
        val state = _controllerState.value
        val payload = """{"steer":${state.steeringAngle.toInt()},"throttle":${state.throttlePercent.toInt()},"brake":${state.brakePercent.toInt()},"gear":"${state.gear.label}"}"""
        
        viewModelScope.launch {
            DrivePacketTransmitter.sendUdpPacket(state.targetIp, state.targetPort, payload)
            _controllerState.value = _controllerState.value.copy(lastPacketSent = payload)
        }
    }

    /** Publishes the latest controller state at a bounded 30 Hz rate; latest state wins. */
    private fun startTransmitLoop() {
        transmitJob = viewModelScope.launch {
            while (true) {
                val state = _controllerState.value
                val payload = GameRemoteProtocol.encode(state)
                val result = DrivePacketTransmitter.sendUdpPacket(
                    state.targetIp,
                    state.targetPort,
                    payload
                )
                _controllerState.value = _controllerState.value.copy(
                    isTransmittingUdp = result.isSuccess,
                    lastPacketSent = if (result.isSuccess) payload else _controllerState.value.lastPacketSent
                )
                delay(33L)
            }
        }
    }

    // Room Database Device Operations
    fun selectVehicle(id: Long) {
        viewModelScope.launch {
            repository.selectVehicle(id)
            triggerHaptic(40)
            logCommand("Vehicle Selected", "Switched active device ID $id")
        }
    }

    fun addVehicle(name: String, model: String, connection: String, target: String) {
        viewModelScope.launch {
            val newDevice = VehicleDevice(
                name = name,
                model = model,
                deviceType = "RC_ROVER",
                connectionType = connection,
                targetAddress = target,
                isSelected = false
            )
            repository.insertVehicle(newDevice)
            triggerHaptic(50)
            logCommand("Device Added", "Paired $name ($target)")
        }
    }

    fun deleteVehicle(id: Long) {
        viewModelScope.launch {
            repository.deleteVehicle(id)
            triggerHaptic(50)
            logCommand("Device Removed", "Deleted device ID $id")
        }
    }

    fun logCommand(command: String, details: String) {
        viewModelScope.launch {
            repository.logCommand(command, details)
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
            triggerHaptic(40)
        }
    }

    private fun triggerHaptic(durationMs: Long) {
        if (!_hapticsEnabled.value) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Ignore if vibration not permitted
        }
    }

    private fun startTelemetryLoop() {
        telemetryJob = viewModelScope.launch {
            while (true) {
                delay(120)
                val cState = _controllerState.value
                val vState = _vehicleState.value

                // Dynamic speed calculation based on throttle, brake, and gear
                var targetSpeed = when (cState.gear) {
                    Gear.PARK -> 0f
                    Gear.NEUTRAL -> 0f
                    Gear.REVERSE -> (cState.throttlePercent * 0.35f)
                    Gear.DRIVE -> (cState.throttlePercent * 1.4f)
                    Gear.SPORT -> (cState.throttlePercent * 1.8f)
                }

                if (cState.isNitroActive && cState.gear == Gear.SPORT) {
                    targetSpeed *= 1.35f
                }

                if (cState.brakePercent > 0) {
                    targetSpeed = (targetSpeed - (cState.brakePercent * 1.5f)).coerceAtLeast(0f)
                }

                val currentSpeed = cState.speedKmh
                val newSpeed = (currentSpeed + (targetSpeed - currentSpeed) * 0.15f).coerceIn(0f, 180f)
                val newRpm = if (vState.isEngineRunning || cState.gear != Gear.PARK) {
                    ((newSpeed * 35) + (cState.throttlePercent * 30) + 850).toInt().coerceIn(850, 7500)
                } else {
                    0
                }

                _controllerState.value = cState.copy(
                    speedKmh = newSpeed,
                    rpm = newRpm
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        hornJob?.cancel()
        telemetryJob?.cancel()
    }
}
