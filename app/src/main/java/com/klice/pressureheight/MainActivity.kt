package com.klice.pressureheight

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.klice.pressureheight.controller.PressureHeightController
import com.klice.pressureheight.controller.PressureHeightControllerData
import com.klice.pressureheight.service.AndroidLocationSensor
import com.klice.pressureheight.service.AndroidPressureSensor
import com.klice.pressureheight.service.AwcWeatherService
import com.klice.pressureheight.ui.theme.PressureHeightTheme


class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        val pressureHeightController = PressureHeightController(
            weatherService = AwcWeatherService(),
            pressureService = AndroidPressureSensor(this),
            locationService = AndroidLocationSensor(fusedLocationClient),
        )

        setContent {
            PressureHeightTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var pressure by remember {
                        mutableStateOf(
                            PressureHeightControllerData(
                                null,
                                null,
                                null,
                                null,
                            )
                        )
                    }
                    pressureHeightController.registerListener { pressure = it }
                    PressureData(pressure)
                }
            }
        }
    }
}

@Composable
fun PressureData(data: PressureHeightControllerData) {
    Column {
        Text(text = data.altitude.toString())
        Text(text = data.pressure.toString())
        data.location?.let {
            Text(text = it.lat.toString())
            Text(text = it.long.toString())
        }
        data.station?.let {
            Text(text = it.id)
        }
    }
}