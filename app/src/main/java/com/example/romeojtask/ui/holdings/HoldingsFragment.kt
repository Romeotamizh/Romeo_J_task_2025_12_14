package com.example.romeojtask.ui.holdings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.romeojtask.R
import com.example.romeojtask.data.utils.CalculationUtils
import com.example.romeojtask.databinding.FragmentHoldingsBinding
import com.example.romeojtask.ui.DividerItemDecoration
import com.example.romeojtask.ui.utils.FormattingUtils
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

            val totalCurrentValue = CalculationUtils.calculateTotalCurrentValue(holdings)
            val totalInvestment = CalculationUtils.calculateTotalInvestment(holdings)
            val todaysPnl = CalculationUtils.calculateTodaysTotalPnl(holdings)
            val totalPnl = CalculationUtils.calculateOverallPnl(totalCurrentValue, totalInvestment)
            val totalPnlPercentage = CalculationUtils.calculateOverallPnlPercentage(totalPnl, totalInvestment)

            binding.currentValueTotal.text = FormattingUtils.formatToIndianCurrency(totalCurrentValue)
            binding.totalInvestmentValue.text = FormattingUtils.formatToIndianCurrency(totalInvestment)
            binding.todaysPnlValue.text = FormattingUtils.formatToIndianCurrency(todaysPnl)
            binding.totalPnlValue.text = "${FormattingUtils.formatToIndianCurrency(totalPnl)} (${String.format("%.2f", totalPnlPercentage)}%)"

            // Set colors for P&L values
            val red = ContextCompat.getColor(requireContext(), R.color.red)
            val green = ContextCompat.getColor(requireContext(), R.color.green)
            val defaultColor = binding.totalInvestmentValue.currentTextColor

            binding.todaysPnlValue.setTextColor(if (todaysPnl >= 0) green else red)
            binding.totalPnlValue.setTextColor(if (totalPnl >= 0) green else red)
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