package com.example.sjhs_notifier_kotlin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper (context: Context): SQLiteOpenHelper(context, "tableDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val tableSQL = "create table tb_tt (" +
                "subject int(10) NOT NULL," +
                "teacher int(10) NOT NULL," +
                "day int(10) NOT NULL," +
                "sPeriod int(10) NOT NULL," +
                "ePeriod int(10) NOT NULL," +
                "stClass int(10) NOT NULL," +
                "stGroup int(10) NOT NULL)"
        db?.execSQL(tableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table tb_tt")
        onCreate(db)
    }
}