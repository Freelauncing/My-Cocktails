package com.mycocktails.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CocktailDetail")
data class CocktailDetail(
    @PrimaryKey
    @ColumnInfo(name = "idDrink")
    var idDrink:String,
    @ColumnInfo(name = "strDrink")
    var strDrink:String,
    @ColumnInfo(name = "strAlcoholic")
    var strAlcoholic:String,
    @ColumnInfo(name = "strGlass")
    var strGlass:String,
    @ColumnInfo(name = "strCategory")
    var strCategory:String,
    // No need for Column Infor because no attribute on that API so which conflict to ROOM DB
    var strInstructions:String,
    @ColumnInfo(name = "strDrinkThumb")
    var strDrinkThumb:String,
    @ColumnInfo(name = "ingredients")
    var ingredients:String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var imageData:ByteArray? = null,
)
