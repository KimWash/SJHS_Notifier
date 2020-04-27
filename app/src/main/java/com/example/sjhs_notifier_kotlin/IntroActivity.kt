package com.example.sjhs_notifier_kotlin

import android.Manifest
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_intro.*
import java.io.File


val permissionALL = 1
val permissionList = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)


public class IntroActivity : AppCompatActivity()  {

    private var mDownloadQueueId:Long? = null
    private var mFileName:String? = null
    private var lastestVersion:String? = null
    private val SPLASH_TIME = 2000
    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2

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

    fun checkConnectivity(){
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Build.VERSION.SDK_INT >= 23){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("DEBUG","Permission: "+permissions[0]+ "was "+grantResults[0])
                checkConnectivity()
            }
            else {
                Log.d("DEBUG","Permission denied");
                // TODO : 퍼미션이 거부되는 경우에 대한 코드
            }
        }
    }

    fun updateChecker(){
        val version = checkUpdate().execute().get()
        Log.e(TAG, version.toString())
        val versionName = BuildConfig.VERSION_NAME
        if (versionName.toDouble() < version){
            Toast.makeText(this, "앱이 구버전이네요. 업데이트를 진행할게요!", Toast.LENGTH_SHORT)
            downloadApp(version)
            //TODO: 업데이트 구문
        }
        else{
            checkConnectivity()
        }
    }

    fun downloadApp(version:Double){
        backupDB.exportDB(this)
        var mDownloadManager:DownloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val versionStr = version.toString().split("\\.".toRegex())
        Log.e(TAG, "http://kmnas.asuscomm.com/files/sjhs_notifier_" + versionStr[0] + "_" + versionStr[1] + ".apk")
        val url = Uri.parse("http://kmnas.asuscomm.com/files/sjhs_notifier_" + versionStr[0] + "_" + versionStr[1] + ".apk")
        val request:DownloadManager.Request = DownloadManager.Request(url)
        request.setTitle("서전고 앱 업데이트")
        request.setDescription("다운로드중이에요...")
        request.setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS, "sjhs_notifier_" + versionStr[0] + "_" + versionStr[1] + ".apk");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI and DownloadManager.Request.NETWORK_MOBILE)
        mFileName = "sjhs_notifier_" + versionStr[0] + "_" + versionStr[1] + ".apk"
        mDownloadQueueId = mDownloadManager.enqueue(request)
        Log.e(TAG, mFileName + ", " + mDownloadQueueId.toString())
        lastestVersion = version.toString().split("\\.".toRegex())[0] + "_"+ version.toString().split("\\.".toRegex())[1]
    }

    private val mCompleteReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e(TAG, "receiver")
            val action = intent.action
            if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val intent = Intent(Intent.ACTION_VIEW)
                val apkUri = FileProvider.getUriForFile(baseContext, "com.example.sjhs_notifier_kotlin.fileprovider", File(Environment.getExternalStorageDirectory().absolutePath + "/Download/sjhs_notifier_$lastestVersion.apk"))
                Log.e(TAG, apkUri.toString())
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Not found. Cannot open file.", Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        unregisterReceiver(mCompleteReceiver)
    }

    override fun onResume() {
        super.onResume()
        val completeFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(mCompleteReceiver, completeFilter)
    }

    fun installApp() {

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
        if(!permissionManager.hasPermissions(this, permissionList)){
            ActivityCompat.requestPermissions(this, permissionList, permissionALL)
        }
        else{
            updateChecker()
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






    }


    override fun onBackPressed(){
    }
}