package com.example.gmailappclone.viewmodels

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gmailappclone.dataclasses.MessageState
import com.example.gmailappclone.dataclasses.PersonalDetails
import com.example.gmailappclone.dataclasses.UserDetail
import com.example.gmailappclone.firebaseUtils.FirebaseAuthenticationHelper
import com.example.gmailappclone.firebaseUtils.FirestoreHelper
import com.example.gmailappclone.repositories.PersonalDetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    val firebaseAuthenticationHelper: FirebaseAuthenticationHelper,
    val firestoreHelper: FirestoreHelper,
    val personalDetailRepository: PersonalDetailRepository
) : ViewModel() {

    private val TAG = "LoginScreenViewModel"

    private val _userDetailState = MutableLiveData<UserDetail>(UserDetail("", "", "", "", ""))
    val userDetailState : LiveData<UserDetail> get() = _userDetailState
    var messageState by mutableStateOf(MessageState())
        private set

    fun onFromChange(from: String) {
        messageState = messageState.copy(from = from)
        Log.d(TAG,"From changed to ${messageState.from}")
    }

    fun insertPersonalDetail(details: PersonalDetails){
        viewModelScope.launch (Dispatchers.IO){
            personalDetailRepository.insertPersonalDetail(details)
        }
    }


    fun isAlreadySignedIn() : Boolean{
        return firebaseAuthenticationHelper.isAlreadySignedIn()
    }

    fun signOut(){
        firebaseAuthenticationHelper.userSignOut()
    }

    var isLogin by mutableStateOf(false)
        private set

    fun onToggleLogin() {
        isLogin = !isLogin
    }

    fun onUserDetailChange(userName : String) {
        _userDetailState.value = _userDetailState.value?.copy(userName = userName)

    }

    fun onEmailChange(email : String) {
        _userDetailState.value = _userDetailState.value?.copy(email = email)
    }

    fun onPasswordChange(password : String) {
        _userDetailState.value = _userDetailState.value?.copy(password = password)
    }

    fun onNumberChange(number : String) {
        _userDetailState.value = _userDetailState.value?.copy(number = number)
    }

    fun onRemoteTokenChange(remoteToken : String) {
        _userDetailState.value = _userDetailState.value?.copy(remoteToken = remoteToken)
    }

    fun onLoginButtonClick(userName: String, password: String, onSuccess : () -> Unit, onFailure : () -> Unit) {
        firebaseAuthenticationHelper.signInUser(userName,password,onSuccess,onFailure)


    }
    fun onRegisterButtonClick(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        onFailureFirestore: (String) -> Unit,
        onSuccessfullyDeletingAfterFirestoreFailure: (Int, String) -> Unit
    ) {
        val email = _userDetailState.value?.email
        val password = _userDetailState.value?.password

        if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
            firebaseAuthenticationHelper.RegisterUser(
                email,
                password,
                onSuccess = {
                    firestoreHelper.insertUser(
                        _userDetailState.value!!,
                        onSuccess = { onSuccess() },
                        onFailure = { code, firestoreFailure ->
                            onFailureFirestore(firestoreFailure)
                            viewModelScope.launch(Dispatchers.IO) {
                                val result = firebaseAuthenticationHelper.removeUserByEmail(email, password)
                                if (result.isSuccess) {
                                    Log.d(TAG, "User removed successfully after failure in Firestore")
                                    onSuccessfullyDeletingAfterFirestoreFailure(code, firestoreFailure)
                                } else {
                                    Log.d(TAG, "Error removing user: ${result.exceptionOrNull()?.message}")
                                }
                            }
                        }
                    )
                },
                onErrror = { onFailure(it) }
            )
        } else {
            Log.d(TAG, "Email or password is empty. Email: $email, Password: $password")
        }
    }

    fun deleteExistingUser(remoteToken: String, onSuccess: () -> Unit) {
        firestoreHelper.deleteExistingDevice(
            remoteToken,
            onSuccess = onSuccess,
            onFailure = {
                Log.d(TAG, "Error deleting existing user: $it")
            },
            onEmailAndPass = { email, pass ->
                viewModelScope.launch(Dispatchers.IO) {
                    val result = firebaseAuthenticationHelper.removeUserByEmail(email, pass)
                    if (result.isSuccess) {
                        Log.d(TAG, "User removed successfully after failure in Firestore")
                    } else {
                        Log.d(TAG, "Error removing user: ${result.exceptionOrNull()?.message}")
                    }
                }
            }
        )
    }
}