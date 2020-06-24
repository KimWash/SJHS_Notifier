package com.example.sjhs_notifier_kotlin

import com.example.sjhs_notifier_kotlin.R
import android.R.id.icon_frame
import android.R.id.message
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class notiClass : FirebaseMessagingService() {
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        FirebaseMessaging.getInstance().subscribeToTopic("selfCheck")
        FirebaseMessaging.getInstance().subscribeToTopic("update")
        Log.d("FCM_TEST", p0)
    }


    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        if (p0 != null) {
            FirebaseMessaging.getInstance().subscribeToTopic("selfCheck")
            FirebaseMessaging.getInstance().subscribeToTopic("update")
            val title = p0.data.get("title")
            val message = p0.data.get("message")
            val category = p0.data.get("category")
            var pendingIntent:PendingIntent? = null
            if (category =="selfCheck"){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://eduro.cbe.go.kr/hcheck/index.jsp"))
                intent.putExtra("category", category)
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            else {
                val intent = Intent(this, IntroActivity::class.java)
                intent.putExtra("category", category)
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            var notificationBuilder: NotificationCompat.Builder? = null
            Log.e("NotiTest", category)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (category == "selfCheck") {
                    val channel = "selfCheck"
                    val channel_nm = "자가진단 알림"

                    val notichannel =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val channelMessage = NotificationChannel(
                        channel, channel_nm,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    channelMessage.description = "자가진단 알림을 받습니다."
                    channelMessage.enableLights(true)
                    channelMessage.enableVibration(true)
                    channelMessage.setShowBadge(false)
                    channelMessage.vibrationPattern = longArrayOf(1000, 1000)
                    notichannel.createNotificationChannel(channelMessage)
                    //푸시알림을 Builder를 이용하여 만듭니다.

                    notificationBuilder = NotificationCompat.Builder(this, channel)
                        .setSmallIcon(R.drawable.ic_hospital)
                        .setContentTitle(title) //푸시알림의 제목
                        .setContentText(message) //푸시알림의 내용
                        .setChannelId(channel)
                        .setAutoCancel(true) //선택시 자동으로 삭제되도록 설정.
                        .setContentIntent(pendingIntent) //알림을 눌렀을때 실행할 인텐트 설정.
                        .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                } else if (category == "update") {
                        val channel = "update"
                        val channel_nm = "업데이트 알림"

                        val notichannel =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        val channelMessage = NotificationChannel(
                            channel, channel_nm,
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                        channelMessage.description = "업데이트 알림을 받습니다."
                        channelMessage.enableLights(true)
                        channelMessage.enableVibration(true)
                        channelMessage.setShowBadge(false)
                        channelMessage.vibrationPattern = longArrayOf(1000, 1000)
                        notichannel.createNotificationChannel(channelMessage)
                        //푸시알림을 Builder를 이용하여 만듭니다.

                        notificationBuilder = NotificationCompat.Builder(this, channel)
                            .setSmallIcon(R.drawable.ic_update)
                            .setContentTitle(title) //푸시알림의 제목
                            .setContentText(message) //푸시알림의 내용
                            .setChannelId(channel)
                            .setAutoCancel(true) //선택시 자동으로 삭제되도록 설정.
                            .setContentIntent(pendingIntent) //알림을 눌렀을때 실행할 인텐트 설정.
                            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                    }

                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    notificationManager.notify(9999, notificationBuilder?.build())
            }

        }
    }
}