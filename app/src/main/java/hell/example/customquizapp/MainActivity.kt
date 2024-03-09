package hell.example.customquizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.tongueeye.memorization.hell.databinding.ActivityMainBinding
import com.tongueeye.memorization.hell.databinding.DialogConfirmBinding

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
        // 다이얼로그 뷰바인딩 초기화
        val dialogBinding = DialogConfirmBinding.inflate(LayoutInflater.from(this))
        // AlertDialog.Builder를 사용하여 다이얼로그 생성
        val dialogBuilder = AlertDialog.Builder(this)
        // AlertDialog 생성
        val alertDialog = dialogBuilder.create()

        // 다이얼로그의 레이아웃 설정
        alertDialog.setView(dialogBinding.root)

        // 다이얼로그 배경을 투명색으로 설정
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 내용 설정
        dialogBinding.confirmTextView.text = "종료하시겠습니까?"

        // "취소" 버튼 클릭 이벤트 설정
        dialogBinding.noButton.setOnClickListener {
            alertDialog.dismiss() // 다이얼로그를 닫음
        }

        // "확인" 버튼 클릭 이벤트 설정
        dialogBinding.yesButton.setOnClickListener {
            alertDialog.dismiss() // 다이얼로그 닫음
            finish() // 액티비티 종료
        }
        // 다이얼로그 표시
        alertDialog.show()
    }
}