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
import com.example.romeojtask.data.utils.calculateOverallPnl
import com.example.romeojtask.data.utils.calculateOverallPnlPercentage
import com.example.romeojtask.data.utils.calculateTodaysTotalPnl
import com.example.romeojtask.data.utils.calculateTotalCurrentValue
import com.example.romeojtask.data.utils.calculateTotalInvestment
import com.example.romeojtask.databinding.FragmentHoldingsBinding
import com.example.romeojtask.ui.DividerItemDecoration
import com.example.romeojtask.ui.utils.setPnlTextColor
import com.example.romeojtask.ui.utils.toIndianCurrency
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
        setupSummaryView()
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

        viewModel.allHoldingsForSummary.observe(viewLifecycleOwner) { holdings ->
            if (holdings.isNullOrEmpty()) return@observe

            val totalCurrentValue = holdings.calculateTotalCurrentValue()
            val totalInvestment = holdings.calculateTotalInvestment()
            val todaysPnl = holdings.calculateTodaysTotalPnl()
            val totalPnl = calculateOverallPnl(totalCurrentValue, totalInvestment)
            val totalPnlPercentage = calculateOverallPnlPercentage(totalPnl, totalInvestment)

            binding.currentValueTotal.text = totalCurrentValue.toIndianCurrency()
            binding.totalInvestmentValue.text = totalInvestment.toIndianCurrency()
            binding.todaysPnlValue.text = todaysPnl.toIndianCurrency()
            binding.totalPnlValue.text = "${totalPnl.toIndianCurrency()} (${String.format("%.2f", totalPnlPercentage)}%)"

            // Set colors for P&L values
            binding.todaysPnlValue.setPnlTextColor(todaysPnl)
            binding.totalPnlValue.setPnlTextColor(totalPnl)
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            holdingsAdapter.refresh()
        }
    }

    private fun setupSummaryView() {
        binding.summaryHeader.setOnClickListener {
            val isExpanded = binding.expandedContent.isVisible
            binding.expandedContent.visibility = if (isExpanded) View.GONE else View.VISIBLE
            binding.summaryChevron.rotation = if (isExpanded) 0f else 180f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}