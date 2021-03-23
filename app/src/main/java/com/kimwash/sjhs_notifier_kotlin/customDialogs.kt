package com.kimwash.sjhs_notifier_kotlin

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object customDialogs {
    fun yesNoDialog(context: Context, title: String, message:String, yesEvent: () -> Unit, noEvent:() -> Unit){
        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        yesEvent()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        noEvent()
                    }
                }
            }


        val alertDialog = AlertDialog.Builder(
            context,
            R.style.AlertDialogTheme
        ).apply{
            this.setTitle(title)
            this.setMessage(message)
            this.setPositiveButton("예", dialogClickListener)
            this.setNegativeButton("아니오", dialogClickListener)
        }

        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    fun yesOnlyDialog(context: Context, msg:String, okEvent: () -> Unit, title:String, icon:Int?){
        val alert_confirm = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        alert_confirm.setMessage(msg)
        alert_confirm.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
            okEvent()
        })
        if (icon != null){
            alert_confirm
                .setTitle(title)
                .create()
                .setIcon(icon)
        }
        else{
            alert_confirm
                .setTitle(title)
                .create()
        }

        val alert = alert_confirm.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()

    }
}