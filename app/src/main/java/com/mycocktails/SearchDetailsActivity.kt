package com.mycocktails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SearchDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_details)
        getSupportActionBar()?.hide()


    }
}