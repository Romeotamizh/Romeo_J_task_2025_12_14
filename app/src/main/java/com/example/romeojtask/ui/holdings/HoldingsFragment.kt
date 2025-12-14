package com.example.romeojtask.ui.holdings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.romeojtask.ui.utils.calculateOverallPnl
import com.example.romeojtask.ui.utils.calculateOverallPnlPercentage
import com.example.romeojtask.ui.utils.calculateTodayTotalPnl
import com.example.romeojtask.ui.utils.calculateTotalCurrentValue
import com.example.romeojtask.ui.utils.calculateTotalInvestment
import com.example.romeojtask.databinding.FragmentHoldingsBinding
import com.example.romeojtask.ui.DividerItemDecoration
import com.example.romeojtask.ui.utils.setPnlTextColor
import com.example.romeojtask.ui.utils.toIndianCurrency
import com.example.romeojtask.viewmodel.HoldingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HoldingsFragment : Fragment() {

    private lateinit var binding: FragmentHoldingsBinding

    private lateinit var viewModel: HoldingsViewModel
    private val holdingsAdapter = HoldingsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoldingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewModel()
        setupSwipeToRefresh()
        setupSummaryView()
    }

    private fun setupRecyclerView() = with(binding.holdingsRecyclerView) {
        adapter = holdingsAdapter
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(DividerItemDecoration(requireContext()))
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[HoldingsViewModel::class.java]

        lifecycleScope.launch {
            viewModel.holdingsStream.collectLatest {
                holdingsAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            holdingsAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.swipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading

                val errorState = loadStates.refresh as? LoadState.Error
                errorState?.let {
                    Toast.makeText(context, "API call failed: ${it.error.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.allHoldingsForSummary.observe(viewLifecycleOwner) { holdings ->
            if (holdings.isNullOrEmpty()) return@observe

            with(binding) {
                val totalCurrentValue = holdings.calculateTotalCurrentValue()
                val totalInvestment = holdings.calculateTotalInvestment()
                val todayPnl = holdings.calculateTodayTotalPnl()
                val totalPnl = calculateOverallPnl(totalCurrentValue, totalInvestment)
                val totalPnlPercentage = calculateOverallPnlPercentage(totalPnl, totalInvestment)

                currentValueTotal.text = totalCurrentValue.toIndianCurrency()
                totalInvestmentValue.text = totalInvestment.toIndianCurrency()
                todaysPnlValue.text = todayPnl.toIndianCurrency()
                totalPnlValue.text =
                    "${totalPnl.toIndianCurrency()} (${String.format("%.2f", totalPnlPercentage)}%)"

                // Set colors for P&L values
                todaysPnlValue.setPnlTextColor(todayPnl)
                totalPnlValue.setPnlTextColor(totalPnl)
            }
        }
    }

    private fun setupSwipeToRefresh() = with(binding.swipeRefreshLayout) {
        setOnRefreshListener {
            holdingsAdapter.refresh()
        }
    }

    private fun setupSummaryView() = with(binding) {
        summaryHeader.setOnClickListener {
            val isExpanded = expandedContent.isVisible
            expandedContent.visibility = if (isExpanded) View.GONE else View.VISIBLE
            summaryChevron.rotation = if (isExpanded) 0f else 180f
        }
    }
}