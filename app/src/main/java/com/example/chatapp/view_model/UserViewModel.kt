package com.example.chatapp.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.data.FriendshipStatus
import com.example.chatapp.domain.data.People
import com.example.chatapp.domain.data.User
import com.example.chatapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _people = MutableStateFlow<List<People>>(emptyList())
    val people get() = _people

    private val _loading = MutableStateFlow(false)
    val loading get() = _loading

    private var peopleJob: Job? = null

    fun startListening(currentUser: User) {
        peopleJob?.cancel()
        peopleJob = viewModelScope.launch {
            userRepository.listenPeopleFlow(currentUser).collect { peopleList ->
                _people.value = peopleList
            }
        }
    }

    fun stopAllListeners() {
        peopleJob?.cancel()
        peopleJob = null
    }

    fun toggleFriendRequest(currentUser: User, sendToUser: User, status: FriendshipStatus){
        _loading.value = true
        viewModelScope.launch {
            val result = userRepository.toggleFriendRequest(currentUser, sendToUser, status)
            if (result.isSuccess){
                Log.d("MyLog - UserViewModel", "Send add friend successfully")
            } else {
                Log.d("MyLog - UserViewModel", "Send add friend failed")
            }
            _loading.value = false
        }
    }

//    fun declineFriendRequest(currentUser: User, sendToUser: User){
//        _loading.value = true
//        viewModelScope.launch {
//            val result = userRepository.declineFriendRequest(currentUser, sendToUser)
//            if (result.isSuccess){
//                Log.d("MyLog - UserViewModel", "Send add friend successfully")
//            } else {
//                Log.d("MyLog - UserViewModel", "Send add friend failed")
//            }
//            _loading.value = false
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        stopAllListeners()
    }



}