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
    private var gravity = FloatArray(3)
    private var linearAcceleration = FloatArray(3)
    private val alpha = 0.8f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Register the sensor listener
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Apply the low-pass filter
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

        // Subtract the gravity component of the acceleration
        linearAcceleration[0] = event.values[0] - gravity[0]
        linearAcceleration[1] = event.values[1] - gravity[1]
        linearAcceleration[2] = event.values[2] - gravity[2]

        // Calculate the magnitude of the linear acceleration
        val magnitude = sqrt(linearAcceleration[0] * linearAcceleration[0] + linearAcceleration[1] * linearAcceleration[1] + linearAcceleration[2] * linearAcceleration[2])

        // Display the acceleration value
        findViewById<TextView>(R.id.accelerationView).text = "Ускорение: $magnitude m/s²"

        // Detect steps based on the magnitude of the linear acceleration
        if (magnitude > 10) {
            steps++
            val distance = steps * stepLength / 100 // convert cm to meters
            findViewById<TextView>(R.id.distanceView).text = "Расстояние: $distance m"
            findViewById<TextView>(R.id.stepsView).text = "Количество шагов: $steps"
        }
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