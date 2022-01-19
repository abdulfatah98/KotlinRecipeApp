package com.example.kotlinrecipeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.kotlinrecipeapp.databinding.ActivityUpdateRecipeInfoBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateRecipeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateRecipeInfoBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateRecipeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("nameExtra").toString()
        val ingredients = intent.getStringExtra("ingredientsExtra").toString()
        val steps = intent.getStringExtra("stepsExtra").toString()
        val type = intent.getStringExtra("typeExtra").toString()

        binding.etRecipeName.setText(name, TextView.BufferType.EDITABLE)
        binding.etRecipeIngredients.setText(ingredients, TextView.BufferType.EDITABLE)
        binding.etRecipeSteps.setText(steps, TextView.BufferType.EDITABLE)

        val save = findViewById<Button>(R.id.btnSave)
        save.setOnClickListener {

            val editedName = binding.etRecipeName.text.toString()
            val editedIngredients = binding.etRecipeIngredients.text.toString()
            val editedSteps = binding.etRecipeSteps.text.toString()

            if (binding.etRecipeName.text.isNotEmpty() && binding.etRecipeIngredients.text.isNotEmpty() && binding.etRecipeSteps.text.isNotEmpty()) {

                database = FirebaseDatabase.getInstance().getReference("Recipe").child(type)
                //val recipe = Recipe(editedName, editedIngredients, editedSteps, type)
                val recipe = mapOf<String, String>(
                    "recipeName" to editedName,
                    "recipeIngredients" to editedIngredients,
                    "recipeSteps" to editedSteps,
                    "reciptType" to type
                )

                database.child(editedName).updateChildren(recipe).addOnSuccessListener {

                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {

                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }

            } else {

                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }


            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val delete = findViewById<Button>(R.id.btnDelete)
        delete.setOnClickListener {

            if (binding.etRecipeName.text.isNotEmpty() && binding.etRecipeIngredients.text.isNotEmpty() && binding.etRecipeSteps.text.isNotEmpty()) {

                database = FirebaseDatabase.getInstance().getReference("Recipe").child(type)

                database.child(name).removeValue().addOnSuccessListener {

                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {

                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }

            } else {

                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val cancel = findViewById<Button>(R.id.btnCancel)
        cancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}