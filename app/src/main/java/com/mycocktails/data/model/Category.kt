package com.mycocktails.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Category")
data class Category (
    @PrimaryKey
    @ColumnInfo(name = "strCategory")
    var strCategory:String
)
