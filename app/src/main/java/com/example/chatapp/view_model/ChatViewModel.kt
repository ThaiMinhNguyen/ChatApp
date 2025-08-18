package com.example.chatapp.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.data.ChatListItem
import com.example.chatapp.domain.data.Message
import com.example.chatapp.domain.data.Room
import com.example.chatapp.domain.data.User
import com.example.chatapp.domain.repository.ChatRepository
import com.example.chatapp.domain.repository.UserRepository
import com.example.chatapp.utils.UserUtils
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.http.Query
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading get() = _loading

    private val _currentRoom = MutableStateFlow<Room?>(null)
    val currentRoom get() = _currentRoom

    private val _currentChatUser = MutableStateFlow<User?>(null)
    val currentChatUser get() = _currentChatUser

    private val _currentMessageList = MutableStateFlow<List<Message>>(emptyList())
    val currentMessageList get() = _currentMessageList

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms get() = _rooms

    private val _unreadTotal = MutableStateFlow(0)
    val unreadTotal get() = _unreadTotal

    private val _unreadByRoom = MutableStateFlow<Map<String, Int>>(emptyMap())
    val unreadByRoom get() = _unreadByRoom

    private val _error = MutableStateFlow<String?>(null)
    val error get() = _error

    private val _searchResults = MutableStateFlow<List<ChatListItem.SearchResultItem>>(emptyList())
    val searchResults get() = _searchResults

    private var oldestDoc: DocumentSnapshot? = null
    private var isLoadingMore = false
    private var hasMorePage = true

    sealed interface NavEvent {
        data class ToDetail(val roomId: String) : NavEvent
    }

    private val _navEvents = MutableSharedFlow<NavEvent>(extraBufferCapacity = 1)// sharedFlow: suitable cho event, extraBufferCapacity: lưu thêm giá tri để tránh miss nav event
    val navEvents = _navEvents.asSharedFlow()

    private var roomJob: Job? = null

    private var roomMessageJob: Job? = null

    private var unreadTotalJob: Job? = null
    private var unreadByRoomJob: Job? = null

    fun onErrorHandle(){
        _error.value = null
    }

    fun setCurrentRoom(room: Room?){
        _currentRoom.value = room
    }

    fun setCurrentChatUser(user: User?){
        _currentChatUser.value = user
    }

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
                    _error.value = result.exceptionOrNull().toString()
                    Log.d("MyLog - ChatViewModel", "Error creating room: ${result.exceptionOrNull().toString()}")
                }
            } finally {
                loading.value = false
            }
        }
    }

    fun sendMessage(currentUser: User, chosenUserId: String, content: String){
        viewModelScope.launch {
            chatRepository.sendMessage(currentUser, chosenUserId, content)
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

    fun listenToRoomMessage(roomId: String){
        roomMessageJob?.cancel()
        roomMessageJob = viewModelScope.launch {
            chatRepository.listenRoomMessages(roomId, 20).collect{ (messages, lastDoc) ->
                val latestAsc = messages.asReversed()
                val old =_currentMessageList.value
                val merged = (old + latestAsc).distinctBy { it.uid }
                _currentMessageList.value = merged
                oldestDoc = lastDoc
            }
        }
    }

    fun loadMore() {
        val roomId = _currentRoom.value?.participants?.sorted()?.joinToString("_") ?: return
        if (isLoadingMore || !hasMorePage || oldestDoc == null) return

        viewModelScope.launch {
            isLoadingMore = true
            try {
                chatRepository.loadMore(roomId, 20, oldestDoc)
                    .onSuccess { (olderAsc, lastDoc) ->
                        if (olderAsc.isEmpty()) {
                            hasMorePage  = false
                        } else {
                            val cur = _currentMessageList.value
                            _currentMessageList.value = (olderAsc + cur).distinctBy { it.uid }
                            oldestDoc = lastDoc
                        }
                    }
                    .onFailure { e ->
                        _error.value = e.message
                    }
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun stopRoomMessageListener(){
        roomMessageJob?.cancel()
        roomMessageJob = null
    }

    fun stopRoomFlowListener(){
        roomJob?.cancel()
        roomJob = null
    }

    fun listenUnreadTotal(currentUserId: String){
        unreadTotalJob?.cancel()
        unreadTotalJob = viewModelScope.launch {
            chatRepository.listenUnreadTotal(currentUserId).collect { total ->
                _unreadTotal.value = total
            }
        }
    }

    fun listenUnreadByRoom(currentUserId: String){
        unreadByRoomJob?.cancel()
        unreadByRoomJob = viewModelScope.launch {
            chatRepository.listenUnreadByRoom(currentUserId).collect { map ->
                _unreadByRoom.value = map
            }
        }
    }

    fun stopUnreadListeners(){
        unreadTotalJob?.cancel()
        unreadTotalJob = null
        unreadByRoomJob?.cancel()
        unreadByRoomJob = null
    }

    fun markMessageAsRead(roomId: String, currentUserId: String){
        viewModelScope.launch {
            chatRepository.updateRoomMessagesIsRead(roomId, currentUserId)
        }
    }


    fun searchMessages(currentUserId: String, query: String) {
        viewModelScope.launch {
            chatRepository.searchMessagesMatch(currentUserId, query)
                .onSuccess { _searchResults.value = it }
                .onFailure { _searchResults.value = emptyList() }
        }
    }
}