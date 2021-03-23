package com.kimwash.sjhs_notifier_kotlin

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object permissionManager{
fun hasPermissions(context: Context, permissions:Array<String>): Boolean{
    for (permission in permissions){
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
            return false
        }
    }
    return true
}
}