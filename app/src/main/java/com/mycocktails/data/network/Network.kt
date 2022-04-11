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
import com.mycocktails.data.model.CocktailDetail
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
        mode:String,
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
                            itemName,
                            mode
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
        mode:String,
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
                            itemName,
                            mode
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

    fun getFullCocktailDetail(
        itemId:String,
        listener: Response.Listener<ArrayList<CocktailDetail>>,
        errorListener: Response.ErrorListener
    ): StringRequest {
        val stringRequest = StringRequest(
            Request.Method.GET, Utils.COCKTAIL_DETAILS_URL+itemId,
            Response.Listener { response ->
                val obj = JSONObject(response)
                val list = obj.getJSONArray("drinks")
                var newList = ArrayList<CocktailDetail>()
                for(i in 0 until list.length()){
                    newList.add(
                        CocktailDetail(
                            JSONObject(list.get(i).toString()).getString("idDrink"),
                            JSONObject(list.get(i).toString()).getString("strDrink"),
                            JSONObject(list.get(i).toString()).getString("strAlcoholic"),
                            JSONObject(list.get(i).toString()).getString("strGlass"),
                            JSONObject(list.get(i).toString()).getString("strCategory"),
                            JSONObject(list.get(i).toString()).getString("strInstructions"),
                            JSONObject(list.get(i).toString()).getString("strDrinkThumb"),
                            getCombineIngredients(JSONObject(list.get(i).toString()))
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

    fun getCombineIngredients(jsonObject: JSONObject):String{
        var result = ""

        for (i in 1 until 16){
            if(jsonObject.getString("strMeasure"+i).toString()!="null" || jsonObject.getString("strIngredient"+i).toString()!="null")
                result = result + jsonObject.getString("strMeasure"+i).toString() +"  "+ jsonObject.getString("strIngredient"+i).toString()+ " , "
        }

        return result
    }
}

open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null
    fun getInstance(arg: A): T = instance ?: synchronized(this) { instance ?:
        constructor(arg).also{ instance = it }
    }

}