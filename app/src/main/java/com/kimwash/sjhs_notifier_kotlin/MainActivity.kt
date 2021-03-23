package com.kimwash.sjhs_notifier_kotlin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.kimwash.sjhs_notifier_kotlin.IntroActivity.Companion.launchChromeTab
import com.skydoves.balloon.*
import kotlinx.android.synthetic.main.activity_main.*
import kr.go.neis.api.School
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var idButton: LinearLayout

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
                    mealName.setText("\uD83C\uDF5C️ 오늘의 아침")
                    meal.setText(menu[day-1].breakfast)
                }
                else if (hour in 8..12 && checkNull(menu[day-1].lunch) == false) {
                    mealName.setText("\uD83C\uDF5C️ 오늘의 점심")
                    meal.setText(menu[day-1].lunch)
                }
                else if (hour in 13..18 && checkNull(menu[day-1].dinner) == false){
                    mealName.setText("\uD83C\uDF5C️ 오늘의 저녁")
                    meal.setText(menu[day-1].dinner)
                }
                else if (hour in 19..23 && checkNull(menu[day].breakfast) == false){
                    mealName.setText("\uD83C\uDF5C️ 내일의 아침")
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

    fun checkConnectivity():Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val status = networkInfo != null && networkInfo.isConnected
        return status
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        if (!checkConnectivity()){
            val alert_confirm =
                AlertDialog.Builder(this)
            alert_confirm.setMessage("인터넷에 연결되어있지 않습니다. 확인 후 다시 이용 바랍니다.").setCancelable(false)
                .setPositiveButton(
                    "확인"
                ) { dialog, which ->
                    // 'YES'
                }
            val alert = alert_confirm.create()
            alert.show()
        }
        else{
            getInformations(getTime(0), getTime(1), getTime(2), getTime(3))
        }

        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        if (isNightModeActive(this)){
            setTheme(R.style.DarkTheme)
        }
        else if(!isNightModeActive(this)){
            setTheme(R.style.LightTheme)
        }
        setContentView(R.layout.activity_main)
        dispWelcome()
        idButton = findViewById(R.id.id_button)



        var prefs: SharedPreferences = getSharedPreferences("Pref", MODE_PRIVATE)
        var isFirstRun = prefs.getBoolean("isFirstRun", true)
        if (isFirstRun){
            var alert_confirm = AlertDialog.Builder(this)
            alert_confirm.setMessage("본 버전은 클로즈베타 버전입니다.\n 오류나 개선점을 발견하시면 설정 -> 오류 신고하는 곳에서 신고 부탁드립니다.\n관심가져주셔서 감사합니다.")
            alert_confirm.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                val balloon = createBalloon(this) {
                    setArrowSize(10)
                    setWidth(BalloonSizeSpec.WRAP)
                    setHeight(65)
                    setArrowPosition(0.7f)
                    setCornerRadius(4f)
                    setAlpha(0.9f)
                    setPadding(10)
                    setText("전자 학생증을 사용해보세요!")
                    setTextColorResource(R.color.colorWhite)
                    setTextIsHtml(true)
                    setBackgroundColorResource(R.color.ballonColor)
                    setOnBalloonClickListener{
                        Toast.makeText(this@MainActivity, "Click", Toast.LENGTH_SHORT).show()
                    }
                    setBalloonAnimation(BalloonAnimation.FADE)
                    setLifecycleOwner(lifecycleOwner)
                }
                balloon.show(idButton, idButton.x.toInt(), idButton.y.toInt())
            }
            alert_confirm
                .setTitle("안내")
                .create()
                .setIcon(R.drawable.ic_build_black_24dp)
            alert_confirm.show()
            prefs.edit().putBoolean("isFirstRun", false).apply()


        }

        idButton.setOnClickListener {
            val idCardIntent = Intent(this, idCardActivity::class.java)
            startActivity(idCardIntent)
        }

        var meallayout = findViewById<LinearLayout>(R.id.meallayout)
        meallayout.setOnClickListener{ val mealIntent = Intent(this@MainActivity, mealActivity::class.java); startActivity(mealIntent) }

        var timetable = findViewById<LinearLayout>(R.id.timetableButton)
        timetable.setOnClickListener{ val timeIntent = Intent(this@MainActivity, timeTableActivity::class.java); startActivity(timeIntent) }

        var settingButton = findViewById<LinearLayout>(R.id.setting)
        settingButton.setOnClickListener { val settingIntent = Intent(this@MainActivity, settingActivity::class.java); startActivity(settingIntent) }

        var h4payButton = findViewById<LinearLayout>(R.id.h4payButton)
        h4payButton.setOnClickListener {
            launchChromeTab(this, "https://h4pay.co.kr")
        }

        var selfCheckLink = resources.getString(R.string.selfCheckLink)
        selfTestButton.setOnClickListener{val testIntent = Intent(Intent.ACTION_VIEW, Uri.parse(selfCheckLink)); startActivity(testIntent)}
    }
}