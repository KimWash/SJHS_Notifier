package com.example.sjhs_notifier_kotlin

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

private var myWebView:WebView? = null

class h4payActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_h4pay)

        myWebView = findViewById(R.id.webview)
        val webSettings = myWebView!!.settings
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setDomStorageEnabled(true);

        myWebView!!.webViewClient = SslWebViewConnect()
        myWebView!!.webChromeClient = WebChromeClient()
        myWebView!!.loadUrl("http://h4pay.co.kr")

    }
    private var backBtnTime: Long = 0
    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        val gapTime: Long = curTime - backBtnTime
        if (gapTime in 0..2000) {
            super.onBackPressed()
        } else {
            backBtnTime = curTime
            Toast.makeText(this, "한번 더 누르면 메인으로 나갑니다.", Toast.LENGTH_SHORT).show()
        }

    }

}