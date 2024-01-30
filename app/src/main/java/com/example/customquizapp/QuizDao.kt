package com.example.customquizapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz WHERE folderName = :folderName")
    fun getAllQuizzes(folderName: String): List<Quiz>

    @Insert
    fun insertQuiz(quiz: Quiz)

    @Update
    fun updateQuiz(quiz: Quiz)

    @Delete
    fun deleteQuiz(quiz: Quiz)
}