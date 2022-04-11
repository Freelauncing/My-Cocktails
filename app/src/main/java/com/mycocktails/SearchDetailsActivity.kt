package com.mycocktails

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.mycocktails.data.model.Category
import com.mycocktails.data.model.CocktailDetail
import com.mycocktails.data.model.SearchResultModel
import com.mycocktails.data.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text


class SearchDetailsActivity : AppCompatActivity() {

    val itemName by lazy { findViewById<TextView>(R.id.itemName) }
    val alcoholic by lazy { findViewById<TextView>(R.id.alcoholic) }
    val servedIn by lazy { findViewById<TextView>(R.id.servedIn) }
    val category by lazy { findViewById<TextView>(R.id.category) }
    val preperations by lazy { findViewById<TextView>(R.id.preperations) }
    val ingredients by lazy { findViewById<TextView>(R.id.ingredients) }
    val realimage by lazy { findViewById<ImageView>(R.id.realimage) }


    val addToDatabaseBtn by lazy { findViewById<Button>(R.id.addToDatabase) }
    val scoreitBtn by lazy { findViewById<Button>(R.id.scoreit) }

    val network = Network.getInstance(this@SearchDetailsActivity)
    val queue by lazy{ Volley.newRequestQueue(this@SearchDetailsActivity)}

    private var itemId:String = ""

    private var myCocktailDetailList:ArrayList<CocktailDetail> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_search_details)
        getSupportActionBar()?.hide()

        itemId = intent.getStringExtra("itemId").toString()

        addToDatabaseBtn.setOnClickListener {
            showDatabaseDialogue(this,layoutInflater)
        }

        scoreitBtn.setOnClickListener {
            showRatingDialogue(this,layoutInflater)
        }
    }

    override fun onResume() {
        super.onResume()

        fetchCocktailDetail(itemId, Response.Listener {
            myCocktailDetailList = it
            setDetails()
                                                      Log.v("Hello,",it.get(0).toString())
        }, Response.ErrorListener {
            Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
        })
    }

    private fun setDetails() {
        itemName.text = myCocktailDetailList.get(0).strDrink
        alcoholic.text = myCocktailDetailList.get(0).strAlcoholic
        servedIn.text = myCocktailDetailList.get(0).strGlass
        category.text = myCocktailDetailList.get(0).strCategory
        preperations.text = myCocktailDetailList.get(0).strInstructions
        ingredients.text = myCocktailDetailList.get(0).ingredients

        Glide.with(this)
            .load(myCocktailDetailList.get(0).strDrinkThumb)
            .centerCrop()
            .into(realimage);
    }

    private fun fetchCocktailDetail(itemId: String,
                                    listener: Response.Listener<ArrayList<CocktailDetail>>,
                                    errorListener: Response.ErrorListener) {
        GlobalScope.launch(Dispatchers.Main) {
            val categories = withContext(Dispatchers.IO) {
                //database.dao.getIngredient()
            }
            val request =  network.getFullCocktailDetail(itemId,Response.Listener {
                listener.onResponse(it)
            }, Response.ErrorListener {
                errorListener.onErrorResponse(it)
            })

            queue.add(request)
        }
    }

    private fun showRatingDialogue(context: Context, layoutInflater: LayoutInflater){
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_rating_dialogue, null)
        dialogBuilder.setView(dialogView)

        val btn_no = dialogView.findViewById<Button>(R.id.btn_no)
        val btn_yes = dialogView.findViewById<Button>(R.id.btn_yes)
        val ratingbar = dialogView.findViewById<RatingBar>(R.id.rating)
        val alertDialog = dialogBuilder.create()

        ratingbar.setOnRatingBarChangeListener(OnRatingBarChangeListener { ratingBar, rating, fromUser ->

            Toast.makeText(this@SearchDetailsActivity, "" + rating, Toast.LENGTH_SHORT).show()
        })

        btn_no.setOnClickListener {
            alertDialog.dismiss()
        }

        btn_yes.setOnClickListener {

            alertDialog.dismiss()

        }

        alertDialog.show()
    }

    private fun showDatabaseDialogue( context: Context, layoutInflater: LayoutInflater){
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_database_dialogue, null)
        dialogBuilder.setView(dialogView)

        val btn_yes = dialogView.findViewById<Button>(R.id.btn_yes)
        val alertDialog = dialogBuilder.create()

        btn_yes.setOnClickListener {

            alertDialog.dismiss()

        }

        alertDialog.show()
    }
}