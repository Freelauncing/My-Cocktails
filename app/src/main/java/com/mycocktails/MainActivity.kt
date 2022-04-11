package com.mycocktails

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat


class MainActivity : AppCompatActivity() {

    val categoryBtn by lazy { findViewById<Button>(R.id.button5) }
    val ingredientBtn by lazy { findViewById<Button>(R.id.button6) }
    val perfromSearch by lazy { findViewById<Button>(R.id.perfromSearch) }
    val categoryLayout by lazy { findViewById<LinearLayoutCompat>(R.id.categoryLayout) }
    val ingredientsLayout by lazy { findViewById<LinearLayoutCompat>(R.id.ingredientsLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        categoryBtn.setOnClickListener {
            categoryLayout.visibility = View.VISIBLE
            ingredientsLayout.visibility = View.GONE
        }
        ingredientBtn.setOnClickListener {
            ingredientsLayout.visibility = View.VISIBLE
            categoryLayout.visibility = View.GONE
        }

        perfromSearch.setOnClickListener {
            val myIntent = Intent(this@MainActivity, SearchResultActivity::class.java)
            myIntent.putExtra("key", "value") //Optional parameters
            this@MainActivity.startActivity(myIntent)
        }
    }
}