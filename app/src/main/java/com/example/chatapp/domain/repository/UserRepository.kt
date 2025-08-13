package com.example.chatapp.domain.repository

import android.util.Log
import com.example.chatapp.domain.data.Friendship
import com.example.chatapp.domain.data.FriendshipStatus
import com.example.chatapp.domain.data.People
import com.example.chatapp.domain.data.User
import com.example.chatapp.utils.DateUtils
import com.example.chatapp.utils.UserUtils
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getCurrentUser(userId: String): Result<User> {
        Log.d("MyLog - UserRepo", "Start getCurrentUser | userId: $userId")

        return try {
            val documentSnapshot = firestore
                .collection("users")
                .document(userId)
                .get()
                .await()

            Log.d("MyLog - UserRepo", "Document fetch complete | exists: ${documentSnapshot.exists()}")

            if (!documentSnapshot.exists()) {
                Log.w("MyLog - UserRepo", "Document not found for userId: $userId")
                return Result.failure(Exception("User not found for ID: $userId"))
            }

            val user = documentSnapshot.toObject(User::class.java)
            Log.d("MyLog - UserRepo", "Document toObject result | user: $user")

            if (user == null) {
                Log.w("MyLog - UserRepo", "Failed to parse User, returning fallback User | userId: $userId")
                return Result.success(
                    User(
                        uid = userId,
                        email = null,
                        displayName = null,
                        photoUrl = null,
                        phoneNumber = null,
                        dateOfBirth = null,
                        isEmailVerified = false
                    )
                )
            }

            Log.d("MyLog - UserRepo", "User successfully parsed | displayName: ${user.displayName}")
            Result.success(user)

        } catch (exception: Exception) {
            Log.e("MyLog - UserRepo", "Error in getCurrentUser | userId: $userId | error: ${exception.message}", exception)
            Result.failure(exception)
        }
    }


    suspend fun createUser(user: User): Result<User> {
        return try {
            firestore.collection("users")
                .document(user.uid)
                .set(user)
                .await()
            Result.success(user)
        } catch (e: Exception) {
            Log.e("MyLog - UserRepo", "Error creating user: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun toggleFriendRequest(currentUser: User, sendToUser: User, status: FriendshipStatus) : Result<Friendship>{
        return try {
            val uid = UserUtils.generateId(currentUser.uid, sendToUser.uid)
            val friendRequest = Friendship(
                user1 = currentUser.uid,
                user2 = sendToUser.uid,
                status = status,
                createdAt = DateUtils.getCurrentTimestamp(),
                requestedBy = currentUser.uid

            )
            firestore.collection("friendships")
                .document(uid)
                .set(friendRequest)
                .await()
            return Result.success(friendRequest)
        } catch (e:Exception){
            Log.e("MyLog - UserRepo", "Error creating user: ${e.message}")
            Result.failure(e)
        }
    }

    fun listenPeopleFlow(currentUser: User): Flow<List<People>> = callbackFlow {
        var usersList: List<User> = emptyList()
        var friendshipsList: List<Friendship> = emptyList()

        fun emitPeople() {
            val peopleList = usersList
                .filter { it.uid != currentUser.uid }
                .map { otherUser ->
                    val friendship = friendshipsList.find {
                        (it.user1 == currentUser.uid && it.user2 == otherUser.uid) ||
                                (it.user1 == otherUser.uid && it.user2 == currentUser.uid)
                    }

                    val isFriend = friendship?.status == FriendshipStatus.ACCEPTED
                    val isRequestSent = friendship?.status == FriendshipStatus.PENDING && friendship.requestedBy == currentUser.uid
                    val isRequestReceived = friendship?.status == FriendshipStatus.PENDING && friendship.requestedBy != currentUser.uid

                    People(
                        user = otherUser,
                        isFriend = isFriend,
                        isRequestSent = isRequestSent,
                        isRequestReceived = isRequestReceived
                    )
                }
            trySend(peopleList).isSuccess
        }

        val usersReg = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog - UserRepo", "Error listening to users: ${error.message}")
                    return@addSnapshotListener
                }
                usersList = snapshot?.documents?.mapNotNull { it.toObject(User::class.java) } ?: emptyList()
                emitPeople()
            }

        val friendshipsReg = firestore.collection("friendships")
            .where(
                Filter.or(
                    Filter.equalTo("user1", currentUser.uid),
                    Filter.equalTo("user2", currentUser.uid)
                )
            )
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog - UserRepo", "Error listening to friendships: ${error.message}")
                    return@addSnapshotListener
                }
                friendshipsList = snapshot?.documents?.mapNotNull { it.toObject(Friendship::class.java) } ?: emptyList()
                emitPeople()
            }

        awaitClose {
            usersReg.remove()
            friendshipsReg.remove()
        }
    }

    suspend fun updateUserProfile(updateCurrentUser: User): Result<User> {
        return try {
            firestore.collection("users")
                .document(updateCurrentUser.uid)
                .set(updateCurrentUser, SetOptions.merge())
                .await()
            Result.success(updateCurrentUser)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}