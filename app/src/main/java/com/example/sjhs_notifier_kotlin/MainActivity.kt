package com.example.sjhs_notifier_kotlin

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import kotlinx.android.synthetic.main.activity_main.*
import kr.go.neis.api.School
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    fun getTime(type:Int): Int {
        val now = System.currentTimeMillis()
        val date = Date(now)
        if (type == 0){
            val yearFormat = SimpleDateFormat("yyyy")
            val yearNow = yearFormat.format(date)
            return Integer.parseInt(yearNow)
        }
        if (type == 1){
            val monthFormat = SimpleDateFormat("MM")
            val monthNow = monthFormat.format(date)
            return Integer.parseInt(monthNow)
        }
        else if (type == 2){
            val dateFormat = SimpleDateFormat("dd")
            val dayNow = dateFormat.format(date)
            return Integer.parseInt(dayNow)
        }
        else if (type == 3){
            val hourFormat = SimpleDateFormat("HH")
            val hourNow = hourFormat.format(date)
            return Integer.parseInt(hourNow)
        }
        else if (type == 4){
            val minFormat = SimpleDateFormat("mm")
            val minnow = minFormat.format(date)
            return Integer.parseInt(minnow)
        }
        return 808
    }
    fun dispWelcome(): Int{
        
        if (getTime(3) in 7..11) {
            welcome.setText("좋은 아침이에요. \n아침은 먹었나요?")
        }
        else if (getTime(3) in 12..13) {
            welcome.setText("점심시간이에요. \n식사 맛있게하세요!")
        }
        else if (getTime(3) in 14..19){
            welcome.setText("점심도 맛있게 먹었겠다 \n열심히 공부해봐요!")
        }
        else if (getTime(3) == 20) {
            welcome.setText("공부도 \n저녁먹고 든든하게!")
        }
        else if (getTime(3) in 21..23){
            welcome.setText("내일까지 해야하는 과제, \n확인했나요?")
        }
        else if (getTime(3) == 0){
            welcome.setText("빨리 안자면 \n내일 지각할거에요!!")
        }
        else if (getTime(3) in 1..7) {
            welcome.setText("헉.. 시험기간인가봐요! \n화이팅!")
        }
        if (getTime(3) == 808){ //에러코드
            welcome.setText("시간을 불러오지 못했습니다.")
        }
        return 0
    }


    fun checkNull(text:String): Boolean{
        if (text == ""){
            return true
        }
        return false
    }

    fun getInformations(gotyear:Int, gotmonth:Int, day:Int, hour:Int){//Todo: SSL 인증서 추가
        Log.e("Debug", gotyear.toString() + "년" + gotmonth + "월" + day + "일" + hour + "시")
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val thread = Thread(Runnable {
            val school = School(School.Type.HIGH, School.Region.CHUNGBUK, "M100002171") //setSchool
            val schedule = school.getMonthlySchedule(gotyear, gotmonth) //get Schedule
            val menu = school.getMonthlyMenu(gotyear, gotmonth) // get Menu
            runOnUiThread {
                // UI Update
                if (hour in 0..7 && checkNull(menu[day-1].breakfast) == false){
                    mealName.setText("오늘의 아침")
                    meal.setText(menu[day-1].breakfast)
                }
                else if (hour in 8..12 && checkNull(menu[day-1].lunch) == false) {
                    mealName.setText("오늘의 점심")
                    meal.setText(menu[day-1].lunch)
                }
                else if (hour in 13..18 && checkNull(menu[day-1].dinner) == false){
                    mealName.setText("오늘의 저녁")
                    meal.setText(menu[day-1].dinner)
                }
                else if (hour in 19..23 && checkNull(menu[day].breakfast) == false){
                    mealName.setText("내일의 아침")
                    meal.setText(menu[day].breakfast)
                }
                else {
                    meal.setText("이런, 오늘은 급식이 없는 것 같군요. \uD83D\uDE22")
                }
                schedules.setText("")



                for (i in schedule.indices) {
                    if (schedule[i].toString().equals("토요휴업일\n") || schedule[i].toString().equals("") || schedule[i].toString().equals("휴업일\n")){
                    }
                    else {
                        schedules.append("${i + 1}일 "+schedule[i].toString())
                    }
                }
                if (schedules.text == ""){
                    schedules.setText("이번달은 학사일정이 없어요! ☺")
                }
            }

        })
        thread.start()
    }
companion object {
    fun isNightModeActive(context: Context): Boolean {
        val defaultNightMode = AppCompatDelegate.getDefaultNightMode()
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return true
        }
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            return false
        }
        val currentNightMode = (context.resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK)
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> return false
            Configuration.UI_MODE_NIGHT_YES -> return true
            Configuration.UI_MODE_NIGHT_UNDEFINED -> return false
        }
        return false
    }
}


    override fun onCreate(savedInstanceState: Bundle?) {
        getInformations(getTime(0), getTime(1), getTime(2), getTime(3))
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        if (isNightModeActive(this) == true){
            setTheme(R.style.DarkTheme)
        }
        else if(isNightModeActive(this) == false){
            setTheme(R.style.LightTheme)
        }

        setContentView(R.layout.activity_main)
        dispWelcome()
        var meallayout = findViewById(R.id.meallayout) as LinearLayout
        meallayout.setOnClickListener{ it: View? -> val mealIntent = Intent(this@MainActivity, mealActivity::class.java); startActivity(mealIntent) }

        var timetable = findViewById(R.id.timetableButton) as LinearLayout
        timetable.setOnClickListener{ it: View? -> val timeIntent = Intent(this@MainActivity, timeTableActivity::class.java); startActivity(timeIntent) }

        var settingButton = findViewById<LinearLayout>(R.id.setting)
        settingButton.setOnClickListener { View -> val settingIntent = Intent(this@MainActivity, settingActivity::class.java); startActivity(settingIntent) }

    }
}