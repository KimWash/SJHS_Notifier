package com.example.sjhs_notifier_kotlin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_setting.*
import java.io.File


val TAG = "DEBUG"
public class settingActivity : AppCompatActivity()  {

    fun getVersionInfo(context: Context) : String{
        val info: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val version = info.versionName
        return version
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK){
            val fileUri = data?.data
            if (fileUri != null) {
                Log.e(TAG, getRealPathFromURI(fileUri))
            }
        }

    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
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

        versiontext.setText("${getVersionInfo(this)}")


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

       importButton.setOnClickListener{
            Toast.makeText(this, "죄송합니다, 준비중인 기능입니다.", Toast.LENGTH_SHORT)
            val fileSIntent = Intent(Intent.ACTION_GET_CONTENT)
            fileSIntent.setType("*/*")
            startActivityForResult(fileSIntent, 1)
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