package com.example.sjhs_notifier_kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_meal.*

public class settingActivity : AppCompatActivity()  {

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        if (MainActivity.isNightModeActive(this) == true) {
            setTheme(R.style.DarkTheme)
        } else if (MainActivity.isNightModeActive(this) == false) {
            setTheme(R.style.LightTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.title = "설정"
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }
}