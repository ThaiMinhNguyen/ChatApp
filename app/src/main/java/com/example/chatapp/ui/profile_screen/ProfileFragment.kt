package com.example.chatapp.ui.profile_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.DialogLanguageSelectionBinding
import com.example.chatapp.databinding.ProfileScreenBinding
import com.example.chatapp.utils.LanguageManager
import com.example.chatapp.utils.setImageUrl
import com.example.chatapp.view_model.AuthenticationViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(){
    private var _binding : ProfileScreenBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthenticationViewModel by activityViewModels()

    private var currentLanguage = LanguageManager.getCurrentLanguageCode()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpObserver()
        setUpListener()
    }

    private fun setUpView() {
        binding.apply {
            tvLanguage.text = if(currentLanguage == "vi") requireContext().getString(R.string.vietnamese) else requireContext().getString(R.string.english)
        }
        val user = authViewModel.user.value
        if (user != null) {
            binding.tvProfileName.text = user.displayName
            binding.tvProfileEmail.text = user.email
            binding.ivAvatar.setImageUrl(user.photoUrl)
        } else {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.user.collect { user ->
                        if (user != null) {
                            binding.tvProfileName.text = user.displayName
                            binding.tvProfileEmail.text = user.email
                            binding.ivAvatar.setImageUrl(user.photoUrl)
                        }
                    }
                }
            }
        }
    }

    private fun setUpListener() {
        binding.apply {
            llSignOut.setOnClickListener {
                authViewModel.signOut()
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT)
                    .show()
            }

            llLanguage.setOnClickListener {
                showLanguageDialog()
            }

            llNotifications.setOnClickListener {
                Toast.makeText(requireContext(), "Notification settings not implemented yet", Toast.LENGTH_SHORT).show()
            }

            ivEditProfile.setOnClickListener {
                findNavController().navigate(R.id.detailProfileFragment)
            }
        }
    }


    private fun showLanguageDialog() {
        val dialogBinding = DialogLanguageSelectionBinding.inflate(layoutInflater)
        when(currentLanguage){
            "vi" -> dialogBinding.rbVietnamese.isChecked = true
            "en" -> dialogBinding.rbEnglish.isChecked = true
            else -> dialogBinding.rbVietnamese.isChecked = true
        }

        LanguageManager.debugLanguageInfo(requireContext())

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.btnOk.setOnClickListener {
            val selectedLanguage = when (dialogBinding.rgLanguage.checkedRadioButtonId) {
                R.id.rbVietnamese -> "vi"
                R.id.rbEnglish -> "en"
                else -> "vi"
            }

            if (selectedLanguage != currentLanguage) {
                LanguageManager.setLanguage(requireContext(), selectedLanguage)

                val message = if (selectedLanguage == "vi") {
                    "Đã chuyển sang tiếng Việt"
                } else {
                    "Switched to English"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}