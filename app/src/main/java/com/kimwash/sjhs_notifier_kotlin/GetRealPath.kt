package com.kimwash.sjhs_notifier_kotlin

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log

/**
 * Created by IamDeveloper on 8/29/2016.
 */

object GetRealPath {
    @SuppressLint("NewApi")
    fun getRealPathFromAPI19(
        context: Context,
        uri: Uri
    ): String? {
        Log.i("GetRealPath", uri.toString())
        var filePath: String? = null
        try {
            val getID = DocumentsContract.getDocumentId(uri)
            Log.i("GetRealPath", "getID = $getID")
            val id = getID.split(":".toRegex()).toTypedArray()[1]
            Log.i("GetRealPath", "id = $id")
            val column =
                arrayOf(MediaStore.Files.FileColumns.DATA)
            Log.i("GetRealPath", "column = " + column[0])
            val select = MediaStore.Files.FileColumns._ID + "=?"
            Log.i("GetRealPath", "select = $select")
            val cursor = context.contentResolver.query(
                MediaStore.Files.getContentUri("external"), column,
                select, arrayOf(id), null
            )
            Log.i("GetRealPath", "cursor = $cursor")
            assert(cursor != null)
            val columnIndex = cursor!!.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            Log.i("GetRealPath", "filePath = $filePath")
            cursor.close()
            return filePath
        } catch (e: RuntimeException) {
            Log.d("RuntimeException", e.toString())
        }
        return filePath
    }

    @SuppressLint("NewApi")
    fun getRealPathFromSD_CARD(uri: Uri?): String? {
        var realPath: String? = null
        try {
            val getID = DocumentsContract.getDocumentId(uri)
            val id = getID.split(":".toRegex()).toTypedArray()[1]
            realPath = "/storage/sdcard/$id"
        } catch (e: RuntimeException) {
            Log.d("RuntimeException", e.toString())
        }
        return realPath
    }
}