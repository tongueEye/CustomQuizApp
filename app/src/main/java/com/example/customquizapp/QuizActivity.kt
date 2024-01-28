package com.example.customquizapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.customquizapp.databinding.ActivityQuizBinding
import com.example.customquizapp.databinding.DialogCreateQuizBinding
import java.io.InputStream

class QuizActivity: AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private var folderName: String = " "
    private var selectedImageBitmap: Bitmap? = null // 이미지를 저장할 변수 추가
    private lateinit var dialogBinding: DialogCreateQuizBinding // 다이얼로그 바인딩 변수 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 폴더 ID 가져오기
        folderName = intent.getStringExtra("folder_name").toString()

        binding.addQuizBtn.setOnClickListener {
            showCreateQuizDialog()
        }
    }

    private val PICK_IMAGE_REQUEST = 1
    private fun showCreateQuizDialog() {
        dialogBinding = DialogCreateQuizBinding.inflate(LayoutInflater.from(this))
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setTitle("퀴즈 카드 만들기")
        val dialog = dialogBuilder.create()

        // 다이얼로그 배경을 투명색으로 설정
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.addPhotoIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // 다이얼로그 내 버튼 클릭 이벤트 처리
        dialogBinding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.saveBtn.setOnClickListener {
            // 저장 버튼 클릭 시 처리할 작업 수행
            val quizText = dialogBinding.quizEditText.text.toString()
            val answerText = dialogBinding.quizAnswerEditText.text.toString()
            Toast.makeText(this, "폴더 ID: $folderName", Toast.LENGTH_SHORT).show()
            Toast.makeText(this,"${quizText} ${answerText}",Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "사진 Uri: $selectedImageBitmap", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            // 선택한 이미지를 비트맵으로 변환하여 selectedImageBitmap 변수에 저장
            selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
            // 선택한 이미지를 addPhotoIV 이미지 뷰에 표시
            selectedImageBitmap?.let {
                dialogBinding.addPhotoIV.setImageBitmap(it)
            }
        }
    }
}