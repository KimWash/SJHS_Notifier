package com.example.sjhs_notifier_kotlin

import android.content.Context
import android.content.Intent
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
import com.yoonlab.mathproject.Setting.PreferenceManager
import kotlinx.android.synthetic.main.activity_edittable.*
import kotlinx.android.synthetic.main.activity_meal.*
import kotlinx.android.synthetic.main.activity_meal.toolbar
import java.util.EnumSet.range

class editTableActivity : AppCompatActivity()  {
    var selectedSubject:Int = 0
    var selectedTeacher:Int = 0
    var selectedDay:Int = 0
    var selectedsPeriod:Int = 0
    var selectedePeriod:Int = 0
    val editTableContext: Context = this

    fun uiStartUp(){
        setSupportActionBar(toolbar)
        getSupportActionBar()?.title = "수정"
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
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
                ePeriodItems = mutableListOf<String>()
                for (x in 1..periodItems.size - 1){
                    Log.e("DEBUG", "$x 차시기, 선택:$selectedsPeriod periodItem: " + periodItems[x])
                    if (periodItems[x].split("교".toRegex())[0].toInt() >= selectedsPeriod!!){
                        ePeriodItems.add((x).toString() + "교시")
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
                selectedePeriod = position + selectedsPeriod
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        var menuInflater = getMenuInflater()
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
                val helper = DataBaseHelper(this)
                val db = helper.writableDatabase
                val cursor = db.rawQuery("select subject, teacher, day, sPeriod, ePeriod from tb_tt", null)
                val cursor2 = db.rawQuery("select ePeriod from tb_tt where day = $selectedDay", null)

                cursor.moveToFirst()
                while (cursor.moveToNext()){
                    Log.e("DEBUG", cursor.getString(3))
                    var sPeriodOnDB = cursor.getString(3)
                    var ePeriodOnDB = cursor.getString(4)
                    var dayOnDB = cursor.getString(2)
                    if (dayOnDB.toInt() == selectedDay){
                        if (sPeriodOnDB.toInt() == selectedsPeriod){
                            //만약 모든 ePeriod값중 selectedePeriod와 같은게 있으면 빠꾸
                            cursor2.moveToFirst()
                            while (cursor2.moveToNext()){
                                var ePeriodOnDB = cursor2.getString(0)
                                if (ePeriodOnDB.toInt() >= selectedePeriod){
                                    Toast.makeText(this, "그 시간에는 이미 수업이 있습니다.", Toast.LENGTH_SHORT).show()
                                    return true
                                }
                            }
                            //선택값 적용 (요일, sPeriod 유지)
                            db.execSQL("update tb_tt set subject = $selectedSubject, teacher = $selectedTeacher, ePeriod = $selectedePeriod")
                            Toast.makeText(this, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                            return true
                        }
                    }
                }

                db.execSQL("insert into tb_tt(subject, teacher, day, sPeriod, ePeriod) values ($selectedSubject, $selectedTeacher, $selectedDay, $selectedsPeriod, $selectedePeriod)")
                Toast.makeText(this, "과목이 추가되었습니다!", Toast.LENGTH_SHORT).show()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}