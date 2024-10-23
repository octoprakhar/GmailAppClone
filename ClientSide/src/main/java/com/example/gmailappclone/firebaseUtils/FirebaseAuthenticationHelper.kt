package com.example.gmailappclone.firebaseUtils

import android.app.Activity
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class FirebaseAuthenticationHelper(
    val activity: Activity
) {
    private val TAG = "FirebaseAuthenticationHelper"
    private val auth : FirebaseAuth = Firebase.auth

    fun isAlreadySignedIn() : Boolean{
        if (auth.currentUser != null){
            return true
        }else{
            return false
        }
    }

    fun userSignOut(){
        auth.signOut()
    }

    fun RegisterUser(email : String, password : String, onSuccess : () -> Unit, onErrror : (String) -> Unit){
        val currentUser = auth.currentUser
        if (currentUser != null){
            Log.d(TAG,"User is already signed in")
        }else{
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(activity) {task->
                    if (task.isSuccessful){

                        onSuccess()
                        Log.d(TAG,"User registered successfully")
                    }else{
                        Log.d(TAG,"User registration failed with exception ${task.exception}")
                        onErrror("${task.exception}")

                    }


                }
        }
    }

    fun signInUser(email : String, password : String, onSuccess : () -> Unit, onError : () -> Unit){
        val currentUser = auth.currentUser
        if (currentUser != null){
            Log.d(TAG,"User is already signed in")
        }else{
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(activity) {task->
                    if (task.isSuccessful){
                        onSuccess()
                        Log.d(TAG,"User signed in successfully")
                    }else{
                        onError()
                        Log.d(TAG,"User sign in failed")
                    }

                }
        }


    }

//    suspend fun removeUserByEmail(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
//        // Step 1: Sign in the user with the email and password
//        auth.signInWithEmailAndPassword(email, password).await()
//
////            val currentUser = auth.currentUser
////            .addOnCompleteListener(activity) { signInTask ->
////                if (signInTask.isSuccessful) {
//                    // Step 2: Get the current user after signing in
//                    val currentUser = auth.currentUser
//                    currentUser?.let { user ->
//                        // Step 3: Delete the user
//                        user.delete().await()
////                            .addOnCompleteListener { deleteTask ->
////                                if (deleteTask.isSuccessful) {
//                        Log.d(TAG, "User account deleted successfully")
//                        onSuccess()
//
////                                } else {
////                                    onError("Failed to delete user: ${deleteTask.exception}")
////                                    Log.w(TAG, "Failed to delete user: ${deleteTask.exception}")
////                                }
//                            } ?: run {
//                        onError("No authenticated user found")
//                        Log.w(TAG, "No authenticated user found")
//                    }
////                } else {
////                    onError("Sign in failed: ${signInTask.exception}")
////                    Log.w(TAG, "Sign in failed: ${signInTask.exception}")
////                }
//            }

    suspend fun removeUserByEmail(email: String, password: String): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            // Step 1: Sign in the user with the email and password
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { signInTask ->
                    if (signInTask.isSuccessful) {
                        // Step 2: Get the current user after signing in
                        val currentUser = auth.currentUser
                        currentUser?.let { user ->
                            // Step 3: Delete the user
                            user.delete().addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    Log.d(TAG, "User account deleted successfully")
                                    continuation.resume(Result.success(Unit)) // Resume the coroutine
                                } else {
                                    Log.w(TAG, "Failed to delete user: ${deleteTask.exception}")
                                    continuation.resume(Result.failure(deleteTask.exception ?: Exception("Unknown error"))) // Resume with failure
                                }
                            }
                        } ?: run {
                            Log.w(TAG, "No authenticated user found")
                            continuation.resume(Result.failure(Exception("No authenticated user found"))) // Resume with failure
                        }
                    } else {
                        Log.w(TAG, "Sign in failed: ${signInTask.exception}")
                        continuation.resume(Result.failure(signInTask.exception ?: Exception("Unknown error"))) // Resume with failure
                    }
                }
        }
    }


}






