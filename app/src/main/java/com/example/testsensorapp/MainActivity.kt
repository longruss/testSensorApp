package com.example.testsensorapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var steps = 0
    private var stepLength = 83.25f // step length in cm
    private var previousY = 0f
    private var currentY = 0f
    private var stepsThreshold = 1.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Register the sensor listener
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculate the magnitude of the acceleration
        val magnitude = sqrt(x * x + y * y + z * z)

        // Detect steps based on the magnitude of the acceleration
        currentY = magnitude
        if (Math.abs(currentY - previousY) > stepsThreshold) {
            steps++
            val distance = steps * stepLength / 100 // convert cm to meters
            findViewById<TextView>(R.id.distanceView).text = "Distance: $distance m"
        }
        previousY = currentY
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onPause() {
        super.onPause()

        // Unregister the sensor listener when the activity is paused
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()

        // Register the sensor listener when the activity is resumed
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }
}
