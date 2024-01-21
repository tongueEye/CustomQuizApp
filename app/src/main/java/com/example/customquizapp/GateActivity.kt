package com.example.customquizapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.customquizapp.databinding.ActivityGateBinding
import com.example.customquizapp.databinding.DialogCreateFolderBinding

class GateActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addBtn.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog(){
        //다이얼로그 뷰바인딩 초기화
        val dialogBinding = DialogCreateFolderBinding.inflate(LayoutInflater.from(this))
        //AlertDialog.Builder를 사용하여 다이얼로그 생성
        val dialogBuilder = AlertDialog.Builder(this)
        //AlertDialog 생성
        val alertDialog = dialogBuilder.create()

        //다이얼로그의 레이아웃 설정
        alertDialog.setView(dialogBinding.root)

        //다이얼로그의 배경을 투명색으로 설정
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // "취소" 버튼 클릭 이벤트 설정
        dialogBinding.cancelBtn.setOnClickListener {
            alertDialog.dismiss() // 다이얼로그를 닫음
        }

        // "저장" 버튼 클릭 이벤트 설정
        dialogBinding.saveBtn.setOnClickListener {
            alertDialog.dismiss() // 다이얼로그 닫음
            Toast.makeText(this, "새 문제집이 추가되었습니다.", Toast.LENGTH_LONG).show()
        }
        // 다이얼로그 표시
        alertDialog.show()

    }
}