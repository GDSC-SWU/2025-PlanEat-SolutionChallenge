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
import com.example.planeat.databinding.FragmentStep1Binding

class Step1Fragment : Fragment() {

    // ViewBinding object declaration
    private var _binding: FragmentStep1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate FragmentStep1Binding
        _binding = FragmentStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initially disable the Next button
        binding.btnNext.isEnabled = false
        binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.light_gray)
        binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary)) // Disabled text color

        // Add a TextWatcher to the name input field
        binding.nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // Check if the input length is >= 2 to enable the button
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().trim()
                if (input.length >= 2) {
                    // Enable the Next button
                    binding.btnNext.isEnabled = true
                    binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.light_blue) // Enabled color
                    binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)) // Enabled text color
                } else {
                    // Disable the Next button
                    binding.btnNext.isEnabled = false
                    binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.light_gray) // Disabled color
                    binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary)) // Disabled text color
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // On Next button click, save the name and go to the next step
        binding.btnNext.setOnClickListener {
            val name = binding.nameInput.text.toString()

            // Save name in UserProfileManager
            UserProfileManager.userData.name = name

            // Navigate to the next step (Step 2)
            (activity as SignupActivity).goToStep(2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}