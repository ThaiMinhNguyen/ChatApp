package com.example.chatapp.domain.repository

import android.util.Log
import com.example.chatapp.domain.data.User
import com.example.chatapp.domain.data.People
import com.example.chatapp.domain.data.Friendship
import com.example.chatapp.domain.data.FriendshipStatus
import com.example.chatapp.utils.DateUtils
import com.example.chatapp.utils.UserUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val activeListeners = mutableListOf<ListenerRegistration>()

    fun stopAllListeners() {
        activeListeners.forEach { it.remove() }
        activeListeners.clear()
    }

    fun stopListener(listener: ListenerRegistration) {
        listener.remove()
        activeListeners.remove(listener)
    }

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

//    suspend fun declineFriendRequest(currentUser: User, sendToUser: User) : Result<Friendship>{
//        return try {
//            val uid = UserUtils.generateId(currentUser.uid, sendToUser.uid)
//            val friendRequest = Friendship(
//                user1 = currentUser.uid,
//                user2 = sendToUser.uid,
//                status = FriendshipStatus.DECLINED,
//                createdAt = DateUtils.getCurrentTimestamp(),
//                requestedBy = currentUser.uid
//
//            )
//            firestore.collection("friendships")
//                .document(uid)
//                .set(friendRequest)
//                .await()
//            return Result.success(friendRequest)
//        } catch (e:Exception){
//            Log.e("MyLog - UserRepo", "Error creating user: ${e.message}")
//            Result.failure(e)
//        }
//    }


    fun listenToPeopleChanges(user: User, onPeopleChanged: (List<People>) -> Unit): List<ListenerRegistration> {
        var usersList: List<User> = emptyList()
        var friendshipsList: List<Friendship> = emptyList()

        fun updatePeopleList() {
            val peopleList = usersList.map { otherUser ->
                val friendship = friendshipsList.find {
                    (it.user1 == user.uid && it.user2 == otherUser.uid) ||
                            (it.user1 == otherUser.uid && it.user2 == user.uid)
                }

                val isFriend = friendship?.status == FriendshipStatus.ACCEPTED
                val isRequestSent = friendship?.status == FriendshipStatus.PENDING && friendship.requestedBy == user.uid
                val isRequestReceived = friendship?.status == FriendshipStatus.PENDING && friendship.requestedBy != user.uid

                People(
                    user = otherUser,
                    isFriend = isFriend,
                    isRequestSent = isRequestSent,
                    isRequestReceived = isRequestReceived
                )
            }
            onPeopleChanged(peopleList)
        }

        val usersListener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog - UserRepo", "Error listening to users: ${error.message}")
                    return@addSnapshotListener
                }

                usersList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(User::class.java)
                }?.filter { it.uid != user.uid } ?: emptyList()

                updatePeopleList()
            }

        val friendshipsListener = firestore.collection("friendships")
            .where(Filter.or(
                Filter.equalTo("user1", user.uid),
                Filter.equalTo("user2", user.uid)
            ))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog - UserRepo", "Error listening to friendships: ${error.message}")
                    return@addSnapshotListener
                }

                friendshipsList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Friendship::class.java)
                } ?: emptyList()

                updatePeopleList()
            }

        activeListeners.add(usersListener)
        activeListeners.add(friendshipsListener)

        return activeListeners
    }


    fun listenToFriendshipChanges(user: User, onFriendshipChanged: (List<Friendship>) -> Unit): ListenerRegistration {
        val friendshipsListener = firestore.collection("friendships")
            .where(Filter.or(
                Filter.equalTo("user1", user.uid),
                Filter.equalTo("user2", user.uid)
            ))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog - UserRepo", "Error listening to friendships: ${error.message}")
                    return@addSnapshotListener
                }
                
                val friendships = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Friendship::class.java)
                } ?: emptyList()
                
                onFriendshipChanged(friendships)
            }
        activeListeners.add(friendshipsListener)
        return friendshipsListener
    }

    fun listenToUserChanges(userId: String, onUserChanged: (User?) -> Unit): ListenerRegistration {
        val userListener = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog - UserRepo", "Error listening to user: ${error.message}")
                    return@addSnapshotListener
                }
                
                val user = snapshot?.toObject(User::class.java)
                onUserChanged(user)
            }
        activeListeners.add(userListener)
        return userListener
    }
}