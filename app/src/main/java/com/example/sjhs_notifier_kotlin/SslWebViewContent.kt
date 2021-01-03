package com.example.sjhs_notifier_kotlin

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient


class SslWebViewConnect : WebViewClient() {
    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        handler.proceed() // SSL 에러가 발생해도 계속 진행!
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true //응용프로그램이 직접 url를 처리함
    }
}