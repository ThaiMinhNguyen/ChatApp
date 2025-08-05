package com.example.chatapp.ui.detail_profile_screen

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chatapp.databinding.DetailProfileScreenBinding
import com.example.chatapp.utils.setImageUrl
import com.example.chatapp.view_model.AuthenticationViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class DetailProfileFragment : Fragment() {
    private var _binding: DetailProfileScreenBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthenticationViewModel by activityViewModels()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data

            binding.ivAvatar.setImageURI(uri)

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

    private fun setUpListener() {
        binding.apply {
            tvSave.setOnClickListener {
                val fullName = binding.etFullName.text.toString()
                val phoneNumber = binding.etPhoneNumber.text.toString()
    //            authViewModel.updateUserProfile(fullName, phoneNumber)

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