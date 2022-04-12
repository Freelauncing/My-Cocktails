package com.mycocktails

class Utils {
    companion object{
        val CATEGORIES_LIST_URL = "https://www.thecocktaildb.com/api/json/v1/1/list.php?c=list"
        val INGREDIENTS_LIST_URL = "https://www.thecocktaildb.com/api/json/v1/1/list.php?i=list"
        val CATEGORIES_FILTER_URL = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?c="
        val INGREDIENTS_FILTER_URL = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?i="
        val COCKTAIL_DETAILS_URL = "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i="
        val LOCAL_SEARCH = "Local Search"
        val INET_SEARCH = "Inet Search"
    }
}