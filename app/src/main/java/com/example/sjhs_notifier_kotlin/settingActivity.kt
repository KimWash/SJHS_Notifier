package com.example.sjhs_notifier_kotlin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_meal.*
import kotlinx.android.synthetic.main.activity_meal.toolbar
import kotlinx.android.synthetic.main.activity_setting.*

public class settingActivity : AppCompatActivity()  {

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        if (MainActivity.isNightModeActive(this) == true) {
            setTheme(R.style.DarkTheme)
        } else if (MainActivity.isNightModeActive(this) == false) {
            setTheme(R.style.LightTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.title = "설정"
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        reseTButton.setOnClickListener{
            val helper = DataBaseHelper(this)
            val db = helper.writableDatabase
            val query = db.rawQuery("select count(*) from sqlite_master where name='tb_tt'", null)
            if (query.count > 0){
                db.execSQL("delete from tb_tt")
                Toast.makeText(this, "시간표를 초기화했어요.", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "시간표가 없는 것 같아요..", Toast.LENGTH_SHORT).show()
            }

        }
    }
}