package com.mycocktails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mycocktails.data.model.SearchResultModel

class SearchResultActivity : AppCompatActivity() {

    val recyclerview by lazy { findViewById<RecyclerView>(R.id.recyclerview) }
    private var searchResultAdapter: SearchResultAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_result)
        getSupportActionBar()!!.setTitle("Category Search: cat");

        searchResultAdapter = SearchResultAdapter(ArrayList(), this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = searchResultAdapter
        (searchResultAdapter as SearchResultAdapter).notifyDataSetChanged()


    }

    override fun onResume() {
        super.onResume()
        searchItems()
    }

    fun searchItems(){
        var list = ArrayList<SearchResultModel>()
        list.add(SearchResultModel("Drink 1","https://www.thecocktaildb.com/images/media/drink/rrtssw1472668972.jpg","1","cat1"))
        list.add(SearchResultModel("Drink 2","https://www.thecocktaildb.com/images/media/drink/rrtssw1472668972.jpg","2","cat2"))
        list.add(SearchResultModel("Drink 3","https://www.thecocktaildb.com/images/media/drink/rrtssw1472668972.jpg","3","cat3"))
        list.add(SearchResultModel("Drink 4","https://www.thecocktaildb.com/images/media/drink/rrtssw1472668972.jpg","4","cat4"))
        searchResultAdapter!!.swapList(list)
    }
}