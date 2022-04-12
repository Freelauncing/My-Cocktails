package com.mycocktails

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.kenmeidearu.searchablespinnerlibrary.SearchableSpinner
import com.kenmeidearu.searchablespinnerlibrary.mListString
import com.mycocktails.data.database.getDatabase
import com.mycocktails.data.model.Category
import com.mycocktails.data.model.Ingredient
import com.mycocktails.data.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    val categoryBtn by lazy { findViewById<Button>(R.id.button5) }
    val ingredientBtn by lazy { findViewById<Button>(R.id.button6) }
    val perfromSearch by lazy { findViewById<Button>(R.id.perfromSearch) }
    val categoryLayout by lazy { findViewById<LinearLayoutCompat>(R.id.categoryLayout) }
    val ingredientsLayout by lazy { findViewById<LinearLayoutCompat>(R.id.ingredientsLayout) }
    val spinner by lazy { findViewById<Spinner>(R.id.spinner) }
    val radioGroup by lazy {   findViewById<RadioGroup>(R.id.radioGroup)}
    val searchView by lazy { findViewById<SearchableSpinner>(R.id.searchableSpinner) }
    var radioButtonSelected: RadioButton? = null

    val network by lazy { Network.getInstance(this@MainActivity) }
    val queue by lazy{ Volley.newRequestQueue(this@MainActivity)}

    val database by lazy { getDatabase(this@MainActivity)}

    private var selectedCategory = ""
    private var myCategoryList:ArrayList<Category> = ArrayList()
    private val myIngredientsList: ArrayList<mListString> = ArrayList() //must crete this to generate data there are n1-n4

    private var mode :String =  "category"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        categoryBtn.setOnClickListener {
            mode = "category"
            categoryLayout.visibility = View.VISIBLE
            ingredientsLayout.visibility = View.GONE
        }
        ingredientBtn.setOnClickListener {
            mode = "ingredient"
            ingredientsLayout.visibility = View.VISIBLE
            categoryLayout.visibility = View.GONE
        }

        perfromSearch.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            // find the radiobutton by returned id
            radioButtonSelected = findViewById(selectedId);

            if(mode == "category") {
                if (radioButtonSelected != null && !selectedCategory.isNullOrEmpty()) {
                    val myIntent = Intent(this@MainActivity, SearchResultActivity::class.java)
                    myIntent.putExtra("searchType", radioButtonSelected!!.text.toString()) //Optional parameters
                    myIntent.putExtra("mode", mode) //Optional parameters
                    myIntent.putExtra("itemName", selectedCategory) //Optional parameters
                    this@MainActivity.startActivity(myIntent)
                    Toast.makeText(
                        this,
                        "Done " + radioButtonSelected!!.text.toString() + " => " + mode + " => " + selectedCategory,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Missing Something",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else if(mode == "ingredient" && searchView.selectedItemPosition>-1){
                if (radioButtonSelected != null && !myIngredientsList.get(searchView.selectedItemPosition-1).nilai1.toString().trim().isNullOrEmpty()) {
                    val myIntent = Intent(this@MainActivity, SearchResultActivity::class.java)
                    myIntent.putExtra("searchType", radioButtonSelected!!.text.toString()) //Optional parameters
                    myIntent.putExtra("mode", mode) //Optional parameters
                    myIntent.putExtra("itemName", myIngredientsList.get(searchView.selectedItemPosition-1).nilai1.toString()) //Optional parameters
                    this@MainActivity.startActivity(myIntent)
                    Toast.makeText(
                        this,
                        "Done " + radioButtonSelected!!.text.toString() + " => " + mode + " => " +myIngredientsList.get(searchView.selectedItemPosition-1).nilai1 ,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Missing Something" ,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        spinner.onItemSelectedListener = this


    }

    override fun onResume() {
        super.onResume()

        fetchOrSetCategories(Response.Listener {
            myCategoryList = it
            val arraySpinner = it.map {
                it.strCategory
            }.toList()

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, arraySpinner
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setAdapter(adapter)
        },Response.ErrorListener {
            Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
        })

        fetchOrSetIngredients(Response.Listener {

            searchView.setTitleList("Select Ingredient") //use this to create initial search first

            var id = 0
            for (s in it) {
                myIngredientsList.add(mListString(id, s.strIngredient1))
                id++
            }

            searchView.setAdapter(myIngredientsList, 2, 1) // type spinner 1-4, searh option 1-4
        },Response.ErrorListener {
            Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
        })
    }

    fun fetchOrSetCategories(listener: Response.Listener<ArrayList<Category>>, errorListener: Response.ErrorListener) {
        // Launch a coroutine
        GlobalScope.launch(Dispatchers.Main) {
            val categories = withContext(Dispatchers.IO) {
                database.categoryDao.getCategories()
            }
            if(categories.isEmpty()) {
                val request = network.getCategories(Response.Listener {
                    GlobalScope.launch(Dispatchers.Main) {
                        it.forEach {
                            database.categoryDao.insertCategory(it)
                        }
                        listener.onResponse(it)
                    }
                }, Response.ErrorListener {
                    errorListener.onErrorResponse(it)
                })

                queue.add(request)
            }else{
                listener.onResponse(categories as ArrayList<Category>)
            }
        }
    }

    fun fetchOrSetIngredients(listener: Response.Listener<ArrayList<Ingredient>>, errorListener: Response.ErrorListener) {
        // Launch a coroutine
        GlobalScope.launch(Dispatchers.Main) {
            val ingredients = withContext(Dispatchers.IO) {
                database.ingredientDao.getIngredient()
            }
            if(ingredients.isEmpty()) {
                val request = network.getIngredients(Response.Listener {
                    GlobalScope.launch(Dispatchers.Main) {
                        it.forEach {
                            database.ingredientDao.insertIngredient(it)
                        }
                        listener.onResponse(it)
                    }
                }, Response.ErrorListener {
                    errorListener.onErrorResponse(it)
                })

                queue.add(request)
            }else{
                listener.onResponse(ingredients as ArrayList<Ingredient>)
            }
        }
    }



    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if(myCategoryList.size>0)
            selectedCategory = myCategoryList.get(p2).strCategory
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        if(myCategoryList.size>0)
            selectedCategory = myCategoryList.get(0).strCategory
    }

}
