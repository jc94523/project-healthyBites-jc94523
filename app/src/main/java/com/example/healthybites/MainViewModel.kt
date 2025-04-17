package com.example.healthybites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthybites.spoonacular.SpoonacularApi
import com.example.healthybites.spoonacular.SpoonacularRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import edu.utap.photolist.ViewModelDBHelper
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    companion object {
        const val TYPE_BREAKFAST = "Breakfast"
        const val TYPE_LUNCH = "Lunch"
        const val TYPE_DINNER = "Dinner"

        const val KEY_PROTEIN = "Protein"
        const val KEY_FAT = "Fat"
        const val KEY_CARBO = "Carbohydrates"
        const val KEY_CALORIES = "Calories"
        const val KEY_NUTRIENTS = "nutrients"

        //Spoonacular api key
        const val API_KEY = "8c311f82aff946f590441faa8830f3ed"
//        const val API_KEY = "66245de305c148b68493a34bf299c69e"
    }

    private val dietList = MutableLiveData<List<Diet>>(mutableListOf())

    private val api = SpoonacularApi.create("https://api.spoonacular.com")
    private val repository = SpoonacularRepository(api)

    //id index
    private var idIndex = 0

    //breakfast
    private val breakfastItems = MutableLiveData<List<Diet>>(mutableListOf())

    //lunch
    private val lunchItems = MutableLiveData<List<Diet>>(mutableListOf())

    //dinner
    private val dinnerItems = MutableLiveData<List<Diet>>(mutableListOf())

    private var email = MutableLiveData("Uninitialized")
    private val dbHelp = ViewModelDBHelper()

    fun observeEmail(): LiveData<String> {
        return email
    }

    fun updateUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            email.postValue(user.email)
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        userLogout()
    }

    private fun userLogout() {
        email.postValue("No email, no active user")
    }

    fun observeBreakfastItems(): LiveData<List<Diet>> {
        return breakfastItems
    }

    fun observeLunchItems(): LiveData<List<Diet>> {
        return lunchItems
    }

    fun observeDinnerItems(): LiveData<List<Diet>> {
        return dinnerItems
    }

    private fun calculateNutritionStats(dietList: List<Diet>): Map<String, Double> {
        return mapOf(
            KEY_PROTEIN to dietList.sumOf { it.protein },
            KEY_FAT to dietList.sumOf { it.fat },
            KEY_CARBO to dietList.sumOf { it.carbohydrate },
            KEY_CALORIES to dietList.sumOf { it.calories }
        )
    }

    //stats nutrition
    private val nutritionStats = MutableLiveData(calculateNutritionStats(dietList.value!!))

    // observe nutrition stats
    fun observeNutritionStats(): LiveData<Map<String, Double>> {
        return nutritionStats
    }

    //recommend recipes list
    private val recommendRecipes = MutableLiveData<List<Recipe>>(mutableListOf())

    // observe recommend recipes
    fun observeRecommendRecipes(): LiveData<List<Recipe>> {
        return recommendRecipes
    }

    // auto complete recipes
    private val autoRecipes = MutableLiveData<List<Recipe>>()

    // observe auto complete recipes
    fun observeAutoRecipes(): LiveData<List<Recipe>> {
        return autoRecipes
    }

    // Add a new diet item to the list
    fun addDiet(diet: Diet) {
        diet.id = idIndex
        idIndex += 1
        //add diet to diet list
        val list = dietList.value as MutableList<Diet>
        list.add(0, diet)
        dietList.postValue(list)
        // Update the corresponding diet list based on the diet type
        when (diet.type) {
            TYPE_BREAKFAST -> {
                val list1 = breakfastItems.value as MutableList<Diet>
                list1.add(0, diet)
                breakfastItems.postValue(list1)
            }

            TYPE_LUNCH -> {
                val list1 = lunchItems.value as MutableList<Diet>
                list1.add(0, diet)
                lunchItems.postValue(list1)
            }

            TYPE_DINNER -> {
                val list1 = dinnerItems.value as MutableList<Diet>
                list1.add(0, diet)
                dinnerItems.postValue(list1)
            }
        }
        nutritionStats.postValue(calculateNutritionStats(dietList.value!!))
    }

    // Remove a diet item from the list
    fun removeDietByID(id: Int) {
        val currentList = dietList.value!!.toMutableList() ?: return
        // find the diet item to remove
        val dietToRemove = currentList.find { it.id == id }
        if (dietToRemove != null) {
            currentList.remove(dietToRemove)
            // remove the diet item from the corresponding diet list
            when (dietToRemove.type) {
                TYPE_BREAKFAST -> {
                    val list1 = breakfastItems.value as MutableList<Diet>
                    list1.remove(dietToRemove)
                    breakfastItems.postValue(list1)
                }

                TYPE_LUNCH -> {
                    val list1 = lunchItems.value as MutableList<Diet>
                    list1.remove(dietToRemove)
                    lunchItems.postValue(list1)
                }

                TYPE_DINNER -> {
                    val list1 = dinnerItems.value as MutableList<Diet>
                    list1.remove(dietToRemove)
                    dinnerItems.postValue(list1)
                }
            }
        }
    }


    /**
     * fetch recommend recipes from web api
     */
    fun fetchRecipes(filter: RecipeFilter) {
        viewModelScope.launch {
            val response = repository.getRecipes(API_KEY, filter)
            if (response.isSuccess) {
                val recipeSearchResponse = response.getOrNull()
                if (recipeSearchResponse != null) {
                    recommendRecipes.postValue(recipeSearchResponse.results)
                }
                Log.e("TAG", "fetchRecipes: " + Gson().toJson(recipeSearchResponse))
            }
        }
    }

    /**
     * autocomplete recipes
     */
    fun autoCompleteRecipes(filter: RecipeFilter) {
        autoRecipes.value = emptyList()
        viewModelScope.launch {
            val response = repository.getRecipes(API_KEY, filter)
            // get response
            if (response.isSuccess) {
                val autoRecipesResponse = response.getOrNull()
                if (autoRecipesResponse != null) {
                    autoRecipes.postValue(autoRecipesResponse.results)
                }
                Log.e("TAG", "autoCompleteRecipes: " + Gson().toJson(autoRecipesResponse))
            }
        }
    }
}