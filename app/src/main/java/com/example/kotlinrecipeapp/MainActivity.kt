package com.example.kotlinrecipeapp

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinrecipeapp.databinding.ActivityMainBinding
import com.google.firebase.database.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeArrayList: ArrayList<Recipe>

    private lateinit var dbref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var selection: String? = null

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
            val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, recipeTypeList)

            binding.spRecipeTypes.adapter = arrayAdapter

            binding.spRecipeTypes.onItemSelectedListener = object :

                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selection = recipeTypeList[p2]
                    Toast.makeText(this@MainActivity, selection, Toast.LENGTH_SHORT).show()
                    recipeRecyclerView = binding.recyclerView
                    binding.recyclerView.apply {

                        layoutManager = LinearLayoutManager(this@MainActivity)
                        setHasFixedSize(true)

                    }
                    recipeArrayList = arrayListOf<Recipe>()
                    getRecipeData(selection!!)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }


        recipeRecyclerView = binding.recyclerView
        binding.recyclerView.apply {

            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)

        }
        recipeArrayList = arrayListOf<Recipe>()

        getRecipeData()

        val addRecipe = findViewById<Button>(com.example.kotlinrecipeapp.R.id.btnAddRecipe)
        addRecipe.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
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

    private fun getRecipeData() {

        dbref = FirebaseDatabase.getInstance().getReference("Recipe")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (recipeSnapshot in snapshot.children) {
                        val recipe = recipeSnapshot.getValue(Recipe::class.java)
                        recipeArrayList.add(recipe!!)
                    }

                    var adapter = RecipeAdapter(recipeArrayList)

                    recipeRecyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object : RecipeAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@MainActivity, RecipeInfoActivity::class.java)
                            intent.putExtra("name", recipeArrayList[position].recipeName)
                            startActivity(intent)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getRecipeData(selection: String) {

        dbref = FirebaseDatabase.getInstance().getReference("Recipe").child(selection)

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (recipeSnapshot in snapshot.children) {
                        val recipe = recipeSnapshot.getValue(Recipe::class.java)
                        recipeArrayList.add(recipe!!)
                    }

                    var adapter = RecipeAdapter(recipeArrayList)
                    recipeRecyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object : RecipeAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@MainActivity, RecipeInfoActivity::class.java)
                            //intent.putExtra("name", recipeArrayList[position].recipeName)
                            intent.putExtra("nameExtra", recipeArrayList[position].recipeName)
                            intent.putExtra(
                                "ingredientsExtra",
                                recipeArrayList[position].recipeIngredients
                            )
                            intent.putExtra("stepsExtra", recipeArrayList[position].recipeSteps)
                            intent.putExtra("typeExtra", selection)
                            startActivity(intent)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

}