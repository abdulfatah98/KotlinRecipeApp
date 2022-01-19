package com.example.kotlinrecipeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinrecipeapp.databinding.ActivityAddRecipeBinding
import com.example.kotlinrecipeapp.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecipeBinding
    private lateinit var database: DatabaseReference
    var selection: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //parse xml
        val pullParserFactory: XmlPullParserFactory
        try {
            pullParserFactory = XmlPullParserFactory.newInstance()
            val parser = pullParserFactory.newPullParser()
            val inputStream = applicationContext.assets.open("recipetypes.xml")
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)

            val recipe = parseXml(parser)
            val recipeTypeList: MutableList<String> = ArrayList()
            for (type in recipe!!) {
                recipeTypeList.add(type.name)
            }
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, recipeTypeList)

            binding.spRecipeTypes.adapter = arrayAdapter

            binding.spRecipeTypes.onItemSelectedListener = object :

                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selection = recipeTypeList[p2]
                    Toast.makeText(this@AddRecipeActivity, selection, Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        binding.btnAdd.setOnClickListener {
            val addName = binding.etRecipeName.text.toString()
            val addIngredients = binding.etRecipeIngredients.text.toString()
            val addSteps = binding.etRecipeSteps.text.toString()
            val addType = selection.toString()

            if (binding.etRecipeName.text.isNotEmpty() && binding.etRecipeIngredients.text.isNotEmpty()&& binding.etRecipeSteps.text.isNotEmpty()) {

                database = FirebaseDatabase.getInstance().getReference("Recipe").child(addType)
                val recipe = Recipe(addName, addIngredients, addSteps, addType)

                database.child(addName).setValue(recipe).addOnSuccessListener {

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

    }

    //parse xml function
    @Throws(XmlPullParserException::class, IOException::class)
    fun parseXml(parser: XmlPullParser): ArrayList<RecipeTypes>? {
        var recipe: ArrayList<RecipeTypes>? = null
        var eventType = parser.eventType
        var type: RecipeTypes? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val name: String
            when (eventType) {
                XmlPullParser.START_DOCUMENT -> recipe = ArrayList()
                XmlPullParser.START_TAG -> {
                    name = parser.name
                    if (name == "type") {
                        type = RecipeTypes()
                        type.id = parser.getAttributeValue(null, "id")
                    } else if (type != null) {
                        if (name == "name") {
                            type.name = parser.nextText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    name = parser.name
                    if (name.equals("type", true) && type != null) {
                        recipe!!.add(type)
                    }
                }
            }
            eventType = parser.next()
        }
        return recipe
    }
}