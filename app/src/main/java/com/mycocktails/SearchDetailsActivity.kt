package com.mycocktails

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.mycocktails.data.database.getDatabase
import com.mycocktails.data.model.Category
import com.mycocktails.data.model.CocktailDetail
import com.mycocktails.data.model.SearchResultModel
import com.mycocktails.data.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL


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

    val database by lazy { getDatabase(this@SearchDetailsActivity) }

    private var itemId:String = ""
    private var SearchType:String = ""

    private var myCocktailDetailList:ArrayList<CocktailDetail> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_search_details)
        getSupportActionBar()?.hide()

        itemId = intent.getStringExtra("itemId").toString()
        SearchType = intent.getStringExtra("searchType").toString()

        if(SearchType == "Local Search"){
            addToDatabaseBtn.isEnabled = false
            addToDatabaseBtn.isClickable = false
            addToDatabaseBtn.setTextColor(Color.parseColor("#FF000000"))
        }

        addToDatabaseBtn.setOnClickListener {
            storeIntoDatabase()
        }

        scoreitBtn.setOnClickListener {
            showRatingDialogue(this,layoutInflater)
        }
    }

    private fun storeIntoDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            val res = database.cocktailDetailDao.insertCocktail(
                CocktailDetail(
                    myCocktailDetailList.get(0).idDrink,
                    myCocktailDetailList.get(0).strDrink,
                    myCocktailDetailList.get(0).strAlcoholic,
                    myCocktailDetailList.get(0).strGlass,
                    myCocktailDetailList.get(0).strCategory,
                    myCocktailDetailList.get(0).strInstructions,
                    myCocktailDetailList.get(0).strDrinkThumb,
                    myCocktailDetailList.get(0).ingredients,
                    convertToByteArray(myCocktailDetailList.get(0).strDrinkThumb)
                )
            )
            launch(Dispatchers.Main) {
                showDatabaseDialogue(this@SearchDetailsActivity,layoutInflater)
            }
        }

    }

    suspend fun convertToByteArray(url:String): ByteArray {
        val baos = ByteArrayOutputStream()
        val bitmap = getImageBitmap(url)
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val photo: ByteArray = baos.toByteArray()
        return photo
    }

    suspend fun getImageBitmap(url:String): Bitmap? {
        try {
            var image: Bitmap? = null
            withContext(Dispatchers.IO) {
                val url = URL(url)
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            }
            Log.v("CHECK",url)
            Log.v("CHECK",image.toString())
            return image
        } catch (e: IOException) {
            System.out.println(e)
        }
        return null
    }

    override fun onResume() {
        super.onResume()

        if(SearchType == "Local Search"){
            GlobalScope.launch(Dispatchers.IO) {
                val res = database.cocktailDetailDao.getCocktailDetailbyId(itemId)
                launch(Dispatchers.Main) {
                    myCocktailDetailList = res as ArrayList<CocktailDetail>
                    setDetails()
                }
            }

        }else if(SearchType == "Inet Search"){
            fetchCocktailDetail(itemId, Response.Listener {
                myCocktailDetailList = it
                setDetails()
                Log.v("Hello,",it.get(0).toString())
            }, Response.ErrorListener {
                Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
            })
        }

    }

    private fun setDetails() {
        itemName.text = myCocktailDetailList.get(0).strDrink
        alcoholic.text = myCocktailDetailList.get(0).strAlcoholic
        servedIn.text = myCocktailDetailList.get(0).strGlass
        category.text = myCocktailDetailList.get(0).strCategory
        preperations.text = myCocktailDetailList.get(0).strInstructions
        ingredients.text = myCocktailDetailList.get(0).ingredients

        if(myCocktailDetailList.get(0).imageData!=null){
            val bmp = BitmapFactory.decodeByteArray(myCocktailDetailList.get(0).imageData, 0, myCocktailDetailList.get(0).imageData!!.size)
            realimage.setImageBitmap(bmp)
        }
        else {
            Glide
                .with(this)
                .load(myCocktailDetailList.get(0).strDrinkThumb)
                .centerCrop()
                .into(realimage);
        }
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