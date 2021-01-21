package com.dell.notescompaniondevice.room.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val desc: String,
    @ColumnInfo(name = "date_created") val dateCreated: Date,
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
)
