package com.example.modul89_a_10905_project2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity(), SensorEventListener {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId1 = 101
    private val description = "Test notification"

    private lateinit var sensorManager : SensorManager
    private lateinit var square : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        setUpSensorStuff()
    }

    private fun setUpSensorStuff() {
        // Keeps phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        square = findViewById(R.id.tv_square)

        // Create the sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Specify the sensor you want to listen to
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Checks for the sensor we have registered
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            //Log.d("Main", "onSensorChanged: sides ${event.values[0]} front/back ${event.values[1]} ")

            // Sides = Tilting phone left(10) and right(-10)
            val sides = event.values[0]

            // Up/Down = Tilting phone up(10), flat (0), upside-down(-10)
            val upDown = event.values[1]

            square.apply {
                rotationX = upDown * 3f
                rotationY = sides * 3f
                rotation = -sides
                translationX = sides * -10
                translationY = upDown * 10
            }

            // Changes the colour of the square if it's completely flat
            val color = if (upDown.toInt() == 0 && sides.toInt() == 0) Color.GREEN else Color.RED
            square.setBackgroundColor(color)

            square.text = "up/down ${upDown.toInt()}\nleft/right ${sides.toInt()}"

            if (sides.toInt() == 1 && upDown.toInt() == 1) {
                val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,0)

                notificationChannel = NotificationChannel(CHANNEL_ID_1, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(false)
                notificationManager.createNotificationChannel(notificationChannel)

                val builder = NotificationCompat.Builder(this,CHANNEL_ID_1)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Yeayy")
                    .setContentText("UGD 8 & 9 Project 2 Sudah Selesai")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                with(NotificationManagerCompat.from(this))
                {
                    notify(notificationId1, builder.build())
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor:Sensor?, accuracy:Int){
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}