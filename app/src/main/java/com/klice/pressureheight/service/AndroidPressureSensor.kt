package com.klice.pressureheight.service

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AndroidPressureSensor(activity: Activity) : DataProviderService<Double>, SubscriptionService<Double>(),
    SensorEventListener {
    private var sensorManager: SensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var sensor: Sensor? = null

    init {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        sensor.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        notifySubscribers(event.values[0].toDouble())
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
}