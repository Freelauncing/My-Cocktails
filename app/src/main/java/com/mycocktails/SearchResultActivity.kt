package com.mycocktails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.mycocktails.Utils.Companion.INET_SEARCH
import com.mycocktails.Utils.Companion.LOCAL_SEARCH
import com.mycocktails.Utils.Companion.MODE_CATEGORY
import com.mycocktails.Utils.Companion.MODE_INGREDIENT
import com.mycocktails.data.database.getDatabase
import com.mycocktails.data.model.CocktailDetail
import com.mycocktails.data.model.SearchResultModel
import com.mycocktails.data.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResultActivity : AppCompatActivity() {

    val loading by lazy { findViewById<ProgressBar>(R.id.loading) }
    val noResult by lazy { findViewById<TextView>(R.id.noResult) }
    val recyclerview by lazy { findViewById<RecyclerView>(R.id.recyclerview) }
    private var searchResultAdapter: SearchResultAdapter? = null

    val network = Network.getInstance(this@SearchResultActivity)
    val queue by lazy{ Volley.newRequestQueue(this@SearchResultActivity)}

    val database by lazy { getDatabase(this@SearchResultActivity) }

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

        showLoading()

        if(Mode == MODE_CATEGORY){
            if(SearchType == LOCAL_SEARCH){
                GlobalScope.launch(Dispatchers.IO) {
                    val res = database.cocktailDetailDao.filterCocktailByCategory(itemName)
                    var myResults:ArrayList<SearchResultModel> = ArrayList()
                    res.forEach {
                        myResults.add(
                            SearchResultModel(
                                it.strDrink,
                                it.strDrinkThumb,
                                it.idDrink,
                                it.strCategory,
                                MODE_CATEGORY,
                                it.imageData
                            )
                        )
                    }
                    launch(Dispatchers.Main) {
                        searchResultAdapter!!.swapList(myResults)
                        hideLoading()
                    }
                }

            }else if(SearchType == INET_SEARCH){
                fetchCategoryList(itemName,Mode,Response.Listener {
                    searchResultAdapter!!.swapList(it)
                    hideLoading()
                },Response.ErrorListener {
                    Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
                    hideLoading()
                })
            }
        }else if(Mode == MODE_INGREDIENT){
            if(SearchType == LOCAL_SEARCH){
                GlobalScope.launch(Dispatchers.IO) {
                    val res = database.cocktailDetailDao.filterCocktailsByIngredients(itemName)
                    var myResults:ArrayList<SearchResultModel> = ArrayList()
                    res.forEach {
                        myResults.add(
                            SearchResultModel(
                                it.strDrink,
                                it.strDrinkThumb,
                                it.idDrink,
                                it.strCategory,
                                MODE_INGREDIENT,
                                it.imageData
                            )
                        )
                    }
                    launch(Dispatchers.Main) {
                        searchResultAdapter!!.swapList(myResults)
                        hideLoading()
                    }
                }

            }else if(SearchType == INET_SEARCH) {
                fetchIngredientList(itemName, Mode, Response.Listener {
                    searchResultAdapter!!.swapList(it)
                    hideLoading()
                }, Response.ErrorListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                    hideLoading()
                })
            }
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
            val request =  network.getIngredientFilterList(itemName,Mode, Response.Listener {
                listener.onResponse(it)
            }, Response.ErrorListener {
                errorListener.onErrorResponse(it)
            })

            queue.add(request)
        }
    }

    fun showLoading(){
        if(!loading.isShown)
            loading.visibility = View.VISIBLE
    }

    fun hideLoading(){
        if(loading.isShown)
            loading.visibility = View.GONE

        if(searchResultAdapter!!.itemCount == 0){
            noResult.visibility = View.VISIBLE
        }

    }
}