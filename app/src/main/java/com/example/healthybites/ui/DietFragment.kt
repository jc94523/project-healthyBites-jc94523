package com.example.healthybites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthybites.Diet
import com.example.healthybites.MainViewModel
import com.example.healthybites.R
import com.example.healthybites.databinding.FragmentDietBinding

class DietFragment : Fragment() {

    private var _binding: FragmentDietBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val dietCards = listOf(
            DietCardAdapter.DietCard(
                icon = R.drawable.ic_breakfast,
                title = "Breakfast",
                type = MainViewModel.TYPE_BREAKFAST,
                items = mutableListOf()
            ),
            DietCardAdapter.DietCard(
                icon = R.drawable.ic_lunch,
                title = "Lunch",
                type = MainViewModel.TYPE_LUNCH,
                items = mutableListOf()
            ),
            DietCardAdapter.DietCard(
                icon = R.drawable.ic_dinner,
                title = "Dinner",
                type = MainViewModel.TYPE_DINNER,
                items = mutableListOf()
            )
        )
        val dietCardAdapter = DietCardAdapter(dietCards, viewModel, requireActivity())
        binding.recyclerView.adapter = dietCardAdapter
        // Observe diets and update the adapter
        viewModel.observeBreakfastItems().observe(viewLifecycleOwner) { breakfastItems ->
            dietCardAdapter.updateDietCards(0, breakfastItems)
        }
        viewModel.observeLunchItems().observe(viewLifecycleOwner) { lunchItems: List<Diet> ->
            dietCardAdapter.updateDietCards(1, lunchItems)
        }
        viewModel.observeDinnerItems().observe(viewLifecycleOwner) { dinnerItems: List<Diet> ->
            dietCardAdapter.updateDietCards(2, dinnerItems)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}