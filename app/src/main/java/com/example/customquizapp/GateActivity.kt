package com.example.customquizapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.customquizapp.databinding.ActivityGateBinding
import com.example.customquizapp.databinding.DialogCreateFolderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ServiceLoader


class GateActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGateBinding
    private lateinit var folderAdapter: FolderAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addBtn.setOnClickListener {
            showAddDialog()
        }

        //RecyclerView 설정
        binding.folderListRV.layoutManager = LinearLayoutManager(this)

        //folderAdapter 초기화
        folderAdapter = FolderAdapter()

        //Adapter 적용
        binding.folderListRV.adapter = folderAdapter

        //폴더 목록 조회
        loadFolderList()
    }

    //폴더 목록 조회
    private fun loadFolderList(){
        val folderList: List<Folder> = AppDatabase.getDatabase(applicationContext)?.folderDao()?.getAllFolder()
            ?: emptyList()

        if (folderList.isNotEmpty()) {
            // 데이터 적용
            folderAdapter.setFolderList(folderList)
        }
    }

    private fun showAddDialog() {
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

            val folderName = dialogBinding.folderNameEditText.text.toString()
            if(folderName.isNotEmpty()){
                alertDialog.dismiss() // 다이얼로그 닫음
                Toast.makeText(this, "새 문제집이 추가되었습니다.", Toast.LENGTH_LONG).show()

                // 폴더 생성
                insertFolder(folderName)
                loadFolderList()

            } else{
                Toast.makeText(this, "문제집 이름을 입력하세요!", Toast.LENGTH_LONG).show()
            }
        }
        // 다이얼로그 표시
        alertDialog.show()
    }

    private fun insertFolder(name: String){
        val folder = Folder(null, name)
        AppDatabase.getDatabase(applicationContext)?.folderDao()?.insertFolder(folder)
    }
}