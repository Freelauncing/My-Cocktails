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
import com.mycocktails.Utils.Companion.MODE_CATEGORY
import com.mycocktails.Utils.Companion.MODE_INGREDIENT
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
    val loading by lazy { findViewById<ProgressBar>(R.id.loading) }
    var radioButtonSelected: RadioButton? = null

    val network by lazy { Network.getInstance(this@MainActivity) }
    val queue by lazy{ Volley.newRequestQueue(this@MainActivity)}

    val database by lazy { getDatabase(this@MainActivity)}

    private var selectedCategory = ""
    private var myCategoryList:ArrayList<Category> = ArrayList()
    private val myIngredientsList: ArrayList<mListString> = ArrayList() //must crete this to generate data there are n1-n4

    private var mode :String =  MODE_CATEGORY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        categoryBtn.setOnClickListener {
            mode = MODE_CATEGORY
            categoryLayout.visibility = View.VISIBLE
            ingredientsLayout.visibility = View.GONE
        }
        ingredientBtn.setOnClickListener {
            mode = MODE_INGREDIENT
            ingredientsLayout.visibility = View.VISIBLE
            categoryLayout.visibility = View.GONE
        }

        perfromSearch.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            // find the radiobutton by returned id
            radioButtonSelected = findViewById(selectedId);

            if(mode == MODE_CATEGORY) {
                if (radioButtonSelected != null && !selectedCategory.isNullOrEmpty()) {
                    val myIntent = Intent(this@MainActivity, SearchResultActivity::class.java)
                    myIntent.putExtra("searchType", radioButtonSelected!!.text.toString()) //Optional parameters
                    myIntent.putExtra("mode", mode) //Optional parameters
                    myIntent.putExtra("itemName", selectedCategory) //Optional parameters
                    this@MainActivity.startActivity(myIntent)
                } else {
                    Toast.makeText(
                        this,
                        "Missing Something",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else if(mode == MODE_INGREDIENT && searchView.selectedItemPosition>0){
                if (radioButtonSelected != null &&
                    !myIngredientsList.get(searchView.selectedItemPosition-1).nilai1.toString().trim().isNullOrEmpty() &&
                    !myIngredientsList.get(searchView.selectedItemPosition-1).nilai1.toString().trim().equals("Select Ingredient")
                ) {
                    val myIntent = Intent(this@MainActivity, SearchResultActivity::class.java)
                    myIntent.putExtra("searchType", radioButtonSelected!!.text.toString()) //Optional parameters
                    myIntent.putExtra("mode", mode) //Optional parameters
                    myIntent.putExtra("itemName", myIngredientsList.get(searchView.selectedItemPosition-1).nilai1.toString()) //Optional parameters
                    this@MainActivity.startActivity(myIntent)
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

        showLoading()
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
            hideLoading()
        },Response.ErrorListener {
            Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
            hideLoading()
        })

        showLoading()
        fetchOrSetIngredients(Response.Listener {

            searchView.setTitleList("Select Ingredient") //use this to create initial search first

            var id = 0
            for (s in it) {
                myIngredientsList.add(mListString(id, s.strIngredient1))
                id++
            }

            searchView.setAdapter(myIngredientsList, 2, 1) // type spinner 1-4, searh option 1-4
            hideLoading()
        },Response.ErrorListener {
            Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
            hideLoading()
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

    fun showLoading(){
        if(!loading.isShown)
            loading.visibility = View.VISIBLE
    }

    fun hideLoading(){
        if(loading.isShown)
            loading.visibility = View.GONE
    }
}
