package com.dell.notescompaniondevice.room.dao
import androidx.room.*
import com.dell.notescompaniondevice.room.entity.Note

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

   /* @Query("UPDATE user set title = :title , desscription = :desc where uid = :userid")
    fun update(userid: Int, title: String, desc: String)*/

}