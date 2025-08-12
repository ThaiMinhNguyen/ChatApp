package com.example.chatapp.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.data.Room
import com.example.chatapp.domain.data.User
import com.example.chatapp.domain.repository.ChatRepository
import com.example.chatapp.utils.UserUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading get() = _loading

    private val _currentRoom = MutableStateFlow<Room?>(null)
    val currentRoom get() = _currentRoom

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms get() = _rooms

    sealed interface NavEvent {
        data class ToDetail(val roomId: String) : NavEvent
    }

    private val _navEvents = MutableSharedFlow<NavEvent>(extraBufferCapacity = 1)// sharedFlow: suitable cho event, extraBufferCapacity: lưu thêm giá tri để tránh miss nav event
    val navEvents = _navEvents.asSharedFlow()

    private var roomJob: Job? = null

    fun createRoom(currentUser: User, chosenUser: User){
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = chatRepository.createRoom(currentUser, chosenUser)
                result.onSuccess {
                    val roomId = UserUtils.generateId(currentUser.uid, chosenUser.uid)
                    _currentRoom.value = result.getOrNull()
                    _navEvents.tryEmit(NavEvent.ToDetail(roomId))
                }.onFailure {
                    Log.d("MyLog - ChatViewModel", "Error creating room: ${result.exceptionOrNull().toString()}")
                }
            } finally {
                loading.value = false
            }
        }
    }

    fun listenToRoomFlow(currentUser: User){
        roomJob?.cancel()
        roomJob = viewModelScope.launch {
            chatRepository.listenRoomFlow(currentUser = currentUser).collect{ rooms ->
                _rooms.value = rooms
            }
        }
    }

    fun stopRoomFlowListener(){
        roomJob?.cancel()
        roomJob = null
    }
}