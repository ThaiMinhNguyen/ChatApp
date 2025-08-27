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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val currentUser = MutableStateFlow<User?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val people: StateFlow<List<People>> = currentUser
        .filterNotNull()
        .flatMapLatest { user ->
            Log.d("MyLog - UserViewModel", "Current user: $user")
            userRepository.listenPeopleFlow(user)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setCurrentUser(user: User?) {
        currentUser.value = user
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

}