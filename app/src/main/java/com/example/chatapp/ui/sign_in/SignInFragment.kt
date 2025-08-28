package com.example.chatapp.ui.sign_in

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.SignInScreenBinding
import com.example.chatapp.view_model.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInFragment : Fragment() {
    private var _binding : SignInScreenBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SignInScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpObserver()
        setUpListener()
    }

    private var orig = 0
    override fun onResume() {
        super.onResume()
        val w = requireActivity().window
        orig = w.attributes.softInputMode
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
    override fun onPause() {
        requireActivity().window.setSoftInputMode(orig)
        super.onPause()
    }

    private fun setUpView(){

    }

    private fun setUpListener(){
        binding.etEmail.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    authViewModel.onEmailChange(p0.toString())
                    authViewModel.checkSignInFieldsFilled()
                }

                override fun afterTextChanged(p0: Editable?) {
                    authViewModel.onEmailChange(p0.toString())
                }

            }
        )
        binding.etPassword.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    authViewModel.onPasswordChange(p0.toString())
                    authViewModel.checkSignInFieldsFilled()
                }

                override fun afterTextChanged(p0: Editable?) {
                    authViewModel.onPasswordChange(p0.toString())
                }

            }
        )

        binding.btnLogin.setOnClickListener{
            clearHelperText()
            if(!authViewModel.validateEmail()){
                binding.tilEmail.helperText = "Please enter a valid email address"
                binding.tilEmail.isHelperTextEnabled = true
            } else if (!authViewModel.validatePassword()){
                binding.tilPassword.helperText = "Password must be at least 6 characters"
                binding.tilEmail.isHelperTextEnabled = true
            } else {
                Log.d("MyLog - SignInScreen", "Email: ${authViewModel.email.value}, Password: ${authViewModel.password.value}")
                authViewModel.signIn()
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }
    }

    private fun setUpObserver(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    authViewModel.isAllFilled.collect { isAllFilled ->
                        changeSignInButtonColor(isAllFilled)
                    }
                }
                launch {
                    combine(
                        authViewModel.email,
                        authViewModel.password
                    ) { email, password ->
                        email.isNotEmpty() && password.isNotEmpty()
                    }.collect { isAllFilled ->
                        changeSignInButtonColor(isAllFilled)
                    }
                }


            }
        }
    }

    

    private fun changeSignInButtonColor(isAllFilled: Boolean){
        if (isAllFilled){
            binding.btnLogin.setBackgroundColor("#4356B4".toColorInt())
        } else {
            binding.btnLogin.setBackgroundColor("#CACACA".toColorInt())
        }

    }

    private fun clearHelperText(){
        binding.tilEmail.isHelperTextEnabled = false
        binding.tilPassword.isHelperTextEnabled = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}