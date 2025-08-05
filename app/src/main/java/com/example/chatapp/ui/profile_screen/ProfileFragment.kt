package com.example.chatapp.ui.profile_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.ProfileScreenBinding
import com.example.chatapp.utils.setImageUrl
import com.example.chatapp.view_model.AuthenticationViewModel

class ProfileFragment : Fragment(){
    private var _binding : ProfileScreenBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthenticationViewModel by activityViewModels()

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
        setUpListener()
    }

    private fun setUpView() {
        val user = authViewModel.user.value
        if (user != null) {
            binding.tvProfileName.text = user.displayName
            binding.tvProfileEmail.text = user.email
            binding.ivAvatar.setImageUrl(user.photoUrl)
        } else {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Language settings not implemented yet", Toast.LENGTH_SHORT).show()

            }

            llNotifications.setOnClickListener {
                Toast.makeText(requireContext(), "Notification settings not implemented yet", Toast.LENGTH_SHORT).show()
            }

            ivEditProfile.setOnClickListener {
                findNavController().navigate(R.id.detailProfileFragment)
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}