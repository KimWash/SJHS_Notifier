package com.example.sjhs_notifier_kotlin

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_meal.*

class timeTableActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MainActivity.isNightModeActive(this) == true){
            setTheme(R.style.DarkTheme)
        }
        else if(MainActivity.isNightModeActive(this) == false){
            setTheme(R.style.LightTheme)
        }
        setContentView(R.layout.activity_timetable)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.title = "시간표"
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        var menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.tabletoolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        when (item.itemId){
            R.id.edit_table -> {
                Log.e("Debug", "시간표 수정진입")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun loadTable(){

    }
}