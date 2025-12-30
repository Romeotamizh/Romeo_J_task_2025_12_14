package com.example.romeojtask.ui.positions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.romeojtask.data.db.HoldingEntity
import com.example.romeojtask.ui.utils.calculateOverallPnl
import com.example.romeojtask.ui.utils.calculateOverallPnlPercentage
import com.example.romeojtask.ui.utils.calculateTodayTotalPnl
import com.example.romeojtask.ui.utils.calculateTotalCurrentValue
import com.example.romeojtask.ui.utils.calculateTotalInvestment
import com.example.romeojtask.ui.utils.toIndianCurrency
import com.example.romeojtask.viewmodel.PositionsViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PositionsScreen(
    viewModel: PositionsViewModel = viewModel()
) {
    val holdings = viewModel.holdingsStream.collectAsLazyPagingItems()
    val allHoldings by viewModel.allHoldingsForSummary.observeAsState(initial = emptyList())
    
    val refreshState = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    isRefreshing = holdings.loadState.refresh is LoadState.Loading

    Scaffold(
        bottomBar = {
            if (allHoldings.isNotEmpty()) {
                PositionsSummary(allHoldings)
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    holdings.refresh()
                }
            },
            state = refreshState,
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(count = holdings.itemCount) { index ->
                    val holding = holdings[index]
                    if (holding != null) {
                        PositionItem(holding)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                    }
                }
                
                if (holdings.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PositionItem(holding: HoldingEntity) {
    val pnl = (holding.details.close - holding.details.ltp) * holding.details.quantity
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = holding.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${holding.details.quantity} @ ${holding.details.ltp.toIndianCurrency()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "P&L: ${pnl.toIndianCurrency()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (pnl >= 0) Color(0xFF4CAF50) else Color.Red
                )
                Text(
                    text = "LTP: ${holding.details.ltp.toIndianCurrency()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun PositionsSummary(holdings: List<HoldingEntity>) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    val totalCurrentValue = holdings.calculateTotalCurrentValue()
    val totalInvestment = holdings.calculateTotalInvestment()
    val todayPnl = holdings.calculateTodayTotalPnl()
    val totalPnl = calculateOverallPnl(totalCurrentValue, totalInvestment)
    val totalPnlPercentage = calculateOverallPnlPercentage(totalPnl, totalInvestment)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "Expand Summary",
                modifier = Modifier.rotate(rotationState)
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow("Current Value", totalCurrentValue.toIndianCurrency())
            SummaryRow("Total Investment", totalInvestment.toIndianCurrency())
            SummaryRow(
                "Today's Profit & Loss",
                todayPnl.toIndianCurrency(),
                color = if (todayPnl >= 0) Color(0xFF4CAF50) else Color.Red
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow(
                "Profit & Loss",
                "${totalPnl.toIndianCurrency()} (${String.format(Locale.getDefault(), "%.2f", totalPnlPercentage)}%)",
                color = if (totalPnl >= 0) Color(0xFF4CAF50) else Color.Red
            )
        } else {
             // Collapsed view showing just P&L
             Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                 Text(text = "Profit & Loss", style = MaterialTheme.typography.titleMedium)
                 Text(
                    text = "${totalPnl.toIndianCurrency()} (${String.format(Locale.getDefault(), "%.2f", totalPnlPercentage)}%)",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (totalPnl >= 0) Color(0xFF4CAF50) else Color.Red
                 )
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, color: Color = Color.Unspecified) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = color)
    }
}
