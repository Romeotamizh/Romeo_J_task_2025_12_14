package com.example.romeojtask.ui.holdings

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.romeojtask.R
import com.example.romeojtask.data.db.HoldingEntity
import com.example.romeojtask.databinding.ListItemHoldingBinding

class HoldingsAdapter : PagingDataAdapter<HoldingEntity, HoldingsAdapter.ViewHolder>(HoldingComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemHoldingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holding ->
            holder.binding.symbol.text = holding.symbol
            holder.binding.netQtyValue.text = holding.quantity.toString()
            holder.binding.ltpValue.text = holding.ltp.toString()

            val pnl = (holding.ltp - holding.close) * holding.quantity
            holder.binding.pnlValue.text = String.format("%.2f", pnl)

            val context = holder.itemView.context
            val pnlColor = when {
                pnl > 0 -> ContextCompat.getColor(context, R.color.green)
                pnl < 0 -> ContextCompat.getColor(context, R.color.red)
                else -> {
                    val typedValue = TypedValue()
                    context.theme.resolveAttribute(R.attr.textColorOnPrimary, typedValue, true)
                    typedValue.data
                }
            }
            holder.binding.pnlValue.setTextColor(pnlColor)
        }
    }

    class ViewHolder(val binding: ListItemHoldingBinding) : RecyclerView.ViewHolder(binding.root)

    object HoldingComparator : DiffUtil.ItemCallback<HoldingEntity>() {
        override fun areItemsTheSame(oldItem: HoldingEntity, newItem: HoldingEntity) = oldItem.symbol == newItem.symbol
        override fun areContentsTheSame(oldItem: HoldingEntity, newItem: HoldingEntity) = oldItem == newItem
    }
}