package com.shreyas.Pedometer

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EndlessService : Service(), SensorEventListener {

    private var wakeLock:PowerManager.WakeLock? = null
    private var isServiceStarted = false
    lateinit var sm:SensorManager

    override fun onCreate() {
        super.onCreate()

        var notification = createNotification()
        startForeground(1, notification)
    }

    private fun createNotification(): Notification {
        val CHANNELID = "ENDLESS CHANNEL"

        if (Build.VERSION.SDK_INT >= O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNELID,
                "Endless Service notifications channel",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Endless Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it
            }
            notificationManager.createNotificationChannel(channel)
        }
        val pendingIntent:PendingIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

       val  builder:Notification.Builder  = if (Build.VERSION.SDK_INT >= O) {
            Notification.Builder(this, CHANNELID)
       }else {
            Notification.Builder(this)
       }
        return builder
            .setContentTitle("Pedometer app is running")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null ){
            val actions = intent.action
            when (actions) {
                Actions.START.name -> startService(intent, startId)
                Actions.STOP.name -> stopService(intent, startId)
                else -> {
                    Log.d("service_test_123", "shouldNot reach here")

                }
            }
        }
        return START_STICKY
    }

    private fun startService(intent: Intent, startId: Int) {
        if (isServiceStarted) return
        Toast.makeText(this, "Service is started", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setSharedData(this, ServiceState.STARTED)

        //when phone sleeps
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ENDLESS::LOCK").apply {
                acquire()
            }
        }

        //start coroutines
        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO){
                    setPedometer()
                }
                delay(500)
            }
        }
    }

    private fun setPedometer() {
         sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) as Sensor
        if (sensor != null) {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }

    }

    private fun stopService(intent: Intent, startId: Int) {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show()
        setCount(this, 0)
        try {
            sm.unregisterListener(this)
        }catch (e:Exception) {

        }
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        }catch (e:Exception) {
            Log.d("service_test_123", "shouldNot reach here:Exception  "+e.message)
        }
        isServiceStarted = false
        setSharedData(this, ServiceState.STOPPED)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        var count = getCount(this)
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            setCount(this, count+1)
        }
        Log.d("qw44","sdasd:     ${(event.sensor.type == Sensor.TYPE_STEP_COUNTER)}"+"        "+ getCount(this))
    }
}