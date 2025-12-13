package com.example.romeojtask.ui.holdings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.romeojtask.databinding.FragmentHoldingsBinding
import com.example.romeojtask.ui.DividerItemDecoration
import com.example.romeojtask.viewmodel.HoldingsViewModel

class HoldingsFragment : Fragment() {

    private var _binding: FragmentHoldingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HoldingsViewModel
    private lateinit var holdingsAdapter: HoldingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHoldingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        holdingsAdapter = HoldingsAdapter(emptyList())
        binding.holdingsRecyclerView.adapter = holdingsAdapter
        binding.holdingsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.holdingsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext()))

        viewModel = ViewModelProvider(this).get(HoldingsViewModel::class.java)

        viewModel.allHoldings.observe(viewLifecycleOwner) {
            holdingsAdapter.updateData(it)
        }

        viewModel.refreshHoldings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}