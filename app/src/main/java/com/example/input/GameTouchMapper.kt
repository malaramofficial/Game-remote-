package com.example.input

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Converts normalized controller values into safe screen coordinates.
 *
 * Coordinates are normalized (0..1) so the mapper works across phone resolutions.
 * Defaults are intentionally generic; the receiver can later expose per-game calibration.
 */
data class TouchPoint(val x: Float, val y: Float) {
    fun px(width: Int, height: Int): Pair<Float, Float> =
        (x.coerceIn(0f, 1f) * width) to (y.coerceIn(0f, 1f) * height)
}

data class GameTouchProfile(
    val steeringCenter: TouchPoint = TouchPoint(0.50f, 0.84f),
    val steeringLeft: TouchPoint = TouchPoint(0.22f, 0.84f),
    val steeringRight: TouchPoint = TouchPoint(0.78f, 0.84f),
    val throttle: TouchPoint = TouchPoint(0.90f, 0.78f),
    val brake: TouchPoint = TouchPoint(0.72f, 0.78f),
    val clutch: TouchPoint = TouchPoint(0.58f, 0.78f),
    val gear: TouchPoint = TouchPoint(0.50f, 0.18f),
    val nitro: TouchPoint = TouchPoint(0.88f, 0.18f)
)

class GameTouchMapper(context: Context) {
    private val prefs = context.getSharedPreferences("game_touch_mapper", Context.MODE_PRIVATE)

    fun profile(): GameTouchProfile = GameTouchProfile(
        steeringCenter = point("steer_center", 0.50f, 0.84f),
        steeringLeft = point("steer_left", 0.22f, 0.84f),
        steeringRight = point("steer_right", 0.78f, 0.84f),
        throttle = point("throttle", 0.90f, 0.78f),
        brake = point("brake", 0.72f, 0.78f),
        clutch = point("clutch", 0.58f, 0.78f),
        gear = point("gear", 0.50f, 0.18f),
        nitro = point("nitro", 0.88f, 0.18f)
    )

    fun savePoint(key: String, point: TouchPoint) {
        prefs.edit()
            .putFloat("${key}_x", point.x.coerceIn(0f, 1f))
            .putFloat("${key}_y", point.y.coerceIn(0f, 1f))
            .apply()
    }

    fun reset() {
        prefs.edit().clear().apply()
    }

    private fun point(key: String, x: Float, y: Float): TouchPoint =
        TouchPoint(
            prefs.getFloat("${key}_x", x),
            prefs.getFloat("${key}_y", y)
        )
}
