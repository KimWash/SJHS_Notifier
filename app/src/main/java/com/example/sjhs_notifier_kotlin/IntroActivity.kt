package com.example.sjhs_notifier_kotlin

import android.app.Activity;
import  android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity


public class IntroActivity : AppCompatActivity()  {

    private val SPLASH_TIME = 2000

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val hander = Handler()
        hander.postDelayed({
            startActivity(Intent(application, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            this@IntroActivity.finish()

        }, SPLASH_TIME.toLong())
    }


    override fun onBackPressed(){
    }
}