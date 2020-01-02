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
    fun getTime(type:String): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val hourFormat = SimpleDateFormat("HH")
        val timeFormat = SimpleDateFormat("HH시 mm분 ss초")
        val dateFormat = SimpleDateFormat("YYYY년 MM월 DD일")
        val hourNow = hourFormat.format(date)
        val timeNow = timeFormat.format(date)
        val dateNow = dateFormat.format(date)
        
        if (type == "date"){
            return dateNow
        }
        else if (type == "time"){
            return timeNow
        }
        return "시간을 불러오지 못했습니다."
    }
    fun getHour(): Int{
        val now = System.currentTimeMillis()
        val date = Date(now)
        val hourFormat = SimpleDateFormat("HH")
        val hourNow = hourFormat.format(date)
        return Integer.parseInt(hourNow)
    }
    fun dispWelcome(): Int{
        
        if (getHour() in 7..11) {
            welcome.setText("좋은 아침이에요. \n아침은 먹었나요?")
        }
        else if (getHour() in 12..13) {
            welcome.setText("점심시간이에요. \n식사 맛있게하세요!")
        }
        else if (getHour() in 14..19){
            welcome.setText("점심도 맛있게 먹었겠다 \n열심히 공부해봐요!")
        }
        else if (getHour() == 20) {
            welcome.setText("공부도 \n저녁먹고 든든하게!")
        }
        else if (getHour() in 21..23){
            welcome.setText("내일까지 해야하는 과제, \n확인했나요?")
        }
        else if (getHour() == 0){
            welcome.setText("빨리 안자면 \n내일 지각할거에요!!")
        }
        else if (getHour() in 1..7) {
            welcome.setText("헉.. 시험기간인가봐요! \n화이팅!")
        }
        if (getTime("date") == "시간을 불러오지 못했습니다."){
            welcome.setText("시간을 불러오지 못했습니다.")
        }
        return 0
    }
    fun isNetworkAvailable() {
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
            getInformations()
        }


    }
    fun getInformations(){
        val thread = Thread(Runnable {
            val school = School(School.Type.HIGH, School.Region.CHUNGBUK, "M100002171")
            val schedule = school.getMonthlySchedule(2019, 12)
// 2019년 1월 2일 점심 급식 식단표
            val menu = school.getMonthlyMenu(2019, 12)
            runOnUiThread {
                // UI작업
                meal.setText(menu[1].lunch)
// 2018년 12월 5일 학사일정
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
        isNetworkAvailable()
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