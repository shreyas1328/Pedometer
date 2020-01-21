package com.shreyas.Pedometer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.O

class ServiceReciver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getSharedData(context) == ServiceState.STARTED) {
            Intent(context, EndlessService::class.java).also {serviceIntent ->
                serviceIntent.action = Actions.START.name
                if (Build.VERSION.SDK_INT >= O) {
                    context.startForegroundService(intent)
                    return
                }
                context.startService(intent)
            }
        }
    }

}