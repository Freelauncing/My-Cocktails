package com.mycocktails.data.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.mycocktails.data.model.Category
import com.mycocktails.data.model.Ingredient

@Dao
interface CategoryDao {

    @Query("select * from category")
    suspend fun getCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)
}

@Dao
interface IngredientDao {

    @Query("select * from ingredient")
    suspend fun getIngredient(): List<Ingredient>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient)
}


// Database

private lateinit var INSTANCE: CocktailDatabase

@Database(entities = [Category::class,Ingredient::class], version = 2)
abstract class CocktailDatabase : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val ingredientDao: IngredientDao
}


fun getDatabase(context: Context): CocktailDatabase {
    synchronized(CocktailDatabase::class.java){
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                CocktailDatabase::class.java,
                "products")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}