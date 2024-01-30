package com.example.customquizapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.customquizapp.databinding.ActivityQuizBinding
import com.example.customquizapp.databinding.DialogCreateQuizBinding

class QuizActivity: AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var quizAdapter: QuizAdapter

    private var folderName: String = " "
    private lateinit var dialogBinding: DialogCreateQuizBinding // 다이얼로그 바인딩 변수 추가

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

        //quizAdapter 초기화
        quizAdapter = quizDao?.let { QuizAdapter(it) }!!

        //Adapter 적용
        binding.quizListRV.adapter = quizAdapter

        // 처음 QuizActivity에 들어갈 때 퀴즈 목록을 화면에 표시
        loadQuizList(folderName)
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
            saveQuiz(quizText, answerText, folderName)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun saveQuiz(quizText: String, answerText: String, folderName: String) {
        val quiz = Quiz(folderName = folderName, question = quizText, answer = answerText)
        AppDatabase.getDatabase(applicationContext)?.quizDao()?.insertQuiz(quiz)
        Toast.makeText(this, "퀴즈가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        loadQuizList(folderName)
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