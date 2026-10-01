package com.example.network

import com.example.ui.model.ControllerState
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicLong

/** Versioned wire format for Phone B -> Phone A local Wi-Fi control. */
object GameRemoteProtocol {
    const val VERSION = 2
    const val DEFAULT_PORT = 8888
    private const val MAX_PACKET_BYTES = 4096
    private val sequence = AtomicLong(0)

    fun encode(state: ControllerState): String =
        JSONObject()
            .put("v", VERSION)
            .put("type", "controller_state")
            .put("seq", sequence.incrementAndGet())
            .put("ts", System.currentTimeMillis())
            .put("steer", (state.steeringAngle / 90f).coerceIn(-1f, 1f))
            .put("throttle", (state.throttlePercent / 100f).coerceIn(0f, 1f))
            .put("brake", (state.brakePercent / 100f).coerceIn(0f, 1f))
            .put("clutch", (state.clutchPercent / 100f).coerceIn(0f, 1f))
            .put("gear", state.gear.label)
            .put("highBeam", state.isHighBeamOn)
            .put("nitro", state.isNitroActive)
            .put("cruise", state.isCruiseControlOn)
            .toString()

    fun decode(payload: String): ReceivedControllerState? {
        return try {
            if (payload.toByteArray(Charsets.UTF_8).size > MAX_PACKET_BYTES) return null
            val json = JSONObject(payload)
            if (json.optInt("v", -1) != VERSION) return null
            if (json.optString("type") != "controller_state") return null

            val seq = json.optLong("seq", -1L)
            val ts = json.optLong("ts", -1L)
            if (seq < 0L || ts <= 0L) return null

            val steer = json.optDouble("steer", Double.NaN).toFloat()
            val throttle = json.optDouble("throttle", Double.NaN).toFloat()
            val brake = json.optDouble("brake", Double.NaN).toFloat()
            val clutch = json.optDouble("clutch", 0.0).toFloat()
            if (!steer.isFinite() || !throttle.isFinite() || !brake.isFinite() || !clutch.isFinite()) return null
            if (steer !in -1f..1f || throttle !in 0f..1f || brake !in 0f..1f || clutch !in 0f..1f) return null

            ReceivedControllerState(
                sequence = seq,
                timestampMs = ts,
                steering = steer,
                throttle = throttle,
                brake = brake,
                clutch = clutch,
                gear = json.optString("gear", "P"),
                highBeam = json.optBoolean("highBeam", false),
                nitro = json.optBoolean("nitro", false),
                cruise = json.optBoolean("cruise", false)
            )
        } catch (_: Exception) {
            null
        }
    }
}

data class ReceivedControllerState(
    val sequence: Long,
    val timestampMs: Long,
    val steering: Float,
    val throttle: Float,
    val brake: Float,
    val clutch: Float,
    val gear: String,
    val highBeam: Boolean,
    val nitro: Boolean,
    val cruise: Boolean
)
