package com.example.gmailappclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gmailappclone.broadcast.ConnectivityMonitor
import com.example.gmailappclone.firebaseUtils.FirebaseAuthenticationHelper
import com.example.gmailappclone.firebaseUtils.FirebaseStorageHelper
import com.example.gmailappclone.firebaseUtils.FirestoreHelper
import com.example.gmailappclone.services.UploadService
import com.example.gmailappclone.singletons.MailItemGraph
import com.example.gmailappclone.ui.theme.GmailAppCloneTheme
import com.example.gmailappclone.viewmodels.ComposeViewModel
import com.example.gmailappclone.viewmodels.DrawerViewModel
import com.example.gmailappclone.viewmodels.LoginScreenViewModel
import com.example.gmailappclone.viewmodels.ProfileScreenViewModel
import com.example.gmailappclone.viewmodels.viewModelFactory
import com.google.firebase.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivityResponse"
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var imageUri = mutableListOf<Uri?>()
    private lateinit var connectivityMonitor : ConnectivityMonitor
    private var isConnected by mutableStateOf(true)

    val permissionsArray =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_MEDIA_IMAGES , // For images access in Android 13+
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC,
                Manifest.permission.ACCESS_NETWORK_STATE
            )
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_MEDIA_IMAGES , // For images access in Android 13+
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.ACCESS_NETWORK_STATE
            )
        }else{
            arrayOf(Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestNotificationPermissions()
        super.onCreate(savedInstanceState)



        //Initialize connectivity monitor
        connectivityMonitor = ConnectivityMonitor(this)

        connectivityMonitor.registerNetworkCallback(
            onConnected = {
                isConnected = true
            },
            onDisconnected = {
                isConnected = false
            }
        )

        val firebaseAuthenticationHelper = FirebaseAuthenticationHelper(this)
        val firestoreHelper = FirestoreHelper()
        val firebaseStorageHelper = FirebaseStorageHelper()

        val profileScreenViewModelFactory = viewModelFactory {
            ProfileScreenViewModel(MailItemGraph.allMailsRepository)
        }

        val profileScreenViewModel = ViewModelProvider(this , profileScreenViewModelFactory)[ProfileScreenViewModel::class.java]

        val loginScreenViewModelFactory = viewModelFactory {
            LoginScreenViewModel(firebaseAuthenticationHelper = firebaseAuthenticationHelper, firestoreHelper = firestoreHelper, personalDetailRepository = MailItemGraph.personalDetailRepository)
        }
        val loginScreenViewModel = ViewModelProvider(this , loginScreenViewModelFactory)[LoginScreenViewModel::class.java]

        val composeViewModelFactory = viewModelFactory {
            ComposeViewModel(firestoreHelper = firestoreHelper, firebaseStorageHelper = firebaseStorageHelper, personalDetailRepository = MailItemGraph.personalDetailRepository)
        }

        val composeViewModel = ViewModelProvider(this , composeViewModelFactory)[ComposeViewModel::class.java]

        val drawerViewModelFactory = viewModelFactory {
            DrawerViewModel(MailItemGraph.allMailsRepository)
        }
        val drawerViewModel = ViewModelProvider(this , drawerViewModelFactory)[DrawerViewModel::class.java]


        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if (result.resultCode == RESULT_OK){
                val data = result.data
                if (data != null && data.clipData != null){
                    val clippedData = data.clipData

                    for (i in 0 until clippedData!!.itemCount){
                        imageUri.add(clippedData.getItemAt(i).uri)
                        Log.d("MainActivityResponse", "Image URI: ${imageUri[i]}")
                    }
                    Log.d("MainActivityResponse", "Image URI: ${imageUri}")

                    composeViewModel.setImageUri(imageUri.toList().map { it?.toString() ?: "" })
                    Log.d(TAG,"set image uri in viewModel as ${composeViewModel.imageUri.value}")
                }else{
                    Log.d("MainActivityResponse", "No data or data.data is null")
                    Log.d("MainActivityResponse", "Resolved Activity: ${data?.resolveActivity(packageManager)}")

                }


                Log.d("MainActivityResponse", "Image URI: ${imageUri}")

            }
        }

        enableEdgeToEdge()
        setContent {
            GmailAppCloneTheme {


                AppNavigation(
                    context = this,
                    profileScreenViewModel = profileScreenViewModel,
                    loginScreenViewModel = loginScreenViewModel,
                    composeViewModel = composeViewModel,
                    drawerViewModel = drawerViewModel,
                    onPickingFile = {
                        val intent = Intent(Intent.ACTION_PICK).apply {
                            type = "*/*"
                            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                            addCategory(Intent.CATEGORY_OPENABLE)

                        }
                        if (intent.resolveActivity(packageManager) != null){
                            imagePickerLauncher.launch(intent)
                        }else{
                            Log.d("MainActivityResponse", "No activity found to handle intent")
                            Log.d("MainActivityResponse", "Resolved Activity: ${intent.resolveActivity(packageManager)}")

                        }

                    },
                    onUploadServiceStart = {fileLinks->
//                        Log.d(TAG,"Got fileLink: $fileLink and have imageLink : ${imageUri}")
                        val uploadIntent = Intent(this@MainActivity, UploadService::class.java)
                        uploadIntent.putStringArrayListExtra("fileLink", ArrayList(fileLinks))
                        uploadIntent.putExtra("fileName","uploadedImage")

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            startForegroundService(uploadIntent)

                        }else{
                            startService(uploadIntent)
                        }
                    },
                    checkInternetConnection = {isConnected},
                    onUserDeleted = {
                        Log.d(TAG, "On user delete trigger")
                        var emailId by mutableStateOf<String?>(null)
                        var password by mutableStateOf<String?>(null)

                        Toast.makeText(this, "User deleted due to some backend work. Please contact our team regarding your data.", Toast.LENGTH_LONG).show()

                        CoroutineScope(Dispatchers.IO).launch {
                            // Step 1: Delete all mails
                            MailItemGraph.allMailsRepository.deleteAllMails()
                            Log.d(TAG, "All mails deleted successfully")


                            emailId = MailItemGraph.personalDetailRepository.getEmail()
                            password = MailItemGraph.personalDetailRepository.getPassword()

//
                            Log.d(TAG, "Email id: $emailId")
                            Log.d(TAG, "Password: $password")

                            // Step 3: Check if email and password are available
                            if (emailId != null && password != null) {
                                // Step 4: Remove user using FirebaseAuthHelper
                                val result = firebaseAuthenticationHelper.removeUserByEmail(emailId!!, password!!)

                                if (result.isSuccess) {
                                    Log.d(TAG, "User deleted successfully after being removed from Firestore.")
                                } else {
                                    Log.d(TAG, "Failed to delete user when removed from Firestore: ${result.exceptionOrNull()?.message}")
                                }
                            } else {
                                Log.d(TAG, "Email or password is null. Cannot proceed with user deletion.")
                            }

                            // Step 5: Delete all personal details
                            MailItemGraph.personalDetailRepository.deleteAll()
                            Log.d(TAG, "All data deleted successfully")
                        }
                    }
                )

            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Unregister the network callback when the activity is destroyed
        connectivityMonitor.unregisterNetworkCallback()
    }

    private fun requestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val permissions = arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_MEDIA_IMAGES , // For images access in Android 13+
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC,
                Manifest.permission.ACCESS_NETWORK_STATE
            )

            val hasPermission = permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this, permissions, 0)
            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            val permissions = arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_MEDIA_IMAGES , // For images access in Android 13+
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.ACCESS_NETWORK_STATE
            )

            val hasPermission = permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this, permissions, 0)
            }
        } else {
            // For Android versions below TIRAMISU (Android 13)
            val permissions = arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE
            )

            val hasPermission = permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this, permissions, 0)
            }
        }
    }

}

