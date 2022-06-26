package com.klice.pressureheight.controller

import kotlinx.coroutines.*
import com.klice.pressureheight.common.Location
import com.klice.pressureheight.common.Station
import com.klice.pressureheight.service.DataProviderService
import com.klice.pressureheight.service.SubscriptionService
import com.klice.pressureheight.service.WeatherService
import kotlin.math.pow


data class PressureHeightControllerData(
    val location: Location?,
    val pressure: Double?,
    val station: Station?,
    val altitude: Double?
)

class PressureHeightController(
    private val weatherService: WeatherService,
    pressureService: DataProviderService<Double>,
    locationService: DataProviderService<Location>,
) : SubscriptionService<PressureHeightControllerData>() {
    private var currentPressure: Double? = null

    init {
        locationService.registerListener { location ->
            currentLocation = location
            notifySubscribers(getState())
        }
        pressureService.registerListener { pressure ->
            currentPressure = pressure
            notifySubscribers(getState())
        }
    }

    private var currentStation: Station? = null
    private var currentLocation: Location? = null

    private fun selfCheck(): Boolean {
        if (currentLocation == null) return false
        if (currentPressure == null) return false

        // TODO: update station based on time and location
        if (currentStation == null) {
            runBlocking { currentStation = weatherService.getNearestStation(currentLocation!!) }
        }
        return (currentStation != null)
    }


    private fun getState(): PressureHeightControllerData {
        return PressureHeightControllerData(
            location = currentLocation,
            pressure = currentPressure,
            station = currentStation,
            altitude = getAltitude()
        )
    }

    fun getAltitude(): Double? {
        if (selfCheck()) {
            val seaLevelPressure = currentStation!!.pressure
            val temp = currentStation!!.temperature
            return (((seaLevelPressure / currentPressure!!).pow(1 / 5.257) - 1) * (temp + 273.15)) / 0.0065
        }
        return null
    }
}
