package com.mycocktails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.mycocktails.data.model.SearchResultModel
import com.mycocktails.data.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResultActivity : AppCompatActivity() {

    val recyclerview by lazy { findViewById<RecyclerView>(R.id.recyclerview) }
    private var searchResultAdapter: SearchResultAdapter? = null

    val network = Network.getInstance(this@SearchResultActivity)
    val queue by lazy{ Volley.newRequestQueue(this@SearchResultActivity)}

    private var Mode:String = ""
    private var SearchType:String = ""
    private var itemName:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_result)

        Mode = intent.getStringExtra("mode").toString()
        SearchType = intent.getStringExtra("searchType").toString()
        itemName = intent.getStringExtra("itemName").toString()

        getSupportActionBar()!!.setTitle("Category Search: "+itemName);

        searchResultAdapter = SearchResultAdapter(ArrayList(), this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = searchResultAdapter
        (searchResultAdapter as SearchResultAdapter).notifyDataSetChanged()


    }

    override fun onResume() {
        super.onResume()

        if(Mode == "category"){
            fetchCategoryList(itemName,Mode,Response.Listener {
                searchResultAdapter!!.swapList(it)
            },Response.ErrorListener {
                Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
            })
        }else if(Mode == "ingredient"){
            fetchIngredientList(itemName,Mode,Response.Listener {
                searchResultAdapter!!.swapList(it)
            },Response.ErrorListener {
                Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
            })
        }
    }

    fun fetchCategoryList(
        itemName: String,
        Mode: String,
        listener: Response.Listener<ArrayList<SearchResultModel>>,
        errorListener: Response.ErrorListener
    ) {
        // Launch a coroutine
        GlobalScope.launch(Dispatchers.Main) {
            val categories = withContext(Dispatchers.IO) {
                //database.dao.getIngredient()
            }
            val request =  network.getCategoryFilterList(itemName,Mode,Response.Listener {
                listener.onResponse(it)
            }, Response.ErrorListener {
                errorListener.onErrorResponse(it)
            })

            queue.add(request)
        }
    }

    fun fetchIngredientList(
        itemName: String,
        Mode: String,
        listener: Response.Listener<ArrayList<SearchResultModel>>,
        errorListener: Response.ErrorListener
    ) {
        // Launch a coroutine
        GlobalScope.launch(Dispatchers.Main) {
            val categories = withContext(Dispatchers.IO) {
                //database.dao.getIngredient()
            }
            val request =  network.getIngredientFilterList(itemName,Mode, Response.Listener {
                listener.onResponse(it)
            }, Response.ErrorListener {
                errorListener.onErrorResponse(it)
            })

            queue.add(request)
        }
    }

}