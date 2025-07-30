package com.example.chatapp.view_model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email get() = _email

    private val _password = MutableStateFlow("")
    val password get() = _password

    private val _fullName = MutableStateFlow("")
    val fullName get() = _fullName

    private val _isAllFilled = MutableStateFlow(false)
    val isAllFilled get() = _isAllFilled

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
        viewModelScope.launch {
            authRepository.signInWithEmailAndPassword(email.value, password.value)
                .onSuccess { user ->
                    Log.d("MyLog - AuthViewModel", "Sign-in successful: ${user.displayName}")
                }
                .onFailure { exception ->
                    Log.e("MyLog - AuthViewModel", "Sign-in failed: ${exception.message}")
                }
        }
    }

    fun signUp() {
        viewModelScope.launch {
            authRepository.signUpWithEmailAndPassword(email.value, password.value, fullName.value)
                .onSuccess { user ->
                    Log.d("MyLog - AuthViewModel", "Sign-up successful: ${user.displayName}")
                }
                .onFailure { exception ->
                    Log.e("MyLog - AuthViewModel", "Sign-up failed: ${exception.message}")
                }
        }
    }

}