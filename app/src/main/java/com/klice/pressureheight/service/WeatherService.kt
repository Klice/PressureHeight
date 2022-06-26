package com.klice.pressureheight.service

import com.klice.pressureheight.common.Station
import com.klice.pressureheight.common.Location

interface WeatherService {
    suspend fun getNearestStation(location: Location): Station
}
