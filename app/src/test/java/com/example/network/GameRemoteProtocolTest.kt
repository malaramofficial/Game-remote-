package com.example.network

import com.example.ui.model.ControllerState
import com.example.ui.model.Gear
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class GameRemoteProtocolTest {
    @Test
    fun encodeDecode_preservesNormalizedControls() {
        val original = ControllerState(
            steeringAngle = 45f,
            throttlePercent = 80f,
            brakePercent = 10f,
            gear = Gear.SPORT,
            isHighBeamOn = true,
            isNitroActive = true
        )
        val decoded = GameRemoteProtocol.decode(GameRemoteProtocol.encode(original))
        assertNotNull(decoded)
        assertEquals(0.5f, decoded!!.steering, 0.001f)
        assertEquals(0.8f, decoded.throttle, 0.001f)
        assertEquals(0.1f, decoded.brake, 0.001f)
        assertEquals("S", decoded.gear)
        assertEquals(true, decoded.highBeam)
        assertEquals(true, decoded.nitro)
    }

    @Test
    fun decode_rejectsInvalidRange() {
        val invalid = """{"v":1,"type":"controller_state","seq":1,"ts":1,"steer":2.0,"throttle":0,"brake":0,"gear":"P"}"""
        assertNull(GameRemoteProtocol.decode(invalid))
    }

    @Test
    fun decode_rejectsWrongVersion() {
        val invalid = """{"v":99,"type":"controller_state","seq":1,"ts":1,"steer":0,"throttle":0,"brake":0,"gear":"P"}"""
        assertNull(GameRemoteProtocol.decode(invalid))
    }
}
