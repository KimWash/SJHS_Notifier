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
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_intro.*
import java.io.File


val permissionALL = 1
val permissionList = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)


public class IntroActivity : AppCompatActivity()  {
    private var mDownloadManager:DownloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var mDownloadQueueId:Int? = null
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
        lastestVersion = version.toString().split("\\.".toRegex())[0] + version.toString().split("\\.".toRegex())[1]
    }

    fun downloadApp(version:Double){
        val versionStr = version.toString().split(".".toRegex())
        val url = Uri.parse("https://kmnas.asuscomm.com/files/sjhs_notifier_" + versionStr[0] + "_" + versionStr[1] + ".apk")
        val request:DownloadManager.Request = DownloadManager.Request(url)
        request.setTitle("앱 업데이트")
        request.setDescription("다운로드중이에요...")
        val pathSegmentList: List<String> = url.pathSegments
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/temp").mkdirs();  //경로는 입맛에 따라...바꾸시면됩니다.
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/temp/", pathSegmentList.get(pathSegmentList.size-1) );
        mFileName = pathSegmentList.get(pathSegmentList.size-1)
        mDownloadQueueId = mDownloadManager.enqueue(request).toInt()
    }

    /**
     * 다운로드 완료 액션을 받을 리시버.
     */
    private val mCompleteReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                Toast.makeText(context, "Complete.", Toast.LENGTH_SHORT).show()
                val intent1 = Intent()
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent1.action = Intent.ACTION_VIEW
                intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                val localUrl: String =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + "/temp/sjhs_notifier_$lastestVersion.apk" //저장했던 경로..
                val extension = MimeTypeMap.getFileExtensionFromUrl(localUrl)
                val mimeType =
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                val file = File(localUrl)
                intent1.setDataAndType(Uri.fromFile(file), mimeType)
                try {
                    startActivity(intent1)
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
            checkConnectivity()
        }
        updateChecker()
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