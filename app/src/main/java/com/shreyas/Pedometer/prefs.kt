package com.shreyas.Pedometer

import android.content.Context
import android.content.SharedPreferences

val PREFNAME: String = "COUNTPREF"
val MODE: Int = 0
val COUNTNAME: String = "COUNT"

fun setCount(context: Context, count: Int) {
    if (getSharedData(context) == ServiceState.STARTED) {
        val pref = getCountPrefs(context)
        pref.edit().also {
            it.putInt(COUNTNAME, count)
            it.apply()
        }
    }
}

fun getCount(context: Context):Int {
    val pref = getCountPrefs(context)
    return pref.getInt(COUNTNAME, 0)
}

fun getCount(context: Context, value:Int): SharedPreferenceIntLiveData {
    return SharedPreferenceIntLiveData(getCountPrefs(context), COUNTNAME, 0)
}

fun getCountPrefs(context: Context): SharedPreferences {

    return context.getSharedPreferences(PREFNAME, MODE)
}