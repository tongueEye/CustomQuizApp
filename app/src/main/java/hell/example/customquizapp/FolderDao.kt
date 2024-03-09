package hell.example.customquizapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FolderDao {
    @Query("SELECT * FROM folder")
    fun getAllFolder(): List<Folder>

    @Insert
    fun insertFolder(folder: Folder)

    @Update
    fun updateFolder(folder: Folder)

    @Delete
    fun deleteFolder(folder: Folder)

    @Query("SELECT * FROM folder WHERE folderName = :name LIMIT 1")
    fun getFolderByName(name: String): Folder?

    @Query("UPDATE quiz SET folderName = :newName WHERE folderName = :oldName")
    fun updateQuizFolderName(oldName: String, newName: String)

    @Query("DELETE FROM quiz WHERE folderName = :folderName")
    fun deleteQuizzesInFolder(folderName: String)
}