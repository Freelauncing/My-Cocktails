package com.mycocktails

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mycocktails.data.model.SearchResultModel

class SearchResultAdapter (
    val reverImageList: ArrayList<SearchResultModel>,
    context: Context
) :
    RecyclerView.Adapter<SearchResultAdapter.MyViewHolder>() {

    var cxt: Context

    init {
        cxt = context
    }

    fun swapList(mreverImageList: ArrayList<SearchResultModel>){
        reverImageList.clear()
        Log.v("CHEKOO=>", mreverImageList.size.toString())
        reverImageList.addAll(mreverImageList)
        Log.v("CHEKOO=>", reverImageList.size.toString())
        this.notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reverImageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = reverImageList.get(position)

        holder.showDetialsButton.setOnClickListener {
            val myIntent = Intent(cxt, SearchDetailsActivity::class.java)
            myIntent.putExtra("itemId", currentItem.idDrink) //Optional parameters
            myIntent.putExtra("mode", currentItem.mode) //Optional parameters
            myIntent.putExtra("itemName", currentItem.strDrink) //Optional parameters
            if(currentItem.imageData!=null){
                myIntent.putExtra("searchType", "Local Search") //Optional parameters
            }else{
                myIntent.putExtra("searchType", "Inet Search") //Optional parameters
            }
            cxt.startActivity(myIntent)
        }

        holder.category.text = currentItem.category
        holder.itemName.text = currentItem.strDrink

        if(currentItem.imageData!=null){
            val bmp = BitmapFactory.decodeByteArray(currentItem.imageData, 0, currentItem.imageData!!.size)
            holder.realimage.setImageBitmap(bmp)
        }
        else {
            Glide
                .with(cxt)
                .load(currentItem.strDrinkThumb)
                .centerCrop()
                .into(holder.realimage);
        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var realimage: ImageView
        var itemName: TextView
        var category: TextView
        var showDetialsButton:ImageButton

        init {
            realimage = itemView.findViewById(R.id.realimage)
            itemName = itemView.findViewById(R.id.itemName)
            category = itemView.findViewById(R.id.category)
            showDetialsButton = itemView.findViewById(R.id.showDetialsButton)
        }
    }


}