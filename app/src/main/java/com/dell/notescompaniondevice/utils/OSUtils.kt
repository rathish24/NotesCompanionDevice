package com.dell.notescompaniondevice.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileWriter


object OSUtils {
    private lateinit var fileName: String
    private var path: String = ""
    private var dirCreated: Boolean = false

    fun createFolderLocally(): String {
        val dir = File(Environment.getExternalStorageDirectory(), "NCD")
        if (!dir.exists()) {
            dirCreated = dir.mkdir()
        }else{
            path = dir.absolutePath
        }
        if (dirCreated) path = dir.absolutePath
        return path
    }

    fun createTextFile(file: String): String {
        try {
            val file = File(file, "Note.txt")
            val writer = FileWriter(file)
            writer.append("This is sample text file.")
            writer.flush()
            writer.close()
        } catch (e: java.lang.Exception) {
        }
        return file+File.separator+"Note.txt"
    }

    fun createFolder(context: Context): String {
        val files_folder: File = context.filesDir
        val files_child = File(files_folder, "notes")
        files_child.mkdirs()
        return files_child.name
    }

    fun getFileFromFilesDir(context: Context): String {
        val path: String = context.filesDir.absolutePath
        val directory = File(path)
        val files = directory.listFiles()
        for (i in files.indices) {
            fileName = files[i].name
        }
        return "$path/$fileName"
    }
}
