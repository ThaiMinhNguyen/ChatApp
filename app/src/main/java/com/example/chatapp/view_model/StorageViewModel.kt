package com.example.chatapp.view_model

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.repository.AuthRepository
import com.example.chatapp.domain.repository.ChatRepository
import com.example.chatapp.domain.repository.StorageRepository
import com.example.chatapp.domain.repository.UserRepository
import com.example.chatapp.utils.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val prefs: Prefs
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error get() = _error

    fun onErrorHandle(){
        _error.value = null
    }

    fun uploadAvatar(
        filePath: Uri,
        fileName: String?,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val finalFileName = fileName ?: prefs.getLastUid()
                _loading.value = true
                if (finalFileName == null) return@launch
                val result =
                    storageRepository.uploadFileFromUri("avatar", filePath, finalFileName, context)
                result.onSuccess {
                    val userId = prefs.getLastUid()
                    if (userId.isNullOrEmpty()) {
                        Toast.makeText(context, "User ID is null or empty", Toast.LENGTH_SHORT)
                            .show()
                        return@onSuccess
                    }
                    val run = async {
                        userRepository.updateUserAvatar(
                            userId,
                            "$it?ts=${System.currentTimeMillis()}"
                        ) //Trick để trigger thay đổi ở user
                        authRepository.updateFirebaseAuthAvatar(userId, it)
                    }
                    run.await()
                }.onFailure {
                    _error.value = it.message
                }
            } finally {
                _loading.value = false
            }
        }

    }

}