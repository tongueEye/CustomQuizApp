package hell.example.customquizapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folder")
data class Folder (

    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "folderName")
    var folderName: String?,

)