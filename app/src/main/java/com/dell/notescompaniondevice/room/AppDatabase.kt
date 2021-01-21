package com.dell.notescompaniondevice.room
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dell.notescompaniondevice.room.dao.NotesDao
import com.dell.notescompaniondevice.room.entity.Note
import com.dell.notescompaniondevice.room.utils.Converters




@Database(entities = [Note::class],version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun getNotesDao(): NotesDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_lists"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}