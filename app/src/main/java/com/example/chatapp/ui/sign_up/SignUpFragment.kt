package com.example.chatapp.ui.sign_up

import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.SignUpScreenBinding
import com.example.chatapp.view_model.AuthenticationViewModel

class SignUpFragment : Fragment() {
    private var _binding : SignUpScreenBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthenticationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SignUpScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpListener()
    }

    private fun setUpView(){
        setUpTermText()
    }

    private fun setUpListener(){
        binding.tvLoginLink.setOnClickListener {
            findNavController().navigate(R.id.signInFragment)
        }

        binding.ivBack.setOnClickListener{
            findNavController().navigate(R.id.signInFragment)
        }

        binding.etFullName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    authViewModel.onFullNameChange(p0.toString())
                    authViewModel.checkSignUpFieldsFilled()
                }

                override fun afterTextChanged(p0: Editable?) {
                    authViewModel.onFullNameChange(p0.toString())
                }

            }
        )

        binding.etEmail.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    authViewModel.onEmailChange(p0.toString())
                    authViewModel.checkSignUpFieldsFilled()
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
                    authViewModel.checkSignUpFieldsFilled()
                }

                override fun afterTextChanged(p0: Editable?) {
                    authViewModel.onPasswordChange(p0.toString())
                }

            }
        )

        binding.btnSignUp.setOnClickListener {
            clearHelperText()
            val mailHelperText = getString(R.string.email_helper_text)
            val passwordHelperText = getString(R.string.password_helper_text)
            val nameHelperText = getString(R.string.full_name_helper_text)
            val termWarning = getString(R.string.term_warning)
            if(!authViewModel.validateEmail()){
                binding.tilEmail.helperText = mailHelperText
                binding.tilEmail.isHelperTextEnabled = true
            } else if (!authViewModel.validatePassword()){
                binding.tilPassword.helperText = passwordHelperText
                binding.tilEmail.isHelperTextEnabled = true
            } else if (!authViewModel.validateFullName()){
                binding.tilFullName.helperText = nameHelperText
                binding.tilFullName.isHelperTextEnabled = true
            } else if (!binding.cbTerms.isChecked) {
                Toast.makeText(requireContext(), termWarning, Toast.LENGTH_SHORT).show()
            } else {
                Log.d("MyLog - SignUpScreen", "Email: ${authViewModel.email.value}, Password: ${authViewModel.password.value}, Full Name: ${authViewModel.fullName.value}")
                authViewModel.signUp()
            }
        }

    }

    private fun setUpTermText(){
        val text = binding.tvTermsText.text.toString()
        val spannableString = SpannableString(text)
        val policy = if(text.contains("chính sách")) {
            "chính sách"
        } else {
            "policies"
        }

        val term = if(text.contains("điều khoản")) {
            "điều khoản"
        } else {
            "terms"
        }
        val policyStart = text.indexOf(policy)
        val policyEnd = policyStart + policy.length
        val termStart = text.indexOf(term)
        val termEnd = termStart + term.length

        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.primary_blue, null)),
            policyStart, policyEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.primary_blue, null)),
            termStart, termEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            object : ClickableSpan() {
                override fun onClick(p0: View) {
                    openPolicy()                }
            },
            policyStart, policyEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            object : ClickableSpan() {
                override fun onClick(p0: View) {
                    openTerms()                }
            },
            termStart, termEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTermsText.text = spannableString
        binding.tvTermsText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun openPolicy() {
        Toast.makeText(requireContext(), "Mở chính sách", Toast.LENGTH_SHORT).show()
    }

    private fun openTerms() {
        Toast.makeText(requireContext(), "Mở điều khoản", Toast.LENGTH_SHORT).show()
    }

    private fun clearHelperText(){
        binding.tilEmail.isHelperTextEnabled = false
        binding.tilPassword.isHelperTextEnabled = false
        binding.tilFullName.isHelperTextEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}