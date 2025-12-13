package com.example.romeojtask

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.romeojtask.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.customToolbar.searchIcon.setOnClickListener {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
        }

        binding.customToolbar.profileImage.setOnClickListener {
            Toast.makeText(this, "Profile icon clicked", Toast.LENGTH_SHORT).show()
        }

        binding.viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "POSITIONS"
                1 -> "HOLDINGS"
                else -> null
            }
        }.attach()

        // Disable the first tab (POSITIONS)
        binding.tabLayout.getTabAt(0)?.view?.isClickable = false
        binding.tabLayout.getTabAt(0)?.view?.alpha = 0.5f

        // Set the initial tab to HOLDINGS
        binding.viewPager.currentItem = 1
    }
}