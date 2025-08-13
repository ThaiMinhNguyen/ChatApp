package com.example.chatapp.domain.repository

import android.util.Log
import com.example.chatapp.domain.data.User
import com.example.chatapp.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository
) {


    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Log.d("MyLog - AuthRepo", "Sign-in successful: ${result.user?.uid}")
            val user = userRepository.getCurrentUser(result.user!!.uid).getOrNull()
                ?: return Result.failure(Exception("User not found"))
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
            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun updateUserProfile(updateCurrentUser: User): Result<User> {
        return try {
            val profileUpdates = userProfileChangeRequest {
                displayName = updateCurrentUser.displayName
                //TODO: Implement photo later
            }
            val user = firebaseAuth.currentUser?: return Result.failure(IllegalStateException("No signed-in user"))
            userRepository.updateUserProfile(updateCurrentUser).getOrThrow()
            user.updateProfile(profileUpdates).await()
            Result.success(updateCurrentUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
