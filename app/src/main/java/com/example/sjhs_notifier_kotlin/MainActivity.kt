package com.example.sjhs_notifier_kotlin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.StrictMode
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kr.go.neis.api.School
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    fun getTime(type:Int): Int {
        val now = System.currentTimeMillis()
        val date = Date(now)
        if (type == 0){
            val yearFormat = SimpleDateFormat("YYYY")
            val yearNow = yearFormat.format(date)
            return Integer.parseInt(yearNow)
        }
        if (type == 1){
            val monthFormat = SimpleDateFormat("MM")
            val monthNow = monthFormat.format(date)
            return Integer.parseInt(monthNow)
        }
        else if (type == 2){
            val dateFormat = SimpleDateFormat("DD")
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
    fun isNetworkAvailable(year:Int, month:Int, day:Int, hour:Int) {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val status = networkInfo != null && networkInfo.isConnected
        if (status == false) {
            val alert_confirm =
                AlertDialog.Builder(this)
            alert_confirm.setMessage("인터넷에 연결되어있지 않습니다. 확인 후 다시 이용 바랍니다.").setCancelable(false)
                .setPositiveButton(
                    "확인"
                ) { dialog, which ->
                    // 'YES'
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            val alert = alert_confirm.create()
            alert.show()

        }
        else {
            getInformations(year, month, day, hour)
        }


    }
    fun getInformations(gotyear:Int, gotmonth:Int, day:Int, hour:Int){
        val thread = Thread(Runnable {
            val school = School(School.Type.HIGH, School.Region.CHUNGBUK, "M100002171") //setSchool
            val schedule = school.getMonthlySchedule(gotyear, gotmonth) //get Schedule
            val menu = school.getMonthlyMenu(gotyear, gotmonth) // get Menu
            runOnUiThread {
                // UI Update
                if (hour in 1..7){
                    mealName.setText("오늘의 아침")
                    meal.setText(menu[day+1].breakfast)
                }
                else if (hour in 8..12) {
                    mealName.setText("오늘의 점심")
                    meal.setText(menu[day+1].lunch)
                }
                else if (hour in 13..18){
                    mealName.setText("오늘의 저녁")
                    meal.setText(menu[day+1].dinner)
                }
                else if (hour in 19..0){
                    mealName.setText("내일의 아침")
                    meal.setText(menu[day+2].lunch)
                }
                schedules.setText("")
                for (i in schedule.indices) {
                    if (schedule[i].toString().equals("토요휴업일\n") || schedule[i].toString().equals("")){
                    }
                    else {
                        schedules.append("${i + 1}일 "+schedule[i].toString())
                    }
                }
            }

        })
        thread.start()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        isNetworkAvailable(getTime(0), getTime(1), getTime(2), getTime(3))
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dispWelcome()
        var meallayout = findViewById(R.id.meallayout) as LinearLayout
        meallayout.setOnClickListener{
            val mealIntent = Intent(this@MainActivity, mealActivity::class.java)
            startActivity(mealIntent)
        }
    }
}