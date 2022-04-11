package com.mycocktails.data.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mycocktails.Utils
import com.mycocktails.data.model.Category
import com.mycocktails.data.model.Ingredient
import com.mycocktails.data.model.SearchResultModel
import org.json.JSONObject


class Network private constructor (context: Context) {

    companion object: SingletonHolder<Network, Context>(::Network)



    fun getCategories(
        listener: Response.Listener<ArrayList<Category>>,
        errorListener: Response.ErrorListener
    ): StringRequest {
        val stringRequest = StringRequest(
            Request.Method.GET, Utils.CATEGORIES_LIST_URL,
            Response.Listener { response ->
                val obj = JSONObject(response)
                val list = obj.getJSONArray("drinks")
                var newList = ArrayList<Category>()
                for(i in 0 until list.length()){
                    newList.add(
                        Category( JSONObject(list.get(i).toString()).getString("strCategory")
                        )
                    )
                }
                listener.onResponse(newList)
                Log.v("Hello","Response is: ${response}")
            },
            Response.ErrorListener {
                errorListener.onErrorResponse(it)
                Log.v("Hello", "That didn't work!")
            })

        return stringRequest
    }

    fun getIngredients(
        listener: Response.Listener<ArrayList<Ingredient>>,
        errorListener: Response.ErrorListener
    ): StringRequest {
        val stringRequest = StringRequest(
            Request.Method.GET, Utils.INGREDIENTS_LIST_URL,
            Response.Listener { response ->
                val obj = JSONObject(response)
                val list = obj.getJSONArray("drinks")
                var newList = ArrayList<Ingredient>()
                for(i in 0 until list.length()){
                    newList.add(
                        Ingredient( JSONObject(list.get(i).toString()).getString("strIngredient1")
                        )
                    )
                }
                listener.onResponse(newList)
                Log.v("Hello","Response is: ${response}")
            },
            Response.ErrorListener {
                errorListener.onErrorResponse(it)
                Log.v("Hello", "That didn't work!")
            })

        return stringRequest
    }

    fun getCategoryFilterList(
        itemName:String,
        listener: Response.Listener<ArrayList<SearchResultModel>>,
        errorListener: Response.ErrorListener
    ): StringRequest {
        val stringRequest = StringRequest(
            Request.Method.GET, Utils.CATEGORIES_FILTER_URL+itemName,
            Response.Listener { response ->
                val obj = JSONObject(response)
                val list = obj.getJSONArray("drinks")
                var newList = ArrayList<SearchResultModel>()
                for(i in 0 until list.length()){
                    newList.add(
                        SearchResultModel(
                            JSONObject(list.get(i).toString()).getString("strDrink"),
                            JSONObject(list.get(i).toString()).getString("strDrinkThumb"),
                            JSONObject(list.get(i).toString()).getString("idDrink"),
                            itemName
                        )
                    )
                }
                listener.onResponse(newList)
                Log.v("Hello","Response is: ${response}")
            },
            Response.ErrorListener {
                errorListener.onErrorResponse(it)
                Log.v("Hello", "That didn't work!")
            })

        return stringRequest
    }

    fun getIngredientFilterList(
        itemName:String,
        listener: Response.Listener<ArrayList<SearchResultModel>>,
        errorListener: Response.ErrorListener
    ): StringRequest {
        val stringRequest = StringRequest(
            Request.Method.GET, Utils.INGREDIENTS_FILTER_URL+itemName,
            Response.Listener { response ->
                val obj = JSONObject(response)
                val list = obj.getJSONArray("drinks")
                var newList = ArrayList<SearchResultModel>()
                for(i in 0 until list.length()){
                    newList.add(
                        SearchResultModel(
                            JSONObject(list.get(i).toString()).getString("strDrink"),
                            JSONObject(list.get(i).toString()).getString("strDrinkThumb"),
                            JSONObject(list.get(i).toString()).getString("idDrink"),
                            itemName
                        )
                    )
                }
                listener.onResponse(newList)
                Log.v("Hello","Response is: ${response}")
            },
            Response.ErrorListener {
                errorListener.onErrorResponse(it)
                Log.v("Hello", "That didn't work!")
            })

        return stringRequest
    }
}

open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null
    fun getInstance(arg: A): T = instance ?: synchronized(this) { instance ?:
        constructor(arg).also{ instance = it }
    }

}