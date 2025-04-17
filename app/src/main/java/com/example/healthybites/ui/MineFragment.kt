package com.example.healthybites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.healthybites.AuthActivity
import com.example.healthybites.MainViewModel
import com.example.healthybites.databinding.FragmentMineBinding


class MineFragment : Fragment() {
    private var _binding: FragmentMineBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMineBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.observeEmail().observe(viewLifecycleOwner) { email ->
            binding.tvEmail.text = email
        }
        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
            val intent = android.content.Intent(requireContext(), AuthActivity::class.java)
            requireActivity().startActivity(intent)
            requireActivity().finish()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}