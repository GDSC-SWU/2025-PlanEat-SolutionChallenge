package com.example.planeat.ui.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planeat.R
import com.example.planeat.data.UserProfileManager
import com.example.planeat.databinding.FragmentStep2Binding

class Step2Fragment : Fragment() {

    private var _binding: FragmentStep2Binding? = null
    private val binding get() = _binding!!

    private var selectedGender: String? = null // "male" or "female"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Disable the Next button initially
        disableNextButton()

        // Add TextWatchers to input fields
        binding.ageInput.addTextChangedListener(inputWatcher)
        binding.heightInput.addTextChangedListener(inputWatcher)
        binding.weightInput.addTextChangedListener(inputWatcher)
        binding.fromInput.addTextChangedListener(inputWatcher)

        // Set gender selection click listeners
        binding.maleBtn.setOnClickListener {
            selectGender("male")
        }

        binding.femaleBtn.setOnClickListener {
            selectGender("female")
        }

        // Set Next button click listener
        binding.btnNext.setOnClickListener {
            val age = binding.ageInput.text.toString().toIntOrNull()
            val height = binding.heightInput.text.toString().toIntOrNull()
            val weight = binding.weightInput.text.toString().toIntOrNull()
            val from = binding.fromInput.text.toString()

            var isValid = true

            if (selectedGender == null) {
                isValid = false
            }
            if (age == null || age <= 0) {
                binding.ageInput.error = getString(R.string.error_invalid_age)
                isValid = false
            }
            if (height == null || height <= 0) {
                binding.heightInput.error = getString(R.string.error_invalid_height)
                isValid = false
            }
            if (weight == null || weight <= 0) {
                binding.weightInput.error = getString(R.string.error_invalid_weight)
                isValid = false
            }
            if (from.isEmpty()) {
                binding.fromInput.error = getString(R.string.error_empty_from)
                isValid = false
            }

            if (isValid) {
                // Save data to UserProfileManager
                UserProfileManager.userData.gender = selectedGender
                UserProfileManager.userData.age = age
                UserProfileManager.userData.height = height
                UserProfileManager.userData.weight = weight
                UserProfileManager.userData.location = from

                // Navigate to Step 3
                (activity as SignupActivity).goToStep(3)
            }
        }
    }

    // TextWatcher for all inputs
    private val inputWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateInputs()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    // Validates all input fields and gender selection
    private fun validateInputs() {
        val age = binding.ageInput.text.toString().toIntOrNull()
        val height = binding.heightInput.text.toString().toIntOrNull()
        val weight = binding.weightInput.text.toString().toIntOrNull()
        val from = binding.fromInput.text.toString()

        val isValid = selectedGender != null &&
                age != null && age > 0 &&
                height != null && height > 0 &&
                weight != null && weight > 0 &&
                from.isNotEmpty()

        if (isValid) {
            enableNextButton()
        } else {
            disableNextButton()
        }
    }

    // Change UI based on selected gender
    private fun selectGender(gender: String) {
        selectedGender = gender

        // Apply selected style to the selected button and reset the other
        if (gender == "male") {
            setGenderSelected(binding.maleBtn, true)
            setGenderSelected(binding.femaleBtn, false)
        } else {
            setGenderSelected(binding.femaleBtn, true)
            setGenderSelected(binding.maleBtn, false)
        }

        validateInputs()
    }

    private fun setGenderSelected(button: View, selected: Boolean) {
        button.isSelected = selected
        if (selected) {
            button.setBackgroundTintList(
                ContextCompat.getColorStateList(requireContext(), R.color.edit)
            )
            button.setForegroundTintList(
                ContextCompat.getColorStateList(requireContext(), R.color.white)
            )
            (button as? android.widget.Button)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
        } else {
            button.setBackgroundTintList(
                ContextCompat.getColorStateList(requireContext(), R.color.white)
            )
            button.setForegroundTintList(
                ContextCompat.getColorStateList(requireContext(), R.color.answer)
            )
            (button as? android.widget.Button)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.answer)
            )
        }
    }

    // Enable the Next button with active styles
    private fun enableNextButton() {
        binding.btnNext.isEnabled = true
        binding.btnNext.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.light_blue)
        binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    // Disable the Next button with inactive styles
    private fun disableNextButton() {
        binding.btnNext.isEnabled = false
        binding.btnNext.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.light_gray)
        binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}