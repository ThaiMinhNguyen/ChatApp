package com.example.chatapp.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.data.User
import com.example.chatapp.domain.repository.AuthRepository
import com.example.chatapp.domain.repository.UserRepository
import com.example.chatapp.utils.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val prefs: Prefs
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email get() = _email

    private val _password = MutableStateFlow("")
    val password get() = _password

    private val _fullName = MutableStateFlow("")
    val fullName get() = _fullName

    private val _isAllFilled = MutableStateFlow(false)
    val isAllFilled get() = _isAllFilled

    private val _user = MutableStateFlow<User?>(null)
    val user get() = _user

    private val _loading = MutableStateFlow(false)
    val loading get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error get() = _error

    private var userJob : Job? = null

    fun onErrorHandle(){
        _error.value = null
    }

    fun onEmailChange(email: String){
        _email.value = email
    }

    fun onPasswordChange(password: String){
        _password.value = password
    }

    fun onFullNameChange(fullName: String){
        _fullName.value = fullName
    }

    fun checkSignInFieldsFilled() {
        _isAllFilled.value = _email.value.isNotEmpty() && _password.value.isNotEmpty()
    }

    fun validateEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
    }

    fun validatePassword(): Boolean {
        return password.value.length >= 6
    }

    fun validateFullName(): Boolean {
        return fullName.value.isNotEmpty()
    }

    fun checkSignUpFieldsFilled() {
        _isAllFilled.value = _fullName.value.isNotEmpty() && _email.value.isNotEmpty() && _password.value.isNotEmpty()
    }

    fun signIn(){
        _loading.value = true
        viewModelScope.launch {
            authRepository.signInWithEmailAndPassword(email.value, password.value)
                .onSuccess { user ->
                    _user.value = user
                    Log.d("MyLog - AuthViewModel", "Sign-in successful: ${user.displayName}")
                }
                .onFailure { exception ->
                    Log.e("MyLog - AuthViewModel", "Sign-in failed: ${exception.message}")
                    _error.value = "Time out or network error. Please try again later."
                }
            _loading.value = false
        }
    }

    fun signUp() {
        _loading.value = true

        viewModelScope.launch {
            authRepository.signUpWithEmailAndPassword(email.value, password.value, fullName.value)
                .onSuccess { user ->
                    _user.value = user
                    Log.d("MyLog - AuthViewModel", "Sign-up successful: ${user.displayName}")
                }
                .onFailure { exception ->
                    Log.e("MyLog - AuthViewModel", "Sign-up failed: ${exception.message}")
                    _error.value = "Time out or network error. Please try again later."
                }
            _loading.value = false
        }
    }

    fun restoreSessionIfPossible() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val fbUser = authRepository.getCurrentUser()
                val uid = fbUser?.uid ?: return@launch
                authRepository.loadUserByUid(uid)
                    .onSuccess { loaded -> _user.value = loaded }
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun signOut() {
        prefs.clear()
        _user.value = null
        authRepository.signOut()
    }

    fun updateUserProfile(updateUser: User){
        viewModelScope.launch {
            _loading.value = true
            try {
                authRepository.updateUserProfile(updateUser)
                    .onSuccess {
                        Log.d("MyLog - AuthViewModel", "Update user profile successfully")
                        _user.value = it
                    }
                    .onFailure {
                        _user.value = _user.value?.copy(displayName = authRepository.getCurrentUser()?.displayName)
                        Log.e("MyLog - AuthViewModel", "Update user profile failed")
                        _error.value = "Time out or network error. Please try again later."
                    }
            } catch (e:Exception){
                Log.e("MyLog - AuthViewModel", "Error updating user profile: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun listenToUserProfile(uid: String) {
        userJob?.cancel()
        userJob = viewModelScope.launch {
            userRepository.listenToUserById(uid).collect { user ->
                Log.d("MyLog - AuthViewModel", "User profile updated: ${user.displayName}")
                _user.value = user
            }
        }
    }

    fun stopListeningToUserProfile() {
        userJob?.cancel()
        userJob = null
    }

}