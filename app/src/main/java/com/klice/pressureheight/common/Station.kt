package com.klice.pressureheight.common

enum class StationType {
    AIRPORT,
}

abstract class Station{
    abstract val name: String
    abstract val id: String
    abstract val pressure: Double
    abstract val temperature: Double
    abstract val type: StationType
}
