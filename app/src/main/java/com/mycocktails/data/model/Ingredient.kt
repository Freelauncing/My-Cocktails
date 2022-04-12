package com.mycocktails.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Ingredient")
data class Ingredient (
    @PrimaryKey
    @ColumnInfo(name = "strIngredient1")
    var strIngredient1:String
    )