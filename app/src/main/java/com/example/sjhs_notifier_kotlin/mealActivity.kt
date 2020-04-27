package com.example.sjhs_notifier_kotlin

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.StrictMode
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.CalendarView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_meal.*
import kr.go.neis.api.Menu
import kr.go.neis.api.School
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date


class mealActivity : AppCompatActivity() {

    var date:Long = 0
    val yearFormat = SimpleDateFormat("yyyy")
    val monthFormat = SimpleDateFormat("MM")
    val dateFormat = SimpleDateFormat("dd")
    val hourFormat = SimpleDateFormat("HH")
    val minFormat = SimpleDateFormat("mm")
    val inf = "Information"
    var school = School(School.Type.HIGH, School.Region.CHUNGBUK, "M100002171")

    fun getTime(type:Int): Int {
        val now = System.currentTimeMillis()
        val date = Date(now)
        if (type == 0){
            val yearNow = yearFormat.format(date)
            return Integer.parseInt(yearNow)
        }
        if (type == 1){
            val monthNow = monthFormat.format(date)
            return Integer.parseInt(monthNow)
        }
        else if (type == 2){
            val dayNow = dateFormat.format(date)
            return Integer.parseInt(dayNow)
        }
        else if (type == 3){

            val hourNow = hourFormat.format(date)
            return Integer.parseInt(hourNow)
        }
        else if (type == 4){
            val minnow = minFormat.format(date)
            return Integer.parseInt(minnow)
        }
        return 808
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MainActivity.isNightModeActive(this) == true) {
            setTheme(R.style.DarkTheme)
        } else if (MainActivity.isNightModeActive(this) == false) {
            setTheme(R.style.LightTheme)
        }
        setContentView(R.layout.activity_meal)
        bMenu.itemIconTintList = null
        setSupportActionBar(mealToolbar)
        getSupportActionBar()?.title = "식단표"
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        getAvailableMeals(getTime(0), getTime(1), getTime(2))

        var thread = Thread(Runnable {
            runOnUiThread({
                var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bMenu)
                bottomNavigationView?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
            })
        }).start()
        val thread2 = Thread(Runnable {
            runOnUiThread {
                val calendarViewfun = findViewById<CalendarView>(R.id.calendarView)
                calendarViewfun?.setOnDateChangeListener { view, year, month, dayOfMonth -> getAvailableMeals(year, month, dayOfMonth)}
            }
        }).start()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        var year = Integer.parseInt(yearFormat.format(date))
        var month = Integer.parseInt(monthFormat.format(date))
        var day = Integer.parseInt(dateFormat.format(date))
        when (menuItem.itemId) {
            R.id.action_one -> {
                dispInformations(year, month, day, 0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_two -> {
                dispInformations(year, month, day, 1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_three -> {
                dispInformations(year, month, day, 2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun dispInformations(year:Int, month:Int, day:Int, meal:Int) {
        var meals = Array<String>(3) {""; ""; ""}
        val thread = Thread(Runnable{
            Log.i(inf,year.toString() + "/" + month.toString() + "/" + day.toString() + "/" + meal)
            var menu = school.getMonthlyMenu(year, month)
            meals[0] = menu[day-1].breakfast
            meals[1] = menu[day-1].lunch
            meals[2] = menu[day-1].dinner
            runOnUiThread {
                if (meal == 0) lunch.setText(meals[0])
                else if (meal == 1) lunch.setText(meals[1])
                else if (meal == 2) lunch.setText(meals[2])
            }
        }).start()
    }

    //TODO:급식이 없는 경우 특정 메시지 표시, 처음 구동시에도 가능한 실행하게 하기
    private fun getAvailableMeals(year:Int, month:Int, day:Int): Array<String> {
        var meals = Array<String>(3) {""; ""; ""}
        val thread = Thread(Runnable{
            var c:Calendar = Calendar.getInstance()
            c.set(year, month, day)
            date = c.timeInMillis
            Log.i(inf, year.toString() + "/" + (month).toString() + "/" + day)
            var menu = school.getMonthlyMenu(year, month+1)
            meals[0] = menu[day-1].breakfast
            meals[1] = menu[day-1].lunch
            meals[2] = menu[day-1].dinner
            runOnUiThread {
                lunch.setText(meals[0])
                for (i in 0..2){
                    if (meals[i] == ""){
                        when (i){
                            0 -> bMenu.getMenu().findItem(R.id.action_one).setVisible(false)
                            1 -> bMenu.getMenu().findItem(R.id.action_two).setVisible(false)
                            2 -> bMenu.getMenu().findItem(R.id.action_three).setVisible(false)
                        }
                    }
                    else if(meals[i] != ""){
                        when (i){
                            0 -> bMenu.getMenu().findItem(R.id.action_one).setVisible(true);
                            1 -> bMenu.getMenu().findItem(R.id.action_two).setVisible(true);
                            2 -> bMenu.getMenu().findItem(R.id.action_three).setVisible(true);
                        }
                    }
                }
                bMenu.isVisible = true
                when (bMenu.selectedItemId){
                    R.id.action_one -> lunch.setText(meals[0])
                    R.id.action_two -> lunch.setText(meals[1])
                    R.id.action_three -> lunch.setText(meals[2])
                }
                if (bMenu.getMenu().findItem(R.id.action_one).isVisible == false && bMenu.getMenu().findItem(R.id.action_two).isVisible == false && bMenu.getMenu().findItem(R.id.action_three).isVisible == false){
                    lunch.setText("이 날에는 급식 정보가 없는 것 같네요.")
                    bMenu.isVisible = false
                }
            }
        }).start()
        return meals
    }
}


