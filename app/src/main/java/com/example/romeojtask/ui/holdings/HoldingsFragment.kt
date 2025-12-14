package com.example.romeojtask.ui.holdings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.romeojtask.databinding.FragmentHoldingsBinding
import com.example.romeojtask.ui.DividerItemDecoration
import com.example.romeojtask.viewmodel.HoldingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HoldingsFragment : Fragment() {

    private var _binding: FragmentHoldingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HoldingsViewModel
    private val holdingsAdapter = HoldingsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHoldingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewModel()
        setupSwipeToRefresh()
    }

    private fun setupRecyclerView() {
        binding.holdingsRecyclerView.adapter = holdingsAdapter
        binding.holdingsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.holdingsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext()))
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(HoldingsViewModel::class.java)

        lifecycleScope.launch {
            viewModel.holdingsStream.collectLatest {
                holdingsAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            holdingsAdapter.loadStateFlow.collectLatest {
                binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
            }
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            holdingsAdapter.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}