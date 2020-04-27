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
        setSupportActionBar(timeTableToolbar)
        getSupportActionBar()?.title = "시간표"
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        var textArray:MutableList<TextView> = arrayListOf(subject0, subject1, subject2, subject3, subject4, subject5, subject6, subject7, subject8, subject9, subject10, subject11, subject12, subject13, subject14, subject15, subject16, subject17, subject18, subject19, subject20, subject21, subject22, subject23, subject24, subject25, subject26, subject27, subject28, subject29, subject30, subject31, subject32, subject33, subject34)
        for (x in 0..34){
            textArray[x].setOnClickListener {
                setTable(x)
            }
        }
        loadTable()
    }

    fun setTable(period:Int){
        val editIntent = Intent(this, editTableActivity::class.java)
        editIntent.putExtra("period", period)
        editIntent.putExtra("from", 1)
        startActivity(editIntent)
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
                editIntent.putExtra("from", 0)
                startActivity(editIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "RESUME")
        loadTable()
    }


    fun loadTable(){
        var subjects = resources.getStringArray(R.array.subject)
        var teachers = resources.getStringArray(R.array.teacher)
        var textArray:MutableList<TextView> = arrayListOf(subject0, subject1, subject2, subject3, subject4, subject5, subject6, subject7, subject8, subject9, subject10, subject11, subject12, subject13, subject14, subject15, subject16, subject17, subject18, subject19, subject20, subject21, subject22, subject23, subject24, subject25, subject26, subject27, subject28, subject29, subject30, subject31, subject32, subject33, subject34)
        val helper = DataBaseHelper(this)
        val db = helper.writableDatabase
        val cursor = db.rawQuery("select subject, teacher, day, sPeriod, ePeriod from tb_tt", null)
        while (cursor.moveToNext()){ //한줄한줄...
            textArray[(cursor.getInt(2) * 7) - (7-cursor.getInt(3))-1].setText(subjects[cursor.getInt(0)] + "\n" + teachers[cursor.getInt(1)])
            Log.e(TAG, cursor.getString(3) + " , " + cursor.getString(4))
            for (x in cursor.getInt(3) + 1..cursor.getInt(4)){
                Log.e(TAG, (x).toString())
                textArray[(cursor.getInt(2) * 7) - (7-x)-1].setText(subjects[cursor.getInt(0)] + "\n" + teachers[cursor.getInt(1)])
            }
        }
    }

}