package com.example.planeat.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.planeat.R
import com.example.planeat.RetrofitClient
import com.example.planeat.data.remote.dto.FoodResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class MealActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)

        val ingredientSearch = findViewById<AutoCompleteTextView>(R.id.ingredient_search)
        val ingredient1Input = findViewById<EditText>(R.id.ingredient1_input)
        val ingredient2Input = findViewById<EditText>(R.id.ingredient2_input)
        val ingredient3Input = findViewById<EditText>(R.id.ingredient3_input)
        val goButton = findViewById<Button>(R.id.btn_go)

        val fields = listOf(ingredient1Input, ingredient2Input, ingredient3Input)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        ingredientSearch.setAdapter(adapter)

        ingredientSearch.threshold = 1

        // Add a text watcher to search for food items as user types
        ingredientSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s?.toString()?.trim() ?: return
                if (keyword.isNotBlank()) {
                    lifecycleScope.launch {
                        try {
                            // Fetch food data from API
                            val response: Response<FoodResponse> = RetrofitClient.apiService.searchFoods(keyword, 10)

                            if (response.isSuccessful && response.body() != null) {
                                val foodResponse = response.body()!!
                                val names = foodResponse.data.map { it.name }

                                adapter.clear()
                                adapter.addAll(names)

                                runOnUiThread {
                                    adapter.notifyDataSetChanged()
                                    if (ingredientSearch.hasFocus()) {
                                        ingredientSearch.showDropDown()
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    Toast.makeText(this@MealActivity, "Search failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(this@MealActivity, "Search failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })

        // Assign selected ingredient to the first empty field
        ingredientSearch.setOnItemClickListener { _, _, position, _ ->
            val selected = adapter.getItem(position) ?: return@setOnItemClickListener
            val emptyField = fields.firstOrNull { it.text.isNullOrBlank() }
            if (emptyField != null) {
                emptyField.setText(selected)
            } else {
                Toast.makeText(this, "You can add no more ingredients.", Toast.LENGTH_SHORT).show()
            }

            ingredientSearch.setText("")
            ingredientSearch.clearFocus()
        }

        // Clear content when input field is clicked
        fields.forEach { field ->
            field.setOnClickListener {
                if (!field.text.isNullOrBlank()) field.setText("")
            }
        }

        // Handle Go button click to move to the MealDetailActivity
        goButton.setOnClickListener {
            val ingredient1 = ingredient1Input.text.toString()
            val ingredient2 = ingredient2Input.text.toString()
            val ingredient3 = ingredient3Input.text.toString()

            if (ingredient1.isNotBlank() || ingredient2.isNotBlank() || ingredient3.isNotBlank()) {
                val intent = Intent(this, MealDetailActivity::class.java).apply {
                    putExtra("ingredient1", ingredient1)
                    putExtra("ingredient2", ingredient2)
                    putExtra("ingredient3", ingredient3)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select at least one ingredient.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
