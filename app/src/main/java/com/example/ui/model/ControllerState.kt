package com.example.ui.model

enum class Gear(val label: String) {
    PARK("P"),
    REVERSE("R"),
    NEUTRAL("N"),
    DRIVE("D"),
    SPORT("S")
}

data class ControllerState(
    val steeringAngle: Float = 0f,
    val throttlePercent: Float = 0f,
    val brakePercent: Float = 0f,
    val clutchPercent: Float = 0f,
    val gear: Gear = Gear.PARK,
    val speedKmh: Float = 0f,
    val rpm: Int = 0,
    val isHighBeamOn: Boolean = false,
    val isNitroActive: Boolean = false,
    val isCruiseControlOn: Boolean = false,
    val isTiltSteeringEnabled: Boolean = false,
    val targetIp: String = "192.168.4.1",
    val targetPort: Int = 8888,
    val isTransmittingUdp: Boolean = false,
    val lastPacketSent: String = ""
)
