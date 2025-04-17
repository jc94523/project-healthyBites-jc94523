package com.example.healthybites.ui

import android.R
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.healthybites.Diet
import com.example.healthybites.MainViewModel
import com.example.healthybites.MainViewModel.Companion.KEY_NUTRIENTS
import com.example.healthybites.Recipe
import com.example.healthybites.RecipeFilter
import com.example.healthybites.databinding.DialogAddDietBinding
import com.example.healthybites.databinding.ItemDietCardBinding
import com.example.healthybites.databinding.ItemFoodBinding

class DietCardAdapter(
    private val dietCards: List<DietCard>,
    val viewModel: MainViewModel,
    val activity: FragmentActivity
) :
    RecyclerView.Adapter<DietCardAdapter.DietCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietCardViewHolder {
        val binding =
            ItemDietCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DietCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietCardViewHolder, position: Int) {
        val dietCard = dietCards[position]
        holder.bind(dietCard)
    }

    data class DietCard(
        val icon: Int,
        val title: String,
        val type: String,
        val items: MutableList<Diet>
    )

    fun updateDietCards(index: Int, diets: List<Diet>) {
        dietCards[index].items.clear()
        dietCards[index].items.addAll(diets)
        notifyItemChanged(index)
    }

    /**
     * show add diet dialog
     */
    fun showAddDietDialog(binding: ItemDietCardBinding, dietCard: DietCard) {
        val dialogBinding =
            DialogAddDietBinding.inflate(LayoutInflater.from(binding.root.context))
        //show auto complete list of recipes
        viewModel.observeAutoRecipes().observe(activity) { recipes ->
            val suggestions = recipes.map { it.title }.toTypedArray()
            val adapter = ArrayAdapter(
                dialogBinding.root.context,
                R.layout.simple_dropdown_item_1line,
                suggestions
            )
            dialogBinding.editDietName.setAdapter(adapter)
            if (dialogBinding.editDietName.text.isNotEmpty()) {
                dialogBinding.editDietName.showDropDown()
            }
        }
        var selectedRecipe: Recipe? = null
        dialogBinding.editDietName.setOnItemClickListener { _, _, position, _ ->
            // Handle item click
            selectedRecipe = viewModel.observeAutoRecipes().value?.get(position)
        }

        dialogBinding.btnSearchFood.setOnClickListener {
            val inputRecipe = dialogBinding.editDietName.text.toString()
            if (dialogBinding.editDietName.text.isNotEmpty()) {
                //search recipes by user input
                val filter = RecipeFilter(query = inputRecipe, number = 8)
                viewModel.autoCompleteRecipes(filter)
            }
        }
        // Handle add click
        val builder = AlertDialog.Builder(binding.root.context)
            .setView(dialogBinding.root)
            .setTitle("Add diet to ${dietCard.title.lowercase()}")
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null).setCancelable(false)
        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                // Handle add action
                if (!dialogBinding.editDietWeight.text.isEmpty()) {
                    val weight = dialogBinding.editDietWeight.text.toString().toDouble()
                    if (selectedRecipe != null) {
                        val diet = Diet(
                            name = selectedRecipe!!.title,
                            type = dietCard.type,
                            weight = weight,
                            calories = selectedRecipe!!.nutrition[KEY_NUTRIENTS]!![0].amount,
                            protein = selectedRecipe!!.nutrition[KEY_NUTRIENTS]!![1].amount,
                            fat = selectedRecipe!!.nutrition[KEY_NUTRIENTS]!![2].amount,
                            carbohydrate = selectedRecipe!!.nutrition[KEY_NUTRIENTS]!![3].amount
                        )
                        // add diet to view model
                        viewModel.addDiet(diet)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            "Please search a recipe first!", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "Please enter weight!", Toast.LENGTH_SHORT
                    ).show()
                    // Do not dismiss the dialog
                }
            }
            val negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun getItemCount(): Int = dietCards.size

    inner class DietCardViewHolder(private val binding: ItemDietCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val selectedItems = mutableListOf<Diet>()

        fun bind(dietCard: DietCard) {
            binding.iconImageView.setImageResource(dietCard.icon)
            binding.titleTextView.text = dietCard.title
            // Handle add click
            binding.addButton.setOnClickListener {
                showAddDietDialog(binding, dietCard)
            }
            binding.deleteButton.setOnClickListener {
                // Handle delete click
                AlertDialog.Builder(binding.root.context)
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes") { _, _ ->
                        // Handle delete action
                        for (item in selectedItems) {
                            viewModel.removeDietByID(item.id)
                        }
                        selectedItems.clear()
                        binding.deleteButton.isEnabled = false
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            }

            binding.deleteButton.isEnabled = selectedItems.isNotEmpty()
            for (item in dietCard.items) {
                val tableRow = TableRow(binding.root.context)

                val itemBinding =
                    ItemFoodBinding.inflate(
                        LayoutInflater.from(binding.root.context),
                        tableRow,
                        false
                    )
                val layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                itemBinding.root.layoutParams = layoutParams

                itemBinding.foodTextView.text = item.name
                itemBinding.weightTextView.text = item.weight.toString()
                itemBinding.caloriesTextView.text = item.calories.toString()

                itemBinding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedItems.add(item)
                    } else {
                        selectedItems.remove(item)
                    }
                    binding.deleteButton.isEnabled = selectedItems.isNotEmpty()
                }
                tableRow.addView(itemBinding.root)
                binding.foodListTableLayout.addView(tableRow)
            }
        }
    }
}