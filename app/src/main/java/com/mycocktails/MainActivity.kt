package com.mycocktails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import java.nio.file.Files.find

class MainActivity : AppCompatActivity() {

    val categoryBtn by lazy { findViewById<Button>(R.id.button5) }
    val ingredientBtn by lazy { findViewById<Button>(R.id.button6) }
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
    }
}