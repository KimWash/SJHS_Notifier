package com.example.sjhs_notifier_kotlin

import android.app.Activity;
import android.content.Context
import  android.content.Intent;
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_intro.*


public class IntroActivity : AppCompatActivity()  {

    private val SPLASH_TIME = 2000

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

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isNightModeActive(this)){
            setTheme(R.style.DarkTheme)
        }
        else if(!isNightModeActive(this)){
            setTheme(R.style.LightTheme)
        }
        setContentView(R.layout.activity_intro)
        Stetho.initializeWithDefaults(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        if (isNightModeActive(this)){
            imageView.setImageResource(R.drawable.splash_dark)
        }
        else if(!isNightModeActive(this)){
            imageView.setImageResource(R.drawable.splash)
        }
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
        else{
            val hander = Handler()
            hander.postDelayed({
                startActivity(Intent(application, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

                this@IntroActivity.finish()

            }, SPLASH_TIME.toLong())
        }



    }


    override fun onBackPressed(){
    }
}