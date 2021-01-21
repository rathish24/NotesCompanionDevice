package com.dell.notescompaniondevice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dell.notescompaniondevice.R
import com.dell.notescompaniondevice.googledriveintegration.DriveServiceHelper
import com.dell.notescompaniondevice.utils.OSUtils
import com.dell.notescompaniondevice.utils.PreferenceUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.gson.Gson
import java.io.File

import java.util.*


class MainActivity : AppCompatActivity() {
    private val WRITE_PERMISSIONS = 4519
    private var folder_id: String? = null
    private lateinit var mDriveServiceHelper: DriveServiceHelper
    private val FILEPICKER_RESULT_CODE = 45199
    private val RC_SIGN_IN = 1000
    private val TAG: String = "NCD"
    private val tvItemPath: TextView? = null
    private var fileUri: Uri? = null
    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_folder_file).setOnClickListener {
            val permissions =
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                    permissions,
                    WRITE_PERMISSIONS)
            }else{
                val folderPath = OSUtils.createFolderLocally()
                Log.i(TAG, "onCreate: $folderPath")
                filePath = OSUtils.createTextFile(folderPath)
                Log.i(TAG, "onCreate: $filePath")
            }

        }

        findViewById<Button>(R.id.btn_upload).setOnClickListener {
            uploadFileToGoogleDrive()
        }

        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener {
            checkForGooglePermissions()
        }

//        findViewById<Button>(R.id.btn_upload).setOnClickListener {
//            var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
//            chooseFile.type = "*/*"
//            chooseFile = Intent.createChooser(chooseFile, "Choose a file")
//            startActivityForResult(chooseFile, FILEPICKER_RESULT_CODE)
//        }
    }

    fun googleSignin() {
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {
            signIn()
        } else {
            val credential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
                this,
                Collections.singleton(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = account.account
            var googleDriveService = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credential
            )
                .setApplicationName("AppName")
                .build()
            mDriveServiceHelper = DriveServiceHelper(googleDriveService)
            createFolderInDrive(mDriveServiceHelper)
        }

    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private var ACCESS_DRIVE_SCOPE: Scope = Scope(Scopes.DRIVE_FILE)
    private var SCOPE_EMAIL: Scope = Scope(Scopes.EMAIL)
    private val RC_AUTHORIZE_DRIVE = 4519
    private fun checkForGooglePermissions() {
        if (!GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(applicationContext),
                ACCESS_DRIVE_SCOPE,
                SCOPE_EMAIL
            )
        ) {
            GoogleSignIn.requestPermissions(
                this@MainActivity,
                RC_AUTHORIZE_DRIVE,
                GoogleSignIn.getLastSignedInAccount(applicationContext),
                ACCESS_DRIVE_SCOPE,
                SCOPE_EMAIL
            )
        } else {
            Toast.makeText(
                this,
                "Permission to access Drive and Email has been granted",
                Toast.LENGTH_SHORT
            ).show()
            googleDriveSetUp()
        }
    }

    private fun googleDriveSetUp() {
        val mAccount = GoogleSignIn.getLastSignedInAccount(this@MainActivity)
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
        mDriveServiceHelper =
            DriveServiceHelper(
                googleDriveService
            )
        createFolderInDrive(mDriveServiceHelper)
    }

    private fun createFolderInDrive(mDriveServiceHelper: DriveServiceHelper) {
        Log.i(TAG, "Creating a Folder...")
        mDriveServiceHelper.createFolder("Notes Companion Device", null)
            .addOnSuccessListener {
                val gson = Gson()
                Toast.makeText(this, gson.toJson(it), Toast.LENGTH_LONG).show()
                folder_id = gson.toJson(it.id)
                Log.i(TAG, "Folder created: $folder_id")
                PreferenceUtils.writeToPrefs(this,"folder_id",folder_id)
            }
            .addOnFailureListener {
                Log.i(TAG, "onFailure of Folder creation: " + it.cause)
                Toast.makeText(this, "error" + it.cause, Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadFileToGoogleDrive(){
        val _id = PreferenceUtils.getPrefs(this,"folder_id", "")
        Log.i(TAG, "Folder id: $_id")
        mDriveServiceHelper.uploadFile(
            File(filePath),
            "application/vnd.google-apps.file",
            "1lWtWGTCAvNXoHCD1GaKkv9uYTJ7ABO7t"
        ).addOnSuccessListener {
            val gson = Gson()
            Toast.makeText(this, gson.toJson(it), Toast.LENGTH_LONG).show()
            val file_id = gson.toJson(it.id)
            Log.i(TAG, "file created: $file_id")

        }.addOnFailureListener {
            Log.i(TAG, "file creation failed" + it.stackTrace.toString())
            Toast.makeText(this, "error" + it.cause, Toast.LENGTH_LONG).show()
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            Log.i("Login data", "onActivityResult: ${data.toString()}")
            Toast.makeText(this, data.toString(), Toast.LENGTH_LONG).show()
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILEPICKER_RESULT_CODE) {
            /* fileUri = data?.data
             filePath = fileUri?.path
             Log.i(TAG, "onActivityResult: $filePath")*/

            mDriveServiceHelper.uploadFile(
                File(filePath),
                "application/vnd.google-apps.file",
                "1ABuMPJBYCyMnWUHg1_CFd7K0AzlzGIc2"
            ).addOnSuccessListener {
                val gson = Gson()
                Toast.makeText(this, gson.toJson(it), Toast.LENGTH_LONG).show()
                folder_id = gson.toJson(it.id)
                Log.i(TAG, "onSuccess of file creation: $folder_id")

            }.addOnFailureListener {
                Log.i(TAG, "onFailure of file creation: " + it.stackTrace)
                Toast.makeText(this, "error" + it.cause, Toast.LENGTH_LONG).show()
            }
        }
        else if (requestCode == WRITE_PERMISSIONS){
            val folderPath = OSUtils.createFolderLocally()
            Log.i(TAG, "onCreate: $folderPath")
            filePath = OSUtils.createTextFile(folderPath)
            Log.i(TAG, "onCreate: $filePath")
        }
    }

}