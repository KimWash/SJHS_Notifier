package com.example.sjhs_notifier_kotlin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edittable.*
import kotlinx.android.synthetic.main.activity_meal.*
import kotlinx.android.synthetic.main.activity_meal.toolbar
import java.util.EnumSet.range

class editTableActivity : AppCompatActivity()  {
    var selectedSubject:Int? = null
    var selectedTeacher:Int? = null
    var selectedDay:Int? = null
    var selectedsPeriod:Int? = null
    var selectedePeriod:Int? = null
    val editTableContext: Context = this

    fun uiStartUp(){
        setSupportActionBar(toolbar)
        getSupportActionBar()?.title = "수정"
        teacherSpinner.setEnabled(false)
        teacherSpinner.setClickable(false)
        sPeriodSpinner.setEnabled(false)
        sPeriodSpinner.setClickable(false)
        ePeriodSpinner.setEnabled(false)
        ePeriodSpinner.setClickable(false)
        daySpinner.setEnabled(false)
        daySpinner.setClickable(false)
        subjectSpinner.setSelection(0, false)
        teacherSpinner.setSelection(0, false)
        daySpinner.setSelection(0, false)
        sPeriodSpinner.setSelection(0, false)
        ePeriodSpinner.setSelection(0, false)

    }
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MainActivity.isNightModeActive(this) == true){
            setTheme(R.style.DarkTheme)
        }
        else if(MainActivity.isNightModeActive(this) == false){
            setTheme(R.style.LightTheme)
        }
        setContentView(R.layout.activity_edittable)
        uiStartUp()



        var subjectItems = resources.getStringArray(R.array.subject)
        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subjectItems)
        subjectSpinner.adapter = myAdapter
        subjectSpinner.setSelection(0, false)
        subjectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                teacherSpinner.setEnabled(true)
                teacherSpinner.setClickable(true)
                selectedSubject = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        var teacherItems = resources.getStringArray(R.array.teacher)
        var teacherItem:ArrayList<String> = ArrayList<String>()
        when (selectedSubject){
            0 -> teacherItem.add(teacherItems[0])
            1 -> teacherItem.add(teacherItems[5])
            2 -> teacherItem.add(teacherItems[7])
        }
        val teacherAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, teacherItems)
        teacherSpinner.adapter = teacherAdapter
        teacherSpinner.setSelection(0, false)
        teacherSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                daySpinner.setEnabled(true)
                daySpinner.setClickable(true)
                selectedTeacher = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        var dayItems = resources.getStringArray(R.array.day)
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dayItems)
        daySpinner.adapter = dayAdapter
        daySpinner.setSelection(0, false)
        daySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                sPeriodSpinner.setEnabled(true)
                sPeriodSpinner.setClickable(true)
                selectedDay = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        var periodItems = resources.getStringArray(R.array.period)
        var ePeriodItems = mutableListOf<String>()
        val periodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, periodItems)
        sPeriodSpinner.adapter = periodAdapter
        sPeriodSpinner.setSelection(0, false)
        sPeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.

                selectedsPeriod = position
                for (x in 0..periodItems.size -1){
                    Log.e("DEBUG", "$x 차시기, 선택:$selectedsPeriod periodItem: " + periodItems[x])
                    if (periodItems[x].split("교".toRegex())[0].toInt()-1 >= selectedsPeriod!!){
                        ePeriodItems.add((x+1).toString() + "교시")
                    }
                }
                Log.e("DEBUG", ePeriodItems.toString())
                val ePeriodAdapter = ArrayAdapter(editTableContext, android.R.layout.simple_spinner_dropdown_item, ePeriodItems)
                ePeriodSpinner.adapter = ePeriodAdapter
                ePeriodSpinner.setEnabled(true)
                ePeriodSpinner.setClickable(true)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        ePeriodSpinner.setSelection(0, false)
        ePeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                selectedePeriod = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


    }
}