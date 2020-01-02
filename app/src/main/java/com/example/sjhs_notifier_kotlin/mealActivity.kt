package com.example.sjhs_notifier_kotlin

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kr.go.neis.api.School
import java.text.SimpleDateFormat
import java.util.*

class mealActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)

    }
}