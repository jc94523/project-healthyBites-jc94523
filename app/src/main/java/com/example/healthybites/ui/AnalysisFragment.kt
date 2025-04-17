package com.example.healthybites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.healthybites.MainViewModel
import com.example.healthybites.databinding.FragmentAnalysisBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.Locale

class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        // chart setting
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        binding.pieChart.dragDecelerationFrictionCoef = 0.95f
        binding.pieChart.isDrawHoleEnabled = true
        binding.pieChart.setHoleColor(android.graphics.Color.TRANSPARENT)
        binding.pieChart.holeRadius = 58f
        binding.pieChart.transparentCircleRadius = 61f
        binding.pieChart.setDrawCenterText(true)
        binding.pieChart.rotationAngle = 0f
        binding.pieChart.isRotationEnabled = true
        binding.pieChart.isHighlightPerTapEnabled = true
        binding.pieChart.setEntryLabelColor(android.graphics.Color.WHITE)
        binding.pieChart.setEntryLabelTextSize(12f)
        viewModel.observeNutritionStats().observe(viewLifecycleOwner) { nutritionStats ->
            val protein = nutritionStats[MainViewModel.KEY_PROTEIN]!!
            val fat = nutritionStats[MainViewModel.KEY_FAT]!!
            val carbo = nutritionStats[MainViewModel.KEY_CARBO]!!
            val calories = nutritionStats[MainViewModel.KEY_CALORIES]!!
            val total = protein + fat + carbo + 0.0001
            val pieChartData = listOf(
                Pair("Protein", (protein / total).toFloat() * 100f),
                Pair("Fats", (fat / total).toFloat() * 100f),
                Pair("Carbs", (carbo / total).toFloat() * 100f)
            )
            val entries = pieChartData.map { PieEntry(it.second, it.first) }
            val dataSet = PieDataSet(entries, "")
            dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
            dataSet.valueTextColor = android.graphics.Color.WHITE
            dataSet.valueTextSize = 16f
            val pieData = PieData(dataSet)
            binding.pieChart.data = pieData

            // Update the text views with the calculated values
            binding.rowProtein.text = String.format(Locale.US, "%.2f", protein)
            binding.rowFat.text = String.format(Locale.US, "%.2f", fat)
            binding.rowCarbohydrate.text = String.format(Locale.US, "%.2f", carbo)
            binding.rowCalories.text = String.format(Locale.US, "%.2f", calories)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}