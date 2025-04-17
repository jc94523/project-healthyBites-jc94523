package com.example.healthybites

import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

/**
 * define diet object
 */
data class Diet(
    var id: Int = -1,                // Unique identifier for the diet item
    var name: String,           // The name of the food item or meal
    var weight: Double,         // Weight of the food item in grams
    var calories: Double = 0.0,         // Caloric content of the food item
    var protein: Double = 0.0,        // Protein content in grams
    var fat: Double = 0.0,            // Fat content in grams
    var carbohydrate: Double = 0.0,   // Carbohydrate content in grams
    var createAt: LocalDate = LocalDate.now(),          // date
    var type: String = MainViewModel.TYPE_BREAKFAST,
    @DocumentId var firestoreID: String = "",
)

// Data class to hold nutrition information
data class NutritionItem(
    var name: String,
    var amount: Double,
    var unit: String
)

data class RecipeSearchResponse(
    val offset: Int,
    val number: Int,
    val results: List<Recipe>,
    val totalResults: Int
)

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val nutrition: Map<String, List<NutritionItem>>
)

data class RecipeFilter(
    val includeNutrition: Boolean = true,
    val minProtein: Int = 10,
    val maxProtein: Int = 10000,
    val minFat: Int = 0,
    val maxFat: Int = 5000,
    val minCarbs: Int = 0,
    val maxCarbs: Int = 5000,
    val minCalories: Int = 0,
    val maxCalories: Int = 5000,
    val number: Int = 50,
    val offset: Int = 0,
    var query: String = "pasta"
)

data class AutoRecipe(
    val id: Int,
    val title: String,
    val imageType: String
)