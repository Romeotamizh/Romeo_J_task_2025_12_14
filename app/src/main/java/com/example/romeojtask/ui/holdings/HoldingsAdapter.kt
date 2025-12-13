package com.example.romeojtask.ui.holdings

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.romeojtask.R
import com.example.romeojtask.data.db.HoldingEntity
import com.example.romeojtask.databinding.ListItemHoldingBinding

class HoldingsAdapter(private var holdings: List<HoldingEntity>) : RecyclerView.Adapter<HoldingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemHoldingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holding = holdings[position]
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

    override fun getItemCount() = holdings.size

    fun updateData(newHoldings: List<HoldingEntity>) {
        this.holdings = newHoldings
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ListItemHoldingBinding) : RecyclerView.ViewHolder(binding.root)
}