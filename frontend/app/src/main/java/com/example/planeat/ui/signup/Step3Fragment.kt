package com.example.planeat.ui.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planeat.R
import com.example.planeat.RetrofitClient
import com.example.planeat.data.UserProfileManager
import com.example.planeat.data.remote.dto.SignupRequest
import com.example.planeat.databinding.FragmentStep3Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Step3Fragment : Fragment() {

    private var _binding: FragmentStep3Binding? = null
    private val binding get() = _binding!!

    private var isCook: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle Yes/No button UI updates
        updateYesNoButtons()

        binding.yesBtn.setOnClickListener {
            if (isCook != true) {
                isCook = true
                updateYesNoButtons()
                validateInputsAndToggleGoButton()
            }
        }

        binding.noBtn.setOnClickListener {
            if (isCook != false) {
                isCook = false
                updateYesNoButtons()
                validateInputsAndToggleGoButton()
            }
        }

        // Allergy button toggle
        for (i in 0 until binding.allergiesGrid.childCount) {
            val child = binding.allergiesGrid.getChildAt(i)
            if (child is Button) {
                child.setOnClickListener {
                    toggleAllergyButton(child)
                }
            }
        }

        // Add text watchers to listen for input changes
        binding.mealInput.addTextChangedListener(inputWatcher)
        binding.hungryInput.addTextChangedListener(inputWatcher)

        // Initially disable the Go button
        validateInputsAndToggleGoButton()

        binding.btnGo.setOnClickListener {
            val meal = binding.mealInput.text.toString().toIntOrNull()
            val hungry = binding.hungryInput.text.toString().toIntOrNull()

            var isValid = true

            if (meal == null || meal <= 0) {
                binding.mealInput.error = getString(R.string.error_empty_meal)
                isValid = false
            }

            if (hungry == null || hungry <= 0) {
                binding.hungryInput.error = getString(R.string.error_invalid_hungry)
                isValid = false
            }

            if (isCook == null) {
                Toast.makeText(requireContext(), getString(R.string.error_select_cook), Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if (isValid) {
                val allergies = getSelectedAllergies()

                // Save user input to shared object
                UserProfileManager.userData.mealsPerDay = meal
                UserProfileManager.userData.hungerCycle = hungry
                UserProfileManager.userData.canCook = isCook
                UserProfileManager.userData.intolerances = allergies

                postSignupData()
            }
        }
    }

    // Watcher to validate input fields when text changes
    private val inputWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            validateInputsAndToggleGoButton()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    // Validate inputs and enable/disable Go button accordingly
    private fun validateInputsAndToggleGoButton() {
        val meal = binding.mealInput.text.toString().toIntOrNull()
        val hungry = binding.hungryInput.text.toString().toIntOrNull()

        val isValid = meal != null && meal > 0 &&
                hungry != null && hungry > 0 &&
                isCook != null

        binding.btnGo.isEnabled = isValid
        binding.btnGo.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (isValid) R.color.light_blue else R.color.light_gray
        )
        binding.btnGo.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isValid) R.color.white else R.color.secondary
            )
        )
    }

    // Collect selected allergy options
    private fun getSelectedAllergies(): List<String> {
        val selected = mutableListOf<String>()
        if (binding.allergyEgg.isSelected) selected.add(getString(R.string.allergy_egg))
        if (binding.allergyMilk.isSelected) selected.add(getString(R.string.allergy_milk))
        if (binding.allergyBuckwheat.isSelected) selected.add(getString(R.string.allergy_buckwheat))
        if (binding.allergyPeanut.isSelected) selected.add(getString(R.string.allergy_peanut))
        if (binding.allergySoybean.isSelected) selected.add(getString(R.string.allergy_soybean))
        if (binding.allergyWheat.isSelected) selected.add(getString(R.string.allergy_wheat))
        if (binding.allergyPineNut.isSelected) selected.add(getString(R.string.allergy_pine_nut))
        if (binding.allergyWalnut.isSelected) selected.add(getString(R.string.allergy_walnut))
        if (binding.allergyCrab.isSelected) selected.add(getString(R.string.allergy_crab))
        if (binding.allergyShrimp.isSelected) selected.add(getString(R.string.allergy_shrimp))
        if (binding.allergySquid.isSelected) selected.add(getString(R.string.allergy_squid))
        if (binding.allergyMackerel.isSelected) selected.add(getString(R.string.allergy_mackerel))
        if (binding.allergyShellfish.isSelected) selected.add(getString(R.string.allergy_shellfish))
        return selected
    }

    // Update UI button states for Yes/No selection
    private fun updateYesNoButtons() {
        if (isCook == true) {
            binding.yesBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.yesBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.edit)

            binding.noBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.answer))
            binding.noBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        } else if (isCook == false) {
            binding.noBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.noBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.edit)

            binding.yesBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.answer))
            binding.yesBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        } else {
            binding.yesBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.answer))
            binding.yesBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)

            binding.noBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.answer))
            binding.noBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
    }

    // Toggle allergy button selection
    private fun toggleAllergyButton(button: Button) {
        button.isSelected = !button.isSelected
        if (button.isSelected) {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.edit)
        } else {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.answer))
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
    }

    // Send the signup data to the server
    private fun postSignupData() {
        // Fetch the fresh ID token asynchronously
        UserProfileManager.fetchFreshIdToken { idToken ->
            if (idToken.isNullOrEmpty()) {
                // Show error if unable to fetch the ID token
                Toast.makeText(requireContext(), "Unable to fetch ID token.", Toast.LENGTH_SHORT).show()
                return@fetchFreshIdToken
            }

            // Prepare the request data from user profile
            val data = UserProfileManager.userData

            val request = SignupRequest(
                idToken = idToken,  // Use the fresh token
                name = data.name ?: "",
                gender = data.gender ?: "",
                age = data.age ?: 0,
                height = data.height ?: 0,
                weight = data.weight ?: 0,
                location = data.location ?: "",
                mealsPerDay = data.mealsPerDay ?: 0,
                hungerCycle = data.hungerCycle ?: 0,
                canCook = data.canCook ?: false,
                intolerances = data.intolerances ?: emptyList()
            )

            Log.d("SignupRequest", "Request Payload: $request")

            // Send the signup request
            RetrofitClient.apiService.signup(request).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Navigate to home screen if successful
                        (activity as SignupActivity).goToHome()
                    } else {
                        // Log error response
                        val errorBody = response.errorBody()?.string()
                        Log.e("SignupRequest", "Error response: ${response.code()} / $errorBody")
                        Toast.makeText(requireContext(), "Signup failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Log failure error
                    Log.e("SignupRequest", "Network error: ${t.message}")
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}