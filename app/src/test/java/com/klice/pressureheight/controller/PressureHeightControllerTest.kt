package com.klice.pressureheight.controller

import com.klice.pressureheight.common.Location
import com.klice.pressureheight.common.Station
import com.klice.pressureheight.common.StationType
import com.klice.pressureheight.service.DataProviderService
import com.klice.pressureheight.service.WeatherService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class MockStation(
    override val pressure: Double,
    override val temperature: Double,
) : Station() {
    override val name = "Mock Station"
    override val id = "MOCK"
    override val type = StationType.AIRPORT
}

class MockProvider<T> : DataProviderService<T> {
    private lateinit var subscriber: (d: T) -> Unit
    override fun registerListener(listener: (d: T) -> Unit) {
        subscriber = listener
    }

    fun setValue(value: T) {
        subscriber(value)
    }
}

internal class PressureHeightControllerTest {
    @BeforeEach
    internal fun setUp() {
        coEvery { mockWS.getNearestStation(any()) } returns station
        controller = PressureHeightController(mockWS, mockPS, mockLS)
    }

    private val mockWS = mockk<WeatherService>()
    private val mockLS = MockProvider<Location>()
    private val mockPS = MockProvider<Double>()
    private val station = MockStation(1013.25, 15.0)
    private lateinit var controller: PressureHeightController

    @Test
    fun `no location and no pressure`() {
        val altitude = controller.getAltitude()
        assertEquals(null, altitude)
    }

    @Test
    fun `location and no pressure`() {
        mockLS.setValue(Location(1.0, 1.0))
        val altitude = controller.getAltitude()
        assertEquals(null, altitude)
    }

    @Test
    fun `location and pressure`() {
        mockLS.setValue(Location(1.0, 1.0))
        mockPS.setValue(900.0)
        val altitude = controller.getAltitude()
        assertEquals(1010.83, altitude!!, 0.1)
    }
}