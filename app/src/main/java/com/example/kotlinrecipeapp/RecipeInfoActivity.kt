package com.example.kotlinrecipeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.kotlinrecipeapp.databinding.ActivityMainBinding
import com.example.kotlinrecipeapp.databinding.ActivityRecipeInfoBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RecipeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeInfoBinding
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("nameExtra").toString()
        val ingredients = intent.getStringExtra("ingredientsExtra").toString()
        val steps = intent.getStringExtra("stepsExtra").toString()
        val type = intent.getStringExtra("typeExtra").toString()

        binding.tvRecipeName.text = name
        binding.tvRecipeIngredients.text = ingredients
        binding.tvRecipeSteps.text = steps

        val update = findViewById<Button>(R.id.btnUpdate)
        update.setOnClickListener {
            val intent = Intent(this, UpdateRecipeInfoActivity::class.java)
            intent.putExtra("nameExtra",name)
            intent.putExtra("ingredientsExtra",ingredients)
            intent.putExtra("stepsExtra",steps)
            intent.putExtra("typeExtra",type)
            startActivity(intent)
        }



    }
}