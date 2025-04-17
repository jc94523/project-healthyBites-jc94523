package com.example.healthybites.spoonacular

import android.content.Context
import android.content.SharedPreferences
import com.example.healthybites.Diet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesHelper(context: Context, email: String) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("diet_data_$email", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_ID_INDEX = "id_index"
        private const val KEY_DIET_LIST = "diet_list"
    }

    fun saveIdIndex(idIndex: Int) {
        prefs.edit().putInt(KEY_ID_INDEX, idIndex).apply()
    }

    fun loadIdIndex(): Int {
        return prefs.getInt(KEY_ID_INDEX, 0)
    }

    fun saveDietList(dietList: List<Diet>) {
        val json = gson.toJson(dietList)
        prefs.edit().putString(KEY_DIET_LIST, json).apply()
    }

    fun loadDietList(): List<Diet> {
        val json = prefs.getString(KEY_DIET_LIST, null)
        val type = object : TypeToken<List<Diet>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}