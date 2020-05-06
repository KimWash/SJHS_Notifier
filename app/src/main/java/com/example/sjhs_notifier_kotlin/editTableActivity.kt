package com.example.sjhs_notifier_kotlin

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edittable.*


class editTableActivity : AppCompatActivity()  {
    var selectedSubject:Int = 0
    var selectedTeacher:Int = 0
    var selectedDay:Int = 0
    var selectedsPeriod:Int = 0
    var selectedePeriod:Int = 0
    val editTableContext: Context = this
    private var helper:DataBaseHelper? = null
    private var db: SQLiteDatabase? = null
    var day:Int? = null
    var period:Int? = null

    fun uiStartUp(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "수정"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        teacherSpinner.isEnabled = false
        teacherSpinner.isClickable = false
        sPeriodSpinner.isEnabled = false
        sPeriodSpinner.isClickable = false
        ePeriodSpinner.isEnabled = false
        ePeriodSpinner.isClickable = false
        daySpinner.isEnabled = false
        daySpinner.isClickable = false
        subjectSpinner.setSelection(0, false)
        teacherSpinner.setSelection(0, false)
        ePeriodSpinner.setSelection(0, false)
    }

    fun getSubject(day:Int, period:Int): MutableList<Int>{
        val db = helper!!.readableDatabase
        val cursor = db!!.rawQuery("select sPeriod, ePeriod from tb_tt where day = $day", null)
        var sPeriod:Int? = null
        var ePeriod:Int? = null
        val ranges:MutableList<Int> = arrayListOf()
        while (cursor.moveToNext()){
            sPeriod = cursor.getInt(0)
            ePeriod = cursor.getInt(1)
            Log.e(TAG, "---------------범위불러오기----------------\n$day , $period , $ePeriod")
            if (period in sPeriod..ePeriod){
                try{
                    for (x in sPeriod..ePeriod){
                        Log.e(TAG, "$x 차")
                        ranges.add(x)
                    }
                    Log.e(TAG, ranges.toString())
                    return ranges
                }catch (e: KotlinNullPointerException){
                    Log.e(TAG, e.toString())
                    Toast.makeText(this, "죄송합니다, 현재는 시간표에서 직접 수정할 시 두시간 이상 있는 과목은 수정이 불가합니다.\n양해 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return arrayListOf(0)
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
        helper = DataBaseHelper(this)
        db = helper!!.writableDatabase
        val editIntent = intent
        val activityFrom = editIntent.getIntExtra("from", 2)
        val gotperiod = editIntent.getIntExtra("period", 35)
        when (activityFrom){
            0 -> {
                Log.e(TAG, "직접수정")
                daySpinner.setSelection(0, false)
                sPeriodSpinner.setSelection(0, false)}
            1 -> {
                if (gotperiod == 35){
                    Toast.makeText(this, "오류가 발생한 것 같습니다. 에러코드: 35", Toast.LENGTH_SHORT)
                }
                else{
                    Log.e(TAG, "교시클릭")
                    day = gotperiod / 7
                    selectedDay = day!! + 1
                    period = gotperiod % 7
                    val range = getSubject(day!!+1, period!!+1)
                    selectedsPeriod = range[0]
                    selectedePeriod = range[range.size-1]
                    if (selectedsPeriod == 0 && selectedePeriod == 0){
                        selectedsPeriod = (gotperiod % 7) + 1
                        selectedePeriod = (gotperiod % 7) + 1
                    }

                    Log.e(TAG, "$day, $selectedsPeriod, $selectedePeriod")
                }
            }
            2 -> {}
        }



        val subjectItems = resources.getStringArray(R.array.subject)
        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subjectItems)
        subjectSpinner.adapter = myAdapter
        subjectSpinner.setSelection(0, false)
        subjectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                teacherSpinner.isEnabled = true
                teacherSpinner.isClickable = true
                selectedSubject = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        val teacherItems = resources.getStringArray(R.array.teacher)
        val teacherItem:ArrayList<String> = ArrayList<String>()
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
                daySpinner.isEnabled = true
                daySpinner.isClickable = true
                selectedTeacher = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        val dayItems = resources.getStringArray(R.array.day)
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dayItems)
        daySpinner.adapter = dayAdapter
        daySpinner.setSelection(0, false)
        if (activityFrom == 1){
            daySpinner.setSelection(day!! + 1, false)
        }
        daySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                sPeriodSpinner.isEnabled = true
                sPeriodSpinner.isClickable = true
                selectedDay = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        val periodItems = resources.getStringArray(R.array.period)
        var ePeriodItems = mutableListOf<String>()
        val periodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, periodItems)
        sPeriodSpinner.adapter = periodAdapter
        sPeriodSpinner.setSelection(0, false)
        if (activityFrom == 1){
            Log.e(TAG, "$selectedsPeriod 교시 선택함")
            sPeriodSpinner.setSelection(selectedsPeriod, false)
            sPeriodSpinner.isEnabled = true
            sPeriodSpinner.isClickable = true
            ePeriodSpinner.isEnabled = true
            ePeriodSpinner.isClickable = true
            daySpinner.isEnabled = true
            daySpinner.isClickable = true
            val ePeriodItems = mutableListOf<String>()
            for (x in 1..periodItems.size - 1){
                if (periodItems[x].split("교".toRegex())[0].toInt() >= selectedsPeriod){
                    ePeriodItems.add((x).toString() + "교시")
                }
            }

            val ePeriodAdapter = ArrayAdapter(editTableContext, android.R.layout.simple_spinner_dropdown_item, ePeriodItems)
            ePeriodSpinner.adapter = ePeriodAdapter

        }
        sPeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.

                selectedsPeriod = position
                ePeriodItems = mutableListOf<String>()
                for (x in 1..periodItems.size - 1){
                    if (periodItems[x].split("교".toRegex())[0].toInt() >= selectedsPeriod){
                        ePeriodItems.add((x).toString() + "교시")
                    }
                }
                Log.e("DEBUG", ePeriodItems.toString())
                val ePeriodAdapter = ArrayAdapter(editTableContext, android.R.layout.simple_spinner_dropdown_item, ePeriodItems)
                ePeriodSpinner.adapter = ePeriodAdapter
                ePeriodSpinner.isEnabled = true
                ePeriodSpinner.isClickable = true
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        ePeriodSpinner.setSelection(0, false)
        if (activityFrom == 1){
            ePeriodSpinner.setSelection(selectedePeriod - selectedsPeriod, false)
        }
        ePeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                selectedePeriod = position + selectedsPeriod
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.editmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.ok -> {
                if (selectedSubject == 0 || selectedTeacher == 0 || selectedDay == 0 || selectedsPeriod == 0 || selectedsPeriod == 0){
                    Toast.makeText(this, "모든 값을 설정해주세요!", Toast.LENGTH_SHORT).show()
                    return true
                }
                var schedule:MutableList<Int> = arrayListOf()
                //같은 교시에 있는지 확인하기
                val cursor = db!!.rawQuery("select subject, teacher, day, sPeriod, ePeriod from tb_tt", null)
                val cursor2 = db!!.rawQuery("select ePeriod from tb_tt where day = $selectedDay", null)
                Log.e(TAG, "DB " + cursor.count.toString() + " 줄 존재")
                while (cursor.moveToNext()){
                    Log.e(TAG, "와일문 진입")
                    var sPeriodOnDB = cursor.getInt(3)
                    var ePeriodOnDB = cursor.getInt(4)
                    var dayOnDB = cursor.getInt(2)
                    Log.e(TAG, "DB상 시작 $sPeriodOnDB , DB상 끝 $ePeriodOnDB , 선택된 시작 $selectedsPeriod , 선택된 끝$selectedePeriod")

                    if(selectedsPeriod in sPeriodOnDB..ePeriodOnDB && selectedePeriod > ePeriodOnDB){
                        Log.e(TAG, "과목변경 실패, 동일시간에 수업 존재")
                        Toast.makeText(this, "그 시간에는 이미 수업이 있습니다!", Toast.LENGTH_SHORT).show()
                        finish()
                        return true
                    }
                    else if (dayOnDB == selectedDay && sPeriodOnDB >= selectedsPeriod && ePeriodOnDB <= selectedePeriod){
                        Log.e(TAG, "과목수정(과목: $selectedSubject, 교사: $selectedTeacher, 요일: $selectedDay, 시작교시: $selectedsPeriod, 끝나는교시: $selectedePeriod)")
                        db!!.execSQL("update tb_tt set subject = $selectedSubject, teacher = $selectedTeacher, sPeriod = $selectedsPeriod, ePeriod = $selectedePeriod where day = $selectedDay and sPeriod = $sPeriodOnDB")
                        Toast.makeText(this, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                        return true
                    }
                    else{
                        Log.e(TAG, "과목추가(과목: $selectedSubject, 교사: $selectedTeacher, 요일: $selectedDay, 시작교시: $selectedsPeriod, 끝나는교시: $selectedePeriod)")
                        db!!.execSQL("insert into tb_tt(subject, teacher, day, sPeriod, ePeriod) values ($selectedSubject, $selectedTeacher, $selectedDay, $selectedsPeriod, $selectedePeriod)")
                        Toast.makeText(this, "과목이 추가되었습니다!", Toast.LENGTH_SHORT).show()
                        finish()
                        return true
                    }
                }
                Log.e(TAG, "와일문 통과")
                db!!.execSQL("insert into tb_tt(subject, teacher, day, sPeriod, ePeriod) values ($selectedSubject, $selectedTeacher, $selectedDay, $selectedsPeriod, $selectedePeriod)")
                Toast.makeText(this, "과목이 추가되었습니다!", Toast.LENGTH_SHORT).show()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}