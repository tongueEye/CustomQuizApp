package com.example.customquizapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.customquizapp.databinding.ActivityPlayQuizBinding

class PlayQuizActivity:AppCompatActivity() {
    private lateinit var binding: ActivityPlayQuizBinding
    private var folderName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 폴더 이름을 인텐트로부터 받아옴
        folderName = intent.getStringExtra("folder_name")
        Toast.makeText(this, "${folderName}",Toast.LENGTH_SHORT).show()

        var currentIndex = 0

        loadQuizData(currentIndex)


    }

    fun loadQuizData(currentIdx: Int) {
        //folderName으로 quiz Data를 가져옴
        val db = AppDatabase.getDatabase(applicationContext)
        val quizDao = db?.quizDao()
        val quizData = folderName?.let { quizDao?.getAllQuizzes(it) }

        // 퀴즈 데이터가 null이거나 currentIdx가 퀴즈 데이터의 범위를 벗어나면 함수 종료
        if (quizData == null || currentIdx < 0 || currentIdx >= quizData.size) {
            return
        }

        val currentQuiz = quizData[currentIdx]

        binding.questionTV.text = currentQuiz.question
        binding.answerCheckTV.text = "(정답 확인)"
        binding.dialogTV.text = if (currentQuiz.isCorrect) "맞춘\n문제입니다!" else "맞춰봐!"

        // 배경색 변경
        if (currentQuiz.isCorrect) {
            binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.light_sky))
            binding.leftBtn.setImageResource(R.drawable.arrow_left_white)
            binding.rightBtn.setImageResource(R.drawable.arrow_right_white)
            binding.quizPaperIV.setImageResource(R.drawable.quiz_card_paper_white)
            binding.playQuizBottom.setImageResource(R.drawable.play_quiz_bottom_grass)
            binding.exitBtn.setImageResource(R.drawable.exit_btn_white)
        } else{
            binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_night))
            binding.leftBtn.setImageResource(R.drawable.arrow_left)
            binding.rightBtn.setImageResource(R.drawable.arrow_right)
            binding.quizPaperIV.setImageResource(R.drawable.quiz_card_paper)
            binding.playQuizBottom.setImageResource(R.drawable.gate_bottom)
            binding.exitBtn.setImageResource(R.drawable.exit_btn)
        }

        // 이미지 변경
        val imageResource = if (currentQuiz.isCorrect) {
            R.drawable.angel_icon // isCorrect가 true일 때
        } else {
            R.drawable.devil_icon // isCorrect가 false일 때
        }
        binding.devilCheckBtn.setImageResource(imageResource)

        val imageUri = currentQuiz.id?.let { quizDao?.getImageUri(it)?.toUri() }

        if (imageUri != null && imageUri.toString().isNotBlank()) {
            binding.questionIV.visibility = View.VISIBLE // 이미지가 있을 때 보이도록 설정
            Glide.with(this)
                .load(imageUri)
                .into(binding.questionIV)
        } else{
            binding.questionIV.visibility = View.GONE // 이미지가 없을 때 숨김 처리
        }

        // rightBtn을 클릭했을 때, 인덱스를 증가시키고 퀴즈 사이즈 보다 크면 시 첫 인덱스로 돌아가도록 처리
        binding.rightBtn.setOnClickListener {
            val nextIdx = (currentIdx + 1) % quizData.size
            loadQuizData(nextIdx)
        }

        // leftBtn을 클릭했을 때, 인덱스를 감소시키고 0보다 작으면 그 마지막 인덱스로 가도록 처리
        binding.leftBtn.setOnClickListener {
            val prevIdx = if (currentIdx - 1 < 0) quizData.size - 1 else currentIdx - 1
            loadQuizData(prevIdx)
        }

        binding.devilCheckBtn.setOnClickListener {
            if (binding.answerCheckTV.text == "(문제 확인)"){
                // 현재 퀴즈의 isCorrect 값을 토글
                quizData[currentIdx].isCorrect = !quizData[currentIdx].isCorrect
                quizData[currentIdx].id?.let { quizId ->
                    // 데이터베이스에 업데이트
                    quizDao?.updateQuiz(quizData[currentIdx])

                    loadQuizData(currentIdx)
                }
            }
        }

        binding.answerCheckTV.setOnClickListener {
            if (binding.answerCheckTV.text == "(정답 확인)") {
                // 정답 확인 모드에서는 정답을 보여주고 문제확인 모드 셋팅
                binding.questionTV.text = quizData[currentIdx].answer
                binding.questionIV.visibility = View.GONE
                binding.answerCheckTV.text = "(문제 확인)"
                binding.dialogTV.text = if (currentQuiz.isCorrect) "정답입니다!" else "맞췄다면 \n날 클릭해봐!"

            } else {
                // 문제 확인 모드에서는 다시 문제를 보여주고 정답확인 모드 셋팅
                binding.questionTV.text = quizData[currentIdx].question
                binding.questionIV.visibility = View.VISIBLE
                binding.answerCheckTV.text = "(정답 확인)"

                binding.dialogTV.text = if (currentQuiz.isCorrect) "맞춘\n문제입니다!" else "맞춰봐!"
            }
        }
    }
}
