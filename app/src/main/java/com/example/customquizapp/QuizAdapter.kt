package com.example.customquizapp

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.customquizapp.databinding.DialogConfirm2Binding
import com.example.customquizapp.databinding.DialogConfirmBinding
import com.example.customquizapp.databinding.DialogCreateQuizBinding
import com.example.customquizapp.databinding.ItemQuizBinding


class QuizAdapter(private val quizDao: QuizDao, private val quizActivity: QuizActivity) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    private var quizList: MutableList<Quiz> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = quizList[position]
        holder.binding.QuestionTV.text = quiz.question

        holder.binding.optionBtn.setOnClickListener {
            showPopupMenu(holder.binding.optionBtn, quiz)
        }
    }

    override fun getItemCount() = quizList.size

    fun setQuizList(quizList: List<Quiz>){
        this.quizList.clear()
        this.quizList.addAll(quizList)
        notifyDataSetChanged()
    }

    private fun showPopupMenu(view: View, quiz: Quiz){
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.menu_option)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.action_edit -> {
                    //수정 작업 수행
                    showEditDialog(view, quiz)
                    true
                }
                R.id.action_delete -> {
                    //삭제 작업 수행
                    showDeleteComfirmDialog(view.context, quiz)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showEditDialog(view: View, quiz: Quiz){
        quizActivity.showEditQuizDialog(quiz)
    }

    private fun updateQuestionText(quiz: Quiz, newQuestion: String, newAnswer: String){
        quiz.question = newQuestion
        quiz.answer = newAnswer
        quizDao.updateQuiz(quiz)
        notifyDataSetChanged()
    }

    private fun showDeleteComfirmDialog(context: Context, quiz: Quiz){
        val dialogBinding = DialogConfirm2Binding.inflate(LayoutInflater.from(context))
        val dialogBuilder = AlertDialog.Builder(context)
        val alertDialog = dialogBuilder.create()

        dialogBinding.confirmTextView.text = "퀴즈를 삭제하시겠습니까?"

        alertDialog.setView(dialogBinding.root)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.noButton.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.yesButton.setOnClickListener {
            deleteQuiz(quiz)
            alertDialog.dismiss()
            Toast.makeText(context, "퀴즈가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }
        alertDialog.show()
    }

    private fun deleteQuiz(quiz: Quiz){
        quizDao.deleteQuiz(quiz)
        quizList.remove(quiz)
        notifyDataSetChanged() // 삭제된 항목을 즉시 반영하여 화면 갱신
    }

    class QuizViewHolder(val binding: ItemQuizBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}