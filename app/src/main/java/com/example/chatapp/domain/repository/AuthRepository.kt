package com.example.chatapp.domain.repository

import android.net.Uri
import android.util.Log
import com.example.chatapp.domain.data.User
import com.example.chatapp.utils.Prefs
import com.example.chatapp.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val prefs: Prefs
) {


    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Log.d("MyLog - AuthRepo", "Sign-in successful: ${result.user?.uid}")
            val user = userRepository.getCurrentUser(result.user!!.uid).getOrNull()
                ?: return Result.failure(Exception("User not found"))
            FirebaseMessaging.getInstance().token.addOnSuccessListener{ token ->
                val uid = user.uid
                val ref = FirebaseFirestore.getInstance().collection("users").document(uid)
                ref.set(mapOf("fcmTokens" to FieldValue.arrayUnion(token)), SetOptions.merge())
            }
            prefs.saveLastUid(user.uid)
            prefs.setRememberLogin(true)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        fullName: String
    ): Result<User> {
        return try {
            var result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Failed to create user")
            try {
                firebaseUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                ).await()
            } catch (e: Exception) {
                Log.e("MyLog - AuthRepo", "Failed to update display name", e)
            }
            val user = User(
                uid = result.user!!.uid,
                email = result.user!!.email,
                displayName = fullName,
                photoUrl = result.user!!.photoUrl?.toString(),
                phoneNumber = result.user!!.phoneNumber,
                dateOfBirth = DateUtils.getCurrentDateFormatted(),
                isEmailVerified = result.user!!.isEmailVerified
            )
            val savedUser = userRepository.createUser(user).getOrThrow()
            prefs.saveLastUid(savedUser.uid)
            prefs.setRememberLogin(true)
            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun loadUserByUid(uid: String): Result<User> {
        return userRepository.getCurrentUser(uid)
    }

    fun signOut() {
        val uidBefore = firebaseAuth.currentUser?.uid
        FirebaseMessaging.getInstance().token.addOnSuccessListener{ token ->
            val uid = uidBefore ?: return@addOnSuccessListener
            val ref = FirebaseFirestore.getInstance().collection("users").document(uid)
            ref.set(mapOf("fcmTokens" to FieldValue.arrayRemove(token)), SetOptions.merge())
        }
        prefs.clear()
        firebaseAuth.signOut()
    }

    suspend fun updateUserProfile(updateCurrentUser: User): Result<User> {
        return try {
            val profileUpdates = userProfileChangeRequest {
                displayName = updateCurrentUser.displayName
            }
            val user = firebaseAuth.currentUser?: return Result.failure(IllegalStateException("No signed-in user"))
            userRepository.updateUserProfile(updateCurrentUser).getOrThrow()
            user.updateProfile(profileUpdates).await()
            Result.success(updateCurrentUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFirebaseAuthAvatar(userId: String, url: String): Result<Unit> {
        return try {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(url.toUri())
                .build()

            val currentUser = FirebaseAuth.getInstance().currentUser
                ?: return Result.failure(Exception("User not found"))

            if (currentUser.uid != userId) {
                return Result.failure(Exception("Cannot update avatar for different user"))
            }

            currentUser.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
