package com.example.sjhs_notifier_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edittable.*
import kotlinx.android.synthetic.main.activity_meal.*
import kotlinx.android.synthetic.main.activity_meal.toolbar
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_timetable.*

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

        loadTable()
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
                val editIntent = Intent(this@timeTableActivity, editTableActivity::class.java)
                startActivity(editIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun loadTable(){
        var subjects = resources.getStringArray(R.array.subject)
        var teachers = resources.getStringArray(R.array.teacher)
        var days = resources.getStringArray(R.array.day)
        var periods = resources.getStringArray(R.array.period)

        var textArray:MutableList<TextView> = arrayListOf(subject0, subject1, subject2, subject3, subject4, subject5, subject6, subject7, subject8, subject9, subject10, subject11, subject12, subject13, subject14, subject15, subject16, subject17, subject18, subject19, subject20, subject21, subject22, subject23, subject24, subject25, subject26, subject27, subject28, subject29, subject30, subject31, subject32, subject33, subject34)
        val helper = DataBaseHelper(this)
        val db = helper.writableDatabase
        val cursor = db.rawQuery("select subject, teacher, day, sPeriod, ePeriod from tb_tt", null)
        while (cursor.moveToNext()){ //한줄한줄...
            textArray[(cursor.getInt(2) * 7) - (7-cursor.getInt(3))-1].setText(subjects[cursor.getInt(0)] + "\n" + teachers[cursor.getInt(1)])
        }
    }

}