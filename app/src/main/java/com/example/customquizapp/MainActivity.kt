package com.example.customquizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.customquizapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playBtn.setOnClickListener {
            val intent = Intent(this, GateActivity::class.java)
            startActivity(intent)
        }

        binding.quitBtn.setOnClickListener {
            showExitDialog()
        }
    }

    private fun showExitDialog(){
        // 다이얼로그 생성
        val builder = AlertDialog.Builder(this)
        builder.setTitle("종료 확인")
            .setMessage("앱을 종료하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                // '예' 버튼 클릭 시 앱 종료
                finish()
                Toast.makeText(this, "앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("아니오") { dialog, _ ->
                // '아니오' 버튼 클릭 시 다이얼로그 닫기
                dialog.dismiss()
            }

        // 다이얼로그 표시
        builder.create().show()
    }
}