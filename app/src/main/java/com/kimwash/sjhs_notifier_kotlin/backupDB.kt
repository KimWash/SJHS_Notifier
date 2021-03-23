package com.kimwash.sjhs_notifier_kotlin

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel


object backupDB : Application()
{
    fun importDB(context: Context, fileUri:String) {
        try {
            val sd: File = Environment.getExternalStorageDirectory()
            val data: File = Environment.getDataDirectory()
            if (sd.canWrite()) {
                val currentDBPath = ("//data//" + "com.kimwash.sjhs_notifier_kotlin"
                        + "/databases")

                val currentDB = File("$data$currentDBPath", "tableDB")
                val source = File(fileUri)
                val src: FileChannel = FileInputStream(source).getChannel()
                val dst: FileChannel = FileOutputStream(currentDB).getChannel()
                try{
                    src.transferTo(0, src.size(), dst)
                } catch (e:java.lang.Exception){

                } finally {
                    src.close()
                    dst.close()
                }

                src.close()
                dst.close()
                Toast.makeText(
                    context,
                    "Import Successful!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Import Failed!" + e.message,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

  fun exportDB(context: Context) {
      try {
          val sd = Environment.getExternalStorageDirectory()
          val data = Environment.getDataDirectory()

          if (sd.canWrite()) {
              val currentDBPath = "//data//${context.packageName}/databases"
              Log.e(TAG, currentDBPath)
              val currentDB = File("$data$currentDBPath", "tableDB")
              val backupDB = File(context.filesDir, "tableDB.sqlite")

              if (currentDB.exists()){
                  val src = FileInputStream(currentDB).channel
                  val dst = context.openFileOutput("tableDB.sqlite", Context.MODE_PRIVATE).channel
                  dst.transferFrom(src, 0, src.size())
                  src.close()
                  dst.close()
                  if (backupDB.exists()) {
                      Toast.makeText(context, "소중한 시간표를 백업했어요. 어디로 보낼까요?", Toast.LENGTH_SHORT).show()
                      val intent = Intent(Intent.ACTION_SEND)
                      intent.type = "application/*"
                      intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                      intent.putExtra(
                          Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                              context,
                              BuildConfig.APPLICATION_ID,
                              backupDB
                          )
                      )
                      context.startActivity(intent)
                  }
              }
              else{
                  Toast.makeText(context, "시간표가 없는 것 같습니다.", Toast.LENGTH_SHORT).show()
              }
          }
      } catch (e: Exception) {
          Toast.makeText(
              context,
              "Backup Failed!" + e.message,
              Toast.LENGTH_SHORT
          )
              .show()
      }

  }

}