package com.example.gmailappclone

import android.Manifest
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gmailappclone.dataclasses.PersonalDetails
import com.example.gmailappclone.singletons.MailItemGraph
import com.example.gmailappclone.viewmodels.LoginScreenViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

@Composable
fun LoginOrRegisterScreen(
    viewModel: LoginScreenViewModel,
    onRegisterSuccess: () -> Unit,
    onLoginSuccess: () -> Unit,
    onRegiterFailure: () -> Unit,
    onLoginFailure: () -> Unit,
    context: Context,
) {

    val TAG = "LoginOrRegisterScreen"
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    var signInUserName by remember { mutableStateOf("") }
    var signInPassword by remember { mutableStateOf("") }

    val isLogin = viewModel.isLogin
    val userDetailState by viewModel.userDetailState.observeAsState(null)

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var remoteToken by remember { mutableStateOf<String?>(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Ensures content is at the top and snackbar at the bottom
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isLogin) "Login" else "Register")
            Spacer(modifier = Modifier.height(16.dp))
            Switch(checked = isLogin, onCheckedChange = { viewModel.onToggleLogin() })
            Spacer(modifier = Modifier.height(16.dp))

            if (isLogin) {
                OutlinedTextField(value = signInUserName, onValueChange = { signInUserName = it }, placeholder = { Text("Enter your username") })
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = signInPassword, onValueChange = { signInPassword = it }, placeholder = { Text("Enter your password") })
                Spacer(modifier = Modifier.height(16.dp))

            } else {
                OutlinedTextField(value = userName, onValueChange = { userName = it }, placeholder = { Text("Enter your username") })
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, placeholder = { Text("Enter your email") })
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, placeholder = { Text("Enter your password") })
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = number, onValueChange = { number = it }, placeholder = { Text("Enter your number") })
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(onClick = {

                if (isLogin) {
                    viewModel.onLoginButtonClick(
                        userName = signInUserName,
                        password = signInPassword,
                        onSuccess = { onLoginSuccess() },
                        onFailure = { onLoginFailure() }
                    )
                } else {
                    // Register logic
                    viewModel.insertPersonalDetail(details = PersonalDetails(
                        userName = userName.lowercase(Locale.ROOT),
                        emailId = email,
                        password = password,
                        number = number
                    ))
                    if (number.length == 10 && number.all { it.isDigit() }){
                        viewModel.onFromChange(from = userName.lowercase(Locale.ROOT))
                        viewModel.onEmailChange(email)
                        viewModel.onPasswordChange(password)
                        viewModel.onUserDetailChange(userName)
                        viewModel.onNumberChange(number)
                        scope.launch {
                            remoteToken = FirebaseMessaging.getInstance().token.await()
                            if (remoteToken != null) {
                                viewModel.onRemoteTokenChange(remoteToken!!)
                                Log.d(TAG, "User Detail State is : $userDetailState")
                                viewModel.onRegisterButtonClick(
                                    onSuccess = { onRegisterSuccess() },
                                    onFailure = {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                        onRegiterFailure()
                                    },
                                    onFailureFirestore = { message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    },
                                    onSuccessfullyDeletingAfterFirestoreFailure = { code, failure ->
                                        if (code == 1) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Deleting the existing user?",
                                                    actionLabel = "Delete it"
                                                )

                                                delay(5000)
                                                snackbarHostState.currentSnackbarData?.dismiss()
                                            }
                                        }
                                        Toast.makeText(context, failure, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }else{
                        Toast.makeText(context,"Invalid number",Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(text = if (isLogin) "Login" else "Register")
            }
        }

        // This snackbar host is placed at the bottom using Arrangement.Bottom in Column
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp) // Padding from the bottom
        ) { snackbarData ->
            Snackbar(
                action = {
                    snackbarData.visuals.actionLabel?.let { actionLabel ->
                        Button(onClick = {
                            // Retry logic can go here
                            if (remoteToken != null){

                                scope.launch {
                                    MailItemGraph.allMailsRepository.deleteAllMails()
                                }
                                viewModel.deleteExistingUser(
                                    remoteToken = remoteToken!!,
                                    onSuccess = {
                                        Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
                                        snackbarData.dismiss()
                                    }
                                )
                            }
                        }) {
                            Text(actionLabel)
                        }
                    }
                }
            ) {
                Text(snackbarData.visuals.message)
            }
        }
    }

}
