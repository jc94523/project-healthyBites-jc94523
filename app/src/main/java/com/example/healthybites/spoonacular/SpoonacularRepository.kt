package com.example.healthybites.spoonacular

import android.util.Log
import com.example.healthybites.AutoRecipe
import com.example.healthybites.RecipeFilter
import com.example.healthybites.RecipeSearchResponse
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SpoonacularRepository(private val spoonacularApi: SpoonacularApi) {

    /**
     * get meal plan
     */
    suspend fun getRecipes(appKey: String, filter: RecipeFilter): Result<RecipeSearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    spoonacularApi.getRecipes(
                        appKey,
                        query = filter.query,
                        minFat = filter.minFat,
                        maxFat = filter.maxFat,
                        minProtein = filter.minProtein,
                        maxProtein = filter.maxProtein,
                        minCarbs = filter.minCarbs,
                        maxCarbs = filter.maxCarbs,
                        minCalories = filter.minCalories,
                        maxCalories = filter.maxCalories,
                        includeNutrition = filter.includeNutrition,
                        number = filter.number,
                        offset = filter.offset
                    )
                Result.success(response)
            } catch (e: HttpException) {
                Log.e("TAG", "getRecipes: $e")
                Result.failure(e)
            } catch (e: JsonSyntaxException) {
                Log.e("TAG", "getRecipes: $e")
                Result.failure(e)
            }
        }
    }

    /**
     * autocomplete recipes
     */
    suspend fun autoCompleteRecipes(
        appKey: String,
        query: String
    ): Result<List<AutoRecipe>> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    spoonacularApi.autoCompleteRecipes(appKey, query, number = 5)
                Result.success(response)
            } catch (e: HttpException) {
                Log.e("TAG", "getRecipes: $e")
                Result.failure(e)
            } catch (e: JsonSyntaxException) {
                Log.e("TAG", "getRecipes: $e")
                Result.failure(e)
            }
        }
    }
}
