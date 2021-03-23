package com.kimwash.sjhs_notifier_kotlin

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException


/**
 *
 * 데이터 저장 및 로드 클래스
 *
 */
object PreferenceManager {
    const val PREFERENCES_NAME = "rebuild_preference"
    private const val DEFAULT_VALUE_STRING = ""
    private const val DEFAULT_VALUE_BOOLEAN = false
    private const val DEFAULT_VALUE_INT = -1
    private const val DEFAULT_VALUE_LONG = -1L
    private const val DEFAULT_VALUE_FLOAT = -1f
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun setStringArrayPref(context: Context, key: String, values: ArrayList<String>) {
        val prefs: SharedPreferences = getPreferences(context)
        val editor = prefs.edit()
        val a = JSONArray()
        for (i in 0 until values.size) {
            a.put(values[i])
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString())
        } else {
            editor.putString(key, null)
        }
        editor.apply()
    }

    fun getStringArrayPref(context: Context, key: String): ArrayList<String>? {
        val prefs: SharedPreferences = getPreferences(context)
        val json = prefs.getString(key, null)
        val urls = ArrayList<String>()
        if (json != null) {
            try {
                val a = JSONArray(json)
                for (i in 0 until a.length()) {
                    val url = a.optString(i)
                    urls.add(url)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return urls
    }


    fun setString(
        context: Context,
        key: String?,
        value: String?
    ) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun setBoolean(
        context: Context,
        key: String?,
        value: Boolean
    ) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun setInt(context: Context, key: String?, value: Int) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun setLong(
        context: Context,
        key: String?,
        value: Long
    ) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.commit()
    }

    fun setFloat(
        context: Context,
        key: String?,
        value: Float
    ) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putFloat(key, value)
        editor.commit()
    }

    fun getString(context: Context, key: String?): String? {
        val prefs = getPreferences(context)
        return prefs.getString(key, DEFAULT_VALUE_STRING)
    }

    fun getBoolean(context: Context, key: String?): Boolean {
        val prefs = getPreferences(context)
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }

    fun getInt(context: Context, key: String?): Int {
        val prefs = getPreferences(context)
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }

    fun getLong(context: Context, key: String?): Long {
        val prefs = getPreferences(context)
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }

    fun getFloat(context: Context, key: String?): Float {
        val prefs = getPreferences(context)
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT)
    }

    fun removeKey(context: Context, key: String?) {
        val prefs = getPreferences(context)
        val edit = prefs.edit()
        edit.remove(key)
        edit.commit()
    }

    fun clear(context: Context) {
        val prefs = getPreferences(context)
        val edit = prefs.edit()
        edit.clear()
        edit.commit()
    }
}