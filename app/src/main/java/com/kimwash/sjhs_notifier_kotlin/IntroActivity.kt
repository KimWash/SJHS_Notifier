package com.kimwash.sjhs_notifier_kotlin

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
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.facebook.stetho.Stetho
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_intro.*
import org.json.JSONObject
import java.io.File
import kotlin.system.exitProcess


val permissionALL = 1
val permissionList =
    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)


class IntroActivity : AppCompatActivity() {
    lateinit private var appUpdateManager: AppUpdateManager
    private val UPDATE_REQ_CODE = 0

    fun dispDialog(msg: String, okEvent: () -> Unit, title: String, icon: Int?) {
        val alert_confirm = AlertDialog.Builder(this)
        alert_confirm.setMessage(msg)
        alert_confirm.setPositiveButton(
            "확인",
            DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                okEvent()
            })
        if (icon != null) {
            alert_confirm
                .setTitle(title)
                .create()
                .setIcon(icon)
        } else {
            alert_confirm
                .setTitle(title)
                .create()
        }
        alert_confirm.show()
    }

    private var mDownloadQueueId: Long? = null
    private var mFileName: String? = null
    private var lastestVersion: String? = null
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

    fun checkConnectivity(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val status = networkInfo != null && networkInfo.isConnected
        return status
    }

    companion object {
        fun launchChromeTab(activity: AppCompatActivity, url: String?) {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ContextCompat.getColor(activity, android.R.color.white))
            builder.setShowTitle(false)
            val intent: CustomTabsIntent = builder.build()
            intent.launchUrl(activity, Uri.parse(url))
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Build.VERSION.SDK_INT >= 23) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DEBUG", "Permission: " + permissions[0] + "was " + grantResults[0])
                if (updateChecker() == 0) {
                    checkConnectivity()
                }
            } else {
                Log.d("DEBUG", "Permission denied");
                // TODO : 퍼미션이 거부되는 경우에 대한 코드
            }
        }
    }

    fun getVersionInfo(context: Context): String {
        val i = context.packageManager.getPackageInfo(context.packageName, 0)
        return i.versionName
    }


    fun updateChecker(): Int {
        var jsonObject: JSONObject = JSONObject()
        try {
            jsonObject = checkUpdate().execute().get()
        } catch (e: Exception) {
            Log.e(TAG, "updateServer dead..")
            dispDialog("업데이트 서버와 연결할 수 없습니다. 오프라인 모드로 실행합니다.", {}, "오류", null)
            val hander = Handler()
            hander.postDelayed({
                startActivity(Intent(application, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                this@IntroActivity.finish()
            }, SPLASH_TIME.toLong())
            return 0
        }
        val version = jsonObject.getDouble("version")
        val changes = jsonObject.getString("changes")
        val versionName = getVersionInfo(this)

        if (versionName.toDouble() < version) {
            dispDialog(
                "$version 업데이트가 있어요!\n변경점: $changes",
                {
                    val intent = Intent(Intent.ACTION_VIEW).apply{
                        data = Uri.parse("https://play.google.com/store/apps/details?id=com.kimwash.sjhs_notifier_kotlin")
                        setPackage("com.android.vending")
                    }
                    startActivity(intent)
                }, "업데이트", R.drawable.ic_build_black_24dp
            )

            return 1
        } else {
            val hander = Handler()
            hander.postDelayed({
                startActivity(Intent(application, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                this@IntroActivity.finish()
            }, SPLASH_TIME.toLong())
        }
        return 0
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQ_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Update Failed: ${resultCode}")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        UPDATE_REQ_CODE
                    );
                }
            }
    }


    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (checkConnectivity() == false) {
            dispDialog(
                "인터넷에 연결되어있지 않습니다. 확인 후 다시 이용 바랍니다.",
                { android.os.Process.killProcess(android.os.Process.myPid()) }, "", null
            )

        } else {
            val intent = getIntent()
            if (intent != null) {
                val category = intent.getStringExtra("category")
                if (category == "announce") {
                    val message = intent.getStringExtra("message")

                    dispDialog(
                        message,
                        {}, "공지사항", R.drawable.ic_build_black_24dp
                    )

                } else if (category == "h4pay") {
                    launchChromeTab(this, "https://h4pay.co.kr")
                    finishAffinity()
                    exitProcess(0)
                }
            }
            if (isNightModeActive(this)) {
                setTheme(R.style.DarkTheme)
            } else if (!isNightModeActive(this)) {
                setTheme(R.style.LightTheme)
            }
            if (!permissionManager.hasPermissions(this, permissionList)) {
                ActivityCompat.requestPermissions(this, permissionList, permissionALL)
            } else {

                appUpdateManager = AppUpdateManagerFactory.create(this)

                val appUpdateInfo = appUpdateManager.appUpdateInfo.addOnCompleteListener {
                    if (it.isSuccessful){
                        when (it.result.updateAvailability()) {
                            UpdateAvailability.UPDATE_AVAILABLE -> {
                                appUpdateManager.startUpdateFlowForResult(
                                    it.result,
                                    AppUpdateType.IMMEDIATE,
                                    this,
                                    UPDATE_REQ_CODE
                                )
                            }
                            UpdateAvailability.UPDATE_NOT_AVAILABLE-> {
                                updateChecker()
                            }
                        }
                    }else{
                        updateChecker()
                    }
                }
            }
            setContentView(R.layout.activity_intro)
            Stetho.initializeWithDefaults(this)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            if (isNightModeActive(this)) {
                imageView.setImageResource(R.drawable.splash_dark)
            } else if (!isNightModeActive(this)) {
                imageView.setImageResource(R.drawable.splash)
            }
        }


    }


    override fun onBackPressed() {
    }
}