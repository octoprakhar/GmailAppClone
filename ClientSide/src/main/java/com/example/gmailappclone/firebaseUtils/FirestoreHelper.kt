package com.example.gmailappclone.firebaseUtils

import android.util.Log
import com.example.gmailappclone.dataclasses.UserDetail
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Locale

class FirestoreHelper {

    private val TAG = "FirestoreHelper"
    private val _db = Firebase.firestore

    fun isUserNamePresent(userName: String): Flow<Boolean> = callbackFlow {
        val query = _db.collection("users")
            .whereEqualTo("userNameNew", userName.lowercase(Locale.ROOT))
            .limit(1)

        val listner = query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.e(TAG, "Firestore listen failed.", firebaseFirestoreException)
                return@addSnapshotListener
            }
            val isPresent = querySnapshot != null && !querySnapshot.isEmpty
            Log.d(TAG, "isUserNamePresent: $isPresent")
            trySend(isPresent)
        }
        awaitClose { listner.remove() }

    }

    fun getAllNames(): Flow<List<String>> = callbackFlow {
        val listener = _db.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e(TAG, "Firestore listen failed.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val names = querySnapshot.documents.mapNotNull { it.getString("userNameNew") }
                    trySend(names)
                    Log.d(TAG, "Updated Names: $names")
                } else {
                    Log.d(TAG, "No names found")
                    trySend(emptyList())  // Send an empty list if no documents found
                }
            }

        awaitClose {
            listener.remove()  // Stop listening when the flow is closed
        }
    }


    fun insertUser(userDetail: UserDetail, onSuccess: () -> Unit, onFailure: (Int,String) -> Unit) {
        val userNameQuery = _db.collection("users")
            .whereEqualTo("userNameNew", userDetail.userName.lowercase(Locale.ROOT))

        val tokenQuery = _db.collection("users")
            .whereEqualTo("remoteToken", userDetail.remoteToken)

        // First, check if a user with the same username exists
        userNameQuery.get()
            .addOnSuccessListener { userNameSnapshot ->
                if (!userNameSnapshot.isEmpty) {
                    onFailure(0,"User with the same username already exists")  // Username already exists
                    Log.w(TAG, "User with the same username already exists")
                    return@addOnSuccessListener
                }

                // Then, check if a user with the same token exists
                tokenQuery.get()
                    .addOnSuccessListener { tokenSnapshot ->
                        if (!tokenSnapshot.isEmpty) {
                            onFailure(1,"This device is already exists.")  // Token already exists
                            Log.w(TAG, "This device is already exists.")
                            return@addOnSuccessListener
                        }

                        // If both checks pass, add the new user
                        val user = hashMapOf(
                            "userNameNew" to userDetail.userName.lowercase(Locale.ROOT),
                            "email" to userDetail.email,
                            "password" to userDetail.password,
                            "number" to userDetail.number,
                            "remoteToken" to userDetail.remoteToken
                        )

                        _db.collection("users").add(user)
                            .addOnSuccessListener { documentReference ->
                                onSuccess()
                                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                onFailure(2,"Error adding document")
                                Log.w(TAG, "Error adding document", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        onFailure(3,"Error checking the device")
                        Log.w(TAG, "Error checking token", e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(4,"Error checking username")
                Log.w(TAG, "Error checking username", e)
            }
    }

    fun getTokenByUsername(userName: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val query = _db.collection("users")
            .whereEqualTo("userNameNew", userName.lowercase(Locale.ROOT))
            .limit(1)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty){
                    val remoteToken = querySnapshot.documents[0].getString("remoteToken")
                    if (remoteToken != null){
                        onSuccess(remoteToken)
                    }else{
                        onFailure("Remote token not found for user")
                    }
                }else{
                    onFailure("No user found with username : $userName")

                    }
                }
            .addOnFailureListener { e ->
                onFailure("Error fetching remote token for username : $userName")
            }

    }

    fun updateTokenByUsername(
        userName: String,
        newToken: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val query = _db.collection("users")
            .whereEqualTo("userNameNew", userName.lowercase(Locale.ROOT))
            .limit(1)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentId = querySnapshot.documents[0].id
                    val documentRef = _db.collection("users").document(documentId)

                    // Update the remoteToken field
                    documentRef.update("remoteToken", newToken)
                        .addOnSuccessListener {
                            onSuccess() // Token updated successfully
                        }
                        .addOnFailureListener { e ->
                            onFailure("Failed to update remoteToken: ${e.message}")
                        }
                } else {
                    onFailure("No user found with username: $userName")
                }
            }
            .addOnFailureListener { e ->
                onFailure("Error fetching user by username: ${e.message}")
            }
    }


    fun deleteExistingDevice(remoteToken: String, onSuccess: () -> Unit, onFailure: (String) -> Unit, onEmailAndPass: (String,String) -> Unit){
        val usersCollection = FirebaseFirestore.getInstance().collection("users")
        val query = _db.collection("users")
            .whereEqualTo("remoteToken", remoteToken)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty){
                    val document = querySnapshot.documents[0]
                    val email = document.getString("email") ?: "No email found"
                    val password = document.getString("password") ?: "No password found"
                    onEmailAndPass(email,password)
                    usersCollection.document(document.id).delete()
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure("Error deleting document: $e")

                        }
                }else{
                    onFailure("No document found with remoteToken : $remoteToken")
                }
            }
            .addOnFailureListener { e ->
                onFailure("Error fetching users: ${e.message}")
            }


    }


}