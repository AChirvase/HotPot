package com.alex.mainmodule.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alex.mainmodule.R
import org.koin.core.KoinComponent

class RestaurantsActivity : AppCompatActivity(), KoinComponent {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)
        Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
    }
}