package com.klice.pressureheight.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.klice.pressureheight.common.Station
import com.klice.pressureheight.common.Location
import com.klice.pressureheight.common.StationType
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.QueryMap

internal val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
    setDefaultUseWrapper(false)
}).registerKotlinModule()

interface AwcWeatherServiceInterface {
    @GET("/adds/dataserver_current/httpparam")
    suspend fun getStationByLocationAndRange(@QueryMap options: Map<String, String>): AwcWeatherServiceResponse
}

class AwcWeatherService : WeatherService {
    private val baseUrl = "https://aviationweather.gov"
    private var service: AwcWeatherServiceInterface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create(kotlinXmlMapper))
            .build()
        service = retrofit.create()

    }

    private fun findClosest(data: AwcWeatherServiceData): AwcWeatherServiceMetar {
        return data.metar[0]
    }

    override suspend fun getNearestStation(location: Location): Station {
        val reqParams = mapOf(
            "dataSource" to "metars",
            "requestType" to "retrieve",
            "format" to "xml",
            "radialDistance" to "20;-104.65,39.83",
            "hoursBeforeNow" to "3",
        )
        return findClosest(service.getStationByLocationAndRange(reqParams).data)
    }
}


@JsonRootName("response")
@JsonIgnoreProperties(ignoreUnknown = true)
data class AwcWeatherServiceResponse(
    @set:JsonProperty("data")
    var data: AwcWeatherServiceData
)

@JsonRootName("data")
data class AwcWeatherServiceData(
    @set:JsonProperty("num_results")
    var numResults: Int,

    @set:JsonProperty("METAR")
    var metar: List<AwcWeatherServiceMetar>
)

@JsonRootName("METAR")
@JsonIgnoreProperties(ignoreUnknown = true)
data class AwcWeatherServiceMetar(
    @set:JsonProperty("station_id")
    override var id: String,
    var latitude: Double,
    var longitude: Double,
    @set:JsonProperty("temp_c")
    override var temperature: Double,
    @set:JsonProperty("altim_in_hg")
    override var pressure: Double,
) : Station() {
    override val name = id
    override val type = StationType.AIRPORT
}
