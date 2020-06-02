package com.example.sjhs_notifier_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setclass.*

class setClassActivity : AppCompatActivity(){
    private var selectedClass:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (MainActivity.isNightModeActive(this) == true){
            setTheme(R.style.DarkDialog)
        }
        else if(MainActivity.isNightModeActive(this) == false){
            setTheme(R.style.LightDialog)
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_setclass)
        Log.e(TAG, "setClass진입")
        //UI 객체생성
        val txtText = findViewById<View>(R.id.txtText) as TextView
        //데이터 가져오기
        val intent = intent
        val type = intent.getIntExtra("type", 0)
        if (type == 1){
            txtText.setText("몇 반이신가요?")
        }
        val classItems = arrayListOf<String>("반을 선택해주세요!", "1반", "2반", "3반", "4반", "5반", "6반", "7반", "8반")
        val classAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classItems)
        classSpinner.adapter = classAdapter
        classSpinner.isEnabled = false
        classSpinner.isClickable = false
        classSpinner.setSelection(0, false)
        classSpinner.isEnabled = true
        classSpinner.isClickable = true
        classSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               selectedClass = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }

    }

    //확인 버튼 클릭
    fun mOnClose(v: View?) { //데이터 전달하기
        if (selectedClass == 0){
            Toast.makeText(this@setClassActivity, "반을 선택해주세요!", Toast.LENGTH_SHORT)
        }
        else{
            val intent = Intent()
            intent.putExtra("class", selectedClass)
            setResult(RESULT_OK, intent)
            //액티비티(팝업) 닫기
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean { //바깥레이어 클릭시 안닫히게
        return event.action != MotionEvent.ACTION_OUTSIDE
    }

    override fun onBackPressed() { //안드로이드 백버튼 막기
        return
    }
}