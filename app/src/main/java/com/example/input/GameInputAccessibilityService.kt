package com.example.input

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.example.network.GameRemoteProtocol
import com.example.network.GameRemoteReceiver
import com.example.network.ReceivedControllerState
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

/**
 * Phone A game-side bridge.
 *
 * It keeps listening even after the Game Remote activity is no longer visible.
 * Android's AccessibilityService is used only after the user explicitly enables
 * the service in system Accessibility settings.
 */
class GameInputAccessibilityService : AccessibilityService() {

    private lateinit var mapper: GameTouchMapper
    private var receiver: GameRemoteReceiver? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val enabled = AtomicBoolean(false)
    private var screenWidth = 1
    private var screenHeight = 1
    private var lastSteer = 0f
    private var lastThrottle = 0f
    private var lastBrake = 0f
    private var lastClutch = 0f
    private var lastGear = ""
    private var lastNitro = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        mapper = GameTouchMapper(this)
        enabled.set(true)
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels
        receiver = GameRemoteReceiver(
            port = GameRemoteProtocol.DEFAULT_PORT,
            timeoutMs = 450L,
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
        ).also { r ->
            r.start(
                onState = { state -> mainHandler.post { applyState(state) } },
                onConnectionChanged = { },
                onSafeState = { mainHandler.post { safeState() } }
            )
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Game input is coordinate based; no event inspection is required.
    }

    override fun onInterrupt() {
        safeState()
    }

    override fun onDestroy() {
        enabled.set(false)
        receiver?.stop()
        receiver = null
        safeState()
        super.onDestroy()
    }

    private fun applyState(state: ReceivedControllerState) {
        if (!enabled.get()) return

        val profile = mapper.profile()

        if (abs(state.steering - lastSteer) > 0.025f) {
            steer(state.steering, profile)
            lastSteer = state.steering
        }

        // Tap controls use a threshold edge so a held pedal does not spam taps.
        if (state.throttle > 0.05f && lastThrottle <= 0.05f) tap(profile.throttle)
        if (state.brake > 0.05f && lastBrake <= 0.05f) tap(profile.brake)
        if (state.clutch > 0.05f && lastClutch <= 0.05f) tap(profile.clutch)

        if (state.gear != lastGear && state.gear.isNotBlank()) {
            tap(profile.gear)
            lastGear = state.gear
        }

        if (state.nitro && !lastNitro) tap(profile.nitro)
        lastNitro = state.nitro

        lastThrottle = state.throttle
        lastBrake = state.brake
        lastClutch = state.clutch
    }

    private fun steer(value: Float, profile: GameTouchProfile) {
        val target = when {
            value < 0f -> lerp(profile.steeringCenter, profile.steeringLeft, -value)
            else -> lerp(profile.steeringCenter, profile.steeringRight, value)
        }
        val (tx, ty) = target.px(screenWidth, screenHeight)
        val (cx, cy) = profile.steeringCenter.px(screenWidth, screenHeight)

        val path = Path().apply {
            moveTo(cx, cy)
            lineTo(tx, ty)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(StrokeDescription(path, 0L, 90L))
            .build()
        dispatchGesture(gesture, null, null)
    }

    private fun tap(point: TouchPoint) {
        val (x, y) = point.px(screenWidth, screenHeight)
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(StrokeDescription(path, 0L, 45L))
            .build()
        dispatchGesture(gesture, null, null)
    }

    private fun safeState() {
        lastSteer = 0f
        lastThrottle = 0f
        lastBrake = 0f
        lastClutch = 0f
        // A neutral steering gesture is safer than leaving a prior gesture active.
        if (::mapper.isInitialized) steer(0f, mapper.profile())
    }

    private fun lerp(a: TouchPoint, b: TouchPoint, t: Float): TouchPoint =
        TouchPoint(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)
}
