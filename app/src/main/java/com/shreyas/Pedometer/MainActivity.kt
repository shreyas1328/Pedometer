package com.shreyas.Pedometer

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.O
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var isRuning:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (getSharedData(this) == ServiceState.STARTED) {
            toggle.isChecked = true
        }else {
            toggle.isChecked = false
        }

        setToggle()

    }

    override fun onResume() {
        super.onResume()

       getCount(this, 0).observe(this, Observer {
           tv_count.text = it.toString()
       })
    }

    override fun onPause() {
        super.onPause()

        isRuning = false
    }

    private fun setToggle() {
        toggle.setOnClickListener(View.OnClickListener {
            if (toggle.isChecked) {
                startService1()
            }else {
                stopService1()
            }
        })
    }

    private fun startService1() {
        actionOnService(Actions.START)
    }

    private fun stopService1() {
        actionOnService(Actions.STOP)
    }

    private fun actionOnService(action: Actions) {
        if (getSharedData(this) == ServiceState.STOPPED && action == Actions.STOP) return

        Intent(this, EndlessService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= O) {
                startForegroundService(it)
                return
            }
            startService(it)
        }
    }

}
