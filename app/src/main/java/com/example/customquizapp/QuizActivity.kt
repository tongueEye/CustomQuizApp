package com.example.customquizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.example.customquizapp.databinding.ActivityQuizBinding
import com.example.customquizapp.databinding.DialogCreateQuizBinding
import java.io.InputStream


class QuizActivity: AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var quizAdapter: QuizAdapter

    private var folderName: String = " "
    private var selectedImageUri: Uri?=null // 이미지를 저장할 변수 추가
    private lateinit var dialogBinding: DialogCreateQuizBinding // 다이얼로그 바인딩 변수 추가

    private var DELETE_IMAGE_CHECK = 0 //이미지 삭제 버튼 클릭 여부 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Appdatabase 초기화
        val db = AppDatabase.getDatabase(applicationContext)
        val quizDao = db?.quizDao()

        // 폴더 ID 가져오기
        folderName = intent.getStringExtra("folder_name").toString()

        binding.folderTitleTV.text = folderName

        binding.addQuizBtn.setOnClickListener {
            showCreateQuizDialog()
        }

        //RecyclerView 설정
        binding.quizListRV.layoutManager = LinearLayoutManager(this)

        // quizAdapter 초기화
        quizAdapter = quizDao?.let { QuizAdapter(it, this) }!!

        //Adapter 적용
        binding.quizListRV.adapter = quizAdapter

        // 처음 QuizActivity에 들어갈 때 퀴즈 목록을 화면에 표시
        loadQuizList(folderName)
    }

    private val PICK_IMAGE_REQUEST = 1

    fun showCreateQuizDialog() {
        dialogBinding = DialogCreateQuizBinding.inflate(LayoutInflater.from(this))
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
        val dialog = dialogBuilder.create()

        // 다이얼로그 배경을 투명색으로 설정
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.addPhotoIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        dialogBinding.imageDeleteBtn.setOnClickListener {
            Toast.makeText(this,"이미지를 삭제합니다.",Toast.LENGTH_SHORT).show()
            // 이미지를 기본 이미지로 변경
            dialogBinding.addPhotoIV.setImageResource(R.drawable.add_photo_white)
            // 이미지 삭제 버튼 숨기기
            dialogBinding.imageDeleteBtn.visibility = View.GONE

            // 이미지 삭제 버튼이 클릭 되면 값을 1로 변경
            DELETE_IMAGE_CHECK = 1

        }

        // 다이얼로그 내 버튼 클릭 이벤트 처리
        dialogBinding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.saveBtn.setOnClickListener {
            // 저장 버튼 클릭 시 처리할 작업 수행
            val quizText = dialogBinding.quizEditText.text.toString()
            val answerText = dialogBinding.quizAnswerEditText.text.toString()
            Toast.makeText(this, "비트맵 이미지: $selectedImageUri", Toast.LENGTH_SHORT).show()
            if (DELETE_IMAGE_CHECK == 1){ // 사진이 선택되지 않고 이미지 삭제 버튼이 눌린경우
                saveQuiz(quizText, answerText, folderName, null)
            } else{
                saveQuiz(quizText, answerText, folderName, selectedImageUri)
            }

            selectedImageUri = null
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showEditQuizDialog(quiz: Quiz){
        dialogBinding = DialogCreateQuizBinding.inflate(LayoutInflater.from(this))
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
        val dialog = dialogBuilder.create()

        // 다이얼로그 배경을 투명색으로 설정
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //Appdatabase 초기화
        val db = AppDatabase.getDatabase(applicationContext)
        val quizDao = db?.quizDao()

        // 이미지 URI 가져오기
        val imageUri = quizDao?.getImageUri(quiz.id)?.toUri()

        // 기존 퀴즈 정보 설정
        dialogBinding.quizEditText.setText(quiz.question)
        dialogBinding.quizAnswerEditText.setText(quiz.answer)
        if (imageUri != null && imageUri.toString().isNotBlank()) {
            Toast.makeText(this,"null이 아니라 ${imageUri}",Toast.LENGTH_SHORT).show()
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.add_photo_white) // 기본 이미지 설정
                .into(dialogBinding.addPhotoIV)
            dialogBinding.imageDeleteBtn.visibility = View.VISIBLE
        } else {
            dialogBinding.addPhotoIV.setImageResource(R.drawable.add_photo_white)
            dialogBinding.imageDeleteBtn.visibility = View.GONE
        }

        dialogBinding.addPhotoIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }


        dialogBinding.imageDeleteBtn.setOnClickListener {
            Toast.makeText(this,"이미지를 삭제합니다.",Toast.LENGTH_SHORT).show()
            // 이미지를 기본 이미지로 변경
            dialogBinding.addPhotoIV.setImageResource(R.drawable.add_photo_white)
            // 이미지 삭제 버튼 숨기기
            dialogBinding.imageDeleteBtn.visibility = View.GONE

            // 이미지 삭제 버튼이 클릭 되면 값을 1로 변경
            DELETE_IMAGE_CHECK = 1
        }

        // 다이얼로그 내 버튼 클릭 이벤트 처리
        dialogBinding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.saveBtn.setOnClickListener {
            // 저장 버튼 클릭 시 처리할 작업 수행
            val quizText = dialogBinding.quizEditText.text.toString()
            val answerText = dialogBinding.quizAnswerEditText.text.toString()
            val currentImage = quizDao?.getImageUri(quiz.id)?.toUri()
            Toast.makeText(this, "이미지 URI: $selectedImageUri", Toast.LENGTH_SHORT).show()

            if(selectedImageUri != null){ //갤러리에서 새로 사진을 선택한 경우
                // 선택한 사진을 저장
                updateQuiz(quiz.id, quizText, answerText, folderName, selectedImageUri)
            } else{ //갤러리에서 새로 사진을 선택하지 않은 경우
                // 기존의 이미지를 그대로 저장
                updateQuiz(quiz.id, quizText, answerText, folderName, currentImage)
            }

            if (DELETE_IMAGE_CHECK == 1){ // 사진이 선택되지 않고 이미지 삭제 버튼이 눌린경우
                quizDao?.updateQuizImageUri(quiz.id) //DB에 저장된 이미지를 null로 바꿈
            }

            Toast.makeText(this, "퀴즈가 수정되었습니다.", Toast.LENGTH_SHORT).show()
            loadQuizList(folderName)
            selectedImageUri = null
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DELETE_IMAGE_CHECK = 0 //이미지가 선택되면 0으로 변경
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            // 선택한 이미지를 addPhotoIV 이미지 뷰에 표시
            dialogBinding.addPhotoIV.setImageURI(imageUri)
            selectedImageUri = imageUri

            // 이미지가 선택된 후에 Glide를 사용하여 이미지를 설정합니다.
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.add_photo_white) // 기본 이미지 설정
                .into(dialogBinding.addPhotoIV)

            if (imageUri == null) {
                dialogBinding.imageDeleteBtn.visibility = View.GONE
            } else {
                dialogBinding.imageDeleteBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun saveQuiz(quizText: String, answerText: String, folderName: String, imageUri: Uri?) {
        val imageUriString = imageUri?.toString() ?: ""
        val quiz = Quiz(folderName = folderName, question = quizText, answer = answerText, imageUri = imageUriString)
        AppDatabase.getDatabase(applicationContext)?.quizDao()?.insertQuiz(quiz)
        Toast.makeText(this, "퀴즈가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        loadQuizList(folderName)
    }

    private fun updateQuiz(id: Int, question: String, answer: String, folderName: String, imageUri: Uri?) {
        val imageUriString = imageUri?.toString() ?: ""
        val updatedQuiz = Quiz(id = id, folderName = folderName, question = question, answer = answer, imageUri = imageUriString)
        AppDatabase.getDatabase(applicationContext)?.quizDao()?.updateQuiz(updatedQuiz)
    }

    private fun loadQuizList(folderName: String) {
        val quizList: List<Quiz> = AppDatabase.getDatabase(applicationContext)?.quizDao()?.getAllQuizzes(folderName)
            ?: emptyList()

        if (quizList.isNotEmpty()){
            //데이터 적용
            quizAdapter.setQuizList(quizList)
        }
    }

}