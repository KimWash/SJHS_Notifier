package com.kimwash.sjhs_notifier_kotlin

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_idcard.*
import kotlinx.android.synthetic.main.dialog_idinputs.view.*

class idCardActivity : AppCompatActivity() {

    private var idCode = ""
    fun initScan() {
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.initiateScan()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        var menuInflater = menuInflater
        menuInflater.inflate(R.menu.register, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.editidcard -> {
                if (idCode != "") {
                    customDialogs.yesNoDialog(this, "확인", "이미 학생증이 등록되어 있습니다. 학생증을 교체하시겠습니까?", {
                        customDialogs.yesOnlyDialog(this, "학생증 등록 절차를 시작합니다. 실물 학생증의 바코드를 스캔해주세요.", {
                            initScan()
                        }, "학생증 수정", null)
                    }, {})
                } else {
                    initScan()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        if (MainActivity.isNightModeActive(this)){
            setTheme(R.style.DarkTheme)
        }
        else if(!MainActivity.isNightModeActive(this)){
            setTheme(R.style.LightTheme)
        }
        super.setContentView(R.layout.activity_idcard)
        setSupportActionBar(idToolbar)
        getSupportActionBar()?.title = "전자 학생증"
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        idCode = PreferenceManager.getString(this, "idCode").toString()
        if (idCode == "") {
            customDialogs.yesOnlyDialog(this, "학생증이 등록되어 있지 않습니다. 실물 학생증의 바코드를 스캔해주세요.", {
                initScan()
            }, "학생증 등록", null)
        }else{
            genBarcode(idCode)
        }

        refreshUI()

    }

    fun refreshUI(){
        idName.text = PreferenceManager.getString(this, "idName")
        idBirth.text = PreferenceManager.getString(this, "idBirth")
    }

    fun askInfos(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_idinputs, null)
        val askDialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setView(dialogView)
            .show()

        val idName = dialogView.findViewById<EditText>(R.id.idName)
        val idBirth = dialogView.findViewById<EditText>(R.id.idBirth)
        dialogView.submitID.setOnClickListener {
            Log.d("LOG", "${idName.text}, ${idBirth.text}")
            if (idName.text.toString() != "" && idBirth.text.toString() != ""){
                PreferenceManager.setString(this, "idName", idName.text.toString())
                PreferenceManager.setString(this, "idBirth", idBirth.text.toString())
                askDialog.dismiss()
                refreshUI()
            }
            else{
                Toast.makeText(this, "이름과 생년월일을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "정상적으로 스캔되었습니다.", Toast.LENGTH_SHORT).show()
                PreferenceManager.setString(this, "idCode", result.contents)
                askInfos()

                genBarcode(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun genBarcode(data: String) {
        barcode.post(Runnable {
            val multiFormatWriter = MultiFormatWriter()
            try {
                val bitMatrix =
                    multiFormatWriter.encode(data, BarcodeFormat.CODE_39, barcode.width, 200)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                barcode.setImageBitmap(bitmap)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
        })
    }
}