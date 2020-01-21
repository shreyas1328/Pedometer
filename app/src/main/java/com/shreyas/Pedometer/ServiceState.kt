package com.shreyas.Pedometer

import android.content.Context
import android.content.SharedPreferences

enum class ServiceState {
    STARTED,
    STOPPED
}

private val name:String = "MYAPP"
private val key:String = "SERVICESTATE"
private val mode:Int = 0

//sharedPefs
fun getPrefs(context: Context):SharedPreferences {
    return context.getSharedPreferences(name, mode)
}

fun setSharedData(context: Context, start: ServiceState) {
    val prefs = getPrefs(context)
    prefs.edit().let {
        it.putString(key, start.name)
        it.apply()
    }
}

fun getSharedData(context: Context):ServiceState {
    val prefs = getPrefs(context)
    val value = prefs.getString(key, ServiceState.STOPPED.name)
    return ServiceState.valueOf(value!!)
}