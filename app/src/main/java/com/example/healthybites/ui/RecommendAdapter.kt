package com.example.healthybites.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.healthybites.Diet
import com.example.healthybites.MainViewModel
import com.example.healthybites.MainViewModel.Companion.KEY_NUTRIENTS
import com.example.healthybites.Recipe
import com.example.healthybites.databinding.DialogAddRecipeToDietBinding
import com.example.healthybites.databinding.ItemRecommendCardBinding
import java.util.Locale

class RecommendAdapter(
    private val recommendList: MutableList<Recipe>,
    val viewModel: MainViewModel
) :
    RecyclerView.Adapter<RecommendAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRecommendCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun updateRecommendList(recommendList: List<Recipe>) {
        this.recommendList.clear()
        this.recommendList.addAll(recommendList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recommend = recommendList[position]
        holder.bind(recommend, viewModel)
    }

    override fun getItemCount(): Int {
        return recommendList.size
    }

    class ViewHolder(private val binding: ItemRecommendCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recommend: Recipe, viewModel: MainViewModel) {
            Glide.with(itemView)
                .load(recommend.image)
                .into(binding.recipeImg)
            binding.recipeName.text = recommend.title
            val calories = recommend.nutrition[KEY_NUTRIENTS]!![0].amount
            val protein = recommend.nutrition[KEY_NUTRIENTS]!![1].amount
            val fat = recommend.nutrition[KEY_NUTRIENTS]!![2].amount
            val carbohydrate = recommend.nutrition[KEY_NUTRIENTS]!![3].amount
            binding.rowProtein.text = String.format(Locale.US, "%.2f", protein)
            binding.rowFat.text = String.format(Locale.US, "%.2f", fat)
            binding.rowCarbohydrate.text = String.format(Locale.US, "%.2f", carbohydrate)
            binding.rowCalories.text = String.format(Locale.US, "%.2f", calories)
            binding.addButton.setOnClickListener {
                // Create the AlertDialog object and return it
                val dialogBinding =
                    DialogAddRecipeToDietBinding.inflate(LayoutInflater.from(binding.root.context))
                dialogBinding.editName.setText(recommend.title)
                val dialog = AlertDialog.Builder(binding.root.context)
                    .setView(dialogBinding.root)
                    .setTitle("Add Recipe to Diet")
                    .setPositiveButton("Add", null)
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                // Set the positive button listener
                dialog.setOnShowListener {
                    val positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                    positiveButton.setOnClickListener {
                        val weight = dialogBinding.editWeight.text.toString()
                        // Check if the weight field is empty
                        if (weight.isEmpty()) {
                            Toast.makeText(
                                binding.root.context,
                                "Please enter weight!", Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Get the selected diet type
                            val type = if (dialogBinding.radioButtonBreakfast.isChecked) {
                                MainViewModel.TYPE_BREAKFAST
                            } else if (dialogBinding.radioButtonLunch.isChecked) {
                                MainViewModel.TYPE_LUNCH
                            } else {
                                MainViewModel.TYPE_DINNER
                            }
                            // Create a new diet object
                            val diet = Diet(
                                name = recommend.title,
                                weight = 100.0,
                                calories = calories,
                                protein = protein,
                                fat = fat,
                                carbohydrate = carbohydrate,
                                type = type
                            )
                            viewModel.addDiet(diet)
                            dialog.dismiss()
                        }
                    }
                }
                dialog.show()
            }
        }
    }
}