package com.travelwell.dosssik.replikavoiceprototype

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.build_in_activity.*
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        rxPermissions = RxPermissions(this)
    }

    override fun onStart() {
        super.onStart()

        build_in_btn.setOnClickListener {
            startActivity(Intent(this, BuildInRecognitionActivity::class.java))
        }

        yandex_btn.setOnClickListener {
//            startActivity(Intent(this, YandexKitActivity::class.java))
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show()
        }

        requestRxPermissions()
    }

    override fun onResume() {
        super.onResume()
        requestRxPermissions()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
    }


    private fun requestRxPermissions() {
        rxPermissions
                .request(Manifest.permission.RECORD_AUDIO)
                .subscribe({ granted ->
                    if (!granted) { // Always true pre-M
                        Toast.makeText(this, "Please, give me permissions", Toast.LENGTH_LONG).show()
                    }
                })
    }

}
