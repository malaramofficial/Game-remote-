package com.example.ui.model

data class TirePressure(
    val pressurePsi: Float = 34.0f,
    val status: String = "OPTIMAL" // "OPTIMAL", "LOW", "HIGH"
)

data class VehicleState(
    val isConnected: Boolean = true,
    val isLocked: Boolean = true,
    val isEngineRunning: Boolean = false,
    val isCharging: Boolean = false,
    val batteryPercent: Int = 88,
    val rangeKm: Int = 425,
    val odometerKm: Int = 18450,
    
    // Climate
    val targetTempC: Int = 21,
    val currentInteriorTempC: Int = 22,
    val exteriorTempC: Int = 27,
    val isAcOn: Boolean = false,
    val isDefrostOn: Boolean = false,
    val seatWarmerLevel: Int = 0, // 0 = off, 1 = low, 2 = med, 3 = high
    
    // Components
    val isTrunkOpen: Boolean = false,
    val isFrunkOpen: Boolean = false,
    val areWindowsVented: Boolean = false,
    val isSentryModeOn: Boolean = true,
    val areHazardsFlashing: Boolean = false,
    val isHornSounding: Boolean = false,
    
    // TPMS
    val tireFl: TirePressure = TirePressure(34.2f),
    val tireFr: TirePressure = TirePressure(34.0f),
    val tireRl: TirePressure = TirePressure(35.1f),
    val tireRr: TirePressure = TirePressure(35.0f),
    
    // Remote summon
    val summonActive: Boolean = false,
    val summonDirection: String = "NONE" // "FWD", "REV", "NONE"
)
