package com.example.sjhs_notifier_kotlin

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edittable.*
import kotlinx.android.synthetic.main.activity_meal.*
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_timetable.*

class timeTableActivity : AppCompatActivity(){

    //TODO: 반, 그룹 선언 (수정 필요)
    var stClass = 2
    var stGroup = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isFirstTimeTable = PreferenceManager.getBoolean(this, "isFirstTimeTable")

        if (isFirstTimeTable){
            val intent = Intent(this, setClassActivity::class.java)
            intent.putExtra("type", 0)
            startActivityForResult(intent, 1)
            PreferenceManager.setBoolean(this, "isFirstTimeTable", false)
        }
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
        /**val alert_confirm =
            AlertDialog.Builder(this)
        alert_confirm.setMessage("현재는 시간표에서 직접 수정할 시 두시간 이상 있는 과목은 수정이 불가합니다.\n빠른 시일내에 수정하겠습니다. \uD83D\uDE47\uD83C\uDFFB").setCancelable(false)
            .setPositiveButton(
                "확인"
            ) { dialog, which ->
            }
        val alert = alert_confirm.create()
        alert.show()**/
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


    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                val stClass = data!!.getIntExtra("class", 0)
                Log.e(TAG, "setClassActivity -> timeTableActivity $stClass 반 선택")
                PreferenceManager.setInt(this, "class", stClass)
            }
        }
    }


    fun loadTable(){
        Log.e(TAG, "로드테이블")
        var subjects = resources.getStringArray(R.array.subject)
        var teachers = resources.getStringArray(R.array.teacher)
        var textArray:MutableList<TextView> = arrayListOf(subject0, subject1, subject2, subject3, subject4, subject5, subject6, subject7, subject8, subject9, subject10, subject11, subject12, subject13, subject14, subject15, subject16, subject17, subject18, subject19, subject20, subject21, subject22, subject23, subject24, subject25, subject26, subject27, subject28, subject29, subject30, subject31, subject32, subject33, subject34)
        val helper = DataBaseHelper(this)
        val db = helper.writableDatabase
        val cursor = db.rawQuery("select subject, teacher, day, sPeriod, ePeriod, stClass, stGroup from tb_tt", null)
        Log.e(TAG, cursor.count.toString())
        while (cursor.moveToNext()){ //한줄한줄...
            val subjectDB = cursor.getInt(0)
            val teacherDB = cursor.getInt(1)
            val dayDB = cursor.getInt(2)
            val sPeriodDB = cursor.getInt(3)
            val ePeriodDB = cursor.getInt(4)
            val classDB = cursor.getInt(5)
            val groupDB = cursor.getInt(6)
            Log.e(TAG, "$subjectDB, $teacherDB, $dayDB, $sPeriodDB, $ePeriodDB, $classDB, $groupDB")
            val getClass = getLink("SELECT `stClass` FROM `links` WHERE `subject`=$subjectDB and `teacher`=$teacherDB").execute().get() as List<String>
            var isNoClass = false
            Log.e(TAG, "배열 개수: " + getClass.size.toString())
            for (x in getClass.indices){
                Log.e(TAG, "포문 $x 번째 반복 학급: " + getClass[x])
                if (getClass[x].toInt() == 0){
                    isNoClass = true
                }
            }

            if (isNoClass){
                //학급별로 구분하는 과목
                val linkList = getLink("SELECT `link` FROM `links` WHERE `subject`=$subjectDB and `teacher`=$teacherDB and `stClass`=0 and `stGroup`=$groupDB;").execute().get() as List<String>
                val link = linkList[0]
                Log.e(TAG, link)
            }
            else{
                //그룹별로 나누는 과목
                val linkList = getLink("SELECT `link` FROM `links` WHERE `subject`=$subjectDB and `teacher`=$teacherDB and `stClass`=$classDB and `stGroup`=0;").execute().get() as List<String>
                val link = linkList[0]
                Log.e(TAG, link)
            }
            //반별로 나뉜 과목
            if (subjectDB == 6 || subjectDB == 23 || subjectDB == 1 || subjectDB == 16){
                textArray[(dayDB * 7) - (7-sPeriodDB)-1].setText(subjects[cursor.getInt(0)] + "\n" + teachers[cursor.getInt(1)])
                //textArray[(dayDB * 7) - (7-sPeriodDB)-1].setText(Html.fromHtml("<a href=\"" + link + "\">" + subjects[subjectDB] + "\n" + teachers[teacherDB] + "</a>"))
                Log.e(TAG, "$sPeriodDB , $ePeriodDB")
                for (x in sPeriodDB + 1..ePeriodDB){
                    Log.e(TAG, (x).toString())
                    textArray[(dayDB * 7) - (7-x)-1].setText(subjects[cursor.getInt(0)] + "\n" + teachers[cursor.getInt(1)])
                    //textArray[(dayDB * 7) - (7-x)-1].setText(Html.fromHtml("<a href=\"" + link + "\">" + subjects[subjectDB] + "\n" + teachers[teacherDB] + "</a>"))
                }
            }
        }
    }
}



