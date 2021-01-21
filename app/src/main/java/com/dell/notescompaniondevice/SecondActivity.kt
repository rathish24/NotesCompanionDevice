package com.dell.notescompaniondevice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dell.notescompaniondevice.cardview.DummyNotesAdapter
import com.dell.notescompaniondevice.cardview.Note
import com.dell.notescompaniondevice.googledriveintegration.DriveServiceHelper
import com.dell.notescompaniondevice.ui.newnote.NewNoteActivity
import com.dell.notescompaniondevice.utils.OSUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.gson.Gson


import java.io.File
import java.util.*


class SecondActivity : AppCompatActivity() {
    private lateinit var notesAdapter: DummyNotesAdapter
    private var notesList: ArrayList<Note>? = null
    private val TAG:String = "NCD"
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        val path :String = OSUtils.createFolderLocally()
        Log.i(TAG, "creteFolderLocally: $path")


     //   OSUtils.copyFile(this,OSUtils.createFolder(this))

        //start Notes dummy list view
        recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView

        notesList = ArrayList<Note>()

        notesAdapter = DummyNotesAdapter(this, notesList!!)

        val layoutManager = GridLayoutManager(applicationContext, 1)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = notesAdapter

        prepareDummyNotes()

        //end dummy list notes

//        findViewById<View>(R.id.google_drive).setOnClickListener {
//            getGoogleDrivePermissions()
//        }

        //create folder
        findViewById<View>(R.id.new_folder).setOnClickListener {

        }

        //upload folder
        findViewById<View>(R.id.drive_upload).setOnClickListener {
            // TODO:
        }
        //list view
        findViewById<View>(R.id.notes_list).setOnClickListener {
            findViewById<View>(R.id.dashboard_container).visibility = View.VISIBLE
        }
        //recently modified
        findViewById<View>(R.id.recently_modified).setOnClickListener {
            // TODO:
        }
        findViewById<View>(R.id.llNewNote).setOnClickListener {
            startActivity(Intent(this,NewNoteActivity::class.java))
        }
    }

    private var ACCESS_DRIVE_SCOPE: Scope = Scope(Scopes.DRIVE_FILE)
    private var SCOPE_EMAIL: Scope = Scope(Scopes.EMAIL)
    private val RC_AUTHORIZE_DRIVE = 4519
    private fun getGoogleDrivePermissions() {
        if (!GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(applicationContext),
                ACCESS_DRIVE_SCOPE,
                SCOPE_EMAIL
            )
        ) {
            GoogleSignIn.requestPermissions(
                this,
                RC_AUTHORIZE_DRIVE,
                GoogleSignIn.getLastSignedInAccount(applicationContext),
                ACCESS_DRIVE_SCOPE,
                SCOPE_EMAIL
            )
        } else {
            Toast.makeText(
                this,
                "Permission for Google Drive and Email has been granted",
                Toast.LENGTH_SHORT
            ).show()
            googleDriveSetUp()
        }
    }

    private fun googleDriveSetUp() {
        val mAccount = GoogleSignIn.getLastSignedInAccount(this)
        val credential = GoogleAccountCredential.usingOAuth2(
            applicationContext, Collections.singleton(Scopes.DRIVE_FILE)
        )
        credential.selectedAccount = mAccount!!.account
        var googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName("GoogleDriveIntegration 3")
            .build()
        var mDriveServiceHelper =
            DriveServiceHelper(
                googleDriveService
            )
        createFolderInDrive(mDriveServiceHelper)
    }

    private fun createFolderInDrive(mDriveServiceHelper: DriveServiceHelper) {
        Log.i("MS", "Creating a Folder...");
        mDriveServiceHelper.createFolder("Device",null)
            .addOnSuccessListener {
                val gson = Gson()
                Log.i("MS", "onSuccess of Folder creation: $gson")
                Toast.makeText(this,gson.toString(),Toast.LENGTH_LONG).show()
                mDriveServiceHelper.uploadFile(File(OSUtils.getFileFromFilesDir(this)),
                    "application/vnd.google-apps.file","1M6NzVaVb2gm2XN5WHnPPj5u_LHEngI23")
            }
            .addOnFailureListener {
                Log.i("MS", "onFailure of Folder creation: " + it.cause)
                Toast.makeText(this,"error"+it.cause,Toast.LENGTH_LONG).show()
            }
    }

    private fun prepareDummyNotes() {
        val thumbnails = intArrayOf(
            R.drawable.album1,
            R.drawable.album2,
            R.drawable.album3,
            R.drawable.album4,
            R.drawable.album5,
            R.drawable.album6,
            R.drawable.album7,
            R.drawable.album8,
            R.drawable.album9,
            R.drawable.album10
        )
        notesList?.add(Note("Test", "1 Jan 2021", thumbnails[0]))
        notesList?.add(Note("Test", "2 Jan 2021", thumbnails[1]))
        notesList?.add(Note("Test", "3 Jan 2021", thumbnails[2]))
        notesList?.add(Note("Test", "4 Jan 2021", thumbnails[3]))
        notesList?.add(Note("Test", "5 Jan 2021", thumbnails[4]))
        notesList?.add(Note("Test", "6 Jan 2021", thumbnails[5]))
        notesList?.add(Note("Test", "7 Jan 2021", thumbnails[6]))
        notesList?.add(Note("Test", "8 Jan 2021", thumbnails[7]))
        notesList?.add(Note("Test", "9 Jan 2021", thumbnails[8]))
        notesList?.add(Note("Test", "10 Jan 2021", thumbnails[9]))

    }
}
