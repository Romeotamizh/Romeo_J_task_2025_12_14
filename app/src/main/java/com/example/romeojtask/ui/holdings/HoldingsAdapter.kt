package com.example.romeojtask.ui.holdings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.romeojtask.data.db.HoldingEntity
import com.example.romeojtask.databinding.ListItemHoldingBinding
import com.example.romeojtask.ui.utils.calculateTodayPnl
import com.example.romeojtask.ui.utils.setPnlTextColor
import com.example.romeojtask.ui.utils.toIndianCurrency

class HoldingsAdapter : PagingDataAdapter<HoldingEntity, HoldingsAdapter.ViewHolder>(HoldingComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemHoldingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holding ->
            holder.binding.symbol.text = holding.symbol
            holder.binding.netQtyValue.text = holding.details.quantity.toString()

            holder.binding.ltpValue.text = holding.details.ltp.toIndianCurrency()

            val pnl = holding.calculateTodayPnl()
            holder.binding.pnlValue.text = pnl.toIndianCurrency()
            holder.binding.pnlValue.setPnlTextColor(pnl)
        }
    }

    class ViewHolder(val binding: ListItemHoldingBinding) : RecyclerView.ViewHolder(binding.root)

    object HoldingComparator : DiffUtil.ItemCallback<HoldingEntity>() {
        override fun areItemsTheSame(oldItem: HoldingEntity, newItem: HoldingEntity) = oldItem.symbol == newItem.symbol
        override fun areContentsTheSame(oldItem: HoldingEntity, newItem: HoldingEntity) = oldItem == newItem
    }
}