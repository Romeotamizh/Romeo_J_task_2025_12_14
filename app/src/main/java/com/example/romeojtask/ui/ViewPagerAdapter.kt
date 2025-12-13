package com.example.romeojtask.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.romeojtask.ui.holdings.HoldingsFragment
import com.example.romeojtask.ui.positions.PositionsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PositionsFragment()
            1 -> HoldingsFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}