package com.example.healthybites.spoonacular

import com.example.healthybites.AutoRecipe
import com.example.healthybites.RecipeSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Define the API interface for spoonacular
interface SpoonacularApi {
    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,
        @Query("includeNutrition") includeNutrition: Boolean,
        @Query("minProtein") minProtein: Int,
        @Query("maxProtein") maxProtein: Int,
        @Query("minFat") minFat: Int,
        @Query("maxFat") maxFat: Int,
        @Query("minCarbs") minCarbs: Int,
        @Query("maxCarbs") maxCarbs: Int,
        @Query("minCalories") minCalories: Int,
        @Query("maxCalories") maxCalories: Int,
        @Query("number") number: Int,
        @Query("offset") offset: Int,
    ): RecipeSearchResponse

    @GET("/recipes/autocomplete")
    suspend fun autoCompleteRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,
        @Query("number") number: Int
    ): List<AutoRecipe>

    companion object {
        fun create(httpUrl: String): SpoonacularApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SpoonacularApi::class.java)
        }
    }
}