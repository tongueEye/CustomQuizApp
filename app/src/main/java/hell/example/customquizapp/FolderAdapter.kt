package hell.example.customquizapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.tongueeye.memorization.hell.R
import com.tongueeye.memorization.hell.databinding.DialogConfirmBinding
import com.tongueeye.memorization.hell.databinding.DialogCreateFolderBinding
import com.tongueeye.memorization.hell.databinding.ItemFolderBinding

class FolderAdapter(private val folderDao: FolderDao): RecyclerView.Adapter<FolderAdapter.Holder>() {
    private var folderList: MutableList<Folder> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemFolderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val folder = folderList[position]
        holder.binding.folderNameTV.text = folder.folderName

        // 폴더에 속한 퀴즈들의 isCorrect 값을 확인
        val allQuizzes = folder.folderName?.let {
            AppDatabase.getDatabase(holder.itemView.context)?.quizDao()?.getAllQuizzes(it)
        } ?: emptyList()
        val allCorrect = allQuizzes.all { it.isCorrect }

        // isCorrect 값에 따라서 아이콘 설정
        if (allQuizzes.isEmpty() || !allCorrect) {
            holder.binding.IconIV.setImageResource(R.drawable.flame3)
        } else {
            holder.binding.IconIV.setImageResource(R.drawable.magic_btn1)
        }

        holder.binding.folderNameTV.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, QuizActivity::class.java)
            intent.putExtra("folder_name",folder.folderName)
            context.startActivity(intent)
        }

        holder.binding.optionBtn.setOnClickListener {
            showPopupMenu(holder.binding.optionBtn, folder)
        }

    }

    override fun getItemCount() = folderList.size

    fun setFolderList(folderList: List<Folder>){
        this.folderList.clear()
        this.folderList.addAll(folderList)
        notifyDataSetChanged()
    }

    private fun showPopupMenu(view: View, folder: Folder) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.menu_option)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // 수정 작업 수행
                    showEditDialog(view, folder)
                    true
                }
                R.id.action_delete -> {
                    // 삭제 작업 수행
                    showDeleteConfirmationDialog(view.context, folder)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showEditDialog(view: View, folder: Folder) {
        val dialogBinding = DialogCreateFolderBinding.inflate(LayoutInflater.from(view.context))
        dialogBinding.folderNameEditText.setText(folder.folderName) // EditText에 폴더 이름 설정

        val dialogBuilder = AlertDialog.Builder(view.context)
        val alertDialog = dialogBuilder.create()

        alertDialog.setView(dialogBinding.root)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.saveBtn.setOnClickListener {
            val newName = dialogBinding.folderNameEditText.text.toString()
            if (newName.isNotEmpty()) {
                val existingFolder = folderDao.getFolderByName(newName)
                if (existingFolder == null || existingFolder.id == folder.id) {
                    alertDialog.dismiss()
                    Toast.makeText(view.context, "문제집 이름이 수정되었습니다.", Toast.LENGTH_LONG).show()

                    // 폴더 이름 업데이트
                    updateFolderName(folder, newName)
                } else {
                    // 이미 존재하는 폴더 이름인 경우
                    Toast.makeText(view.context, "이미 존재하는 문제집입니다.", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(view.context, "문제집 이름을 입력하세요!", Toast.LENGTH_LONG).show()
            }
        }
        alertDialog.show()
    }

    private fun updateFolderName(folder: Folder, newName: String) {
        val oldName = folder.folderName
        folder.folderName = newName
        folderDao.updateFolder(folder)

        // 퀴즈 테이블에서도 폴더 이름 업데이트
        if (oldName != null) {
            folderDao.updateQuizFolderName(oldName, newName)
        }

        notifyDataSetChanged()
    }

    private fun showDeleteConfirmationDialog(context: Context, folder: Folder) {
        val dialogBinding = DialogConfirmBinding.inflate(LayoutInflater.from(context))
        val dialogBuilder = AlertDialog.Builder(context)
        val alertDialog = dialogBuilder.create()

        dialogBinding.confirmTextView.text = "삭제하시겠습니까?"

        alertDialog.setView(dialogBinding.root)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.noButton.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.yesButton.setOnClickListener {
            deleteFolder(folder)
            alertDialog.dismiss()
            Toast.makeText(context,"문제집이 삭제되었습니다.",Toast.LENGTH_SHORT).show()
        }

        alertDialog.show()
    }

    private fun deleteFolder(folder: Folder) {
        folderDao.deleteQuizzesInFolder(folder.folderName.toString())
        folderDao.deleteFolder(folder)
        folderList.remove(folder)
        notifyDataSetChanged() // 삭제된 항목을 즉시 반영하여 화면 갱신
    }

    class Holder(val binding: ItemFolderBinding): RecyclerView.ViewHolder(binding.root){

    }
}