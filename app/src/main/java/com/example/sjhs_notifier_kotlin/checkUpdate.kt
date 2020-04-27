package com.example.sjhs_notifier_kotlin

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class checkUpdate(): AsyncTask<Void, Int, Double>(){
    override fun doInBackground(vararg params: Void?): Double {
        var version = 0.0
        try {
            // 서버연결
            val url = URL(
                "https://yoon-lab.xyz/lastest_version.php"
            )
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.connect()
            /* 안드로이드 -> 서버 파라메터값 전달 */
            var outs = conn.outputStream
            outs.flush()
            outs.close()
            /* 서버 -> 안드로이드 파라메터값 전달 */
            val iss = conn.inputStream
            val inn = BufferedReader(InputStreamReader(iss))
            val line = inn.readLine()
            Log.e("RECV DATA*", line)
            version = line.toDouble()
            if (line == "Error 4: No Data") {
                return 0.0
            }
            return version
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0.0
    }
    override fun onPostExecute(result: Double?) {
        super.onPostExecute(result)
        return
    }
}