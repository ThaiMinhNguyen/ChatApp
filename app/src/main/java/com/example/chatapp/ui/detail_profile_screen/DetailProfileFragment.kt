package com.example.chatapp.ui.detail_profile_screen

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.chatapp.databinding.DetailProfileScreenBinding
import com.example.chatapp.utils.setImageUrl
import com.example.chatapp.view_model.AuthenticationViewModel
import com.example.chatapp.view_model.StorageViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class DetailProfileFragment : Fragment() {
    private var _binding: DetailProfileScreenBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthenticationViewModel by activityViewModels()
    private val storageViewModel: StorageViewModel by activityViewModels()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data

            if(uri != null){
                binding.ivAvatar.setImageURI(uri)
                storageViewModel.uploadAvatar(uri, authViewModel.user.value?.uid, requireContext())
            } else {
                Toast.makeText(requireContext(), "Failed to get image", Toast.LENGTH_SHORT).show()
            }


        } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailProfileScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpListener()
        setupDateEditText()
        setUpObserver()
    }

    private fun setUpView() {
        val user = authViewModel.user.value
        if (user != null) {
            binding.apply {
                ivAvatar.setImageUrl(user.photoUrl)
                etFullName.setText(user.displayName)
                etPhoneNumber.setText(user.phoneNumber)
                etBirthday.setText(
                    if (user.dateOfBirth?.isNotEmpty() == true) user.dateOfBirth else ""
                )
            }
        }
    }



    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.user.collect { user ->
                        if (user != null) {
                            binding.apply {
                                ivAvatar.setImageUrl(user.photoUrl)
                                etFullName.setText(user.displayName)
                                etPhoneNumber.setText(user.phoneNumber)
                                etBirthday.setText(
                                    if (user.dateOfBirth?.isNotEmpty() == true) user.dateOfBirth else ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setUpListener() {
        binding.apply {
            tvSave.setOnClickListener {
                val fullName = binding.etFullName.text.toString()
                val phoneNumber = binding.etPhoneNumber.text.toString()
                val dob = binding.etBirthday.text.toString()
                val currentUser = authViewModel.user.value
                val updateUser = currentUser?.copy(displayName = fullName, phoneNumber = phoneNumber, dateOfBirth = dob)
                if(updateUser != null){
                    authViewModel.updateUserProfile(updateUser)
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "Fail to update profile", Toast.LENGTH_SHORT).show()
                }
            }

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            ivEditAvatar.setOnClickListener {

                ImagePicker.with(this@DetailProfileFragment)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .createIntent { intent ->
                        imagePickerLauncher.launch(intent)
                    }
            }
        }
    }


    private fun setupDateEditText() {
        binding.etBirthday.setOnClickListener {
            showDatePicker()
        }

        binding.etBirthday.isFocusable = false
        binding.etBirthday.isFocusableInTouchMode = false
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val currentDate = binding.etBirthday.text.toString()
        if (currentDate.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                calendar.time = dateFormat.parse(currentDate) ?: Date()
            } catch (e: Exception) {
                Log.e("MyLog - DetailProfileFragment", "Error parsing date: ${e.message}")
            }
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Chọn ngày sinh")
            .setSelection(calendar.timeInMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.etBirthday.setText(dateFormat.format(date))
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}