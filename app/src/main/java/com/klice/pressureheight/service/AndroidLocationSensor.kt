package com.klice.pressureheight.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import android.location.Location as AndroidLocation
import com.google.android.gms.location.LocationServices
import com.klice.pressureheight.common.Location

@SuppressLint("MissingPermission")
class AndroidLocationSensor(
    locationClient: FusedLocationProviderClient
) : DataProviderService<Location>, SubscriptionService<Location>() {
    init {
        locationClient.lastLocation
            .addOnSuccessListener { location: AndroidLocation? ->
                if (location != null) {
                    notifySubscribers(Location(location.latitude, location.longitude))
                }
            }
    }
}