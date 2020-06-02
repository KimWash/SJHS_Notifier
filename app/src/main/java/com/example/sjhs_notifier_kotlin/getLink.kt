package com.example.sjhs_notifier_kotlin

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class getLink(private var param:String): AsyncTask<Void, Int, List<String>>(){
    override fun doInBackground(vararg unused: Void?): List<String>? {
        var version = 0.0
        param = "sql=" + param
        Log.e(TAG, param)
        try {
            // 서버연결
            val url = URL(
                "https://yoon-lab.xyz/sjhsnotifier/getLink.php"
            )
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.connect()
            /* 안드로이드 -> 서버 파라메터값 전달 */
            var outs = conn.outputStream
            outs.write(param.toByteArray(charset("UTF-8")))
            outs.flush()
            outs.close()
            /* 서버 -> 안드로이드 파라메터값 전달 */
            val iss = conn.inputStream
            val inn = BufferedReader(InputStreamReader(iss))
            val line = inn.readLines()
            Log.e(TAG, line.toString())
            return line

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    override fun onPostExecute(result: List<String>?) {
        super.onPostExecute(result)
        return
    }
}