package com.example.customquizapp

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz")
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "folderName")
    val folderName: String,
    var question: String,
    var answer: String,
    var imageUri: String? = null,
    var isCorrect: Boolean = false
)