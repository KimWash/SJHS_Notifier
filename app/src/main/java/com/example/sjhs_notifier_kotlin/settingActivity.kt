package com.example.sjhs_notifier_kotlin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_meal.*
import kotlinx.android.synthetic.main.activity_setting.*

val TAG = "DEBUG"
public class settingActivity : AppCompatActivity()  {

    fun getVersionInfo(context: Context) : String{
        val info: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val version = info.versionName
        return version
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        if (MainActivity.isNightModeActive(this)) {
            setTheme(R.style.DarkTheme)
        } else if (!MainActivity.isNightModeActive(this)) {
            setTheme(R.style.LightTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(settingToolbar)
        getSupportActionBar()?.title = "설정"
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        versiontext.setText("Version: ${getVersionInfo(this)}")


        backupButton.setOnClickListener {
            if(!permissionManager.hasPermissions(this, permissionList)){
                ActivityCompat.requestPermissions(this, permissionList, permissionALL)
            }
            else{
                backupDB.exportDB(this)
            }
        }

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

        var sendreport = findViewById<LinearLayout>(R.id.reportButton)
        sendreport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.setType("message/rfc822")
            val emails:Array<String> = arrayOf("ckm0728wash@gmail.com")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, emails)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "서전고 알리미 오류 신고")
            emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                "어플리케이션 버전: " + getVersionInfo(this) +
                        "\n기기 제조사: " + Build.BRAND +
                        "\n기기 모델명: " + Build.MODEL +
                        "\n소프트웨어 빌드번호: " + Build.VERSION.INCREMENTAL +
                        "\n안드로이드 버전: " + Build.VERSION.RELEASE + " (API LEVEL: " + Build.VERSION.SDK_INT +
                        "\n문의 내용: "
            )
            startActivity(emailIntent)

        }
    }
    
}