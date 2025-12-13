package com.example.romeojtask.ui.holdings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.romeojtask.data.model.Holding
import com.example.romeojtask.databinding.ListItemHoldingBinding

class HoldingsAdapter(private var holdings: List<Holding>) : RecyclerView.Adapter<HoldingsAdapter.ViewHolder>() {

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
        holder.binding.pAndLValue.text = String.format("%.2f", pnl)
    }

    override fun getItemCount() = holdings.size

    fun updateData(newHoldings: List<Holding>) {
        this.holdings = newHoldings
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ListItemHoldingBinding) : RecyclerView.ViewHolder(binding.root)
}