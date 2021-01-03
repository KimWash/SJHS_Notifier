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
import androidx.annotation.RequiresApi
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
        FirebaseMessaging.getInstance().subscribeToTopic("announce")
        FirebaseMessaging.getInstance().subscribeToTopic("h4pay")
        Log.d("FCM_TEST", p0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(channel: String, channel_nm: String, desc: String, title: String, message: String, pendingIntent: PendingIntent, icon:Int) {
        var notificationBuilder: NotificationCompat.Builder? = null

        val notichannel =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelMessage = NotificationChannel(
            channel, channel_nm,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channelMessage.description = desc
        channelMessage.enableLights(true)
        channelMessage.enableVibration(true)
        channelMessage.setShowBadge(false)
        channelMessage.vibrationPattern = longArrayOf(1000, 1000)
        notichannel.createNotificationChannel(channelMessage)
        //푸시알림을 Builder를 이용하여 만듭니다.

        notificationBuilder = NotificationCompat.Builder(this, channel)
            .setSmallIcon(icon)
            .setContentTitle(title) //푸시알림의 제목
            .setContentText(message) //푸시알림의 내용
            .setChannelId(channel)
            .setAutoCancel(true) //선택시 자동으로 삭제되도록 설정.
            .setContentIntent(pendingIntent) //알림을 눌렀을때 실행할 인텐트 설정.
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(9999, notificationBuilder?.build())
    }


    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        if (p0 != null) {
            FirebaseMessaging.getInstance().subscribeToTopic("selfCheck")
            FirebaseMessaging.getInstance().subscribeToTopic("update")
            FirebaseMessaging.getInstance().subscribeToTopic("announce")
            FirebaseMessaging.getInstance().subscribeToTopic("h4pay")
            val title = p0.data.get("title")
            val message = p0.data.get("message")
            val category = p0.data.get("category")
            var pendingIntent: PendingIntent? = null
            if (category == "selfCheck") {
                var selfCheckLink = resources.getString(R.string.selfCheckLink)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selfCheckLink))
                intent.putExtra("category", category)
                pendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else if (category == "update") {
                val intent = Intent(this, IntroActivity::class.java)
                intent.putExtra("category", category)
                pendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else if (category == "announce") {
                val intent = Intent(this, IntroActivity::class.java)
                intent.putExtra("category", category)
                intent.putExtra("message", message)
                pendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else if (category == "h4pay") {
                val intent = Intent(this, h4payActivity::class.java)
                intent.putExtra("category", category)
                intent.putExtra("message", message)
                pendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }


            Log.e("NotiTest", category)
            if (title != null && message != null && pendingIntent != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when (category) {
                        "selfCheck" -> {
                            createChannel(
                                "selfCheck",
                                "자가진단 알림",
                                "자가진단 알림을 받습니다.",
                                title!!,
                                message!!,
                                pendingIntent!!,
                                R.drawable.ic_hospital
                            )
                        }
                        "update" -> {
                            createChannel(
                                "update",
                                "업데이트 알림",
                                "업데이트 알림을 받습니다.",
                                title!!,
                                message!!,
                                pendingIntent!!,
                                R.drawable.ic_update
                            )
                        }
                        "announce" -> {
                            createChannel(
                                "announce",
                                "공지사항",
                                "공지사항 알림을 받습니다.",
                                title!!,
                                message!!,
                                pendingIntent!!,
                                R.drawable.ic_baseline_announcement_24
                            )
                        }
                        "h4pay" -> {
                            createChannel(
                                "h4pay",
                                "H4Pay",
                                "H4Pay 관련 알림을 받습니다.",
                                title!!,
                                message!!,
                                pendingIntent!!,
                                R.drawable.ic_baseline_payment_24
                            )
                        }
                    }
                }
            }
        }
    }
}
