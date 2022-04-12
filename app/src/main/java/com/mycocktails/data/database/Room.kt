package com.mycocktails.data.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.mycocktails.data.model.Category
import com.mycocktails.data.model.CocktailDetail
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

@Dao
interface CocktailDetailDao {

    @Query("select * from cocktaildetail")
    suspend fun getCocktails(): List<CocktailDetail>

    @Query("SELECT * FROM cocktaildetail  WHERE idDrink LIKE :search")
    fun getCocktailDetailbyId(search: String?): List<CocktailDetail>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCocktail(cocktailDetail: CocktailDetail)

    @Query("SELECT * FROM cocktaildetail WHERE ingredients LIKE '%' || :search || '%'")
    fun filterCocktailsByIngredients(search: String?): List<CocktailDetail>

    @Query("SELECT * FROM cocktaildetail WHERE strCategory LIKE :search")
    fun filterCocktailByCategory(search: String?): List<CocktailDetail>
}


// Database

private lateinit var INSTANCE: CocktailDatabase

@Database(entities = [Category::class,Ingredient::class,CocktailDetail::class], version = 3)
abstract class CocktailDatabase : RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val ingredientDao: IngredientDao
    abstract val cocktailDetailDao: CocktailDetailDao
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