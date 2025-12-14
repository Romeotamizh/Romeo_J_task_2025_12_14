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
import com.example.romeojtask.databinding.FragmentHoldingsBinding
import com.example.romeojtask.ui.DividerItemDecoration
import com.example.romeojtask.viewmodel.HoldingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

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

            val totalCurrentValue = holdings.sumOf { it.ltp * it.quantity }
            val totalInvestment = holdings.sumOf { it.avgPrice * it.quantity }
            val todaysPnl = holdings.sumOf { (it.ltp - it.close) * it.quantity }
            val totalPnl = totalCurrentValue - totalInvestment
            val totalPnlPercentage = if (totalInvestment > 0) (totalPnl / totalInvestment) * 100 else 0.0

            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

            binding.currentValueTotal.text = currencyFormat.format(totalCurrentValue)
            binding.totalInvestmentValue.text = currencyFormat.format(totalInvestment)
            binding.todaysPnlValue.text = currencyFormat.format(todaysPnl)
            binding.totalPnlValue.text = "${currencyFormat.format(totalPnl)} (${String.format("%.2f", totalPnlPercentage)}%)"

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