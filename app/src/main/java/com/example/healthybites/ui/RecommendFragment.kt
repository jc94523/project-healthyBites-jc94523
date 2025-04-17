package com.example.healthybites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthybites.MainViewModel
import com.example.healthybites.databinding.FragmentRecommendBinding

class RecommendFragment : Fragment() {

    private var _binding: FragmentRecommendBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.recyclerViewRecommend.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val adapter = RecommendAdapter(mutableListOf(), viewModel)
        binding.recyclerViewRecommend.adapter = adapter
        // Observe the recommend recipes and update the adapter
        viewModel.observeRecommendRecipes().observe(viewLifecycleOwner) {
            adapter.updateRecommendList(it)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}