package com.example.myrunsta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GpsInputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps_input)
        title = "Map"
    }
}