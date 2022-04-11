package com.mycocktails

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class SearchDetailsActivity : AppCompatActivity() {

    val addToDatabaseBtn by lazy { findViewById<Button>(R.id.addToDatabase) }
    val scoreitBtn by lazy { findViewById<Button>(R.id.scoreit) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_details)
        getSupportActionBar()?.hide()

        addToDatabaseBtn.setOnClickListener {
            showDatabaseDialogue(this,layoutInflater)
        }

        scoreitBtn.setOnClickListener {
            showRatingDialogue(this,layoutInflater)
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