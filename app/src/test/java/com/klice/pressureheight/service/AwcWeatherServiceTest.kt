package com.klice.pressureheight.service

import com.fasterxml.jackson.module.kotlin.readValue
import com.klice.pressureheight.common.Location
import com.klice.pressureheight.common.Station
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


internal class AwcWeatherServiceTest {

    var ws: WeatherService = AwcWeatherService()

    @Test
    fun getPressure() {
//        mockkConstructor(URL::class)
//        every {
//            constructedWith<URL>(match {
//                it.toString().startsWith("https://aviationweather.gov")
//            }).readText()
//        } returns "<response></response>"
//        every { url.readText() } returns "<test></test>"
        val loc = Location(39.87, -104.65)
        var pressure: Station
        runBlocking {
            pressure = ws.getNearestStation(loc)
        }
        assertEquals(1000, pressure)
    }

    @Test
    fun `test xml deserialization`() {
        val file = this.javaClass.classLoader!!.getResource("response.xml")
        val value = kotlinXmlMapper.readValue<AwcWeatherServiceResponse>(file.readText())
        assertEquals(7, value.data.metar.size)
        assertEquals("KBKF", value.data.metar[0].id)
        assertEquals(-104.75, value.data.metar[0].longitude)
    }

    @Test
    fun `test actual html call`() {
        var res: Station
        runBlocking {
            res = ws.getNearestStation(Location(1.0, 1.0))
        }
        assertEquals("KBKF", res.id)
        assertEquals(30.008858, res.pressure)
    }
}
